package com.app.adapter;

import java.util.List;

import com.app.function.DynamicPart;
import com.app.smart.SmartImageView;
import com.ldxy.letsgoo.R;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DynamicPartAdapter extends BaseAdapter {
	private List<DynamicPart> data;
	private LayoutInflater layoutInflater;
	private Context context;

	public static class ViewHolder {
		private ImageView gender;
		private SmartImageView pictureurl;
		private TextView nickname;
		private TextView phone;
		private TextView school;
		private ImageView call;

	}

	public DynamicPartAdapter(Context context, List<DynamicPart> data) {
		this.data = data;
		this.context = context;
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
	public View getView(final int position, View convertView,
			final ViewGroup parent) {
		layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = layoutInflater
					.inflate(R.layout.joinactivitynum, null);
			holder.gender = (ImageView) convertView.findViewById(R.id.zan_sex);
			holder.pictureurl = (SmartImageView) convertView
					.findViewById(R.id.zan_image);
			holder.nickname = (TextView) convertView
					.findViewById(R.id.zan_name);
			holder.phone = (TextView) convertView
					.findViewById(R.id.zan_phonenumber);
			holder.school = (TextView) convertView
					.findViewById(R.id.zan_school);
			holder.call = (ImageView) convertView.findViewById(R.id.zan_phone);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// 性别
		if (data.get(position).getGender() == "boy") {
			holder.gender.setImageResource(R.drawable.zan_boy);
		} else {
			holder.gender.setImageResource(R.drawable.zan_girl);
		}

		holder.pictureurl.setImageUrl(data.get(position).getPictureurl(), 2);
		holder.nickname.setText(data.get(position).getNickname());
		holder.phone.setText((String) data.get(position).getPhonenumber());
		holder.phone.setVisibility(8);
		holder.school.setText((String) data.get(position).getSchool());
		if ((String) data.get(position).getPhonenumber().trim() == "") {
			holder.call.setVisibility(8);
		} else {
			holder.call.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// 显示号码到拨号界面
					Intent intent = new Intent(Intent.ACTION_DIAL, Uri
							.parse("tel:"
									+ (String) data.get(position)
											.getPhonenumber().trim()));
					parent.getContext().startActivity(intent);
				}
			});
		}
		return convertView;
	}

}
