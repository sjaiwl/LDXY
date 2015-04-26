package com.ldxy.letsgoo;

import java.util.ArrayList;
import java.util.List;

import com.app.adapter.ViewPagerAdapter;
import com.ldxy.letsgoo.R;
import com.ldxy.letsgoo.Welcome;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class Welcome extends Activity implements OnPageChangeListener {
	private SharedPreferences preferences;
	private Editor editor;
	private ViewPager vp;
	private ViewPagerAdapter vpAdapter;
	private List<View> views;
	private LinearLayout ll;
	private LinearLayout lastpage;

	private static final int[] pics = { R.drawable.app_w1, R.drawable.app_w2,
			R.drawable.app_w3, R.drawable.app_w4 };
	private ImageView[] dots;
	private int currentIndex;

	private Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		preferences = getSharedPreferences("phone", Context.MODE_PRIVATE);
		 if(preferences.getBoolean("firststart", true)){
			setContentView(R.layout.activity_welcome);
			editor = preferences.edit();
			editor.putBoolean("firststart", false);
			editor.commit();
			initFirstStart();
		} else {
			setContentView(R.layout.welcome_normal);
			mHandler = new Handler() {
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					Intent intent = new Intent();
					intent.setClass(Welcome.this, MainIndex.class);
					startActivity(intent);
					finish();
				}
			};
			mHandler.postDelayed(mRunnable, 2000);
		}
	}

	private Runnable mRunnable = new Runnable() {
		public void run() {
			mHandler.sendEmptyMessage(1);
		}
	};

	private void initFirstStart() {
		views = new ArrayList<View>();
		LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		// 初始化引导图片列表
		for (int i = 0; i < pics.length - 1; i++) {
			ImageView iv = new ImageView(this);
			iv.setLayoutParams(mParams);
			iv.setImageResource(pics[i]);
			views.add(iv);
		}
		lastpage = (LinearLayout) LayoutInflater.from(this)
				.inflate(R.layout.welcome_lastpage, null)
				.findViewById(R.id.welcome_lastpage);
		views.add(lastpage);
		vp = (ViewPager) this.findViewById(R.id.welcome_viewpage);
		// 初始化Adapter
		vpAdapter = new ViewPagerAdapter(views, this);
		vp.setAdapter(vpAdapter);
		vp.setOnPageChangeListener(this);
		// 初始化定位点
		ll = (LinearLayout) findViewById(R.id.welcome_dot);
		dots = new ImageView[pics.length];
		// 循环取得小点图片
		for (int i = 0; i < pics.length; i++) {
			dots[i] = (ImageView) ll.getChildAt(i);
			dots[i].setEnabled(true);// 都设为灰色
			dots[i].setTag(i);// 设置位置tag，方便取出与当前位置对应
		}
		currentIndex = 0;
		dots[currentIndex].setEnabled(false);// 设置为白色，即选中状态
	}

	private void setCurDot(int positon) {
		if (positon < 0 || positon > pics.length - 1 || currentIndex == positon) {
			return;
		}
		dots[positon].setEnabled(false);
		dots[currentIndex].setEnabled(true);
		currentIndex = positon;
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		if (state == 0) {
			// 什么都没做
		} else if (state == 1) {
			// 正在滑动
		} else if (state == 2) {
			// 滑动完毕
		}
	}

	@Override
	public void onPageScrolled(int cur, float percent, int change) {
		if (percent > 0.5) {
			setCurDot(cur + 1);
		} else {
			setCurDot(cur);
		}
	}

	@Override
	public void onPageSelected(int position) {
		if (position == pics.length - 1) {
			ll.setVisibility(View.GONE);
		} else {
			ll.setVisibility(View.VISIBLE);
		}
	}
}
