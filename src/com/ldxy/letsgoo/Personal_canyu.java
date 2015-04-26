package com.ldxy.letsgoo;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import com.alibaba.fastjson.JSON;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.app.adapter.PersonActivityAdapter;
import com.app.function.ActivityUnit;
import com.app.function.Configuration;
import com.app.function.UserInfo;
import com.app.interFace.IndexListItemClickHelp;
import com.app.xlistview.XListView;
import com.app.xlistview.XListView.IXListViewListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Personal_canyu extends Activity implements IXListViewListener,
		IndexListItemClickHelp {
	private ImageView fanhui;
	private Intent intent;
	private XListView mylistview;
	private PersonActivityAdapter personadapter;
	private List<ActivityUnit> activitylist;
	private RequestQueue mRequestQueue;
	private Handler mhandler;
	private int index = 0;
	private int lastActivityId = 0;
	private static Integer user_id;
	private ImageView canyu_blank;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.personal_canyu);
		initView();
		initData();
	}

	private void initView() {
		mylistview = (XListView) this.findViewById(R.id.canyu_listview);
		canyu_blank = (ImageView) this.findViewById(R.id.canyu_blank);
	}

	private void initData() {
		fanhui = (ImageView) this.findViewById(R.id.fanhui_canyu);
		fanhui.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		if (!UserInfo.isEmpty()) {
			user_id = UserInfo.user.getUser_id();
			mhandler = new Handler();
			mRequestQueue = Volley.newRequestQueue(this);
			intent = new Intent();
			activitylist = new ArrayList<ActivityUnit>();
			mylistview.setPullRefreshEnable(true);
			mylistview.setPullLoadEnable(true);
			personadapter = new PersonActivityAdapter(0, this, activitylist,
					this);
			mylistview.setAdapter(personadapter);
			mylistview.setXListViewListener(this);
			mylistview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapter, View view,
						int position, long id) {
					intent.putExtra("activityid", activitylist
							.get(position - 1).getActivity_id());
					intent.setClass(Personal_canyu.this, Detailpage.class);
					startActivity(intent);
				}
			});
			getData(1);
			mylistview.startLoadMorePic();
		} else {
			canyu_blank.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View item, View widget, int position, int which) {
		intent.setClass(this, JoinActivity_detail.class);
		intent.putExtra("activity_id", activitylist.get(position - 1)
				.getActivity_id());
		intent.putExtra("joiners_num", activitylist.get(position - 1)
				.getjoinsum());
		startActivity(intent);
	}

	private void onLoad() {
		mylistview.stopRefresh();
		mylistview.stopLoadMore();
	}

	@Override
	public void onRefresh() {
		mhandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				index = 0;
				lastActivityId = 0;
				getData(1);
			}
		}, 2000);
	}

	@Override
	public void onLoadMore() {
		mhandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				index = lastActivityId;
				getData(2);
			}
		}, 2000);
	}

	private void getData(final int method) { // type=1 重新生成list type=2 增长list
		String url = Configuration.getdynamiccanyuUrl + "?index=" + index
				+ "&&user_id=" + user_id;
		JsonArrayRequest jar = new JsonArrayRequest(url,
				new Response.Listener<JSONArray>() {
					@Override
					public void onResponse(JSONArray response) {
						List<ActivityUnit> list = JSON.parseArray(
								response.toString(), ActivityUnit.class);
						if (method == 1) {
							activitylist.clear();
						}
						for (int i = 0; i < list.size(); i++) {
							activitylist.add(list.get(i));
						}
						if (!activitylist.isEmpty()) {
							lastActivityId = activitylist.get(
									activitylist.size() - 1).getActivity_id();
							personadapter.notifyDataSetChanged();
							mylistview.stopLoadMorePic();
							onLoad();
						} else {
							mylistview.setVisibility(View.GONE);
							canyu_blank.setVisibility(View.VISIBLE);
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(getApplicationContext(), "活动获取失败",
								Toast.LENGTH_SHORT).show();
						mylistview.stopLoadMorePic();
						if (activitylist.isEmpty()) {
							canyu_blank.setVisibility(View.VISIBLE);
							mylistview.setVisibility(View.GONE);
						}
						mylistview.stopRefresh();
						mylistview.stopLoadMore();
					}
				});
		mRequestQueue.add(jar);
	}
}
