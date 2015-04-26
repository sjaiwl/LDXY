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
import com.app.adapter.DynamicPartAdapter;
import com.app.function.Configuration;
import com.app.function.DynamicPart;
import com.app.xlistview.XListView;
import com.app.xlistview.XListView.IXListViewListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class JoinActivity_detail extends Activity implements IXListViewListener {
	private Intent intent;
	private Integer activity_id;
	private Integer joiners_num;
	private ImageView fanhui;
	private TextView number;
	private XListView mylistview;
	private DynamicPartAdapter personadapter;
	private List<DynamicPart> activitylist;
	private RequestQueue mRequestQueue;
	private Handler mhandler;
	private int index = 0;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.joinactivity_name);
		initView();
		initData();
	}

	private void initView() {
		intent = getIntent();
		activity_id = (Integer) intent.getSerializableExtra("activity_id");
		joiners_num = (Integer) intent.getSerializableExtra("joiners_num");
		number = (TextView) this.findViewById(R.id.canyu_activitynum);
		number.setText(joiners_num + "人参与了活动");
		fanhui = (ImageView) this.findViewById(R.id.fanhui_canyuactivity);
		mylistview = (XListView) this.findViewById(R.id.joinnum_listview);
		fanhui.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void initData() {
		mhandler = new Handler();
		mRequestQueue = Volley.newRequestQueue(this);
		activitylist = new ArrayList<DynamicPart>();
		mylistview.setPullRefreshEnable(true);
		mylistview.setPullLoadEnable(false);
		personadapter = new DynamicPartAdapter(this, activitylist);
		mylistview.setAdapter(personadapter);
		mylistview.setXListViewListener(this);
		getData(1);
		mylistview.startLoadMorePic();
	}

	private void onLoad() {
		mylistview.stopRefresh();
		mylistview.stopLoadMore();
		mylistview.setRefreshTime("刚刚");
	}

	@Override
	public void onRefresh() {
		mhandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				index = 0;
				getData(1);
			}
		}, 2000);
	}

	@Override
	public void onLoadMore() {
	}

	private void getData(final int method) {// type=1 重新生成list type=2 增长list
		String url = Configuration.getdynamicjoinersUrl + "?index=" + index
				+ "&&activity_id=" + activity_id;
		JsonArrayRequest jar = new JsonArrayRequest(url,
				new Response.Listener<JSONArray>() {
					@Override
					public void onResponse(JSONArray response) {
						List<DynamicPart> list = JSON.parseArray(
								response.toString(), DynamicPart.class);
						activitylist.clear();
						for (int i = 0; i < list.size(); i++) {
							activitylist.add(list.get(i));
						}
						personadapter.notifyDataSetChanged();
						mylistview.stopLoadMorePic();
						onLoad();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(getApplicationContext(), "参与人数获取失败",
								Toast.LENGTH_SHORT).show();
						mylistview.stopLoadMorePic();
						mylistview.stopRefresh();
						mylistview.stopLoadMore();
					}
				});
		mRequestQueue.add(jar);
	}
}
