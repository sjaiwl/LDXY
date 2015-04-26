package com.ldxy.letsgoo;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.Header;

import com.app.function.Configuration;
import com.app.function.UserInfo;
import com.app.tools.ExitApplication;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

@SuppressLint("ShowToast")
public class Register_sec extends Activity {
	private ImageView register_back;
	private ImageView takephoto;
	/* 头像名称 */
	private static final String PHOTO_FILE_NAME = "temp_photo.jpg";
	private static final int PHOTO_REQUEST_CAMERA = 1;// 拍照
	private File tempFile;
	private static Integer user_id;

	protected void onCreate(Bundle savedInstanceState) {
		ExitApplication.getInstance().addActivity(this);
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_registersec);
		initView();
		initData();
	}

	private void initView() {
		register_back = (ImageView) this.findViewById(R.id.register_back2);
		takephoto = (ImageView) this.findViewById(R.id.register_takephoto);
	}

	private void initData() {
		user_id=UserInfo.user.getUser_id();
		register_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Register_sec.this.finish();
			}
		});

		takephoto.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				camera(v);
			}
		});
	}

	/*
	 * 上传图片
	 */
	@SuppressLint("ShowToast")
	public void upload(File file) {
		RequestParams params = new RequestParams();
		try {
			params.put("user_id",user_id);
			params.put("checkpicture", file);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		String url = Configuration.uploadimageUrl;

		AsyncHttpClient client = new AsyncHttpClient();
		client.post(url, params, new AsyncHttpResponseHandler() {
			@SuppressLint("ShowToast")
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				try {
					if (statusCode == 200) {
						Toast.makeText(Register_sec.this, "照片上传成功!", 1).show();
						Intent intent = new Intent(Register_sec.this,Register_thir.class);
						Register_sec.this.startActivity(intent);
					} else {
						Toast.makeText(Register_sec.this, "网络访问异常,请重试", 1).show();
					}
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(Register_sec.this,
							"照片上传失败，请重试！" + statusCode, 1).show();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				Toast.makeText(Register_sec.this, "网络访问异常,请重试", 1).show();

			}
		});
	}
	/*
	 * 从相机获取
	 */
	public void camera(View view) {
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		// 判断存储卡是否可以用，可用进行存储
		if (hasSdcard()) {
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(new File(Environment
							.getExternalStorageDirectory(), PHOTO_FILE_NAME)));
		}
		startActivityForResult(intent, PHOTO_REQUEST_CAMERA);
	}

	private boolean hasSdcard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	@SuppressLint("ShowToast")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == PHOTO_REQUEST_CAMERA) {
			if (hasSdcard()) {
				tempFile = new File(Environment.getExternalStorageDirectory(),
						PHOTO_FILE_NAME);
				upload(scaleImage(tempFile));
			} else {
				Toast.makeText(Register_sec.this, "未找到存储卡，无法存储照片！", 1).show();
			}

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private File scaleImage(File file) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inSampleSize=1;
		BitmapFactory.decodeFile(file.getPath(), options);
		int width = options.outWidth;
		int height = options.outHeight;
		int resHeight = 800;
		int resWidth = 480;
		if (width > resWidth || height > resHeight) {
			final int scaleWidth = Math.round(((float) resWidth)
					/ (float) width);
			final int scaleHeight = Math.round(((float) resHeight)
					/ (float) height);
			options.inSampleSize = scaleHeight < scaleWidth ? scaleHeight : scaleWidth;
		}
		options.inJustDecodeBounds = false;
		Bitmap bm =BitmapFactory.decodeFile(file.getPath(), options);
		String dir = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/LDXY/Camera/userIdentify/";
		file = new File(dir);
		if (!file.exists()) {
			// 如果文件不存在，会创建相应文件夹
			file.mkdirs();
		}
		File picture = new File(file, "user_identify.jpg");
		try {
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(picture));
			bm.compress(Bitmap.CompressFormat.JPEG, 60, bos);
			bos.flush();
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return picture;

	}
}
