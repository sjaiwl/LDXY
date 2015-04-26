package com.app.service;

import com.ldxy.letsgoo.Personal_canyu;
import com.ldxy.letsgoo.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ComingActivityService extends Service{

	public static final String ACTION="com.app.service.ComingActivityService";
	private Notification mNotification;
	private NotificationManager mManager;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override  
    public void onCreate() {  
        initNotifiManager();  
    }  
      
    @Override  
    public void onStart(Intent intent, int startId) {  
        new PollingThread().start();  
    }
    //初始化通知栏配置  
    private void initNotifiManager() {  
        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);  
        int icon = R.drawable.ic_launcher;  
        mNotification = new Notification();  
        mNotification.icon = icon;  
        mNotification.tickerText = "新通知";  
        mNotification.defaults |= Notification.DEFAULT_SOUND;  
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;  
    }  
    //弹出Notification  
    private void showNotification() {  
        mNotification.when = System.currentTimeMillis();  
        Intent intent = new Intent(this, Personal_canyu.class);  
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,  
                Intent.FLAG_ACTIVITY_NEW_TASK);  
        mNotification.setLatestEventInfo(this,  
                getResources().getString(R.string.app_name), "有您参加的活动要开始了偶~", pendingIntent);  
        mManager.notify(0, mNotification);  
    }  
    /** 
     * Polling thread 
     * 模拟向Server轮询的异步线程 
     * @Author Ryan 
     * @Create 2013-7-13 上午10:18:34 
     */  
    int count = 0;  
    class PollingThread extends Thread {  
        @Override  
        public void run() {  
            count ++;  
            //当计数能被5整除时弹出通知  
            if (count % 5 == 0) {  
                showNotification();   
            }  
        }  
    }  
      
    @Override  
    public void onDestroy() {  
        super.onDestroy();  
    } 
}
