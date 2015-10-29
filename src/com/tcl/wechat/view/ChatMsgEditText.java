package com.tcl.wechat.view;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.EditText;

/**
 * 聊天文字输入框
 * @author rex.lei
 *
 */
public class ChatMsgEditText extends EditText implements OnMenuItemClickListener{
	
	private final Context mContext;

	private static final int ID_CUT = android.R.id.cut;
    private static final int ID_COPY = android.R.id.copy;
    private static final int ID_PASTE = android.R.id.paste;
    private static final int ID_SELECT_ALL = android.R.id.selectAll;

	public ChatMsgEditText(Context context) {
		super(context);
		this.mContext = context;
	}

	public ChatMsgEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	public ChatMsgEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
	}
	
	@Override
	protected void onCreateContextMenu(ContextMenu menu) {
		menu.add(0, ID_PASTE, 0, getResources().getString(R.string.paste)).setOnMenuItemClickListener(this);
		menu.add(0, ID_CUT, 1, getResources().getString(R.string.cut)).setOnMenuItemClickListener(this);
		menu.add(0, ID_COPY, 1, getResources().getString(R.string.copy)).setOnMenuItemClickListener(this);
		menu.add(0, ID_SELECT_ALL, 1, getResources().getString(R.string.selectAll)).setOnMenuItemClickListener(this);
		super.onCreateContextMenu(menu);
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		// TODO Auto-generated method stub
		return onTextContextMenuItem(item.getItemId());
	}
	
	@Override
	public boolean onTextContextMenuItem(int id) {
		// Do your thing:
		boolean consumed = super.onTextContextMenuItem(id);
		// React:
		switch (id) {
		case ID_CUT:
			break;
		case ID_PASTE:
			break;
		case ID_COPY:
			break;
		case ID_SELECT_ALL:
			break;
		}
		return consumed;
	}

}
