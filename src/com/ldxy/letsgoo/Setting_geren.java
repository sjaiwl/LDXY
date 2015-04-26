package com.ldxy.letsgoo;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.app.function.Configuration;
import com.app.function.UserInfo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

@SuppressLint("ShowToast")
public class Setting_geren extends Activity {
	private ImageView fanhui;
	private EditText name;
	private EditText school;
	private EditText major;
	private ImageView boy;
	private ImageView girl;
	private FrameLayout settingpost;
	private static String sex = null;
	private String successresponse = null;
	private static Integer user_id;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setting_geren);
		initView();
		initData();
	}

	private void initView() {
		fanhui = (ImageView) this.findViewById(R.id.fanhui_geren);
		name = (EditText) this.findViewById(R.id.setting_geren_name);
		school = (EditText) this.findViewById(R.id.setting_school);
		major = (EditText) this.findViewById(R.id.setting_major);

		boy = (ImageView) this.findViewById(R.id.setting_boy);
		girl = (ImageView) this.findViewById(R.id.setting_girl);
		settingpost = (FrameLayout) this.findViewById(R.id.setting_sure);
	}

	private void initData() {
		// 初始化昵称
		user_id = UserInfo.user.getUser_id();
		String nickname = UserInfo.user.getNickname();
		name.setText(nickname);
		name.setSelection(nickname.length());
		fanhui.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		if (UserInfo.user.getGender().equals("m")) {
			boy.setImageResource(R.drawable.r_man_after);
		} else if (UserInfo.user.getGender().equals("f")) {
			girl.setImageResource(R.drawable.r_woman_after);
		}
		boy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boy.setImageResource(R.drawable.r_man_after);
				girl.setImageResource(R.drawable.r_woman_before);
				sex = "m";
			}
		});

		girl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boy.setImageResource(R.drawable.r_man_before);
				girl.setImageResource(R.drawable.r_woman_after);
				sex = "f";
			}
		});
		settingpost.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (DataCheck()) {
					new AlertDialog.Builder(Setting_geren.this)
							.setTitle("确认")
							.setMessage("确定修改吗？")
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											Map<String, Object> map = new HashMap<String, Object>();
											map.put("user_id", user_id);
											map.put("nickname", name.getText()
													.toString().trim());
											map.put("gender", sex);
											map.put("school", school.getText()
													.toString().trim());
											map.put("major", major.getText()
													.toString().trim());

											String url = Configuration.updateuserUrl;
											RequestQueue requestQueue = Volley
													.newRequestQueue(getApplicationContext());
											JSONObject jsonObject = new JSONObject(
													map);
											JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(
													Method.POST,
													url,
													jsonObject,
													new Response.Listener<JSONObject>() {
														@SuppressLint("ShowToast")
														@Override
														public void onResponse(
																JSONObject response) {
															try {
																successresponse = response
																		.get("success")
																		.toString();
															} catch (JSONException e) {
																e.printStackTrace();
															}
															if (successresponse
																	.equals("1")) {
																Toast.makeText(
																		Setting_geren.this,
																		"修改成功",
																		Toast.LENGTH_SHORT)
																		.show();
																finish();
															} else {
																Toast.makeText(
																		Setting_geren.this,
																		"修改失败",
																		Toast.LENGTH_SHORT)
																		.show();
															}
														}
													},
													new Response.ErrorListener() {
														@SuppressLint("ShowToast")
														@Override
														public void onErrorResponse(
																VolleyError error) {
															Toast.makeText(
																	Setting_geren.this,
																	"修改失败", Toast.LENGTH_SHORT)
																	.show();
														}
													});
											requestQueue.add(jsonRequest);
										}
									}).setNegativeButton("取消", null).show();
				}
			}
		});
	}

	@SuppressLint({ "ShowToast", "ResourceAsColor" })
	public Boolean DataCheck() {
		Boolean resoult = true;
		if (TextUtils.isEmpty(name.getText().toString())) {
			Toast.makeText(Setting_geren.this, "请填写昵称", Toast.LENGTH_SHORT)
					.show();
			resoult = false;
		}
		if (sex == null) {
			Toast.makeText(Setting_geren.this, "请选择性别", Toast.LENGTH_SHORT)
					.show();
			resoult = false;
		}
		return resoult;
	}
}
