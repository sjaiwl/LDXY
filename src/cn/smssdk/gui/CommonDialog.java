package cn.smssdk.gui;

import static cn.smssdk.framework.utils.R.getLayoutRes;
import static cn.smssdk.framework.utils.R.getStyleRes;
import android.app.Dialog;
import android.content.Context;

public class CommonDialog {
	public static final Dialog ProgressDialog(Context context){
		int resId = getStyleRes(context, "CommonDialog");
		if (resId > 0) {
			final Dialog dialog = new Dialog(context, resId);
			resId = getLayoutRes(context, "smssdk_progress_dialog");
			if (resId > 0) {
				dialog.setContentView(resId);
				return dialog;
			}
		}
		return null;
	}

}
