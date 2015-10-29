package com.tcl.wechat.ui.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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
import com.tcl.wechat.common.IConstant.ChatMsgType;
import com.tcl.wechat.common.IConstant.EventReason;
import com.tcl.wechat.common.IConstant.EventType;
import com.tcl.wechat.controller.WeiXinMsgManager;
import com.tcl.wechat.controller.listener.NewMessageListener;
import com.tcl.wechat.database.WeiMsgRecordDao;
import com.tcl.wechat.database.WeiUserDao;
import com.tcl.wechat.model.BindUser;
import com.tcl.wechat.model.WeiXinMsgRecorder;
import com.tcl.wechat.model.WeixinMsgInfo;
import com.tcl.wechat.ui.adapter.ChatMsgAdapter;
import com.tcl.wechat.ui.adapter.FaceGVAdapter;
import com.tcl.wechat.ui.adapter.FaceVPAdapter;
import com.tcl.wechat.utils.DateTimeUtil;
import com.tcl.wechat.utils.ExpressionUtil;
import com.tcl.wechat.view.AudioRecorderButton;
import com.tcl.wechat.view.ChatListView;
import com.tcl.wechat.view.ChatListView.OnRefreshListener;
import com.tcl.wechat.view.ChatMsgEditText;
import com.tcl.wechat.view.UserInfoView;
import com.tcl.wechat.xmpp.ReplyResult;
import com.tcl.wechat.xmpp.WeiXmppCommand;
import com.tcl.wechat.xmpp.XmppEvent;
import com.tcl.wechat.xmpp.XmppEventListener;

/**
 * 聊天主界面
 * @author rex.lei
 *
 */
@SuppressLint("InflateParams") 
public class ChatActivity extends Activity {
	
	private static final String TAG = ChatActivity.class.getSimpleName();

	private Context mContext;
	
	/**
	 * 界面信息
	 */
	private UserInfoView mUserIconImg;
	private TextView mUserNameTv;
	private ChatMsgEditText mChatMsgEditText;
	private ChatListView mChatListView;
	private ChatMsgAdapter mAdapter;
	private AudioRecorderButton mRecorderButton;
	
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
	 * 系统用户
	 */
	private BindUser mSysBindUser;
	
	/**
	 * 当天聊天用户
	 */
	private BindUser mBindUser;
	
	/**
	 * 聊天记录
	 */
	private LinkedList<WeiXinMsgRecorder> mALlUserRecorders;
	
	/**
	 * 聊天记录
	 */
	private HashMap<String, WeiXinMsgRecorder> mAllUserRecorderMap;
	
	/**
	 * 静态表情列表
	 */
	private ArrayList<String> mStaticFacesList ;
	
	/**
	 * 消息记录工具类
	 */
	private WeiMsgRecordDao mRecordDao;
	
	/**
	 * 日期格式
	 */
	@SuppressLint("SimpleDateFormat") 
	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
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
		mRecordDao = WeiMsgRecordDao.getInstance();
		mWeiXinMsgManager = WeiXinMsgManager.getInstance();
		mALlUserRecorders = new LinkedList<WeiXinMsgRecorder>();
		mAllUserRecorderMap = new HashMap<String, WeiXinMsgRecorder>();
		mStaticFacesList = new ArrayList<String>();
		
		initData();
		initView();
		initEvent();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
	}
	
	/**
	 * 添加历史记录数据到LinkList
	 * @param recorders
	 */
	private void addHistoryData(ArrayList<WeiXinMsgRecorder> recorders){
		int size = recorders.size();
		for (int i = 0; i < size; i++) {
			WeiXinMsgRecorder recorder = recorders.get(i);
			if (!recorder.getOpenid().equals(mSysBindUser.getOpenId())){
				recorder.setReceived(true);
			} else {
				recorder.setReceived(false);
			}
			mALlUserRecorders.addFirst(recorder);
		}
	}
	
	/**
	 * 数据初始化
	 */
	private void initData() {
		
		/**
		 * 获取数据信息方案：
		 * 1、新消息处理： 使用内存数据库
		 * 2、查询消息处理：查看历史记录
		 * 3、删除消息处理：
		 */
		Bundle bundle = getIntent().getExtras();
		if (bundle == null){
			return ;
		}
		mBindUser = (BindUser) bundle.getParcelable("bindUser");
		mSysBindUser = WeiUserDao.getInstance().getSystemUser();
		
		if (mBindUser == null || mSysBindUser == null){
			Log.e(TAG, "BindUser Or SysBindUser is NULL !!");
			return ;
		}
		
		new AsyncTask<Void, Void, Void>(){

			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				loadData();
				return null;
			}
			
			protected void onPostExecute(Void result) {
				initListView();
			};
			
		}.executeOnExecutor(WeApplication.getExecutorPool());
	}

	/**
	 * 控件初始化
	 */
	private void initView() {
		
		//初始化界面
		mUserIconImg = (UserInfoView) findViewById(R.id.img_chat_usericon);
		mUserNameTv = (TextView) findViewById(R.id.tv_chat_username);
		mChatMsgEditText = (ChatMsgEditText) findViewById(R.id.edt_msg_input);
		mChatListView = (ChatListView) findViewById(R.id.lv_chat_info);
		mRecorderButton = (AudioRecorderButton) findViewById(R.id.btn_sound_reply);
		
		//mMessageLayout = (ScrollView) findViewById(R.id.layout_msg_edit);
		mFaceLayout = (RelativeLayout) findViewById(R.id.layout_faceview);
		mFaceViewPager = (ViewPager) findViewById(R.id.face_viewpager);
		mDotsLayout = (LinearLayout) findViewById(R.id.face_dots_container);
		//mMessageLayout.setVisibility(View.GONE);
		mFaceLayout.setVisibility(View.GONE);
	}
	
	/**
	 * 初始化监听器事件
	 */
	@SuppressWarnings("deprecation")
	private void initEvent() {
		//消息加载监听器
		mChatListView.setOnRefreshListener(onRefreshListener);
		
		//录音状态监听器
		mRecorderButton.setRecorderCompletedListener(audioRecorderStateListener);
		
		//新消息监听器
		mWeiXinMsgManager.addNewMessageListener(mNewMessageListener);
		
		//表情栏目滑动监听器
		mFaceViewPager.setOnPageChangeListener(onPageChangeListener);
	}
	
	
	/**
	 * 加载数据
	 */
	private void loadData(){
		
		mPageCount = (int) Math.ceil(mRecordDao.getRecorderCount() / 15.0) ;
		Log.d(TAG, "PageCount:" + mPageCount);
		
		//1、先读取最新的15条历史记录，之后下拉刷新数据
		//mALlUserRecorders = WeiMsgRecordDao.getInstance().getSystmAndUserRecorder(
        //				mBindUser.getOpenId(), mSysBindUser.getOpenId());
		ArrayList<WeiXinMsgRecorder> recorders = mRecordDao.getUserRecorder(mCurPageIndex,
				mBindUser.getOpenId());
		addHistoryData(recorders);
		
		if (mALlUserRecorders != null){
			int size = mALlUserRecorders.size();
			Log.i(TAG, "Recorder size:" + size);
			
			for (WeiXinMsgRecorder recorder : mALlUserRecorders) {
				if (recorder.getOpenid().equals(mBindUser.getOpenId())){
					recorder.setReceived(true);
				} else {
					recorder.setReceived(false);
				}
				mAllUserRecorderMap.put(recorder.getMsgid(), recorder);
			}
		}
		
		//静态表情初始化 
		//TODO 表情后续要优化， 启动表情的时候再去加载
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
	}
	
	/**
	 * 初始化列表栏目
	 */
	private void initListView(){
		mUserIconImg.setUserIcon(mBindUser.getHeadImageUrl(), true);
		if (TextUtils.isEmpty(mBindUser.getRemarkName())){
			WeiUserDao.getInstance().updateRemarkName(mBindUser.getOpenId(), mBindUser.getNickName());
			mBindUser.setRemarkName(mBindUser.getNickName());
		}
		mUserNameTv.setText(mBindUser.getRemarkName());
		
		
		//数据为空，则不再显示
		if (mALlUserRecorders != null && !mALlUserRecorders.isEmpty()){
			mAdapter = new ChatMsgAdapter(mContext, mBindUser, mALlUserRecorders);
			mChatListView.setAdapter(mAdapter);
			mChatListView.setSelection(mALlUserRecorders.size() - 1);
		}
		
		//表情栏目
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
	 * @return
	 */
	private int getPagerCount() {
		int count = mStaticFacesList.size();
		return count % (COUNT_COLUMN * COUNT_ROWS - 1) == 0 
				? count / (COUNT_COLUMN * COUNT_ROWS - 1)
				: count / (COUNT_COLUMN * COUNT_ROWS - 1) + 1;
	}
	
	
	private ImageView setUpDotsView(int position) {
		View layout = LayoutInflater.from(mContext).inflate(R.layout.dot_image, null);
		ImageView dotImage = (ImageView) layout.findViewById(R.id.face_dot);
		dotImage.setId(position);
		return dotImage;
	}
	
	private View setupViewPager(int position){
		View mView = LayoutInflater.from(mContext).inflate(R.layout.layout_face_gridview, null);
		GridView gridview = (GridView) mView.findViewById(R.id.gv_face_view);
		
		ArrayList<String> subList = new ArrayList<String>();
		
		int start = position * (COUNT_COLUMN * COUNT_ROWS - 1);
		int end = (COUNT_COLUMN * COUNT_ROWS - 1) * (position + 1) > mStaticFacesList.size() ? 
					mStaticFacesList.size() : 
					(COUNT_COLUMN * COUNT_ROWS - 1) * (position + 1);
		subList.addAll(mStaticFacesList.subList(start, end));
		subList.add("emotion_del_normal.png");
		
		
		FaceGVAdapter mGvAdapter = new FaceGVAdapter(mContext, subList);
		gridview.setAdapter(mGvAdapter);
		gridview.setNumColumns(COUNT_COLUMN);
		
		// 单击表情执行的操作
		gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
	 * @param png
	 * @return
	 */
	private SpannableStringBuilder getFace(String png) {
		SpannableStringBuilder sb = new SpannableStringBuilder();
		try {
			String tempText = "#[" + png + "]#";
			sb.append(tempText);
			sb.setSpan(new ImageSpan(mContext, BitmapFactory
							.decodeStream(getAssets().open(png))), 
							sb.length() - tempText.length(), sb.length(),
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
		
		int iCursorStart = Selection.getSelectionStart((mChatMsgEditText.getText()));
		int iCursorEnd = Selection.getSelectionEnd((mChatMsgEditText.getText()));
		
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
		int iCursorStart = Selection.getSelectionStart(mChatMsgEditText.getText());
		if (iCursorEnd > 0) {
			if (iCursorEnd == iCursorStart) {
				if (isDeletePng(iCursorEnd)) {
					String st = "#[face/png/smiley_000.png]#";
					((Editable) mChatMsgEditText.getText()).delete(iCursorEnd - st.length(), iCursorEnd);
				} else {
					((Editable) mChatMsgEditText.getText()).delete(iCursorEnd - 1, iCursorEnd);
				}
			} else {
				((Editable) mChatMsgEditText.getText()).delete(iCursorStart, iCursorEnd);
			}
		}
	}

	/**
	 * 判断即将删除的字符串是否是图片占位字符串tempText 如果是：则讲删除整个tempText
	 * @param cursor
	 * @return
	 */
	private boolean isDeletePng(int cursor) {
		String st = "#[face/png/smiley_000.png]#";
		String content = mChatMsgEditText.getText().toString().substring(0, cursor);
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
			new AsyncTask<Void, Void, Integer>(){

				@Override
				protected Integer doInBackground(Void... params) {
					mCurPageIndex++;
					if (mCurPageIndex < mPageCount){
						ArrayList<WeiXinMsgRecorder> recorders = mRecordDao.getUserRecorder(mCurPageIndex, 
								mBindUser.getOpenId());
						Log.d(TAG, "load More Data:" + recorders);
						if (recorders != null){
							addHistoryData(recorders);
						}
						return recorders == null ? 0 : recorders.size();
					}
					return 0;
				}
				protected void onPostExecute(final Integer result) {
					mChatListView.onRefreshComplete();
					mChatListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
					Log.i(TAG, "result:" + result);
					if (result > 0){
						mAdapter.setData(mALlUserRecorders);
						mAdapter.notifyDataSetChanged();
						mChatListView.post(new Runnable() {
							
							@Override
							public void run() {
								mChatListView.setSelection(result/*mALlUserRecorders.size() - 15*/);
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
	private AudioRecorderStateListener audioRecorderStateListener = new 
			AudioRecorderStateListener() {
		
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
			//1、更新数据库
			WeiXinMsgRecorder msgRecorder = new WeiXinMsgRecorder();
			String messageId = UUID.randomUUID().toString();
			msgRecorder.setOpenid(mSysBindUser.getOpenId());
			msgRecorder.setMsgid(messageId);
			msgRecorder.setMsgtype(ChatMsgType.VOICE);
			msgRecorder.setContent(recorder.getFileName());
			msgRecorder.setFileName(recorder.getFileName());
			mALlUserRecorders.add(msgRecorder);
			mAllUserRecorderMap.put(messageId, msgRecorder);
			
			//2、发送消息
			WeixinMsgInfo weixinMsgInfo = new WeixinMsgInfo();
			weixinMsgInfo.setFromusername(mSysBindUser.getOpenId());
			weixinMsgInfo.setTousername(mBindUser.getOpenId());
			weixinMsgInfo.setMsgtype(ChatMsgType.VOICE);
			weixinMsgInfo.setRecorder(recorder);
			weixinMsgInfo.setMessageid(messageId);
			mWeiXinMsgManager.sendWeiXinMsg(weixinMsgInfo, mListener);
			
			//3、更新列表
			update();
		}
	};
	
	/**
	 * 收到新消息监听器
	 */
	private NewMessageListener mNewMessageListener = new NewMessageListener(){

		@Override
		public void onNewMessage(WeiXinMsgRecorder recorder) {
			// TODO Auto-generated method stub
			if (recorder == null){
				return ;
			}
			recorder.setReceived(true);
			mALlUserRecorders.add(recorder);
			update();
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
	
	/**
	 * 更新聊天列表
	 */
	private void update(){
		mAdapter.notifyDataSetChanged();
		mChatListView.setSelection(mALlUserRecorders.size() -1);
	}
	
	/**
	 * 发送消息
	 * @param view
	 */
	public void sendMessage(View view){
		if ("".equals(mChatMsgEditText.getText().toString())){
			return ;
		}
		String content = ExpressionUtil.getInstance().smileyParser(
				mChatMsgEditText.getText().toString());
		String msgid = UUID.randomUUID().toString();
		WeiXinMsgRecorder recorder = new WeiXinMsgRecorder();
		recorder.setOpenid(mSysBindUser.getOpenId());
		recorder.setMsgtype("text");
		recorder.setMsgid(msgid);
		recorder.setContent(content);
		recorder.setCreatetime(df.format(new Date()));
		
		Log.i(TAG, "msgReply：" + recorder.toString());
		
		//1、回复
		reply(recorder);
		
		//2、保存
		mRecordDao.addRecorder(recorder, mBindUser.getOpenId());
		
		//3、更新
		mALlUserRecorders.add(recorder);
		mAdapter.notifyDataSetChanged();
		mChatListView.setSelection(mALlUserRecorders.size() - 1);
		mChatMsgEditText.setText("");
	}
	
	/**
	 * 消息回复按键回调
	 * @param v
	 */
	public void replyTextClick(View v){
		
//		if (mFaceLayout.getVisibility() == View.VISIBLE){
//			mFaceLayout.setVisibility(View.GONE);
//		} else {
//			if (mMessageLayout.getVisibility() == View.GONE){
//				mMessageLayout.setVisibility(View.VISIBLE);
//				mChatMsgEditText.requestFocus();
//				mChatMsgEditText.setFocusable(true);
//				mChatMsgEditText.setFocusableInTouchMode(true);
//				
//			} else {
//				mMessageLayout.setVisibility(View.GONE);
//				mChatMsgEditText.setText("");
//			}
//		}
	}
	
	/**
	 * 表情回复按键回调
	 * @param v
	 */
	public void replyFaceClick(View v){
		
		if (mFaceLayout.getVisibility() == View.GONE){
			mFaceLayout.setVisibility(View.VISIBLE);
			
//			if (mMessageLayout.getVisibility() == View.GONE){
//				mMessageLayout.setVisibility(View.VISIBLE);
//				mChatMsgEditText.requestFocus();
//				mChatMsgEditText.setFocusable(true);
//				mChatMsgEditText.setFocusableInTouchMode(true);
//			}
			
		} else {
			mFaceLayout.setVisibility(View.GONE);
//			mMessageLayout.setVisibility(View.GONE);
//			mChatMsgEditText.setText("");
		}
	}
	
	/**
	 * 图片选择按钮
	 * @param view
	 */
	public void imgReplyClick(View view){
		Intent intent = new Intent(this, PicSelectActivity.class);
		startActivityForResult(intent, 0);
	}
	
	/**
	 * 回复图片消息
	 * @param fileName
	 */
	private void replyImageClick(String fileName){
		if (TextUtils.isEmpty(fileName)){
			return ;
		}
		Log.i(TAG, "fileName:" + fileName);
		
		//1、更新数据库  ---后续要判断图片发送的状态，比如发送一半断网，取消发送等等情况。
		WeiXinMsgRecorder msgRecorder = new WeiXinMsgRecorder();
		String messageId = UUID.randomUUID().toString();//生成msgid
		msgRecorder.setOpenid(mSysBindUser.getOpenId());
		msgRecorder.setMsgid(messageId);
		msgRecorder.setMsgtype(ChatMsgType.IMAGE);
		msgRecorder.setFileName(fileName);
		msgRecorder.setCreatetime(DateTimeUtil.getNowDateTime().toString());
		msgRecorder.setReceived(false);
		mAllUserRecorderMap.put(messageId, msgRecorder);
		mALlUserRecorders.add(msgRecorder);
		mRecordDao.addRecorder(msgRecorder, mBindUser.getOpenId());
		
		//2、发送消息
		Recorder recorder = new Recorder();
		recorder.setFileName(fileName);
		
		WeixinMsgInfo weixinMsgInfo = new WeixinMsgInfo();
		weixinMsgInfo.setFromusername(mSysBindUser.getOpenId());
		weixinMsgInfo.setTousername(mBindUser.getOpenId());
		weixinMsgInfo.setMsgtype(ChatMsgType.IMAGE);
		weixinMsgInfo.setRecorder(recorder);
		weixinMsgInfo.setMessageid(messageId);
		mWeiXinMsgManager.sendWeiXinMsg(weixinMsgInfo, mListener);
		
		//3、更新列表
		mAdapter.setUpload(true);
		update();
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if (data == null){
			return ;
		}
		
		String[] selectPic = data.getStringArrayExtra("selectPic");
		if (selectPic == null){
			return ;
		}
		
		Log.i(TAG, "selectPic size:" + selectPic.length);
		
		for (int i = 0; i < selectPic.length; i++) {
			replyImageClick(selectPic[i]);
		}
		
	}
	
	/**
	 * 左边消息回复按键回调
	 * @param v
	 */
	public void msgReplyLeftClick(View v){
		
	}
	
	/**
	 * 右边消息回复按键回调
	 * @param v
	 */
	public void msgReplyRightClick(View v){
		
	}
	

	/**
	 * 消息回复
	 */
	private void reply(final WeiXinMsgRecorder recorder){
		Log.i(TAG, "reply-->>");
		
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				Map<String, String> valus = new HashMap<String, String>();
				valus.put("tousername", mBindUser.getOpenId());
				valus.put("fromusername", mSysBindUser.getOpenId());
				valus.put("createtime", String.valueOf(System.currentTimeMillis()));
				valus.put("msgtype", recorder.getMsgtype());
				valus.put("msgid", recorder.getMsgid());
				valus.put("content", recorder.getContent());
				valus.put("mediaid", recorder.getMediaid());
				new WeiXmppCommand(EventType.TYPE_SEND_WEIXINMSG, valus, mListener).execute();
				return null;
			}
		}.executeOnExecutor(WeApplication.getExecutorPool());
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}
	
	@Override  
	public boolean onTouchEvent(MotionEvent event) {  
	  // TODO Auto-generated method stub  
		if(event.getAction() == MotionEvent.ACTION_DOWN){  
			if(getCurrentFocus()!=null && getCurrentFocus().getWindowToken()!=null){ 
				InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
				manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 
						InputMethodManager.HIDE_NOT_ALWAYS);  
			}  
		}  
		return super.onTouchEvent(event);  
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
		
		if (mWeiXinMsgManager != null){
			mWeiXinMsgManager.removeNewMessageListener(mNewMessageListener);
		}
	}
	
	private XmppEventListener mListener = new XmppEventListener(){

		@Override
		public void onEvent(final XmppEvent event) {
			Log.i(TAG, "Event type:" + event.getType());
			switch (event.getType()) {
			case EventType.TYPE_SEND_WEIXINMSG:
				int reason = event.getReason();
				Log.i(TAG, "reason:" + reason);
				if (reason == EventReason.REASON_COMMON_SUCCESS){
					ReplyResult result = (ReplyResult) event.getEventData();
					String msgid = result.getMsgid();
					WeiXinMsgRecorder recorder = mAllUserRecorderMap.get(msgid);
					String url = result.getResult();
					if (!TextUtils.isEmpty(url)){
						Log.i(TAG, "Result url:" + url);
						//在此要判断是否添加成功
						mRecordDao.updateRecorderUrl(msgid, result.getResult());
						recorder.setUrl(url);
						
						int size = mALlUserRecorders.size();
						mALlUserRecorders.set(size - 1, recorder);
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								mAdapter.setUpload(false);
								mAdapter.setData(mALlUserRecorders);
								mAdapter.notifyDataSetChanged();
								mChatListView.setSelection(mALlUserRecorders.size() -1);
							}
						});
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
