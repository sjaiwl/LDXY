package com.ldxy.letsgoo;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.http.Header;
import com.app.function.Configuration;
import com.app.function.UserInfo;
import com.app.tools.SelectPopupWindow;
import com.app.tools.TagPostWindow;
import com.ldxy.letsgoo.R.color;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.TimePicker.OnTimeChangedListener;

public class PostPage extends Activity {
	private Intent intent;
	private TextView cancel,send,postStartTime,postEndTime,postTag;
	private EditText postSubject,postPosition,postPhone,postContent;
	private List<String> tags;
	private TagPostWindow tagwindow;
	private TextView addphoto;
	private ImageView choosephoto;
	private SelectPopupWindow menuWindow;
	private File tempFile;
	private static final int PHOTO_REQUEST_CAMERA = 1;// 拍照
	private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
	private static final String PHOTO_FILE_NAME = "temp_photo.jpg";
	private int Target_Width=120;
	private int Target_Height=120;
	private Uri uri=null;
	private int inputtype=0;
	private Calendar time=Calendar.getInstance(Locale.CHINA);
	private SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
	private DatePicker datePicker;
	private TimePicker timePicker;
	private AlertDialog dialog;
	private int tagnumber=12;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_post_page);
		
		initView();
		initData();
	}
	private void initView(){
		intent=this.getIntent();
		cancel=(TextView)this.findViewById(R.id.post_cancel);
		send=(TextView)this.findViewById(R.id.post_send);
		postSubject=(EditText)this.findViewById(R.id.post_subject);
		postPosition=(EditText)this.findViewById(R.id.post_position);
		postStartTime=(TextView)this.findViewById(R.id.post_starttime);
		postEndTime=(TextView)this.findViewById(R.id.post_endtime);
		postTag=(TextView)this.findViewById(R.id.post_tag);
		postPhone=(EditText)this.findViewById(R.id.post_phone);
		postContent=(EditText)this.findViewById(R.id.post_content);
		addphoto=(TextView)this.findViewById(R.id.post_addphoto);
		choosephoto=(ImageView)this.findViewById(R.id.post_choosephoto);
	}
	private void initData(){
		cancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				InputMethodManager imm = (InputMethodManager)PostPage.this
					    .getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(PostPage.this.getCurrentFocus().getWindowToken(), 
						InputMethodManager.HIDE_NOT_ALWAYS);
				finish();
			}
		});
		send.setClickable(false);
		send.setOnClickListener(null);
		postSubject.addTextChangedListener(watcher);
		postPosition.addTextChangedListener(watcher);
		postPhone.addTextChangedListener(watcher);
		postContent.addTextChangedListener(watcher);
		postStartTime.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				inputtype=0;
				dateTimePickerDialog();
			}});
		postEndTime.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				inputtype=1;
				dateTimePickerDialog();
			}});
		tags=new ArrayList<String>();
		String[] tagname={"球类","招募","讲座","演出","跑步","网游","派对","唱K","骑行","登山","桌游","其他"};
		for(int i=0;i<tagnumber;i++){
			tags.add(tagname[i]);
		}
		postTag.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				InputMethodManager imm = (InputMethodManager)PostPage.this
					    .getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(PostPage.this.getCurrentFocus().getWindowToken(), 
						InputMethodManager.HIDE_NOT_ALWAYS);
				tagwindow=new TagPostWindow(PostPage.this,tags);
				tagwindow.showAtLocation(PostPage.this.findViewById(R.id.post_main), Gravity.CENTER, 0, 0);
			}});
		//添加照片
		addphoto.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				InputMethodManager imm = (InputMethodManager)PostPage.this
					    .getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(PostPage.this.getCurrentFocus().getWindowToken(), 
						InputMethodManager.HIDE_NOT_ALWAYS);
				menuWindow=new SelectPopupWindow(PostPage.this,choosePhotoOnClick);
				menuWindow.showAtLocation(PostPage.this.findViewById(R.id.post_main),
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
				menuWindow.update();
			}
		});
		choosephoto.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				intent.setClass(PostPage.this, ViewPicture.class);
				startActivity(intent);
			}});
	}
	private TextWatcher watcher=new TextWatcher(){
		public void afterTextChanged(Editable s) {}
		public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
		@Override
		public void onTextChanged(CharSequence s, int start, int before,int count) {
			if(s.length()==0){
				disableSend();
			}else{
				enableSend();
			}
		}};
	private AlertDialog dateTimePickerDialog(){
		LinearLayout dateTimeLayout = (LinearLayout)LayoutInflater.from(this).inflate(R.layout.date_time_picker, null);  
        datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.DatePicker);  
        timePicker = (TimePicker) dateTimeLayout.findViewById(R.id.TimePicker);  
        timePicker.setIs24HourView(true);  
          
        OnTimeChangedListener timeListener= new OnTimeChangedListener() {  
            @Override  
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) { 
                time.set(Calendar.HOUR_OF_DAY, hourOfDay);  
                time.set(Calendar.MINUTE, minute);  
            }  
        };  
        timePicker.setOnTimeChangedListener(timeListener);  
        
        OnDateChangedListener dateListener = new OnDateChangedListener() {  
            @Override  
            public void onDateChanged(DatePicker view, int year, int monthOfYear,  
                    int dayOfMonth) {  
                time.set(Calendar.YEAR, year);  
                time.set(Calendar.MONTH, monthOfYear);  
                time.set(Calendar.DAY_OF_MONTH, dayOfMonth); 
            }  
        };  
        datePicker.init(time.get(Calendar.YEAR), time.get(Calendar.MONTH), time.get(Calendar.DAY_OF_MONTH), dateListener);  
        timePicker.setCurrentHour(time.get(Calendar.HOUR_OF_DAY));  
        timePicker.setCurrentMinute(time.get(Calendar.MINUTE));  
          
        dialog = new AlertDialog.Builder(this).setTitle("设置日期时间").setView(dateTimeLayout)  
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {  
                    @Override  
                    public void onClick(DialogInterface dialog, int which) {  
                    	datePicker.clearFocus();
                    	timePicker.clearFocus();
                        time.set(Calendar.YEAR, datePicker.getYear());  
                        time.set(Calendar.MONTH, datePicker.getMonth());  
                        time.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());  
                        time.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());  
                        time.set(Calendar.MINUTE, timePicker.getCurrentMinute());  
                        updateTime(); 
                    }  
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {  
                    @Override  
                    public void onClick(DialogInterface dialog, int which) {
                    	if(isEmptyContext())
                    		disableSend();
                    }  
                }).show();  
        return dialog;  
	}
	private void updateTime(){
		if(inputtype==0){
			postStartTime.setText(format.format(time.getTime()));
		}else if(inputtype==1){
			postEndTime.setText(format.format(time.getTime()));
		}
		enableSend();
	}
	private boolean isEmptyContext(){//添加照片搞定后，要判断-----------------------------------
		if(TextUtils.isEmpty((String) postStartTime.getText())&&TextUtils.isEmpty((String) postEndTime.getText())&&
				TextUtils.isEmpty(postSubject.getText().toString())&&TextUtils.isEmpty(postPosition.getText().toString())&&
				TextUtils.isEmpty(postTag.getText().toString())&&TextUtils.isEmpty(postContent.getText().toString())
				&&TextUtils.isEmpty(postPhone.getText().toString()))
			return true;
		else 
			return false;
	}
	//激活发送键
	private void enableSend(){
		send.setTextColor(color.postEnable);
		send.setClickable(true);
		send.setOnClickListener(mysendOnClick);
	}
	//失活发送键
	private void disableSend(){
		send.setTextColor(color.postHint);
		send.setClickable(false);
		send.setOnClickListener(null);
	}
	//发送监听器
	private OnClickListener mysendOnClick=new OnClickListener(){
		@Override
		public void onClick(View v) {
			String subject=postSubject.getText().toString();
			String position=postPosition.getText().toString();
			String starttime=postStartTime.getText().toString();
			String endtime=postEndTime.getText().toString();
			String tag=postTag.getText().toString();
			String content=postContent.getText().toString();
			String phone=postPhone.getText().toString();
			String[] proj={MediaStore.Images.Media.DATA};
			@SuppressWarnings("deprecation")
			Cursor cursor=managedQuery(uri,proj,null,null,null);
			int actualindex=cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			String img_path =cursor.getString(actualindex);
			File picfile = new File(img_path);
			if(!TextUtils.isEmpty(subject)&&!TextUtils.isEmpty(position)&&
					!TextUtils.isEmpty(starttime)&&!TextUtils.isEmpty(endtime)&&
					!TextUtils.isEmpty(content)&&!TextUtils.isEmpty(tag)&&
					!TextUtils.isEmpty(phone)){
				RequestParams params = new RequestParams();
				try{
					params.put("actheme", subject);
					params.put("acplace", position);
					params.put("starttime", starttime);
					params.put("endtime", endtime);
					params.put("aclabel", tag);
					params.put("user_id", Integer.toString(UserInfo.user.getUser_id()));
					params.put("phonenumber", phone);
					params.put("accontent", content);
					params.put("acpictureurls", picfile);
				}catch(FileNotFoundException e){
					e.printStackTrace();
				}
				String url=Configuration.sendUrl;
				AsyncHttpClient client = new AsyncHttpClient();
				client.post(url, params, new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,byte[] responseBody) {
						try {
							if (statusCode == 200) {
								Toast.makeText(PostPage.this.getApplicationContext(), "发布成功",0).show();
								InputMethodManager imm = (InputMethodManager)PostPage.this
									    .getSystemService(Context.INPUT_METHOD_SERVICE);
								imm.hideSoftInputFromWindow(PostPage.this.getCurrentFocus().getWindowToken(), 
										InputMethodManager.HIDE_NOT_ALWAYS);
								finish();
							} else {
								Toast.makeText(PostPage.this.getApplicationContext(),"发布失败", 0).show();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					@Override
					public void onFailure(int statusCode, Header[] headers,byte[] responseBody, Throwable error) {
						Toast.makeText(PostPage.this.getApplicationContext(),"网络访问异常,请重试 ", 0).show();

					}
				});
			}else{
				Toast.makeText(PostPage.this.getApplicationContext(), "活动信息不完整", Toast.LENGTH_SHORT).show();
			}
		}
	};
	private OnClickListener choosePhotoOnClick = new OnClickListener() {
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
	//从相机获取
	public void camera(View view) {
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		// 判断存储卡是否可以用，可用进行存储
		if (hasSdcard()) {
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(new File(Environment.getExternalStorageDirectory(), PHOTO_FILE_NAME)));
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
	//从相册获取
	public void gallery(View view) {
		// 激活系统图库，选择一张图片
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PHOTO_REQUEST_GALLERY) {
			if (data != null) {
				uri = data.getData();
			}
		} else if (requestCode == PHOTO_REQUEST_CAMERA) {
			if (hasSdcard()) {
				tempFile = new File(Environment.getExternalStorageDirectory(),PHOTO_FILE_NAME);
				uri=Uri.fromFile(tempFile);
			} else {
				Toast.makeText(PostPage.this, "未找到存储卡，无法存储照片", 0).show();
			}
		}
		ContentResolver cr=this.getContentResolver();
		Cursor cursor=cr.query(uri, null, null, null, null);
		Bitmap bitmap=null;
		if(cursor!=null){
			cursor.moveToFirst();
			String filePath=cursor.getString(cursor.getColumnIndex("_data"));
			String orientation=cursor.getString(cursor.getColumnIndex("orientation"));
			cursor.close();
			if(filePath!=null){
				bitmap=BitmapFactory.decodeFile(filePath);
				int angle=0;
				if(orientation!=null&&!"".equals(orientation)){
					angle=Integer.parseInt(orientation);
				}
				if(angle!=0){
					Matrix m=new Matrix();
					int width=bitmap.getWidth();
					int height=bitmap.getHeight();
					m.setRotate(angle);
					bitmap=Bitmap.createBitmap(bitmap, 0, 0, width, height,m,true);
				}
			}
		}
		Bitmap resizedBitmap=resizeImageView(Target_Width,Target_Height,bitmap);
		choosephoto.setImageBitmap(resizedBitmap);
		choosephoto.setClickable(true);
		intent.putExtra("choosephoto", uri);
		enableSend();
		super.onActivityResult(requestCode, resultCode, data);
	}
	private Bitmap resizeImageView(int newWidth,int newHeight,Bitmap bitmap){
		int width=bitmap.getWidth();
		int height=bitmap.getHeight();
		float scaleWidth=((float)newWidth)/width;
		float scaleHeight=((float)newHeight)/height;
		Matrix matrix=new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		return Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);
	}
}
