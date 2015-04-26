package com.app.function;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.json.JSONArray;
import org.json.JSONException;

import com.app.interFace.ThirdPartyLoginCallBack;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;

public class Configuration {
	public static final String server = "http://121.42.40.93/";
//	public static final String server="http://192.168.1.28:3000/";
	public static final String MyActivityService = server
			+ "myactivityservice.json";
	public static final String loginUrl = server + "login.json";
	public static final String indexUrl = server + "get_allactivities.json";
	public static final String getactivityUrl = server
			+ "get_detailactivity.json";
	public static final String getdynamicsUrl = server
			+ "get_activitydynamics.json";
	public static final String collectUrl = server + "newfavorite";
	public static final String uncollectUrl = server + "deletefavorite";
	public static final String sendUrl = server + "newactivity";
	public static final String dynamicsUrl = server + "newdynamic";
	public static final String canceljoinUrl = server + "canceljoin";

	public static final String uploadimageUrl = server + "addcheckpicture";
	public static final String updateuserUrl = server + "update_user";
	public static final String updateuserpicUrl = server + "update_userpicture";
	public static final String getdynamicmessagesUrl = server
			+ "get_personaldynamics.json";
	public static final String getdynamicjoinersUrl = server
			+ "get_joiners.json";
	public static final String getdynamicshoucangUrl = server
			+ "get_favoriteactivities.json";
	public static final String getdynamiccanyuUrl = server
			+ "get_joinedactivities.json";
	public static final String getdynamicfabuUrl = server
			+ "get_publishedactivities.json";
	public static final String appKey = "45c34afd98c6";
	public static final String appSecred = "1b8e98a051b0c70b65da3721b26e3f36";
	
	//ActivityResultInt
	public static final int BACK_IDENTIFY = 11;
	@SuppressWarnings("deprecation")
	public static void showLoginWindow(Activity activity,
			ThirdPartyLoginCallBack callback) {
		ThirdPartyLogin tpl = new ThirdPartyLogin(activity, callback);
		tpl.setCanceledOnTouchOutside(true);
		tpl.show();
		WindowManager windowManager = activity.getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = tpl.getWindow().getAttributes();
		lp.width = (int) (display.getWidth()); // 设置宽度
		lp.gravity = Gravity.BOTTOM;
		tpl.getWindow().setAttributes(lp);
	}

	public static String getNowUTCtime() {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuffer UTCTimeBuffer = new StringBuffer();
		// 1取得本地时间：
		Calendar cal = Calendar.getInstance();
		// 2取得时间偏移量：
		int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);
		// 3取得夏令时差：
		int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);
		// 4从本地时间里扣除这些差量，即可以取得UTC时间：
		cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		UTCTimeBuffer.append(year).append("-").append(month).append("-")
				.append(day);
		UTCTimeBuffer.append(" ").append(hour).append(":").append(minute);
		try {
			format.parse(UTCTimeBuffer.toString());
			return UTCTimeBuffer.toString();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getLocalTimeFromUTC(String UTCTime) {
		if (UTCTime == "") {
			return "时间获取失败";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSS Z");
		UTCTime = UTCTime.replace("Z", " UTC");
		Date dt = null;
		try {
			dt = sdf.parse(UTCTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		GregorianCalendar g = new GregorianCalendar();
		int minutetime = 0;
		minutetime = Math.abs((int) (dt.getTime() - g.getTimeInMillis()))
				/ (1000 * 60);
		if (minutetime <= 0) {
			return "刚刚";
		}
		if (minutetime > 0 && minutetime < 60) {
			return minutetime + "分钟前";
		}
		if (minutetime >= 60 && minutetime < 60 * 24) {
			return (int) minutetime / 60 + "小时前";
		} else {
			return (int) minutetime / (60 * 24) + "天前";
		}
	}

	public static Bitmap getRoundCornerBitmap(Bitmap bitmap, float roundPX) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Bitmap bitmap2 = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap2);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, width, height);
		final RectF rectF = new RectF(rect);

		paint.setColor(color);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawRoundRect(rectF, roundPX, roundPX, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return bitmap2;
	}

	public static int getActivityTimeType(String starttime, String endtime) {
		int type = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
		Date start = null;
		Date end = null;
		try {
			start = sdf.parse(starttime);
			end = sdf.parse(endtime);
		} catch (ParseException e) {
			Log.i("test", e.toString());
		}
		GregorianCalendar g = new GregorianCalendar();
		long startmillis = start.getTime();
		long endmillis = end.getTime();
		long currentmillis = g.getTimeInMillis();
		if (currentmillis < startmillis)
			type = 1;
		if (currentmillis >= startmillis && currentmillis <= endmillis)
			type = 2;
		if (currentmillis > endmillis)
			type = 3;
		return type;
	}

	public static String getActivityNumbers(String jsonString) {
		JSONArray array = null;
		String name = "";
		try {
			array = new JSONArray(jsonString);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		int numbers = array.length();
		if (numbers == 0) {
			return "现在还没人参与了活动";
		}
		for (int i = 0; i < numbers; i++) {
			try {
				name += array.getJSONObject(i).getString("joinername") + ",";
			} catch (JSONException e) {
			}
		}
		int limit = 40;
		if (name.length() < limit)
			return name + "等" + numbers + "人参与了活动";
		else
			return name.substring(0, limit) + "..." + "等" + numbers + "人参与了活动";
	}
}
