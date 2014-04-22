package com.fastdevelopment.travelagent.android.view;

import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fastdevelopment.travelagent.android.R;
import com.fastdevelopment.travelagent.android.model.IModel;

public class ScheduleGridAdapter extends ArrayAdapter<IModel> {

	private String TAG = this.getClass().getSimpleName();

	public ScheduleGridAdapter(Context context, List<IModel> objects) {
		super(context, 0, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = LayoutInflater.from(getContext()).inflate(R.layout.layout_drag_grid_item, null);
		}
		try {
			TextView textView = (TextView) view.findViewById(R.id.drag_grid_item_text);
			textView.setText(getItem(position).getName());

		} catch (Exception e) {
			Log.e(TAG, ExceptionUtils.getStackTrace(e));
		}
		return view;
	}

}
