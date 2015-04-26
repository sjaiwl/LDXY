package com.app.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.function.ActivityUnit;
import com.app.function.Configuration;
import com.app.interFace.IndexListItemClickHelp;
import com.app.smart.SmartImageView;
import com.ldxy.letsgoo.R;

public class PersonActivityAdapter extends BaseAdapter {

	private Context context;
	private List<ActivityUnit> list;
	private IndexListItemClickHelp callback;
	private LayoutInflater mInflater;
	private int type;

	public PersonActivityAdapter(int type, Context context,
			List<ActivityUnit> list, IndexListItemClickHelp callback) {
		this.context = context;
		this.list = list;
		this.callback = callback;
		this.type = type;
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
	public View getView(int position, View view, final ViewGroup parent) {
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewHolder holder;
		if (view == null) {
			view = mInflater.inflate(R.layout.personal_activity, null);
			holder = new ViewHolder();
			holder.type = (TextView) view.findViewById(R.id.personindex_type);
			holder.title = (TextView) view.findViewById(R.id.personindex_title);
			holder.image = (SmartImageView) view
					.findViewById(R.id.personindex_image);
			holder.name = (TextView) view.findViewById(R.id.personindex_name);
			holder.when = (TextView) view.findViewById(R.id.personindex_when);
			holder.starttime = (TextView) view
					.findViewById(R.id.personindex_starttime);
			holder.location = (TextView) view
					.findViewById(R.id.personindex_location);
			holder.content = (TextView) view
					.findViewById(R.id.personindex_content);
			holder.canyunum = (TextView) view
					.findViewById(R.id.personindex_canyunum);
			holder.timetype = (ImageView) view
					.findViewById(R.id.personactivity_timetype);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		final ActivityUnit unit = list.get(position);
		holder.type.setText(unit.getType());
		holder.title.setText(unit.getTitle());
		holder.image.setImageUrl(unit.getUserImage(), 2);
		holder.name.setText(unit.getUserName());
		holder.when.setText(unit.getWhen());
		holder.starttime.setText(unit.getStarttime());
		holder.location.setText(unit.getLocation());
		holder.content.setText(unit.getAccontent());
		int timetype = Configuration.getActivityTimeType(
				unit.getOriginalStarttime(), unit.getEndtime());
		switch (timetype) {
		case 1:
			holder.timetype.setBackgroundResource(R.drawable.activity_type4);
			break;
		case 2:
			holder.timetype.setBackgroundResource(R.drawable.activity_type2);
			break;
		case 3:
			holder.timetype.setBackgroundResource(R.drawable.activity_type1);
			break;
		default:
			break;
		}
		if (type == 1) {
			holder.canyunum.setVisibility(View.VISIBLE);
			holder.canyunum.setText(Configuration.getActivityNumbers(unit
					.getJoiners()));
			final View v = view;
			final int p = position;
			final int which = holder.canyunum.getId();
			holder.canyunum.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					callback.onClick(v, parent, p, which);
				}
			});
		}
		return view;
	}

	public static class ViewHolder {
		TextView type;
		TextView title;
		SmartImageView image;
		TextView name;
		TextView when;
		TextView starttime;
		TextView location;
		TextView content;
		ImageView timetype;
		TextView canyunum;
	}

}
