package com.app.adapter;

import java.util.List;

import com.ldxy.letsgoo.MainIndex;
import com.ldxy.letsgoo.R;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ViewPagerAdapter extends PagerAdapter {

	private List<View> views;
	private Activity context;

	public ViewPagerAdapter(List<View> views, Activity context) {
		this.views = views;
		this.context = context;
	}

	// 销毁position位置的界面
	@Override
	public void destroyItem(View v, int position, Object arg2) {
		((ViewPager) v).removeView(views.get(position));
	}

	@Override
	public int getCount() {
		if (views != null) {
			return views.size();
		}
		return 0;
	}

	@Override
	public Object instantiateItem(View v, int position) {
		((ViewPager) v).addView(views.get(position), 0);
		if (position == 3) {
			Button bt = (Button) v.findViewById(R.id.welcome_bt);
			bt.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent();
					intent.setClass(context, MainIndex.class);
					context.startActivity(intent);
					context.finish();
				}
			});
		}
		return views.get(position);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return (arg0 == arg1);
	}

}
