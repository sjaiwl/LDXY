package com.app.adapter;

import java.util.List;

import com.app.function.ActivityUnit;
import com.app.smart.SmartImageView;
import com.app.interFace.IndexListItemClickHelp;
import com.ldxy.letsgoo.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class IndexListItemAdapter extends BaseAdapter {

	private Context context;
	private List<ActivityUnit> list;
	private IndexListItemClickHelp callback;
	private LayoutInflater mInflater;

	public IndexListItemAdapter(Context context, List<ActivityUnit> list,
			IndexListItemClickHelp callback) {
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
		final ViewHolder holder;
		if (view == null) {
			view = mInflater.inflate(R.layout.activity_unit, null);
			holder = new ViewHolder();
			holder.type = (TextView) view.findViewById(R.id.index_type);
			holder.title = (TextView) view.findViewById(R.id.index_title);
			holder.image = (SmartImageView) view.findViewById(R.id.index_image);
			holder.name = (TextView) view.findViewById(R.id.index_name);
			holder.when = (TextView) view.findViewById(R.id.index_when);
			holder.participation = (TextView) view
					.findViewById(R.id.index_participation);
			holder.comment = (TextView) view.findViewById(R.id.index_comment);
			holder.starttime = (TextView) view
					.findViewById(R.id.index_starttime);
			holder.location = (TextView) view.findViewById(R.id.index_location);
			holder.content = (TextView) view.findViewById(R.id.index_content);
			// holder.zan=(TextView)view.findViewById(R.id.index_z);
			holder.collectbefore = (TextView) view
					.findViewById(R.id.index_collect_before);
			holder.collectafter = (TextView) view
					.findViewById(R.id.index_collect_after);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		ActivityUnit unit = list.get(position);
		holder.type.setText(unit.getType());
		holder.title.setText(unit.getTitle());
		holder.image.setImageUrl(unit.getUserImage(), 2);
		holder.name.setText(unit.getUserName());
		holder.when.setText(unit.getWhen());
		holder.participation.setText(unit.getJoinsum());
		holder.comment.setText(unit.getCommentsum());
		holder.starttime.setText(unit.getStarttime());
		holder.location.setText(unit.getLocation());
		if (unit.getIscollected() == 1) {
			holder.collectafter.setVisibility(View.VISIBLE);
			holder.collectbefore.setVisibility(View.GONE);
		} else {
			holder.collectbefore.setVisibility(View.VISIBLE);
			holder.collectafter.setVisibility(View.GONE);
		}
		holder.content.setText(unit.getAccontent());
		final View v = view;
		final int p = position;
		final int one = holder.collectbefore.getId();
		final int two = holder.collectafter.getId();
		// final int one=holder.zan.getId();
		// holder.zan.setOnClickListener(new OnClickListener(){
		// @Override
		// public void onClick(View arg0) {
		// callback.onClick(v, parent, p, one);
		// }
		// });
		holder.collectbefore.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				callback.onClick(v, parent, p, one);
			}
		});
		holder.collectafter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				callback.onClick(v, parent, p, two);
			}
		});
		return view;
	}

	class ViewHolder {
		TextView type;
		TextView title;
		SmartImageView image;
		TextView name;
		TextView when;
		TextView participation;
		TextView comment;
		TextView starttime;
		TextView location;
		TextView content;
		TextView zan;
		TextView collectbefore;
		TextView collectafter;
	}

}
