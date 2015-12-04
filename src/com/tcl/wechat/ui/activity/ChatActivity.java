package com.tcl.wechat.ui.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tcl.wechat.R;
import com.tcl.wechat.WeApplication;
import com.tcl.wechat.action.recorder.Recorder;
import com.tcl.wechat.action.recorder.listener.AudioRecorderStateListener;
import com.tcl.wechat.common.IConstant.ChatMsgSource;
import com.tcl.wechat.common.IConstant.ChatMsgStatus;
import com.tcl.wechat.common.IConstant.ChatMsgType;
import com.tcl.wechat.common.IConstant.EventReason;
import com.tcl.wechat.common.IConstant.EventType;
import com.tcl.wechat.controller.WeiXinMsgControl;
import com.tcl.wechat.controller.WeiXinMsgManager;
import com.tcl.wechat.controller.WeiXinNotifier;
import com.tcl.wechat.controller.listener.NewMessageListener;
import com.tcl.wechat.database.WeiRecordDao;
import com.tcl.wechat.database.WeiUserDao;
import com.tcl.wechat.model.BindUser;
import com.tcl.wechat.model.WeiXinMessage;
import com.tcl.wechat.model.WeixinMsgInfo;
import com.tcl.wechat.ui.adapter.ChatMsgAdapter;
import com.tcl.wechat.ui.adapter.FaceGVAdapter;
import com.tcl.wechat.ui.adapter.FaceVPAdapter;
import com.tcl.wechat.utils.ExpressionUtil;
import com.tcl.wechat.utils.FontUtil;
import com.tcl.wechat.utils.NetWorkUtil;
import com.tcl.wechat.utils.SystemInfoUtil;
import com.tcl.wechat.view.AudioRecorderButton;
import com.tcl.wechat.view.ChatListView;
import com.tcl.wechat.view.ChatListView.OnRefreshListener;
import com.tcl.wechat.view.ChatMsgEditText;
import com.tcl.wechat.view.ChatMsgView;
import com.tcl.wechat.view.UserInfoView;
import com.tcl.wechat.xmpp.ReplyResult;
import com.tcl.wechat.xmpp.WeiXmppCommand;
import com.tcl.wechat.xmpp.XmppEvent;
import com.tcl.wechat.xmpp.XmppEventListener;

/**
 * 聊天主界面
 * 
 * @author rex.lei
 * 
 */
@SuppressLint("InflateParams")
public class ChatActivity extends BaseActivity {

	private static final String TAG = ChatActivity.class.getSimpleName();

	private Context mContext;

	/**
	 * 界面信息
	 */
	private UserInfoView mUserIconImg;
	private UserInfoView mPrevUserInfoView;
	private UserInfoView mNextUserInfoView;
	private TextView mUserNameTv;
	private ChatMsgEditText mChatMsgEditText;
	private ChatListView mChatListView;
	private ChatMsgAdapter mAdapter;
	private AudioRecorderButton mRecorderButton;
	private RelativeLayout mLeftLayout;
	private RelativeLayout mRightLayout;

	private RelativeLayout mFaceLayout;
	private ViewPager mFaceViewPager;
	private ArrayList<View> mFacePageViews;
	private LinearLayout mDotsLayout;

	private int mPageCount = 0;
	private int mCurPageIndex = 0;

	// 7列3行
	private int COUNT_COLUMN = 7;
	private int COUNT_ROWS = 3;
	
	/**
	 * 当前绑定用户索引值
	 */
	private int mCurBindUserIndex = -1;

	/**
	 * 系统用户
	 */
	private BindUser mSysBindUser;

	/**
	 * 当天聊天用户
	 */
	private BindUser mBindUser;
	
	private BindUser mPrivBindUser;
	
	private BindUser mNextBindUser;
	
	private WeiXinMessage mPrivRecorder;
	
	private WeiXinMessage mNextRecorder;
	
	private LinkedList<String> mAllUserIds;
	
	/**
	 * 聊天记录
	 */
	//private LinkedList<WeiXinMessage> mAllUserRecorders;
	
	/**
	 * 静态表情列表
	 */
	private ArrayList<String> mStaticFacesList;

	/**
	 * 消息记录工具类
	 */
	private WeiRecordDao mRecordDao;
	
	private WeiUserDao mUserDao;
	
	private WeiXinMsgControl mWeiXinMsgControl;
	
	private WeiXinMsgManager mWeiXinMsgManager;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(null);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_chat);

		mContext = ChatActivity.this;
		mUserDao = WeiUserDao.getInstance();
		mRecordDao = WeiRecordDao.getInstance();
		mWeiXinMsgControl = WeiXinMsgControl.getInstance();
		mWeiXinMsgManager = WeiXinMsgManager.getInstance();
		//mAllUserRecorders = new LinkedList<WeiXinMessage>();

		initView();
		initEvent();
		
		//加载数据
		initData();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
	}

	private static final int MSG_UPDATE_MAINVIEW = 0x01;
	private static final int MSG_UPDATE_LEFTVIEW = 0x02;
	private static final int MSG_UPDATE_RIGHTVIEW = 0x03;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_UPDATE_MAINVIEW:
				initConterView();
				break;
				
			case MSG_UPDATE_LEFTVIEW:
				initLeftView();
				break;
			
			case MSG_UPDATE_RIGHTVIEW:
				initRightView();
				break;
				
			default:
				break;
			}
		};
	};
	
	/**
	 * 加载数据
	 */
	private void initData() {

		//通知清除通知栏
		WeiXinNotifier.getInstance().clearNotification();
		mWeiXinMsgManager.deleteAllMessage();
		
		//获取数据
		Bundle bundle = getIntent().getExtras();
		if (bundle == null) {
			return;
		}
		mBindUser = (BindUser) bundle.getParcelable("bindUser");
		mSysBindUser = WeiUserDao.getInstance().getSystemUser();

		if (mBindUser == null || mSysBindUser == null) {
			Log.e(TAG, "BindUser Or SysBindUser is NULL !!");
			return;
		}

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				//1、加载主界面
				loadCenterData();
				
				/**
				 * 加载所有两侧用户数据
				 */
				mAllUserIds = mRecordDao.getAllRecorderUserId();
				
				if (mAllUserIds != null) {
					mCurBindUserIndex = mAllUserIds.indexOf(mBindUser.getOpenId());
				}
				
				//2、加载左侧用户信息
				//loadPrivData();
				
				//3、加载右侧用户信息
				//loadNextData();
				
				//4、设置所有消息为已读
				mRecordDao.setMessageReaded();
				
				return null;
			}
		}.executeOnExecutor(WeApplication.getExecutorPool());
	}
	
	/**
	 * 加载主聊天页面数据
	 */
	private void loadCenterData() {

		mPageCount = (int) Math.ceil(mRecordDao.getRecorderCount() / 15.0);
		Log.d(TAG, "PageCount:" + mPageCount);
		
		// 1、先读取最新的15条历史记录，之后下拉刷新数据
		// mAllUserRecorders =
		// WeiMsgRecordDao.getInstance().getSystmAndUserRecorder(
		// mBindUser.getOpenId(), mSysBindUser.getOpenId());
		
		//fix by rex.lei 2015-11-28
		// LinkedList<WeiXinMessage> recorders = mRecordDao.getUserRecorder(
		// mCurPageIndex, mBindUser.getOpenId());
		// mAllUserRecorders.addAll(0, recorders);
		mWeiXinMsgManager.loadMessage(mCurPageIndex,  mBindUser.getOpenId());
		mHandler.sendEmptyMessage(MSG_UPDATE_MAINVIEW);
	}
	
	/**
	 * 加载左侧聊天页面数据
	 */
	private void loadPrivData(){
		if (mCurBindUserIndex > 0) {
			String openId = mAllUserIds.get(mCurBindUserIndex - 1);
			mPrivBindUser = mUserDao.getUser(openId);
			Log.i(TAG, "PrivBindUser:" + mPrivBindUser.toString());
			mPrivRecorder = mRecordDao.getLatestRecorder(openId);
		} else {
			mPrivRecorder = null;
		}
		mHandler.sendEmptyMessage(MSG_UPDATE_LEFTVIEW);
	}
	
	/**
	 * 加载右侧聊天页面数据
	 */
	private void loadNextData(){
		if (mCurBindUserIndex < mAllUserIds.size() - 1) {
			String openId = mAllUserIds.get(mCurBindUserIndex + 1);
			mNextBindUser = mUserDao.getUser(openId);
			Log.i(TAG, "NextBindUser:" + mNextBindUser.toString());
			mNextRecorder = mRecordDao.getLatestRecorder(openId);
		} else {
			mNextRecorder = null;
		}
		mHandler.sendEmptyMessage(MSG_UPDATE_RIGHTVIEW);
	}
	
	/**
	 * 控件初始化
	 */
	private void initView() {

		// 初始化界面
		mUserIconImg = (UserInfoView) findViewById(R.id.img_chat_usericon);
		mUserNameTv = (TextView) findViewById(R.id.tv_chat_username);
		mChatMsgEditText = (ChatMsgEditText) findViewById(R.id.edt_msg_input);
		mChatListView = (ChatListView) findViewById(R.id.lv_chat_info);
		mRecorderButton = (AudioRecorderButton) findViewById(R.id.btn_sound_reply);
		mLeftLayout = (RelativeLayout) findViewById(R.id.layout_left_msginfo);
		mRightLayout = (RelativeLayout) findViewById(R.id.layout_right_msginfo);
		mPrevUserInfoView = (UserInfoView) findViewById(R.id.chat_left_userview);
		mNextUserInfoView = (UserInfoView) findViewById(R.id.chat_right_userview);
		
		mFaceLayout = (RelativeLayout) findViewById(R.id.layout_faceview);
		mFaceViewPager = (ViewPager) findViewById(R.id.face_viewpager);
		mDotsLayout = (LinearLayout) findViewById(R.id.face_dots_container);
		mFaceLayout.setVisibility(View.GONE);
	}
	
	/**
	 * 初始化监听器事件
	 */
	@SuppressWarnings("deprecation")
	private void initEvent() {
		// 消息加载监听器
		mChatListView.setOnRefreshListener(onRefreshListener);

		// 录音状态监听器
		mRecorderButton.setRecorderCompletedListener(audioRecorderStateListener);

		// 新消息监听器
		mWeiXinMsgControl.addNewMessageListener(mNewMessageListener);

		// 表情栏目滑动监听器
		mFaceViewPager.setOnPageChangeListener(onPageChangeListener);
		
		//TextView输入文本监听
		mChatMsgEditText.addTextChangedListener(watcher);
	}

	/**
	 * 初始化列表栏目
	 */
	private void initConterView() {
		mUserIconImg.setUserIcon(mBindUser.getHeadImageUrl(), true);
		if (TextUtils.isEmpty(mBindUser.getRemarkName())) {
			WeiUserDao.getInstance().updateRemarkName(mBindUser.getOpenId(),
					mBindUser.getNickName());
			mBindUser.setRemarkName(mBindUser.getNickName());
		}
		mUserNameTv.setText(mBindUser.getRemarkName());
		mUserNameTv.setTypeface(WeApplication.getInstance().getTypeface1());

		if (mWeiXinMsgManager.getMessageCount() > 0){
			mAdapter  = new ChatMsgAdapter(mContext);
			mChatListView.setAdapter(mAdapter);
			mChatListView.setSelection(mWeiXinMsgManager.getMessageCount() - 1);
		}
		
		// 数据为空，则不再显示
		// if (mAllUserRecorders != null && !mAllUserRecorders.isEmpty()) {
		// mAdapter = new ChatMsgAdapter(mContext, mBindUser,
		// mAllUserRecorders);
		// mChatListView.setAdapter(mAdapter);
		// mChatListView.setSelection(mAllUserRecorders.size() - 1);
		// }
	}
	
	/**
	 * 左侧View初始化
	 */
	private void initLeftView(){
		
		ChatMsgView childView = new ChatMsgView(mContext);
		childView.addData(mPrivRecorder);
		
		mLeftLayout.removeAllViews();
		mLeftLayout.removeAllViewsInLayout();
		if (mPrivBindUser != null) {
			mPrevUserInfoView.setUserIcon(mPrivBindUser.getHeadImageUrl(), true);
		}
		mLeftLayout.addView(childView);
	}
	
	/**
	 * 右侧View初始化
	 */
	private void initRightView(){
		
		ChatMsgView childView = new ChatMsgView(mContext);
		childView.addData(mNextRecorder);
		
		mRightLayout.removeAllViews();
		if (mNextBindUser != null) {
			mNextUserInfoView.setUserIcon(mNextBindUser.getHeadImageUrl(), true);
		}
		mRightLayout.addView(childView);
	}

	/**
	 * 初始化表情view
	 */
	private void initFaceView() {
		if (mStaticFacesList != null) {
			return;
		}
		
		// 静态表情初始化
		mStaticFacesList = new ArrayList<String>();
		try {
			String[] faces = getAssets().list("face/png");
			for (int i = 0; i < faces.length; i++) {
				Log.i(TAG, "faces[i]:" + faces[i]);
				mStaticFacesList.add(faces[i]);
			}
			mStaticFacesList.remove("emotion_del_normal.png");
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 表情栏目
		mFacePageViews = new ArrayList<View>();
		for (int i = 0; i < getPagerCount(); i++) {
			mFacePageViews.add(setupViewPager(i));
			LayoutParams params = new LayoutParams(16, 16);
			mDotsLayout.addView(setUpDotsView(i), params);
		}
		FaceVPAdapter mVpAdapter = new FaceVPAdapter(mFacePageViews);
		mFaceViewPager.setAdapter(mVpAdapter);
		mDotsLayout.getChildAt(0).setSelected(true);
	}

	/**
	 * 获取页面个数
	 * 
	 * @return
	 */
	private int getPagerCount() {
		int count = mStaticFacesList.size();
		return count % (COUNT_COLUMN * COUNT_ROWS - 1) == 0 ? count
				/ (COUNT_COLUMN * COUNT_ROWS - 1) : count
				/ (COUNT_COLUMN * COUNT_ROWS - 1) + 1;
	}

	private ImageView setUpDotsView(int position) {
		View layout = LayoutInflater.from(mContext).inflate(R.layout.dot_image,
				null);
		ImageView dotImage = (ImageView) layout.findViewById(R.id.face_dot);
		dotImage.setId(position);
		return dotImage;
	}

	private View setupViewPager(int position) {
		View mView = LayoutInflater.from(mContext).inflate(
				R.layout.layout_face_gridview, null);
		GridView gridview = (GridView) mView.findViewById(R.id.gv_face_view);

		ArrayList<String> subList = new ArrayList<String>();

		int start = position * (COUNT_COLUMN * COUNT_ROWS - 1);
		int end = (COUNT_COLUMN * COUNT_ROWS - 1) * (position + 1) > mStaticFacesList
				.size() ? mStaticFacesList.size()
				: (COUNT_COLUMN * COUNT_ROWS - 1) * (position + 1);
		subList.addAll(mStaticFacesList.subList(start, end));
		subList.add("emotion_del_normal.png");

		FaceGVAdapter mGvAdapter = new FaceGVAdapter(mContext, subList);
		gridview.setAdapter(mGvAdapter);
		gridview.setNumColumns(COUNT_COLUMN);

		// 单击表情执行的操作
		gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				try {
					String faceStr = ((TextView) ((LinearLayout) view)
							.getChildAt(1)).getText().toString();
					Log.i(TAG, "faceStr:" + faceStr);
					if (!faceStr.contains("emotion_del_normal")) {
						insert(getFace(faceStr));
					} else {
						delete();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		return gridview;
	}

	/**
	 * 获取表情
	 * 
	 * @param png
	 * @return
	 */
	private SpannableStringBuilder getFace(String png) {
		SpannableStringBuilder sb = new SpannableStringBuilder();
		try {
			String tempText = "#[" + png + "]#";
			sb.append(tempText);
			sb.setSpan(
					new ImageSpan(mContext, BitmapFactory
							.decodeStream(getAssets().open(png))), sb.length()
							- tempText.length(), sb.length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return sb;
	}

	/**
	 * 插入表情
	 * */
	private void insert(CharSequence text) {
		Log.i(TAG, "text:" + text);

		int iCursorStart = Selection.getSelectionStart((mChatMsgEditText
				.getText()));
		int iCursorEnd = Selection
				.getSelectionEnd((mChatMsgEditText.getText()));

		if (iCursorStart != iCursorEnd) {
			mChatMsgEditText.getText().replace(iCursorStart, iCursorEnd, "");
		}
		int iCursor = Selection.getSelectionEnd((mChatMsgEditText.getText()));

		mChatMsgEditText.getText().insert(iCursor, text);
	}

	/**
	 * 删除表情
	 * */
	private void delete() {
		int iCursorEnd = Selection.getSelectionEnd(mChatMsgEditText.getText());
		int iCursorStart = Selection.getSelectionStart(mChatMsgEditText
				.getText());
		if (iCursorEnd > 0) {
			if (iCursorEnd == iCursorStart) {
				if (isDeletePng(iCursorEnd)) {
					String st = "#[face/png/smiley_000.png]#";
					((Editable) mChatMsgEditText.getText()).delete(iCursorEnd
							- st.length(), iCursorEnd);
				} else {
					((Editable) mChatMsgEditText.getText()).delete(
							iCursorEnd - 1, iCursorEnd);
				}
			} else {
				((Editable) mChatMsgEditText.getText()).delete(iCursorStart,
						iCursorEnd);
			}
		}
	}

	/**
	 * 判断即将删除的字符串是否是图片占位字符串tempText 如果是：则讲删除整个tempText
	 * 
	 * @param cursor
	 * @return
	 */
	private boolean isDeletePng(int cursor) {
		String st = "#[face/png/smiley_000.png]#";
		String content = mChatMsgEditText.getText().toString()
				.substring(0, cursor);
		if (content.length() >= st.length()) {
			String checkStr = content.substring(content.length() - st.length(),
					content.length());
			String regex = "(\\#\\[face/png/smiley_)\\d{3}(.png\\]\\#)";
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(checkStr);
			return m.matches();
		}
		return false;
	}

	/**
	 * 消息加载监听器
	 */
	private OnRefreshListener onRefreshListener = new OnRefreshListener() {

		@Override
		public void onRefresh() {
			// TODO Auto-generated method stub
			new AsyncTask<Void, Void, LinkedList<WeiXinMessage>>() {

				@Override
				protected LinkedList<WeiXinMessage> doInBackground(
						Void... params) {
					mCurPageIndex++;
					if (mCurPageIndex < mPageCount) {
						return mRecordDao.getUserRecorder(mCurPageIndex,mBindUser.getOpenId());
					}
					return null;
				}

				protected void onPostExecute(final LinkedList<WeiXinMessage> result) {
					mChatListView.onRefreshComplete();
					mChatListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);

					Log.d(TAG, "load More Data:" + result);
					if (result != null && result.size() > 0) {
						mWeiXinMsgManager.addMessage(result);
						//mAllUserRecorders.addAll(0, result);
						//mAdapter.setData(mAllUserRecorders);
						mAdapter.notifyDataSetChanged();
						mChatListView.post(new Runnable() {

							@Override
							public void run() {
								mChatListView.setSelection(result.size());
							}
						});
					}
				};
			}.executeOnExecutor(WeApplication.getExecutorPool());
		}
	};

	/**
	 * 录音状态监听器
	 */
	private AudioRecorderStateListener audioRecorderStateListener = new AudioRecorderStateListener() {

		@Override
		public void startToRecorder() {
			// TODO Auto-generated method stub
		}

		@Override
		public void onCompleted(Recorder recorder) {
			// TODO Auto-generated method stub
			Log.i(TAG, "onCompleted...");
			Log.i(TAG, "FileName:" + recorder.getFileName());

			/**
			 * 录音完成
			 */
			// 1、更新数据库
			WeiXinMessage msgRecorder = new WeiXinMessage();
			String messageId = UUID.randomUUID().toString();
			msgRecorder.setOpenid(mSysBindUser.getOpenId());
			msgRecorder.setToOpenid(mBindUser.getOpenId());
			msgRecorder.setMsgid(messageId);
			msgRecorder.setMsgtype(ChatMsgType.VOICE);
			msgRecorder.setContent(recorder.getFileName());
			msgRecorder.setFileName(recorder.getFileName());
			msgRecorder.setCreatetime(String.valueOf(System.currentTimeMillis()));
			msgRecorder.setReaded("1");
			msgRecorder.setReceived(ChatMsgSource.SENDED);
			if (NetWorkUtil.isNetworkAvailable()){
				msgRecorder.setStatus(ChatMsgStatus.SEND);
			} else {
				msgRecorder.setStatus(ChatMsgStatus.FAILED);
			}
			mWeiXinMsgManager.addMessage(msgRecorder);
			//mAllUserRecorders.addLast(msgRecorder);
			mRecordDao.addRecorder(msgRecorder);

			// 2、发送消息
			WeixinMsgInfo weixinMsgInfo = new WeixinMsgInfo();
			weixinMsgInfo.setFromusername(mSysBindUser.getOpenId());
			weixinMsgInfo.setTousername(mBindUser.getOpenId());
			weixinMsgInfo.setMsgtype(ChatMsgType.VOICE);
			weixinMsgInfo.setRecorder(recorder);
			weixinMsgInfo.setMessageid(messageId);
			mWeiXinMsgControl.sendWeiXinMsg(weixinMsgInfo, mListener);

			// 3、更新列表
			update();
		}
	};

	/**
	 * 收到新消息监听器
	 */
	private NewMessageListener mNewMessageListener = new NewMessageListener() {

		@Override
		public void onNewMessage(WeiXinMessage recorder) {
			// TODO Auto-generated method stub
			if (recorder == null) {
				return;
			}
			
			Log.i(TAG, "receive new message :" + recorder.toString());

			if (recorder.getOpenid().equals(mBindUser.getOpenId())) {
				mWeiXinMsgManager.addMessage(recorder);
				update();
			} else if (mPrivBindUser != null && 
					recorder.getOpenid().equals(mPrivBindUser.getOpenId())) {
				mPrivRecorder = recorder;
				mHandler.sendEmptyMessage(MSG_UPDATE_LEFTVIEW);
			} else if (mNextBindUser != null && 
					recorder.getOpenid().equals(mNextBindUser.getOpenId())) {
				mNextRecorder = recorder;
				mHandler.sendEmptyMessage(MSG_UPDATE_RIGHTVIEW);
			}
		}
	};

	/**
	 * 表情列表栏目监听器
	 */
	private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
			for (int i = 0; i < mDotsLayout.getChildCount(); i++) {
				mDotsLayout.getChildAt(i).setSelected(false);
			}
			mDotsLayout.getChildAt(arg0).setSelected(true);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}
	};
	
	private TextWatcher watcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			ExpressionUtil.getInstance().EditableToSpann(mContext, s);
		}
	};

	/**
	 * 更新聊天列表
	 */
	private void update() {
		mAdapter.notifyDataSetChanged();
		mChatListView.setSelection(mWeiXinMsgManager.getMessageCount() - 1);
	}

	/**
	 * 发送消息
	 * 
	 * @param view
	 */
	public void sendMessage(View view) {
		if ("".equals(mChatMsgEditText.getText().toString())) {
			mChatMsgEditText.setHint(R.string.hint_no_input);
			return;
		}

		String content = ExpressionUtil.getInstance().smileyParser(
				mChatMsgEditText.getText().toString());
		String msgid = UUID.randomUUID().toString();
		WeiXinMessage recorder = new WeiXinMessage();
		recorder.setOpenid(mSysBindUser.getOpenId());
		recorder.setToOpenid(mBindUser.getOpenId());
		recorder.setMsgtype(ChatMsgType.TEXT);
		recorder.setMsgid(msgid);
		recorder.setContent(content);
		recorder.setCreatetime(String.valueOf(System.currentTimeMillis()));
		recorder.setReceived(ChatMsgSource.SENDED);
		if (NetWorkUtil.isNetworkAvailable()){
			recorder.setStatus(ChatMsgStatus.SEND);
		} else {
			recorder.setStatus(ChatMsgStatus.FAILED);
		}

		Log.i(TAG, "msgReply：" + recorder.toString());

		// 1、更新
		mWeiXinMsgManager.addMessage(recorder);
		//mAllUserRecorders.addLast(recorder);
		mAdapter.notifyDataSetChanged();
		mChatListView.setSelection(mWeiXinMsgManager.getMessageCount() - 1);
		mChatMsgEditText.setText("");
		mChatMsgEditText.setHint("");

		// 2、保存
		mRecordDao.addRecorder(recorder);

		// 3、回复
		reply(recorder);
	}

	/**
	 * 消息回复按键回调
	 * 
	 * @param v
	 */
	public void replyTextClick(View v) {
	}

	/**
	 * 表情回复按键回调
	 * 
	 * @param v
	 */
	public void replyFaceClick(View v) {

		if (mFaceLayout.getVisibility() == View.GONE) {
			initFaceView();
			mFaceLayout.setVisibility(View.VISIBLE);
		} else {
			mFaceLayout.setVisibility(View.GONE);
		}
	}

	/**
	 * 图片选择按钮
	 * 
	 * @param view
	 */
	public void imgReplyClick(View view) {
		Intent intent = new Intent(this, PicSelectActivity.class);
		startActivityForResult(intent, 0);
	}

	/**
	 * 回复图片消息
	 * 
	 * @param fileName
	 */
	private void replyImageClick(String fileName) {
		if (TextUtils.isEmpty(fileName)) {
			return;
		}
		Log.i(TAG, "fileName:" + fileName);

		WeiXinMessage recorder = new WeiXinMessage();
		String messageId = UUID.randomUUID().toString();// 生成msgid
		recorder.setOpenid(mSysBindUser.getOpenId());
		recorder.setToOpenid(mBindUser.getOpenId());
		recorder.setMsgid(messageId);
		recorder.setMsgtype(ChatMsgType.IMAGE);
		recorder.setFileName(fileName);
		recorder.setCreatetime(String.valueOf(System.currentTimeMillis()));
		recorder.setReaded("1");//状态标识为失败
		recorder.setReceived(ChatMsgSource.SENDED);
		//recorder.setStatus(ChatMsgStatus.SEND);
		if (NetWorkUtil.isNetworkAvailable()){
			recorder.setStatus(ChatMsgStatus.SEND);
		} else {
			recorder.setStatus(ChatMsgStatus.FAILED);
		}
		mRecordDao.addRecorder(recorder);
		mWeiXinMsgManager.addMessage(recorder);
		//mAllUserRecorders.addLast(recorder);
		
		WeixinMsgInfo weixinMsgInfo = new WeixinMsgInfo();
		weixinMsgInfo.setFromusername(mSysBindUser.getOpenId());
		weixinMsgInfo.setTousername(mBindUser.getOpenId());
		weixinMsgInfo.setMsgtype(ChatMsgType.VOICE);
		weixinMsgInfo.setRecorder(new Recorder(fileName, 0, 0));
		weixinMsgInfo.setMessageid(messageId);
		mWeiXinMsgControl.sendWeiXinMsg(weixinMsgInfo, mListener);
		
		//更新
		update();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (data == null) {
			return;
		}

		String[] selectPic = data.getStringArrayExtra("selectPic");
		if (selectPic == null) {
			return;
		}

		Log.i(TAG, "selectPic size:" + selectPic.length);

		for (int i = 0; i < selectPic.length; i++) {
			replyImageClick(selectPic[i]);
		}

	}

	/**
	 * 左边消息回复按键回调
	 * 
	 * @param v
	 */
	public void msgReplyLeftClick(View v) {

	}

	/**
	 * 右边消息回复按键回调
	 * 
	 * @param v
	 */
	public void msgReplyRightClick(View v) {

	}

	/**
	 * 消息回复
	 */
	private void reply(final WeiXinMessage recorder) {
		Log.i(TAG, "reply-->>");

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				
				try {
					String content = "<![CDATA[" + URLEncoder.encode(recorder.getContent(), "UTF-8") + "]]>";
					Map<String, String> valus = new HashMap<String, String>();
					valus.put("tousername", mBindUser.getOpenId());
					valus.put("fromusername", mSysBindUser.getOpenId());
					valus.put("createtime", String.valueOf(System.currentTimeMillis()));
					valus.put("msgtype", recorder.getMsgtype());
					valus.put("msgid", recorder.getMsgid());
					valus.put("content", content/*recorder.getContent()*/);
					valus.put("mediaid", recorder.getMediaid());
					new WeiXmppCommand(EventType.TYPE_SEND_WEIXINMSG, valus,
							mListener).execute();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
		}.executeOnExecutor(WeApplication.getExecutorPool());
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		mWeiXinMsgManager.deleteAllMessage();
	}


	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mWeiXinMsgControl != null) {
			mWeiXinMsgControl.removeNewMessageListener(mNewMessageListener);
		}
	}

	private XmppEventListener mListener = new XmppEventListener() {

		@Override
		public void onEvent(final XmppEvent event) {
			Log.i(TAG, "Event type:" + event.getType() + ",reason:" + event.getReason());
			switch (event.getType()) {
			case EventType.TYPE_SEND_WEIXINMSG:
				int reason = event.getReason();
				if (reason == EventReason.REASON_COMMON_SUCCESS) {
					ReplyResult result = (ReplyResult) event.getEventData();
					String msgid = result.getMsgid();
					Log.i(TAG, "msgid:" + msgid);
					if (!TextUtils.isEmpty(msgid)) {
						//更新消息状态会成功
						mRecordDao.updateMessageReadState(msgid, "0");
						mRecordDao.updateMessageState(msgid, ChatMsgStatus.SUCCESS);
						mWeiXinMsgManager.setMessageStatus(msgid, ChatMsgStatus.SUCCESS);
					}
				}
				break;

			case EventType.TYPE_UNBIND_EVENT:
				break;

			default:
				break;
			}
		}
	};
}
