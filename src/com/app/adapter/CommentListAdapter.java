package com.app.adapter;

import java.util.List;

import com.app.function.Dynamic;
import com.app.smart.SmartImageView;
import com.app.interFace.CommentListItemClickHelp;
import com.ldxy.letsgoo.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CommentListAdapter extends BaseAdapter {

	private Context context;
	private List<Dynamic> list;
	private CommentListItemClickHelp callback;
	private LayoutInflater mInflater;

	public CommentListAdapter(Context context, List<Dynamic> list,
			CommentListItemClickHelp callback) {
		this.context = context;
		this.list = list;
		this.callback = callback;
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
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View view, final ViewGroup parent) {
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewHolder holder;
		if (view == null) {
			view = mInflater.inflate(R.layout.listview_reply, null);
			holder = new ViewHolder();
			holder.image = (SmartImageView) view.findViewById(R.id.reply_image);
			holder.name = (TextView) view.findViewById(R.id.reply_name);
			holder.host = (TextView) view.findViewById(R.id.reply_host);
			holder.hostname = (TextView) view
					.findViewById(R.id.reply_host_name);
			holder.when = (TextView) view.findViewById(R.id.reply_when);
			holder.bt = (TextView) view.findViewById(R.id.reply_bt);
			holder.content = (TextView) view.findViewById(R.id.reply_content);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		Dynamic d = list.get(position);
		if (d.getOperate_type() == 4) {
			holder.host.setVisibility(View.VISIBLE);
			holder.hostname.setVisibility(View.VISIBLE);
			holder.hostname.setText(d.getSuffer());
		}
		holder.image.setImageUrl(d.getExecutorpictureurl(), 2);
		holder.name.setText(d.getExecutor());
		holder.when.setText(d.getWhen());
		holder.content.setText(d.getComment());

		final View v = view;
		final int p = position;
		final int one = holder.bt.getId();
		holder.bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				callback.onClick(v, parent, p, one);
			}
		});
		return view;
	}

	public static class ViewHolder {
		public SmartImageView image;
		public TextView name;
		public TextView host;
		public TextView hostname;
		public TextView when;
		public TextView bt;
		public TextView content;
	}

}
