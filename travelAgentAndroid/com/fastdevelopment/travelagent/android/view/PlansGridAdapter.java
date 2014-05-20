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

public class PlansGridAdapter extends ArrayAdapter<IPojoModel> {

	private String TAG = this.getClass().getSimpleName();

	private Resources resources = ServerConfig.resource;

	public PlansGridAdapter(Context context, List<IPojoModel> objects) {
		super(context, 0, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = LayoutInflater.from(getContext()).inflate(R.layout.layout_plans_grid_item, null);
		}
		try {
			TextView textView = (TextView) view.findViewById(R.id.plans_grid_item_text);
			textView.setText(getItem(position).getName());

			if (position % 2 != 0) {
				// distance
				textView.setBackgroundColor(resources.getColor(R.color.lite_blue));
			}

		} catch (Exception e) {
			Log.e(TAG, ExceptionUtils.getStackTrace(e));
		}
		return view;
	}

}
