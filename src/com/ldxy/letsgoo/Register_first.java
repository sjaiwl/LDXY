package com.ldxy.letsgoo;

import static cn.smssdk.framework.utils.R.getBitmapRes;
import static cn.smssdk.framework.utils.R.getIdRes;
import static cn.smssdk.framework.utils.R.getLayoutRes;
import static cn.smssdk.framework.utils.R.getStringRes;
import static cn.smssdk.framework.utils.R.getStyleRes;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import cn.sharesdk.analysis.MobclickAgent;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.framework.FakeActivity;
import cn.smssdk.gui.CommonDialog;
import cn.smssdk.gui.SMSReceiver;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.app.function.Configuration;
import com.app.function.UserInfo;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.ldxy.letsgoo.R;

public class Register_first extends FakeActivity implements TextWatcher {
	private ImageView register_back;
	private TextView phonepost;
	private EditText phone;
	private EditText yanzheng;
	private RelativeLayout next;
	private TextView phonepostnext;

	private Dialog pd;
	private EventHandler handler;
	private BroadcastReceiver smsReceiver;
	private static final int RETRY_INTERVAL = 40;
	private int time = RETRY_INTERVAL;
	private static String phonenumber;
	private String successresponse = null;
	private static Integer user_id;
	private static Boolean smsReceiverState=false;
	
	public void show(Context context) {
		super.show(context, null);
	}

	public void onCreate() {
		activity.setContentView(R.layout.activity_registerfir);
		initView();
		initData();
	}

	private void initView() {
		register_back = (ImageView) this.findViewById(R.id.register_back1);
		phonepost = (TextView) this.findViewById(R.id.register_postphone);
		phone = (EditText) this.findViewById(R.id.register_telephone);
		yanzheng = (EditText) this.findViewById(R.id.register_telecode);
		next = (RelativeLayout) this.findViewById(R.id.register_next1);
		phonepostnext = (TextView) this.findViewById(R.id.register_firnext);
	}

	private void initData() {
		smsReceiverState=false;
		user_id=UserInfo.user.getUser_id();
		phonepostnext.setText("提交验证码");
		register_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (phonepostnext.getText().toString().trim() == "下一步") {
					Intent intent = new Intent(activity, Register_sec.class);
					Register_first.this.startActivity(intent);
				} else {
					SMSSDK.initSDK(activity, Configuration.appKey,
							Configuration.appSecred);
					String verificationCode = yanzheng.getText().toString()
							.trim();
					String phonenum = phone.getText().toString().trim()
							.replaceAll("\\s*", "");
					String countrycode = "+86";
					if (countrycode.startsWith("+")) {
						countrycode = countrycode.substring(1);
					}
					if (!TextUtils.isEmpty(verificationCode)) {
						if (pd != null && pd.isShowing()) {
							pd.dismiss();
						}
						pd = CommonDialog.ProgressDialog(activity);
						if (pd != null) {
							pd.show();
						}
						SMSSDK.submitVerificationCode(countrycode, phonenum,
								verificationCode);
					} else {
						int resId = getStringRes(activity,
								"smssdk_write_identify_code");
						if (resId > 0) {
							Toast.makeText(getContext(), resId,
									Toast.LENGTH_SHORT).show();
						}
					}
				}
			}
		});

		handler = new EventHandler() {
			public void afterEvent(final int event, final int result,
					final Object data) {
				runOnUIThread(new Runnable() {
					public void run() {
						if (pd != null && pd.isShowing()) {
							pd.dismiss();
						}
						if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
							// 提交验证码
							afterSubmit(result, data);
						}
						if (result == SMSSDK.RESULT_COMPLETE) {
							if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
								// 请求验证码后，跳转到验证码填写页面
								yanzheng.requestFocus();
								smsReceiver = new SMSReceiver(
										new SMSSDK.VerifyCodeReadListener() {
											@Override
											public void onReadVerifyCode(
													final String verifyCode) {
												runOnUIThread(new Runnable() {
													@Override
													public void run() {
														yanzheng.setText(verifyCode);
														yanzheng.setSelection(verifyCode
																.length());
													}
												});
											}
										});
								activity.registerReceiver(
										smsReceiver,
										new IntentFilter(
												"android.provider.Telephony.SMS_RECEIVED"));
								smsReceiverState=true;
							}
						} else {
							// 根据服务器返回的网络错误，给toast提示
							try {
								((Throwable) data).printStackTrace();
								Throwable throwable = (Throwable) data;

								JSONObject object = new JSONObject(
										throwable.getMessage());
								String des = object.optString("detail");
								if (!TextUtils.isEmpty(des)) {
									return;
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							// 如果木有找到资源，默认提示
							int resId = getStringRes(activity,
									"smssdk_network_error");
							if (resId > 0) {
								Toast.makeText(activity, resId,
										Toast.LENGTH_SHORT).show();
							}
						}
					}
				});
			}
		};
		SMSSDK.registerEventHandler(handler);

		phonepost.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SMSSDK.initSDK(activity, Configuration.appKey,
						Configuration.appSecred);
				String phonenum = phone.getText().toString().trim()
						.replaceAll("\\s*", "");
				String countrycode = "+86";
				checkPhoneNum(phonenum, countrycode);

			}
		});
	}

	/**
	 * 提交验证码成功后的执行事件
	 * 
	 * @param result
	 * @param data
	 */
	private void afterSubmit(final int result, final Object data) {
		runOnUIThread(new Runnable() {
			public void run() {
				if (pd != null && pd.isShowing()) {
					pd.dismiss();
				}

				if (result == SMSSDK.RESULT_COMPLETE) {
					// 验证成功
					post_phonenumber(phonenumber);
				} else {
					((Throwable) data).printStackTrace();
					// 验证码不正确
					int resId = getStringRes(activity,
							"smssdk_virificaition_code_wrong");
					if (resId > 0) {
						Toast.makeText(activity, resId, Toast.LENGTH_SHORT)
								.show();
					}
				}
			}
		});
	}

	// 检查电话号码
	private void checkPhoneNum(String phone, String code) {
		if (code.startsWith("+")) {
			code = code.substring(1);
		}

		if (TextUtils.isEmpty(phone)) {
			int resId = getStringRes(activity, "smssdk_write_mobile_phone");
			if (resId > 0) {
				Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT).show();
			}
			return;
		}
		int resId = 0;
		if (phone.length() != 11) {
			resId = getStringRes(activity, "smssdk_write_right_mobile_phone");
			if (resId > 0) {
				Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT).show();
			}
			return;
		}
		// 弹出对话框，发送验证码
		phonenumber = phone;
		showDialog(phone, code);
	}

	// 是否请求发送验证码，对话框
	public void showDialog(final String phone, final String code) {
		int resId = getStyleRes(activity, "CommonDialog");
		if (resId > 0) {
			final String phoneNum = "+" + code + " " + splitPhoneNum(phone);
			final Dialog dialog = new Dialog(getContext(), resId);
			resId = getLayoutRes(activity, "smssdk_send_msg_dialog");
			if (resId > 0) {
				dialog.setContentView(resId);
				resId = getIdRes(activity, "tv_phone");
				((TextView) dialog.findViewById(resId)).setText(phoneNum);
				resId = getIdRes(activity, "tv_dialog_hint");
				TextView tv = (TextView) dialog.findViewById(resId);
				resId = getStringRes(activity, "smssdk_make_sure_mobile_detail");
				if (resId > 0) {
					String text = getContext().getString(resId);
					tv.setText(Html.fromHtml(text));
				}
				resId = getIdRes(activity, "btn_dialog_ok");
				if (resId > 0) {
					((Button) dialog.findViewById(resId))
							.setOnClickListener(new OnClickListener() {
								public void onClick(View v) {
									// 跳转到验证码页面
									dialog.dismiss();

									if (pd != null && pd.isShowing()) {
										pd.dismiss();
									}
									pd = CommonDialog.ProgressDialog(activity);
									if (pd != null) {
										pd.show();
									}
									Log.e("verification phone ==>>", phone);
									SMSSDK.getVerificationCode(code,
											phone.trim());
									countDown();
									phonepostnext.setText("提交验证码");

								}
							});
				}
				resId = getIdRes(activity, "btn_dialog_cancel");
				if (resId > 0) {
					((Button) dialog.findViewById(resId))
							.setOnClickListener(new OnClickListener() {
								public void onClick(View v) {
									dialog.dismiss();
								}
							});
				}
				dialog.setCanceledOnTouchOutside(true);
				dialog.show();

			}
		}
	}

	// 倒数计时
	@SuppressLint("ResourceAsColor")
	private void countDown() {
		runOnUIThread(new Runnable() {
			public void run() {
				time--;
				if (time == 0) {
					phonepost.setBackgroundResource(R.drawable.phonepost_before);
					phonepost.setText("发送");
					phonepost.setEnabled(true);
					time = RETRY_INTERVAL;
				} else {
					int resId = getStringRes(activity, "smssdk_receive_msg");
					if (resId > 0) {
						phonepost
								.setBackgroundResource(R.drawable.phonepost_after);
						String unReceive = getContext().getString(resId, time);
						phonepost.setText(Html.fromHtml(unReceive));
					}
					phonepost.setEnabled(false);
					runOnUIThread(this, 1000);
				}
			}
		}, 1000);
	}

	// 分割电话号码
	private String splitPhoneNum(String phone) {
		StringBuilder builder = new StringBuilder(phone);
		builder.reverse();
		for (int i = 4, len = builder.length(); i < len; i += 5) {
			builder.insert(i, ' ');
		}
		builder.reverse();
		return builder.toString();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (s.length() > 0) {
			phonepost.setEnabled(true);
			int resId = getBitmapRes(activity, "smssdk_btn_enable");
			if (resId > 0) {
				phonepost.setBackgroundResource(resId);
			}
		} else {
			phonepost.setEnabled(false);
			int resId = getBitmapRes(activity, "smssdk_btn_disenable");
			if (resId > 0) {
				phonepost.setBackgroundResource(resId);
			}
		}
	}

	@Override
	public void afterTextChanged(Editable s) {

	}

	public void onResume() {
		SMSSDK.registerEventHandler(handler);
		MobclickAgent.onPageStart("Register_first");
	}

	public void onPause() {
		SMSSDK.unregisterEventHandler(handler);
		MobclickAgent.onPageEnd("Register_first");
	}

	public boolean onFinish() {
		SMSSDK.unregisterEventHandler(handler);
		if(smsReceiverState){
		activity.unregisterReceiver(smsReceiver);
		}
		return super.onFinish();
	}

	public void post_phonenumber(String phone) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("user_id", user_id);
		map.put("phonenumber", phone);
		String url = Configuration.updateuserUrl;
		RequestQueue requestQueue = Volley.newRequestQueue(getContext());
		JSONObject jsonObject = new JSONObject(map);
		JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(
				Method.POST, url, jsonObject,
				new Response.Listener<JSONObject>() {
					@SuppressLint("ShowToast")
					@Override
					public void onResponse(JSONObject response) {
						try {
							successresponse = response.get("success")
									.toString();
						} catch (JSONException e) {
							e.printStackTrace();
						}
						if (successresponse.equals("1")) {
							Toast.makeText(activity, "验证成功", 1).show();
							phonepostnext.setText("下一步");

						} else {
							Toast.makeText(activity, "请联网重新验证", 1).show();
						}
					}
				}, new Response.ErrorListener() {
					@SuppressLint("ShowToast")
					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(activity, "请联网重新验证", 1).show();
					}
				});
		requestQueue.add(jsonRequest);
	}

}
