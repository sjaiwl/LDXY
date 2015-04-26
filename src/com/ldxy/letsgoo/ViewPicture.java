package com.ldxy.letsgoo;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import com.app.smart.WebImageCache;
import com.app.tools.BitmapUtil;
import com.app.tools.DragImageView;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;

public class ViewPicture extends Activity {
	private static final int CONNECT_TIMEOUT = 5000;
	private static final int READ_TIMEOUT = 10000;
	private int window_width, window_height;// 控件宽度
	private DragImageView dragImageView;// 自定义控件
	private int state_height;// 状态栏的高度
	private ViewTreeObserver viewTreeObserver;
	private Intent intent;
	private static WebImageCache webImageCache;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_view_picture);
		intent = this.getIntent();
		int type = intent.getIntExtra("phototype", 1);// 默认为1，本地上传图片，其他则为网络图片
		Uri uri = intent.getParcelableExtra("choosephoto");
		/** 获取可見区域高度 **/
		WindowManager manager = getWindowManager();
		window_width = manager.getDefaultDisplay().getWidth();
		window_height = manager.getDefaultDisplay().getHeight();
		ContentResolver cr = this.getContentResolver();
		InputStream is;
		Bitmap bitmap = null;
		if (type == 1) {
			try {
				is = cr.openInputStream(uri);
				BitmapFactory.Options opt = new BitmapFactory.Options();
				opt.inPreferredConfig = Bitmap.Config.RGB_565;
				opt.inPurgeable = true;
				opt.inInputShareable = true;
				bitmap = BitmapFactory.decodeStream(is, null, opt);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			Cursor cursor = cr.query(uri, null, null, null, null);
			if (cursor != null) {
				cursor.moveToFirst();
				String filePath = cursor.getString(cursor
						.getColumnIndex("_data"));
				String orientation = cursor.getString(cursor
						.getColumnIndex("orientation"));
				cursor.close();
				if (filePath != null) {
					int angle = 0;
					if (orientation != null && !"".equals(orientation)) {
						angle = Integer.parseInt(orientation);
					}
					if (angle != 0) {
						Matrix m = new Matrix();
						int width = bitmap.getWidth();
						int height = bitmap.getHeight();
						m.setRotate(angle);
						bitmap = Bitmap.createBitmap(bitmap, 0, 0, width,
								height, m, true);
					}
				}
			}
		} else {
			if (webImageCache == null) {
				webImageCache = new WebImageCache(this);
			}
			if (uri.toString() != null) {
				bitmap = webImageCache.get(uri.toString());
				if (bitmap == null) {
					bitmap = getBitmapFromUrl(uri.toString());
					if (bitmap != null) {
						webImageCache.put(uri.toString(), bitmap);
					}
				}
			}
		}
		bitmap = BitmapUtil.getBitmap(bitmap, window_width, window_height);
		dragImageView = (DragImageView) findViewById(R.id.view_picture);
		// 设置图片
		dragImageView.setImageBitmap(bitmap);
		dragImageView.setmActivity(this);// 注入Activity.
		/** 测量状态栏高度 **/
		viewTreeObserver = dragImageView.getViewTreeObserver();
		viewTreeObserver
				.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						if (state_height == 0) {
							// 获取状况栏高度
							Rect frame = new Rect();
							getWindow().getDecorView()
									.getWindowVisibleDisplayFrame(frame);
							state_height = frame.top;
							dragImageView.setScreen_H(window_height
									- state_height);
							dragImageView.setScreen_W(window_width);
						}
					}
				});
	}

	private Bitmap getBitmapFromUrl(String url) {
		Bitmap bitmap = null;
		try {
			URLConnection conn = new URL(url).openConnection();
			conn.setConnectTimeout(CONNECT_TIMEOUT);
			conn.setReadTimeout(READ_TIMEOUT);
			bitmap = BitmapFactory
					.decodeStream((InputStream) conn.getContent());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}
}
