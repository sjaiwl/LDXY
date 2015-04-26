package cn.smssdk.gui;

import java.util.HashMap;
import android.view.View;
import android.view.ViewGroup;

/** 联系人listview的item自定义接口 */
public interface ContactItemMaker {

	public View getView(HashMap<String,Object> user, View convertView, ViewGroup parent);

}
