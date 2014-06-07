package com.fastdevelopment.travelagent.android.view;

import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fastdevelopment.travelagent.android.R;
import com.fastdevelopment.travelagent.android.common.ServerConfig;
import com.fastdevelopment.travelagent.android.model.IPojoModel;
import com.fastdevelopment.travelagent.android.thirdparty.data.GoogleDistanceMetrix;

public class ScheduleGridAdapter extends ArrayAdapter<IPojoModel> {

	private String TAG = this.getClass().getSimpleName();

	private GoogleDistanceMetrix googleDistanceMetrix;

	private Resources resources = ServerConfig.resources;
	
	private List<IPojoModel> items;

	public ScheduleGridAdapter(Context context, List<IPojoModel> objects, GoogleDistanceMetrix googleDistanceMetrix) {
		super(context, 0, objects);
		this.googleDistanceMetrix = googleDistanceMetrix;
		items = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = LayoutInflater.from(getContext()).inflate(R.layout.layout_schedule_grid_item, null);
		}
		try {
			TextView textView = (TextView) view.findViewById(R.id.schedule_grid_item_text);
			textView.setText(getItem(position).getName());

			if (position % 2 != 0) {
				// distance
				textView.setBackgroundColor(resources.getColor(R.color.pink));
			}

		} catch (Exception e) {
			Log.e(TAG, ExceptionUtils.getStackTrace(e));
		}
		return view;
	}

	public GoogleDistanceMetrix getGoogleDistanceMetrix() {
		return googleDistanceMetrix;
	}

	public List<IPojoModel> getItems() {
		return items;
	}

	public void setItems(List<IPojoModel> items) {
		this.items = items;
	}

}
