package com.ldxy.letsgoo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.alibaba.fastjson.JSON;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.app.adapter.CommentListAdapter;
import com.app.function.ActivityUnit;
import com.app.function.Configuration;
import com.app.function.Dynamic;
import com.app.function.UserInfo;
import com.app.smart.SmartImageView;
import com.app.interFace.CommentListItemClickHelp;
import com.app.interFace.ThirdPartyLoginCallBack;
import com.app.tools.CircularDetailImage;
import com.app.tools.MyScrollView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class Detailpage extends Activity implements CommentListItemClickHelp,
		ThirdPartyLoginCallBack {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_detailpage);
		initView();
		initData();
	}

	private void initData() {
		mRequestQueue = Volley.newRequestQueue(this);
		// 获取活动详情类
		Integer activityId = intent.getIntExtra("activityid", 0);
		int currentuser_id = 0;
		if (!UserInfo.isEmpty()) {
			currentuser_id = UserInfo.user.getUser_id();
		}
		String url = Configuration.getactivityUrl + "?currentuser_id="
				+ currentuser_id + "&&activity_id=" + activityId;
		JsonObjectRequest jar = new JsonObjectRequest(Request.Method.GET, url,
				null, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Gson gson = new Gson();
						unit = gson.fromJson(response.toString(),
								ActivityUnit.class);
						setUIData();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(Detailpage.this.getApplicationContext(),
								"活动信息获取失败", 0).show();
						finish();
					}
				});
		mRequestQueue.add(jar);
	};

	private void setUIData() {
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) Detailpage.this
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(commentedit.getWindowToken(), 0);
				setResult(RESULT_OK);
				finish();
			}
		});
		// 滑动效果
		sv.smoothScrollTo(0, 0);
		sv.setOnTouchListener(new OnTouchListener() {
			private int lastY = 0;
			private int touchEventId = -9983761;
			Handler handler = new Handler() {
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					InputMethodManager imm = (InputMethodManager) commentedit
							.getContext().getSystemService(
									Context.INPUT_METHOD_SERVICE);
					if (imm.isActive()) {
						imm.hideSoftInputFromWindow(
								commentedit.getApplicationWindowToken(), 0);
						dbottom.setVisibility(View.VISIBLE);
						detailedit.setVisibility(View.GONE);
					}
					if (msg.what == touchEventId) {
						if (lastY != sv.getScrollY()) {
							handler.sendMessageDelayed(
									handler.obtainMessage(touchEventId, sv), 5);
							lastY = sv.getScrollY();
							pcc.getLocationOnScreen(location);
							pcc1.getLocationOnScreen(location1);
							if (location[1] <= location1[1]) {
								pcc1.setVisibility(View.VISIBLE);
								pcc.setVisibility(View.INVISIBLE);
							} else {
								pcc.setVisibility(View.VISIBLE);
								pcc1.setVisibility(View.GONE);
							}
						}
					}
				}
			};

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_MOVE
						|| event.getAction() == MotionEvent.ACTION_UP) {
					handler.sendMessageDelayed(
							handler.obtainMessage(touchEventId, v), 5);
				}
				return false;
			}
		});
		// 初始化活动信息
		image.setImageUrl(unit.getImg(), 1);
		image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Uri uri = Uri.parse(unit.getImg());
				intent.putExtra("phototype", 2);
				intent.putExtra("choosephoto", uri);
				intent.setClass(Detailpage.this, ViewPicture.class);
				startActivity(intent);
			}
		});
		type.setText(unit.getType());
		title.setText(unit.getTitle());
		userimg.setImageUrl(unit.getUserImage(), 2);
		name.setText(unit.getUserName());
		when.setText(unit.getWhen());
		participation.setText(unit.getJoinsum());
		commentsum.setText(unit.getCommentsum());
		starttime.setText(unit.getStarttime());
		aclocation.setText(unit.getLocation());
		content.setText(unit.getContent());
		if (unit.getIsjoined() == 1) {
			jointext.setText(R.string.detail_participate_done);
			jointext1.setText(R.string.detail_participate_done);
		} else if (unit.getIsjoined() == 0) {
			jointext.setText(R.string.detail_participate);
			jointext1.setText(R.string.detail_participate);
		}
		// 参与活动和联系发起者
		join.setOnClickListener(joinOnClick);
		join1.setOnClickListener(joinOnClick);
		phone.setOnClickListener(phoneOnClick);
		phone1.setOnClickListener(phoneOnClick);
		// 获取参与、评论、回复的信息
		dynamiclist = new ArrayList<Dynamic>();
		getDynamicList();
		// 最近参与的人
		joiners.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				intent.setClass(Detailpage.this, JoinActivity_detail.class);
				intent.putExtra("activity_id", unit.getActivity_id());
				intent.putExtra("joiners_num", unit.getjoinsum());
				startActivity(intent);
			}
		});
		// 评论楼
		myadapter = new CommentListAdapter(this, new ArrayList<Dynamic>(), this);
		comments.setAdapter(myadapter);
		// 收藏功能
		if (unit.getIscollected() == 1) {
			this.findViewById(R.id.d_collect_picbefore)
					.setVisibility(View.GONE);
			this.findViewById(R.id.d_collect_picafter).setVisibility(
					View.VISIBLE);
			collect.setBackgroundResource(R.color.BtBackground1);
		}
		collect.setOnClickListener(collectClickListener);
		// 评论功能
		commentsend.setOnClickListener(commentSendOnClick);
		comment.setOnClickListener(new OnClickListener() {// 评论按钮激活对活动的评论
			@Override
			public void onClick(View v) {
				if (!UserInfo.isEmpty()) {
					operatedynamic = null;
					dbottom.setVisibility(View.GONE);
					detailedit.setVisibility(View.VISIBLE);
					commentedit.requestFocus();
					commentedit.setHint(R.string.comment_hint);
					InputMethodManager imm = (InputMethodManager) commentedit
							.getContext().getSystemService(
									Context.INPUT_METHOD_SERVICE);
					imm.showSoftInput(commentedit, 0);
				} else {
					Configuration.showLoginWindow(Detailpage.this,
							Detailpage.this);
				}
			}
		});
		// 分享功能
		share.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ShareSDK.initSDK(Detailpage.this);
				OnekeyShare oks = new OnekeyShare();
				oks.disableSSOWhenAuthorize();

				// 分享时Notification的图标和文字
				oks.setNotification(R.drawable.ic_launcher,
						getString(R.string.app_name));
				// title标题，微信、人人网和QQ空间使用
				oks.setTitle(unit.getTitle());
				String text = "分享活动啦~~["+unit.getAclabel()+"]"+unit.getTitle()+" "+
				     "开始时间："+unit.getStarttime()+" "+
					 "地点："+unit.getAcplace()+" "+
				     "具体信息:"+unit.getAccontent().substring(0, 20)+"...点击更多下载app";
				// text是分享文本，所有平台都需要这个字段
				oks.setText(text);
				// 启动分享GUI
				oks.show(Detailpage.this);
			}
		});
	}

	private OnClickListener joinOnClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (UserInfo.isEmpty()) {
				Configuration.showLoginWindow(Detailpage.this, Detailpage.this);
			} else {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("executor_id", UserInfo.user.getUser_id());
				params.put("operate_type", 1);
				params.put("activity_id", unit.getActivity_id());
				JSONObject jsonobject = new JSONObject(params);
				JsonRequest<JSONObject> jr = null;
				if (jointext.getText().equals("参与")) {
					jr = new JsonObjectRequest(Method.POST,
							Configuration.dynamicsUrl, jsonobject,
							new Response.Listener<JSONObject>() {
								@Override
								public void onResponse(JSONObject response) {
									Type type = new TypeToken<Map<String, String>>() {
									}.getType();
									Gson gson = new Gson();
									Map<String, String> map = gson.fromJson(
											response.toString(), type);
									if (map.get("success").equals("1")) {
										jointext.setText(R.string.detail_participate_done);
										jointext1
												.setText(R.string.detail_participate_done);
										participation.setText(unit
												.getFreshJoinsum(1));
										unit.setJoinsums(unit.getjoinsum() + 1);
										SmartImageView simg = new SmartImageView(
												Detailpage.this);
										simg.setImageUrl(
												UserInfo.user.getPictureurl(),
												2);
										simg.setLayoutParams(new LayoutParams(
												60, 60));
										simg.setPadding(5, 0, 5, 0);
										if (Detailpage.this.findViewById(
												R.id.joiners_none)
												.getVisibility() == View.VISIBLE) {
											Detailpage.this
													.findViewById(
															R.id.detail_joiners)
													.setVisibility(View.VISIBLE);
											Detailpage.this.findViewById(
													R.id.joiners_none)
													.setVisibility(View.GONE);
										}
										joinersImg.addView(simg);
									} else {
										Toast.makeText(
												Detailpage.this
														.getApplicationContext(),
												"参与失败", Toast.LENGTH_SHORT)
												.show();
									}
								}
							}, new Response.ErrorListener() {
								@Override
								public void onErrorResponse(VolleyError error) {
									Toast.makeText(
											Detailpage.this
													.getApplicationContext(),
											"参与失败", Toast.LENGTH_SHORT).show();
								}
							});
				} else {
					jr = new JsonObjectRequest(Method.POST,
							Configuration.canceljoinUrl, jsonobject,
							new Response.Listener<JSONObject>() {
								@Override
								public void onResponse(JSONObject response) {
									Type type = new TypeToken<Map<String, String>>() {
									}.getType();
									Gson gson = new Gson();
									Map<String, String> map = gson.fromJson(
											response.toString(), type);
									if (map.get("success").equals("1")) {
										jointext.setText(R.string.detail_participate);
										jointext1
												.setText(R.string.detail_participate);
										participation.setText(unit
												.getFreshJoinsum(-1));
										unit.setJoinsums(unit.getjoinsum() - 1);
										for (int i = 0; i < joinerlist.size(); i++) {
											if (joinerlist
													.get(i)
													.getExecutor_id()
													.equals(UserInfo.user
															.getUser_id())) {
												joinerlist.remove(i);
												break;
											}
										}
										joinersImg.removeAllViews();
										if (joinerlist.isEmpty()) {
											Detailpage.this.findViewById(
													R.id.detail_joiners)
													.setVisibility(View.GONE);
											Detailpage.this
													.findViewById(
															R.id.joiners_none)
													.setVisibility(View.VISIBLE);
										} else {
											if (joinerlist.size() < joinerMaxLength) {
												joinerMaxLength = joinerlist
														.size();
											}
											for (int i = 0; i < joinerMaxLength; i++) {
												SmartImageView simg = new SmartImageView(
														Detailpage.this);
												simg.setImageUrl(
														joinerlist
																.get(i)
																.getExecutorpictureurl(),
														2);
												simg.setLayoutParams(new LayoutParams(
														60, 60));
												simg.setPadding(5, 0, 5, 0);
												joinersImg.addView(simg);
											}
										}
									} else {
										Toast.makeText(
												Detailpage.this
														.getApplicationContext(),
												"取消失败", Toast.LENGTH_SHORT)
												.show();
									}
								}
							}, new Response.ErrorListener() {
								@Override
								public void onErrorResponse(VolleyError error) {
									Toast.makeText(
											Detailpage.this
													.getApplicationContext(),
											"取消失败", Toast.LENGTH_SHORT).show();
								}
							});
				}
				mRequestQueue.add(jr);
			}
		}
	};
	private OnClickListener phoneOnClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (!UserInfo.isEmpty()) {
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
						+ (String) unit.getPhone().trim()));
				v.getContext().startActivity(intent);
			} else {
				Configuration.showLoginWindow(Detailpage.this, Detailpage.this);
			}
		}
	};
	private OnClickListener commentSendOnClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Map<String, Object> params = new HashMap<String, Object>();
			if (operatedynamic != null) {
				params.put("suffer_id", operatedynamic.getExecutor_id());
				params.remove("operate_type");
				params.put("operate_type", 4);
			} else {
				params.remove("operate_type");
				params.put("operate_type", 3);
			}
			params.put("executor_id", UserInfo.user.getUser_id());
			params.put("comment", commentedit.getText().toString());
			params.put("activity_id", unit.getActivity_id());
			params.put("updated_at", Configuration.getNowUTCtime());
			JSONObject jsonobject = new JSONObject(params);
			JsonRequest<JSONObject> jr = null;
			jr = new JsonObjectRequest(Method.POST, Configuration.dynamicsUrl,
					jsonobject, new Response.Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
							Type type = new TypeToken<Map<String, String>>() {
							}.getType();
							Gson gson = new Gson();
							Map<String, String> map = gson.fromJson(
									response.toString(), type);
							if (map.get("success").equals("1")) {
								detailedit.setVisibility(View.INVISIBLE);
								Detailpage.this.recreate();
								sv.fullScroll(ScrollView.FOCUS_DOWN);
								commentedit.setText(null);
							} else {
								Toast.makeText(
										Detailpage.this.getApplicationContext(),
										"评论失败", Toast.LENGTH_SHORT).show();
							}
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							Toast.makeText(
									Detailpage.this.getApplicationContext(),
									"评论失败", Toast.LENGTH_SHORT).show();
						}
					});
			mRequestQueue.add(jr);
		}
	};

	// 点击评论列表触发事件
	@Override
	public void onClick(View view, View widget, int position, int which) {
		if (!UserInfo.isEmpty()) {
			operatedynamic = dynamiclist.get(position);
			String texthint = "回复给:" + operatedynamic.getExecutor();
			dbottom.setVisibility(View.GONE);
			detailedit.setVisibility(View.VISIBLE);
			commentedit.requestFocus();
			commentedit.setHint(texthint);
			InputMethodManager imm = (InputMethodManager) commentedit
					.getContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(commentedit, 0);
		} else {
			Configuration.showLoginWindow(Detailpage.this, Detailpage.this);
		}
	}

	public void getDynamicList() {
		String url = Configuration.getdynamicsUrl + "?activity_id="
				+ Integer.toString(unit.getActivity_id());
		JsonArrayRequest jr = new JsonArrayRequest(url,
				new Response.Listener<JSONArray>() {
					@Override
					public void onResponse(JSONArray response) {
						dynamiclist = JSON.parseArray(response.toString(),
								Dynamic.class);
						joinerlist = new ArrayList<Dynamic>();
						commentlist = new ArrayList<Dynamic>();
						for (int i = 0; i < dynamiclist.size(); i++) {
							switch ((Integer) dynamiclist.get(i)
									.getOperate_type()) {
							case 1:
								joinerlist.add(dynamiclist.get(i));
								break;
							default:
								commentlist.add(dynamiclist.get(i));
								break;
							}
						}
						// 初始化参与人头像列表
						if (joinerlist.isEmpty()) {
							Detailpage.this.findViewById(R.id.detail_joiners)
									.setVisibility(View.GONE);
							Detailpage.this.findViewById(R.id.joiners_none)
									.setVisibility(View.VISIBLE);
						} else {
							if (joinerlist.size() < joinerMaxLength) {
								joinerMaxLength = joinerlist.size();
							}
							for (int i = 0; i < joinerMaxLength; i++) {
								SmartImageView simg = new SmartImageView(
										Detailpage.this);
								simg.setImageUrl(joinerlist.get(i)
										.getExecutorpictureurl(), 2);
								simg.setLayoutParams(new LayoutParams(60, 60));
								simg.setPadding(5, 0, 5, 0);
								joinersImg.addView(simg);
							}
						}
						myadapter = new CommentListAdapter(Detailpage.this,
								commentlist, Detailpage.this);
						comments.setAdapter(myadapter);
						fixListViewHeight(comments);
						if (commentlist.isEmpty()) {
							Detailpage.this.findViewById(R.id.comment_none)
									.setVisibility(View.VISIBLE);
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(Detailpage.this.getApplicationContext(),
								"获取信息失败", Toast.LENGTH_SHORT).show();
					}
				});
		mRequestQueue.add(jr);
	}

	private void fixListViewHeight(ListView listview) {
		ListAdapter listadapter = listview.getAdapter();
		int totalHeight = 0;
		if (listadapter == null) {
			return;
		}
		for (int index = 0, len = listadapter.getCount(); index < len; index++) {
			View listviewitem = listadapter.getView(index, null, listview);
			listviewitem.measure(0, 0);
			totalHeight += listviewitem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listview.getLayoutParams();
		params.height = totalHeight
				+ (listview.getDividerHeight() * (listadapter.getCount() - 1));
		listview.setLayoutParams(params);
	}

	private OnClickListener collectClickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			if (UserInfo.isEmpty()) {
				Configuration.showLoginWindow(Detailpage.this, Detailpage.this);
			} else {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("user_id", UserInfo.user.getUser_id());
				params.put("activity_id", unit.getActivity_id());
				JSONObject jsonobject = new JSONObject(params);
				JsonRequest<JSONObject> jr = null;
				if (Detailpage.this.findViewById(R.id.d_collect_picbefore)
						.getVisibility() == View.VISIBLE) {
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
										collect.setBackgroundResource(R.color.BtBackground1);
										Detailpage.this.findViewById(
												R.id.d_collect_picbefore)
												.setVisibility(View.GONE);
										Detailpage.this.findViewById(
												R.id.d_collect_picafter)
												.setVisibility(View.VISIBLE);
									} else {
										Toast.makeText(
												Detailpage.this
														.getApplicationContext(),
												"收藏失败", Toast.LENGTH_SHORT)
												.show();
									}
								}
							}, new Response.ErrorListener() {
								@Override
								public void onErrorResponse(VolleyError error) {
									Toast.makeText(
											Detailpage.this
													.getApplicationContext(),
											"收藏失败", Toast.LENGTH_SHORT).show();
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
										collect.setBackgroundResource(R.color.BtBackground);
										Detailpage.this.findViewById(
												R.id.d_collect_picbefore)
												.setVisibility(View.VISIBLE);
										Detailpage.this.findViewById(
												R.id.d_collect_picafter)
												.setVisibility(View.GONE);
									} else {
										Toast.makeText(
												Detailpage.this
														.getApplicationContext(),
												"操作失败", Toast.LENGTH_SHORT)
												.show();
									}
								}
							}, new Response.ErrorListener() {
								@Override
								public void onErrorResponse(VolleyError error) {
									Toast.makeText(
											Detailpage.this
													.getApplicationContext(),
											"操作失败", Toast.LENGTH_SHORT).show();
								}
							});
				}
				mRequestQueue.add(jr);
			}
		}
	};

	@Override
	public void donelogin() {
		this.recreate();
	}

	private Intent intent;
	private ActivityUnit unit;
	private TextView back;
	private MyScrollView sv;
	private SmartImageView userimg;
	private CircularDetailImage image;
	private TextView title, type, name, when, participation, commentsum,
			starttime, aclocation, content;
	private TextView jointext, jointext1, commentsend;
	private LinearLayout pcc, join, phone, pcc1, join1, phone1, dbottom;
	private LinearLayout detailedit, collect, comment, share;
	private EditText commentedit;
	private RequestQueue mRequestQueue;
	private List<Dynamic> dynamiclist, joinerlist, commentlist;
	private Dynamic operatedynamic = null;
	private LinearLayout joiners, joinersImg;
	private ListView comments;
	private CommentListAdapter myadapter;
	private int[] location = new int[2];
	private int[] location1 = new int[2];
	private Integer joinerMaxLength = 8;

	private void initView() {
		intent = this.getIntent();
		back = (TextView) this.findViewById(R.id.d_back);
		image = (CircularDetailImage) this.findViewById(R.id.detail_acimage);
		sv = (MyScrollView) this.findViewById(R.id.detail_scroll);
		type = (TextView) this.findViewById(R.id.detail_type);
		title = (TextView) this.findViewById(R.id.detail_title);
		userimg = (SmartImageView) this.findViewById(R.id.detail_image);
		name = (TextView) this.findViewById(R.id.detail_name);
		when = (TextView) this.findViewById(R.id.detail_when);
		participation = (TextView) this.findViewById(R.id.detail_participation);
		commentsum = (TextView) this.findViewById(R.id.detail_comment);
		starttime = (TextView) this.findViewById(R.id.detail_starttime);
		aclocation = (TextView) this.findViewById(R.id.detail_location);
		pcc = (LinearLayout) this.findViewById(R.id.d_pcc);
		join = (LinearLayout) this.findViewById(R.id.d_participate);
		phone = (LinearLayout) this.findViewById(R.id.d_communicate);
		pcc1 = (LinearLayout) this.findViewById(R.id.d_pcc1);
		join1 = (LinearLayout) this.findViewById(R.id.d_participate1);
		jointext = (TextView) this.findViewById(R.id.detail_join_text);
		jointext1 = (TextView) this.findViewById(R.id.detail_join_text1);
		phone1 = (LinearLayout) this.findViewById(R.id.d_communicate1);
		dbottom = (LinearLayout) this.findViewById(R.id.detail_bottom);
		content = (TextView) this.findViewById(R.id.detail_content);
		joiners = (LinearLayout) this.findViewById(R.id.detail_joiners);
		joinersImg = (LinearLayout) this.findViewById(R.id.detail_joiners_imgs);
		comments = (ListView) this.findViewById(R.id.detail_comment_listview);
		collect = (LinearLayout) this.findViewById(R.id.d_collect);
		comment = (LinearLayout) this.findViewById(R.id.d_comment);
		share = (LinearLayout) this.findViewById(R.id.d_share);
		commentedit = (EditText) this.findViewById(R.id.detail_comment_edit);
		detailedit = (LinearLayout) this.findViewById(R.id.detail_edit);
		commentsend = (TextView) this.findViewById(R.id.detail_comment_send);
	}
}
