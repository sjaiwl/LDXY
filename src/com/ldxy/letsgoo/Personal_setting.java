package com.ldxy.letsgoo;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.http.Header;

import cn.sharesdk.framework.ShareSDK;
import cn.smssdk.SMSSDK;

import com.app.function.Configuration;
import com.app.function.UserInfo;
import com.app.tools.CircularLoginImage;
import com.app.tools.SelectPopupWindow;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SdCardPath")
public class Personal_setting extends Activity {
	private ImageView fanhui;
	private TextView setting_name;
	private RelativeLayout setting_geren;
	private RelativeLayout setting_phone;
	private RelativeLayout setting_exit;
	private CircularLoginImage setting_image;

	private static final int PHOTO_REQUEST_CAMERA = 1;// 拍照
	private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
	private static final int PHOTO_REQUEST_CUT = 3;// 结果

	private Bitmap bitmap;
	private static final String PHOTO_FILE_NAME = "temp_photo.jpg";
	private File tempFile;
	private SelectPopupWindow menuWindow;
	private File file;
	private static Integer user_id;
	private Intent intent;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.personal_setting);
		initView();
		initData();
	}

	private void initView() {
		intent=this.getIntent();
		fanhui = (ImageView) this.findViewById(R.id.fanhui_setting);
		setting_image = (CircularLoginImage) this
				.findViewById(R.id.setting_image);
		setting_name = (TextView) this.findViewById(R.id.setting_name);
		setting_geren = (RelativeLayout) this.findViewById(R.id.setting_geren);
		setting_phone = (RelativeLayout) this.findViewById(R.id.setting_phone);
		setting_exit = (RelativeLayout) this.findViewById(R.id.setting_exit);
	}

	private void initData() {
		// 用户存在
		if (!UserInfo.isEmpty()) {
			user_id = UserInfo.user.getUser_id();
			setting_image.setImageUrl(UserInfo.user.getPictureurl(), 2);
			setting_name.setText(UserInfo.user.getNickname());
		} else {
			setting_image.setImageResource(R.drawable.userphoto);
			setting_name.setText("Ming");
		}

		setting_exit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(Personal_setting.this)
						.setTitle("确认")
						.setMessage("确定退出吗？")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,int which) {
										int paramint=ShareSDK.platformNameToId(UserInfo.user.getUsertype());
										ShareSDK.getPlatform(ShareSDK.platformIdToName(paramint)).getDb().removeAccount();
										UserInfo.setEmpty();
										setResult(RESULT_OK, intent);
										finish();
									}
								}).setNegativeButton("取消", null).show();
			}
		});
		fanhui.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		setting_geren.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Personal_setting.this,
						Setting_geren.class);
				Personal_setting.this.startActivity(intent);
			}
		});
		setting_phone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SMSSDK.initSDK(Personal_setting.this, Configuration.appKey,
						Configuration.appSecred);
				Setting_phone phone = new Setting_phone();
				phone.show(Personal_setting.this);
			}
		});
		setting_image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 实例化SelectPicPopupWindow
				menuWindow = new SelectPopupWindow(Personal_setting.this,
						itemsOnClick);
				// 显示窗口
				menuWindow.showAtLocation(
						Personal_setting.this.findViewById(R.id.setting_image),
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
				// 设置layout在PopupWindow中显示的位置
				menuWindow.update();
			}
		});
	}

	private OnClickListener itemsOnClick = new OnClickListener() {
		public void onClick(View v) {
			menuWindow.dismiss();
			switch (v.getId()) {
			case R.id.take_photo:
				camera(v);
				break;
			case R.id.take_picture:
				gallery(v);
				break;
			default:
				break;
			}
		}

	};
	/*
	 * 上传照片
	 */
	@SuppressLint("ShowToast")
	public void upload(File file) {
		RequestParams params = new RequestParams();
		try {
			params.put("user_id", user_id);
			params.put("pictureurl", file);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		String url = Configuration.updateuserpicUrl;
		AsyncHttpClient client = new AsyncHttpClient();
		client.post(url, params, new AsyncHttpResponseHandler() {
			@SuppressLint("ShowToast")
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				try {
					if (statusCode == 200) {
						Toast.makeText(Personal_setting.this, "头像上传成功", 0)
								.show();
						setting_image.setImageBitmap(bitmap);
					} else {
						Toast.makeText(Personal_setting.this, "网络访问异常，请重试", 0)
								.show();

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			@SuppressLint("ShowToast")
			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				Toast.makeText(Personal_setting.this, "网络访问异常,请重试", 0).show();

			}
		});
	}
	/*
	 * 相册
	 */
	public void gallery(View view) {
		//从相册选择照片
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
	}
	/*
	 * 拍照
	 */
	public void camera(View view) {
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		// 拍照选择
		if (hasSdcard()) {
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(new File(Environment
							.getExternalStorageDirectory(), PHOTO_FILE_NAME)));
		}
		startActivityForResult(intent, PHOTO_REQUEST_CAMERA);
	}
	@SuppressLint("ShowToast")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PHOTO_REQUEST_GALLERY) {
			if (data != null) {
				// 获取uri
				Uri uri = data.getData();
				crop(uri);
			}
		} else if (requestCode == PHOTO_REQUEST_CAMERA) {
			if (hasSdcard()) {
				tempFile = new File(Environment.getExternalStorageDirectory(),
						PHOTO_FILE_NAME);
				crop(Uri.fromFile(tempFile));
			} else {
				Toast.makeText(Personal_setting.this, "未找到存储卡，无法存储照片", 0).show();
			}

		} else if (requestCode == PHOTO_REQUEST_CUT) {
			String dir = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/LDXY/Camera/userImage/";
			file = new File(dir);
			if (!file.exists()) {
				// file不存在
				file.mkdirs();
			}
			File picture = new File(file, "user_image.jpg");
			try {
				bitmap = data.getParcelableExtra("data");
				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(picture));
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
				bos.flush();
				bos.close();
				upload(picture);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	/**
	 *裁剪
	 * 
	 * @function:
	 * @author:Jerry
	 * @date:2013-12-30
	 * @param uri
	 */
	private void crop(Uri uri) {
		// 调用系统裁剪
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		// 比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// 大小
		intent.putExtra("outputX", 250);
		intent.putExtra("outputY", 250);
		// 输出
		intent.putExtra("outputFormat", "JPEG");
		intent.putExtra("noFaceDetection", true);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}
	private boolean hasSdcard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

}
