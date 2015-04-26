package com.app.function;

import com.app.fragment.FragmentMessage;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class EditTextSpuer extends EditText {
	InputMethodManager imm;

	public EditTextSpuer(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_ENTER) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public boolean onKeyPreIme(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			FragmentMessage.hiddenpopupWindow();
			return true;
		}
		return super.onKeyPreIme(keyCode, event);
	}

}
