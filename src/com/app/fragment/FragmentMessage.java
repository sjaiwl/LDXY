package com.app.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
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
import com.app.adapter.Messageadapter;
import com.app.function.Configuration;
import com.app.function.DynamicMessage;
import com.app.function.EditTextSpuer;
import com.app.function.UserInfo;
import com.app.interFace.IndexListItemClickHelp;
import com.app.interFace.ThirdPartyLoginCallBack;
import com.app.xlistview.XListView;
import com.app.xlistview.XListView.IXListViewListener;
import com.ldxy.letsgoo.Detailpage;
import com.ldxy.letsgoo.R;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FragmentMessage extends Fragment implements IXListViewListener,
		IndexListItemClickHelp, ThirdPartyLoginCallBack {

	private XListView mylistview;
	private Messageadapter messageadapter;
	private static List<DynamicMessage> messagelist;
	private RequestQueue mRequestQueue;
	private Handler mhandler;
	private Intent intent;
	private int index = 0;
	private int lastActivityId = 0;
	private static Integer user_id;
	private static EditTextSpuer comment_edit;
	private static TextView send;
	private DynamicMessage message = null;
	private String successresponse = null;
	private static View child = null;
	private static PopupWindow mpopupWindow;
	private static String texthint;
	private ImageView message_blank;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_message, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
	}

	@Override
	public void onResume() {
		if (!UserInfo.isEmpty()) {
			user_id = UserInfo.user.getUser_id();
			initData();
			getData(1);
		} else {
			message_blank.setVisibility(View.VISIBLE);
		}
		super.onResume();
	}

	private void initView() {
		mylistview = (XListView) getActivity().findViewById(
				R.id.message_listview);
		message_blank = (ImageView) getActivity().findViewById(
				R.id.message_blank);
	}

	private void initData() {
		intent = new Intent();
		mhandler = new Handler();
		mRequestQueue = Volley.newRequestQueue(getActivity());
		messagelist = new ArrayList<DynamicMessage>();
		mylistview.setPullRefreshEnable(true);
		mylistview.setPullLoadEnable(true);
		messageadapter = new Messageadapter(getActivity(), messagelist, this);
		mylistview.setAdapter(messageadapter);
		mylistview.setXListViewListener(this);
		mylistview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long id) {
				intent.putExtra("activityid", messagelist.get(position - 1)
						.getActivity_id());
				intent.setClass(getActivity(), Detailpage.class);
				startActivity(intent);
			}
		});
		mylistview.startLoadMorePic();
	}

	// 回复点击
	@Override
	public void onClick(View item, View widget, int position, int which) {
		message = messagelist.get(position);
		child = mylistview.getChildAt(position + 1);
		texthint = "回复给:" + message.getNickname();
		mylistview.setSelection(position + 1);
		ReplyBox();
		popupInputMethodWindow();
	}

	private void getData(final int method) {
		String url = Configuration.getdynamicmessagesUrl + "?index=" + index
				+ "&&user_id=" + user_id;
		JsonArrayRequest jar = new JsonArrayRequest(url,
				new Response.Listener<JSONArray>() {
					@Override
					public void onResponse(JSONArray response) {
						List<DynamicMessage> list = JSON.parseArray(
								response.toString(), DynamicMessage.class);
						if (method == 1) {
							messagelist.clear();
						}
						for (int i = 0; i < list.size(); i++) {
							messagelist.add(list.get(i));
						}
						if (!messagelist.isEmpty()) {
							lastActivityId = messagelist.get(
									messagelist.size() - 1).getDynamic_id();
						}
						if (!messagelist.isEmpty()) {
							messageadapter.notifyDataSetChanged();
							mylistview.stopLoadMorePic();
							onLoad();
						} else {
							// 消息列表为空时f
							mylistview.setVisibility(View.GONE);
							message_blank.setVisibility(View.VISIBLE);
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(getActivity().getApplicationContext(),
								"消息获取失败", Toast.LENGTH_SHORT).show();
						mylistview.stopLoadMorePic();
						if (messagelist.isEmpty()) {
							message_blank.setVisibility(View.VISIBLE);
							mylistview.setVisibility(View.GONE);
						}
						mylistview.stopRefresh();
						mylistview.stopLoadMore();
					}
				});
		mRequestQueue.add(jar);
	}

	private void postMessage() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("executor_id", user_id);
		params.put("suffer_id", message.getExecutor_id());
		params.put("comment", comment_edit.getText().toString().trim());
		params.put("activity_id", message.getActivity_id());
		params.put("updated_at", Configuration.getNowUTCtime());
		params.put("operate_type", 4);
		String url = Configuration.dynamicsUrl;
		JSONObject jsonobject = new JSONObject(params);
		JsonRequest<JSONObject> jr = null;
		jr = new JsonObjectRequest(Method.POST, url, jsonobject,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						try {
							successresponse = response.get("success")
									.toString();
						} catch (JSONException e) {
							e.printStackTrace();
						}
						if (successresponse.equals("1")) {
							InputMethodManager imm = (InputMethodManager) comment_edit
									.getContext().getSystemService(
											Context.INPUT_METHOD_SERVICE);
							if (imm.isActive()) {
								imm.hideSoftInputFromWindow(mpopupWindow
										.getContentView().getWindowToken(), 0);
								mpopupWindow.dismiss();
							}
							// 添加数据到item
							if (child != null) {
								LinearLayout text = (LinearLayout) child
										.findViewById(R.id.message_reply);
								String comment = comment_edit.getText()
										.toString().trim();
								String msg = UserInfo.user.getNickname() + ":"
										+ comment;
								TextView reply = new TextView(getActivity());
								reply.setTextSize(18);
								reply.setText(msg);
								text.addView(reply);
								child.invalidate();
							}

						}

					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(getActivity().getApplicationContext(),
								"回复失败", Toast.LENGTH_SHORT).show();
					}
				});
		mRequestQueue.add(jr);
	}

	@Override
	public void onRefresh() {
		mhandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				child = null;
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

	private void onLoad() {
		mylistview.stopRefresh();
		mylistview.stopLoadMore();
	}

	public void ReplyBox() {
		final View view = View.inflate(getActivity(),
				R.layout.message_replybox, null);
		comment_edit = (EditTextSpuer) view
				.findViewById(R.id.message_replybox_edit);
		send = (TextView) view.findViewById(R.id.message_replybox_send);
		TextView dismiss = (TextView) view.findViewById(R.id.message_replytop);
		send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(comment_edit.getText().toString().trim())) {
					Toast.makeText(getActivity(), "请填写回复内容", Toast.LENGTH_SHORT)
							.show();
				} else {
					postMessage();
				}

			}
		});
		dismiss.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) comment_edit
						.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				if (imm.isActive()) {
					imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
				}
				mpopupWindow.dismiss();
			}
		});
		view.startAnimation(AnimationUtils.loadAnimation(getActivity(),
				R.anim.fade_in));
		if (mpopupWindow == null) {
			mpopupWindow = new PopupWindow(getActivity());
			mpopupWindow.setWidth(LayoutParams.MATCH_PARENT);
			mpopupWindow.setHeight(LayoutParams.WRAP_CONTENT);
			mpopupWindow.setBackgroundDrawable(new ColorDrawable());

			mpopupWindow.setFocusable(true);
			mpopupWindow.setTouchable(true);
			mpopupWindow.setOutsideTouchable(true);
			mpopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
			mpopupWindow
					.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		}
		mpopupWindow.setContentView(view);
		comment_edit.requestFocus();
		comment_edit.setText("");
		comment_edit.setHint(texthint);
		comment_edit.setHintTextColor(getResources()
				.getColor(R.color.hintcolor));
		mpopupWindow.showAtLocation(mylistview, Gravity.BOTTOM, 0, 0);
		mpopupWindow.update();
	}

	private void popupInputMethodWindow() {
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm.isActive()) {
					imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
							InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}
		}, 0);
	}

	public static void hiddenpopupWindow() {
		if (mpopupWindow != null && mpopupWindow.isShowing()) {
			InputMethodManager imm = (InputMethodManager) comment_edit
					.getContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm.isActive()) {
				imm.hideSoftInputFromWindow(mpopupWindow.getContentView()
						.getWindowToken(), 0);
			}
			mpopupWindow.dismiss();
			mpopupWindow = null;
		}
	}

	@Override
	public void donelogin() {
		this.onResume();
	}
}
