package com.app.tools;

import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.app.adapter.TagChooseAdapter;
import com.ldxy.letsgoo.R;

public class TagPostWindow extends PopupWindow {

	private View mview;
	private TextView choosetag;
	private GridView gv;
	private List<String> tags = null;
	private TextView posttag;
	private TagChooseAdapter adapter;
	private TextView send;
	private int[] tagcolor = { R.color.tag1, R.color.tag2, R.color.tag3,
			R.color.tag4, R.color.tag5, R.color.tag6, R.color.tag7,
			R.color.tag8, R.color.tag9, R.color.tag10, R.color.tag11,
			R.color.tag12 };

	public TagPostWindow(Activity context, List<String> tags) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mview = inflater.inflate(R.layout.tag_choose, null);
		this.tags = tags;
		choosetag = (TextView) mview.findViewById(R.id.choosetag);
		posttag = (TextView) context.findViewById(R.id.post_tag);
		send = (TextView) context.findViewById(R.id.post_send);
		choosetag.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dismiss();
			}
		});
		this.setContentView(mview);
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		this.setBackgroundDrawable(null);
		adapter = new TagChooseAdapter(context, tags);
		gv = (GridView) mview.findViewById(R.id.grid_tag);
		gv.setAdapter(adapter);
		gv.setOnItemClickListener(itemsOnClick);
	}

	private OnItemClickListener itemsOnClick = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			String atag = tags.get(position);
			posttag.setText(atag);
			TextView tv = (TextView) view.getTag();
			for (int i = 0; i < parent.getChildCount(); i++) {
				((TextView) parent.getChildAt(i).getTag())
						.setBackgroundResource(R.color.tagunchoose);
			}
			Random random = new Random();
			tv.setBackgroundResource(tagcolor[random.nextInt(10)]);
			send.setTextColor(R.color.postEnable);
			send.setClickable(true);
		}
	};
}
