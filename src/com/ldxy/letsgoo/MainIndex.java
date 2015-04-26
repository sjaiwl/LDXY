package com.ldxy.letsgoo;

import java.util.List;

import org.json.JSONArray;

import com.alibaba.fastjson.JSON;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.app.adapter.IndexListItemAdapter;
import com.app.fragment.FragmentIndex;
import com.app.function.ActivityUnit;
import com.app.function.Configuration;
import com.app.function.UserInfo;
import com.app.service.ComingActivityService;
import com.app.tools.ExitApplication;
import com.app.tools.PollingUtils;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainIndex extends FragmentActivity {
	private Fragment[] mFragments;
	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction;
	private LinearLayout bottomIndex;
	private LinearLayout bottomMessage;
	private LinearLayout bottomMine;
	private TextView index;
	private TextView message;
	private TextView mine;
	private long exitTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		Context context = getApplicationContext();
		XGPushManager.registerPush(context, new XGIOperateCallback() {
			@Override
			public void onFail(Object data, int errCode, String msg) {
				// Log.i("test", "注册失败，错误码：" + errCode + ",错误信息：" + msg);
			}
			@Override
			public void onSuccess(Object data, int flag) {
				// Log.i("test", "注册成功,设备token为："+data);
			}
		});

		mFragments = new Fragment[3];
		fragmentManager = getSupportFragmentManager();
		mFragments[0] = fragmentManager.findFragmentById(R.id.fragment_index);
		mFragments[1] = fragmentManager.findFragmentById(R.id.fragment_message);
		mFragments[2] = fragmentManager.findFragmentById(R.id.fragment_mine);
		fragmentTransaction = fragmentManager.beginTransaction()
				.hide(mFragments[0]).hide(mFragments[1]).hide(mFragments[2]);
		fragmentTransaction.show(mFragments[0]).commit();
		setFragmentIndicator();
		// 用户登录后，开启轮询服务
		// if(!UserInfo.isEmpty()){
		// RequestQueue mRequestQueue=Volley.newRequestQueue(context);
		// String
		// url=Configuration.MyActivityService+"?user_id="+UserInfo.user.getUser_id();
		// JsonArrayRequest jar=new JsonArrayRequest(url,
		// new Response.Listener<JSONArray>() {
		// @Override
		// public void onResponse(JSONArray response) {
		// // List<ActivityUnit>
		// list=JSON.parseArray(response.toString(),ActivityUnit.class);
		// }
		// },
		// new Response.ErrorListener(){
		// @Override
		// public void onErrorResponse(VolleyError error) {
		// Log.e("test", error.toString());
		// }
		// });
		// mRequestQueue.add(jar);
		// PollingUtils.startPollingService(this, 10,
		// ComingActivityService.class, ComingActivityService.ACTION);
		// }else{
		// PollingUtils.stopPollingService(this, ComingActivityService.class,
		// ComingActivityService.ACTION);
		// }
	}

	private void setFragmentIndicator() {
		bottomIndex = (LinearLayout) this.findViewById(R.id.bottom_index);
		bottomMessage = (LinearLayout) this.findViewById(R.id.bottom_message);
		bottomMine = (LinearLayout) this.findViewById(R.id.bottom_mine);
		index = (TextView) this.findViewById(R.id.bottom_index_pic);
		message = (TextView) this.findViewById(R.id.bottom_message_pic);
		mine = (TextView) this.findViewById(R.id.bottom_mine_pic);
		bottomIndex.setOnClickListener(mOnClickListener);
		bottomMessage.setOnClickListener(mOnClickListener);
		bottomMine.setOnClickListener(mOnClickListener);
	}

	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			fragmentTransaction = fragmentManager.beginTransaction()
					.hide(mFragments[0]).hide(mFragments[1])
					.hide(mFragments[2]);
			switch (v.getId()) {
			case R.id.bottom_index:
				index.setBackgroundResource(R.drawable.index_click);
				bottomIndex.setBackgroundResource(R.color.BtBackground1);
				message.setBackgroundResource(R.drawable.message);
				bottomMessage.setBackgroundResource(R.color.BtBackground);
				mine.setBackgroundResource(R.drawable.mine);
				bottomMine.setBackgroundResource(R.color.BtBackground);
				fragmentTransaction.show(mFragments[0]).commit();
				break;
			case R.id.bottom_message:
				index.setBackgroundResource(R.drawable.index);
				bottomIndex.setBackgroundResource(R.color.BtBackground);
				message.setBackgroundResource(R.drawable.message_click);
				bottomMessage.setBackgroundResource(R.color.BtBackground1);
				mine.setBackgroundResource(R.drawable.mine);
				bottomMine.setBackgroundResource(R.color.BtBackground);
				fragmentTransaction.show(mFragments[1]).commit();
				break;
			case R.id.bottom_mine:
				index.setBackgroundResource(R.drawable.index);
				bottomIndex.setBackgroundResource(R.color.BtBackground);
				message.setBackgroundResource(R.drawable.message);
				bottomMessage.setBackgroundResource(R.color.BtBackground);
				mine.setBackgroundResource(R.drawable.mine_click);
				bottomMine.setBackgroundResource(R.color.BtBackground1);
				fragmentTransaction.show(mFragments[2]).commit();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onBackPressed() {
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			Toast.makeText(MainIndex.this, "再按一次退出应用", Toast.LENGTH_SHORT)
					.show();
			exitTime = System.currentTimeMillis();
			return;
		}
		ExitApplication.getInstance().exit();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case RESULT_OK:
			mFragments[0].onResume();
			mFragments[1].onResume();
			mFragments[2].onResume();
			break;
		case Configuration.BACK_IDENTIFY:
			index.setBackgroundResource(R.drawable.index);
			bottomIndex.setBackgroundResource(R.color.BtBackground);
			message.setBackgroundResource(R.drawable.message);
			bottomMessage.setBackgroundResource(R.color.BtBackground);
			mine.setBackgroundResource(R.drawable.mine_click);
			bottomMine.setBackgroundResource(R.color.BtBackground1);
			fragmentTransaction.show(mFragments[2]).commit();
			break;
		}
	}
}
