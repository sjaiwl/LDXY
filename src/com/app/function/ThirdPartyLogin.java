package com.app.function;

import java.util.HashMap;

import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.interFace.ThirdPartyLoginCallBack;
import com.google.gson.Gson;
import com.ldxy.letsgoo.R;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;

public class ThirdPartyLogin extends Dialog implements OnClickListener,
		Callback, PlatformActionListener {
	private static final int MSG_USERID_FOUND = 1;
	private static final int MSG_AUTH_CANCEL = 2;
	private static final int MSG_AUTH_ERROR = 3;
	private static final int MSG_AUTH_COMPLETE = 4;
	private Handler handler;
	private TextView weixin;
	private TextView qq;
	private TextView sina;
	private Activity activity;
	private Platform p;
	private RequestQueue mRequestQueue;
	private UserInfo user = null;
	private ThirdPartyLoginCallBack callback;

	public ThirdPartyLogin(Activity activity, ThirdPartyLoginCallBack callback) {
		super(activity);
		this.activity = activity;
		this.callback = callback;
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_page);

		ShareSDK.initSDK(activity);
		handler = new Handler(this);
		mRequestQueue = Volley.newRequestQueue(activity);
		weixin = (TextView) findViewById(R.id.login_page_weixin);
		qq = (TextView) findViewById(R.id.login_page_qq);
		sina = (TextView) findViewById(R.id.login_page_sina);

		weixin.setOnClickListener(this);
		qq.setOnClickListener(this);
		sina.setOnClickListener(this);
	}

	@Override
	public void onCancel(Platform platform, int action) {
		if (action == Platform.ACTION_USER_INFOR) {
			handler.sendEmptyMessage(MSG_AUTH_CANCEL);
		}
	}

	@Override
	public void onComplete(Platform platform, int action,
			HashMap<String, Object> res) {
		if (action == Platform.ACTION_USER_INFOR) {
			Message m = new Message();
			m.what = MSG_AUTH_COMPLETE;
			m.obj = platform.getName();
			handler.sendMessage(m);
		}
	}

	@Override
	public void onError(Platform platform, int action, Throwable t) {
		if (action == Platform.ACTION_USER_INFOR) {
			handler.sendEmptyMessage(MSG_AUTH_ERROR);
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case MSG_AUTH_CANCEL: {
			Toast.makeText(activity.getApplicationContext(), "已取消授权", 0).show();
		}
			break;
		case MSG_AUTH_ERROR: {
			Toast.makeText(activity.getApplicationContext(), "授权失败", 0).show();
		}
			break;
		case MSG_AUTH_COMPLETE: {
			dismiss();
			int paramint = ShareSDK.platformNameToId(String.valueOf(msg.obj));
			p = ShareSDK.getPlatform(ShareSDK.platformIdToName(paramint));
			Authentication(p.getDb().getUserId(), p.getName(), p.getDb()
					.getUserName(), p.getDb().getUserGender());
		}
			break;
		case MSG_USERID_FOUND: {
			dismiss();
			int paramint = ShareSDK.platformNameToId(String.valueOf(msg.obj));
			p = ShareSDK.getPlatform(ShareSDK.platformIdToName(paramint));
			Authentication(p.getDb().getUserId(), p.getName(), p.getDb()
					.getUserName(), p.getDb().getUserGender());
		}
			break;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_page_weixin:
			Platform weixin = ShareSDK.getPlatform(Wechat.NAME);
			authorize(weixin);
			break;
		case R.id.login_page_qq:
			Platform qq = ShareSDK.getPlatform(QZone.NAME);
			authorize(qq);
			break;
		case R.id.login_page_sina:
			Platform sina = ShareSDK.getPlatform(SinaWeibo.NAME);
			authorize(sina);
			break;
		}
	}

	private void authorize(Platform plat) {
		if (plat.isValid()) {
			String userId = plat.getDb().getUserId();
			if (userId != null) {
				Message m = new Message();
				m.what = MSG_USERID_FOUND;
				m.obj = plat.getName();
				handler.sendMessage(m);
				return;
			}
		}
		plat.SSOSetting(true);
		plat.setPlatformActionListener(this);
		plat.showUser(null);
	}

	private void Authentication(String account, String usertype,
			String nickname, String gender) {
		String url = Configuration.loginUrl;
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("account", account);
		params.put("usertype", usertype);
		params.put("nickname", nickname);
		params.put("gender", gender);
		JsonObjectRequest jar = new JsonObjectRequest(Request.Method.POST, url,
				new JSONObject(params), new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Gson gson = new Gson();
						user = gson.fromJson(response.toString(),
								UserInfo.class);
						if (user == null) {
							Toast.makeText(activity.getApplicationContext(),
									"登录失败", 0).show();
						} else {
							UserInfo.setUserInfo(user);
							if (TextUtils.isEmpty(UserInfo.user.getPictureurl())) {
								UserInfo.user.setPictureurl(p.getDb()
										.getUserIcon());
							}
							callback.donelogin();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(activity.getApplicationContext(),
								"登录失败", 0).show();
					}
				});
		mRequestQueue.add(jar);
	}
}
