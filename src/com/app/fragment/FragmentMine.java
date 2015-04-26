package com.app.fragment;

import com.app.function.Configuration;
import com.app.function.UserInfo;
import com.app.interFace.ThirdPartyLoginCallBack;
import com.app.tools.CircularLoginImage;
import com.app.tools.ExitApplication;
import com.ldxy.letsgoo.MainIndex;
import com.ldxy.letsgoo.Personal_canyu;
import com.ldxy.letsgoo.Personal_fabu;
import com.ldxy.letsgoo.Personal_setting;
import com.ldxy.letsgoo.Personal_shoucang;
import com.ldxy.letsgoo.R;
import com.ldxy.letsgoo.Register_first;
import com.ldxy.letsgoo.Register_thir;
import com.ldxy.letsgoo.ViewPicture;

import cn.smssdk.SMSSDK;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FragmentMine extends Fragment implements ThirdPartyLoginCallBack {
	private MainIndex activity;
	private RelativeLayout my_shoucang;
	private RelativeLayout my_canyu;
	private RelativeLayout my_fabu;
	private RelativeLayout my_shezhi;
	private CircularLoginImage image;
	private TextView myname;
	private ImageView my_renzheng;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_mine, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		ExitApplication.getInstance().addActivity(getActivity());
		super.onActivityCreated(savedInstanceState);
		initView();
		initData();
	}

	private void initView() {
		activity = (MainIndex) this.getActivity();
		my_shoucang = (RelativeLayout) this.getView().findViewById(
				R.id.my_shoucang);
		my_canyu = (RelativeLayout) this.getView().findViewById(R.id.my_canyu);
		my_fabu = (RelativeLayout) this.getView().findViewById(R.id.my_fabu);
		my_shezhi = (RelativeLayout) this.getView()
				.findViewById(R.id.my_shezhi);
		image = (CircularLoginImage) this.getView().findViewById(R.id.my_image);
		myname = (TextView) this.getView().findViewById(R.id.my_name);
		my_renzheng = (ImageView) this.getView().findViewById(
				R.id.my_renzhengsure);
	}

	private void initData() {
		my_shoucang.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(activity, Personal_shoucang.class);
				activity.startActivity(intent);
			}
		});
		my_canyu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(activity, Personal_canyu.class);
				activity.startActivity(intent);
			}
		});
		my_fabu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(activity, Personal_fabu.class);
				activity.startActivity(intent);
			}
		});
		my_shezhi.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!UserInfo.isEmpty()) {
					Intent intent = new Intent(activity, Personal_setting.class);
					activity.startActivity(intent);
				} else {
					Configuration.showLoginWindow(getActivity(),
							FragmentMine.this);
				}
			}
		});
		image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!UserInfo.isEmpty()) {
					Intent intent = new Intent();
					intent.putExtra("choosephoto",
							UserInfo.user.getPictureurl());
					intent.putExtra("phototype", 2);
					intent.setClass(activity, ViewPicture.class);
					startActivity(intent);
				} else {
					Configuration.showLoginWindow(getActivity(),
							FragmentMine.this);
				}
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!UserInfo.isEmpty()) {
			image.setImageUrl(UserInfo.user.getPictureurl(), 1);
			myname.setText(UserInfo.user.getNickname());
		} else {
			image.setImageResource(R.drawable.userphoto);
			myname.setText("昵称");
		}
		if (!UserInfo.isEmpty()) {
			my_renzheng.setVisibility(View.VISIBLE);
			my_renzheng.setClickable(true);
//			if (UserInfo.user.getChecked().equals("0")) {
//				my_renzheng.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						SMSSDK.initSDK(getActivity(), Configuration.appKey,
//								Configuration.appSecred);
//						Register_first register_first = new Register_first();
//						register_first.show(getActivity());
//					}
//				});
//			} else if (UserInfo.user.getChecked().equals("1")) {
//				my_renzheng.setVisibility(View.VISIBLE);
//				my_renzheng.setClickable(true);
//				my_renzheng.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						SMSSDK.initSDK(getActivity(), Configuration.appKey,
//								Configuration.appSecred);
//						Intent intent = new Intent(activity,
//								Register_thir.class);
//						activity.startActivity(intent);
//					}
//				});
//			}
		}
	}

	@Override
	public void donelogin() {
		this.onResume();
	}

}
