package com.app.tools;

import com.ldxy.letsgoo.R;

import android.R.color;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class SelectPopupWindow extends PopupWindow {

	private TextView btn_take_photo, btn_pick_photo, btn_cancel;
	private View mMenuView;
	@SuppressWarnings("unused")
	private LinearLayout layout;

	public SelectPopupWindow(final Activity context,
			OnClickListener itemsOnClick) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.checkimage, null);
		layout = (LinearLayout) mMenuView.findViewById(R.id.popimage_layout);
		btn_take_photo = (TextView) mMenuView.findViewById(R.id.take_photo);
		btn_pick_photo = (TextView) mMenuView.findViewById(R.id.take_picture);
		btn_cancel = (TextView) mMenuView.findViewById(R.id.take_cancel);
		btn_cancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				WindowManager.LayoutParams lp = context.getWindow()
						.getAttributes();
				lp.alpha = 1f;
				context.getWindow().setAttributes(lp);
				dismiss();
			}
		});
		btn_pick_photo.setOnClickListener(itemsOnClick);
		btn_take_photo.setOnClickListener(itemsOnClick);
		this.setContentView(mMenuView);
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		this.setAnimationStyle(R.style.AnimBottom);
		ColorDrawable dw = new ColorDrawable(color.transparent);
		this.setBackgroundDrawable(dw);
		mMenuView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {

				int height = mMenuView.findViewById(R.id.popimage_layout)
						.getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP
						|| event.getAction() == MotionEvent.ACTION_DOWN) {
					if (y < height) {
						dismiss();
					}
				}
				return true;
			}
		});
	}

}
