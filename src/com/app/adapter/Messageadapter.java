package com.app.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.function.DynamicMessage;
import com.app.interFace.IndexListItemClickHelp;
import com.app.smart.SmartImageView;
import com.ldxy.letsgoo.R;

public class Messageadapter extends BaseAdapter {
	private List<DynamicMessage> data;
	private LayoutInflater layoutInflater;
	private IndexListItemClickHelp callback;
	private Context context;
	private int type;

	public Messageadapter(Context context, List<DynamicMessage> data,
			IndexListItemClickHelp callback) {
		this.context = context;
		this.data = data;
		this.callback = callback;
	}

	public static class ViewHolder {
		public SmartImageView image;
		public SmartImageView acimage;
		public TextView name;
		public TextView activity;
		public TextView content;
		public TextView pingluntype;
		public TextView time;
		public ImageView huifu;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, final ViewGroup parent) {
		// 获得组件，实例化组件
		layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = layoutInflater.inflate(R.layout.message_pinglunlist,
					null);
			holder.image = (SmartImageView) convertView
					.findViewById(R.id.message_pinglun_image);
			holder.acimage = (SmartImageView) convertView
					.findViewById(R.id.message_pinglun_acimage);
			holder.name = (TextView) convertView
					.findViewById(R.id.message_pinglun_name);
			holder.activity = (TextView) convertView
					.findViewById(R.id.message_pinglun_activity);
			holder.content = (TextView) convertView
					.findViewById(R.id.message_pinglun_content);
			holder.pingluntype = (TextView) convertView
					.findViewById(R.id.message_pinglun_type);
			holder.time = (TextView) convertView
					.findViewById(R.id.message_pinglun_time);
			holder.huifu = (ImageView) convertView
					.findViewById(R.id.message_pinglun_huifu);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final View v = convertView;
		final int p = position;
		final int which = holder.huifu.getId();
		// 数据不为空
		if (!data.isEmpty()) {
			type = data.get(position).getOperate_type();
			switch (type) {
			case 1:
				holder.huifu.setVisibility(View.GONE);
				holder.content.setVisibility(View.GONE);
				holder.image.setImageUrl(data.get(position).getPictureurl(), 2);
				holder.name.setText(data.get(position).getNickname());
				holder.pingluntype.setText("参与了你的活动");
				holder.acimage.setImageUrl(data.get(position)
						.getAcpictureurls(), 1);
				holder.activity.setText(data.get(position).getActheme());
				holder.time.setText(data.get(position).getUpdated_at());
				break;
			case 3:
				holder.huifu.setVisibility(View.VISIBLE);
				holder.content.setVisibility(View.VISIBLE);
				holder.image.setImageUrl(data.get(position).getPictureurl(), 2);
				holder.name.setText((String) data.get(position).getNickname());
				holder.pingluntype.setText("评论了你的活动");
				holder.acimage.setImageUrl(data.get(position)
						.getAcpictureurls(), 1);
				holder.activity.setText(data.get(position).getActheme());
				holder.content
						.setText((String) data.get(position).getComment());
				holder.time.setText(data.get(position).getUpdated_at());
				holder.huifu.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						callback.onClick(v, parent, p, which);
					}
				});
				break;
			case 4:
				holder.huifu.setVisibility(View.VISIBLE);
				holder.content.setVisibility(View.VISIBLE);
				holder.image.setImageUrl(data.get(position).getPictureurl(), 2);
				holder.name.setText((String) data.get(position).getNickname());
				holder.pingluntype.setText("回复了你");
				holder.acimage.setImageUrl(data.get(position)
						.getAcpictureurls(), 1);
				holder.activity.setText(data.get(position).getActheme());
				holder.content
						.setText((String) data.get(position).getComment());
				holder.time.setText(data.get(position).getUpdated_at());
				holder.huifu.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						callback.onClick(v, parent, p, which);
					}
				});
				break;
			}
		}
		return convertView;
	}

}
