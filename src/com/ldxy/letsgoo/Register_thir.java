package com.ldxy.letsgoo;

import com.app.function.Configuration;
import com.app.function.UserInfo;
import com.app.tools.ExitApplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Register_thir extends Activity {
	private ImageView register_back;
	private RelativeLayout next;
	private TextView nextcontent;
	private ImageView resoult;

	protected void onCreate(Bundle savedInstanceState) {
		ExitApplication.getInstance().addActivity(this);
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_registerthd);
		initView();
		initData();
	}

	private void initView() {
		register_back = (ImageView) this.findViewById(R.id.register_back3);
		next = (RelativeLayout) this.findViewById(R.id.register_next3);
		nextcontent = (TextView) this.findViewById(R.id.register_next3text);
		resoult = (ImageView) this.findViewById(R.id.register_thdresoult);
	}

	private void initData() {
		if (UserInfo.user.getChecked().equals("1")) {
			resoult.setBackgroundResource(R.drawable.renzheng_after);
			nextcontent.setText("开始新旅程");
			register_back.setVisibility(View.GONE);
		}
		register_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Register_thir.this, MainIndex.class);
				Register_thir.this.startActivityForResult(intent, Configuration.BACK_IDENTIFY);
				finish();
			}
		});
	}
}
