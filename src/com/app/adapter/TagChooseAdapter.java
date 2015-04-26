package com.app.adapter;

import java.util.List;

import com.ldxy.letsgoo.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TagChooseAdapter extends BaseAdapter {

	private Context context;
	private List<String> list;

	public TagChooseAdapter(Context context, List<String> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		TextView tv = null;
		if (view == null) {
			view = LayoutInflater.from(context)
					.inflate(R.layout.tag_item, null);
			tv = (TextView) view.findViewById(R.id.tag_text);
			view.setTag(tv);
		} else {
			tv = (TextView) view.getTag();
		}
		tv.setText(list.get(position));
		return view;
	}
}
