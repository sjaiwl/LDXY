package com.app.fragment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.app.adapter.IndexListItemAdapter;
import com.app.function.ActivityUnit;
import com.app.function.Configuration;
import com.app.function.UserInfo;
import com.app.tools.CircularIndexImage;
import com.app.xlistview.XListView;
import com.app.xlistview.XListView.IXListViewListener;
import com.app.interFace.IndexListItemClickHelp;
import com.app.interFace.ThirdPartyLoginCallBack;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldxy.letsgoo.Detailpage;
import com.ldxy.letsgoo.PostPage;
import com.ldxy.letsgoo.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentIndex extends Fragment implements IXListViewListener,
		IndexListItemClickHelp, ThirdPartyLoginCallBack {
	private Intent intent;
	private CircularIndexImage rimageview;
	private TextView login;
	private TextView recommend;
	private TextView all;
	private LinearLayout post;
	private XListView mylistview;
	private List<ActivityUnit> unitList;
	private IndexListItemAdapter myadapter;
	private Handler mhandler;
	private RequestQueue mRequestQueue;
	private int type = 0; // 列表类型，0为全部，1为推荐
	private int index = 0; // 请求列表页 ，0为第一条，lastActivityId为上一条
	private int lastActivityId = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_index, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		initData();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!UserInfo.isEmpty()) {
			login.setVisibility(View.GONE);
			rimageview.setVisibility(View.VISIBLE);
			rimageview.setImageUrl(UserInfo.user.getPictureurl(), 1);
		} else {
			login.setVisibility(View.VISIBLE);
			rimageview.setVisibility(View.GONE);
		}
		getData(1);
	}

	private void initView() {
		intent = new Intent();
		// SharedPreferences
		// userinfo=getActivity().getSharedPreferences("user_info",0);
		// String usericon=userinfo.getString("usericon", null);
		// String username=userinfo.getString("username", null);
		// String usergender=userinfo.getString("usergender", null);
		// if(!Configuration.isEmpty(username)){
		// isUser=true;
		// }
		login = (TextView) getView().findViewById(R.id.index_login);
		rimageview = (CircularIndexImage) getView().findViewById(
				R.id.index_userphoto);
		recommend = (TextView) getView().findViewById(R.id.index_recommend);
		all = (TextView) getView().findViewById(R.id.index_all);
		post = (LinearLayout) getView().findViewById(R.id.index_post);
		mylistview = (XListView) getActivity()
				.findViewById(R.id.index_listview);
	}

	private void initData() {
		mhandler = new Handler();
		mRequestQueue = Volley.newRequestQueue(getActivity());
		login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Configuration
						.showLoginWindow(getActivity(), FragmentIndex.this);
			}
		});
		recommend.setOnClickListener(myclicklistener);
		all.setOnClickListener(myclicklistener);
		post.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (!UserInfo.isEmpty()) {
					intent.setClass(getActivity(), PostPage.class);
					startActivity(intent);
				} else {
					Configuration.showLoginWindow(getActivity(),
							FragmentIndex.this);
				}
			}
		});
		unitList = new ArrayList<ActivityUnit>();
		mylistview.setPullRefreshEnable(true);
		mylistview.setPullLoadEnable(true);
		myadapter = new IndexListItemAdapter(getActivity(), unitList, this);
		mylistview.setAdapter(myadapter);
		mylistview.setXListViewListener(this);
		mylistview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long id) {
				intent.putExtra("activityid", unitList.get(position - 1)
						.getActivity_id());
				intent.setClass(getActivity(), Detailpage.class);
				startActivity(intent);
			}
		});
		mylistview.startLoadMorePic();
	}

	private OnClickListener myclicklistener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.index_all:
				all.setBackgroundResource(R.drawable.allafter);
				recommend.setBackgroundResource(R.drawable.recommendbefore);
				type = 0;
				break;
			case R.id.index_recommend:
				all.setBackgroundResource(R.drawable.allbefore);
				recommend.setBackgroundResource(R.drawable.recommentafter);
				type = 1;
				break;
			}
			index = 0;
			mylistview.startLoadMorePic();
			getData(1);
		}
	};

	private void getData(final int method) { // type=1 重新生成list type=2 增长list
		int userid = 0;
		if (!UserInfo.isEmpty()) {
			userid = UserInfo.user.getUser_id();
		}
		String url = Configuration.indexUrl + "?type=" + type + "&&index="
				+ index + "&&currentuser_id=" + String.valueOf(userid);
		JsonArrayRequest jar = new JsonArrayRequest(url,
				new Response.Listener<JSONArray>() {
					@Override
					public void onResponse(JSONArray response) {
						List<ActivityUnit> list = JSON.parseArray(
								response.toString(), ActivityUnit.class);
						if (method == 1) {
							unitList.clear();
						}
						for (int i = 0; i < list.size(); i++) {
							unitList.add(list.get(i));
						}
						lastActivityId = unitList.get(unitList.size() - 1)
								.getActivity_id();
						myadapter.notifyDataSetChanged();
						mylistview.stopLoadMorePic();
						getActivity().findViewById(R.id.index_nulldata)
								.setVisibility(View.GONE);
						onLoad();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(getActivity().getApplicationContext(),
								"活动获取失败", Toast.LENGTH_SHORT).show();
						if (unitList.isEmpty()) {
							getActivity().findViewById(R.id.index_nulldata)
									.setVisibility(View.VISIBLE);
						}
						mylistview.stopLoadMorePic();
						mylistview.stopRefresh();
						mylistview.stopLoadMore();
					}
				});
		mRequestQueue.add(jar);
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

	@Override
	public void onClick(final View item, View widget, final int position,
			int which) {
		if (!UserInfo.isEmpty()) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("user_id", UserInfo.user.getUser_id());
			params.put("activity_id", unitList.get(position).getActivity_id());
			JSONObject jsonobject = new JSONObject(params);
			JsonRequest<JSONObject> jr = null;
			if (item.findViewById(R.id.index_collect_before).getVisibility() == View.VISIBLE) {
				jr = new JsonObjectRequest(Method.POST,
						Configuration.collectUrl, jsonobject,
						new Response.Listener<JSONObject>() {
							@Override
							public void onResponse(JSONObject response) {
								Type type = new TypeToken<Map<String, String>>() {
								}.getType();
								Gson gson = new Gson();
								Map<String, String> map = gson.fromJson(
										response.toString(), type);
								if (map.get("success").equals("1")) {
									item.findViewById(R.id.index_collect_before)
											.setVisibility(View.GONE);
									item.findViewById(R.id.index_collect_after)
											.setVisibility(View.VISIBLE);
								}
							}
						}, new Response.ErrorListener() {
							@Override
							public void onErrorResponse(VolleyError error) {
							}
						});
			} else {
				jr = new JsonObjectRequest(Method.POST,
						Configuration.uncollectUrl, jsonobject,
						new Response.Listener<JSONObject>() {
							@Override
							public void onResponse(JSONObject response) {
								Type type = new TypeToken<Map<String, String>>() {
								}.getType();
								Gson gson = new Gson();
								Map<String, String> map = gson.fromJson(
										response.toString(), type);
								if (map.get("success").equals("1")) {
									item.findViewById(R.id.index_collect_before)
											.setVisibility(View.VISIBLE);
									item.findViewById(R.id.index_collect_after)
											.setVisibility(View.GONE);
								}
							}
						}, new Response.ErrorListener() {
							@Override
							public void onErrorResponse(VolleyError error) {
							}
						});
			}
			mRequestQueue.add(jr);
		} else {
			Configuration.showLoginWindow(getActivity(), this);
		}
	}

	@Override
	public void donelogin() {
		getActivity().recreate();
	}
}
