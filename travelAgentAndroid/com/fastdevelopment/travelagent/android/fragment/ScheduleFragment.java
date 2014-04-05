package com.fastdevelopment.travelagent.android.fragment;

import org.apache.commons.lang.exception.ExceptionUtils;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fastdevelopment.travelagent.android.R;
import com.fastdevelopment.travelagent.android.common.ThirdPartyHandler;
import com.google.android.gms.maps.GoogleMap;

public class ScheduleFragment extends Fragment {

	private GoogleMap map;
	private Handler httpResponseHandler;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_schedule, container, false);

		// map = ((SupportMapFragment)
		// getFragmentManager().findFragmentById(R.id.support_map_fragment)).getMap();

		initMessageHandler();
		try {
			ThirdPartyHandler tp = ThirdPartyHandler.getInstance();
			tp.invokeDistanceTimeEvent(httpResponseHandler);
		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), ExceptionUtils.getStackTrace(e));
		}

		return v;
	};

	@SuppressLint("HandlerLeak")
	protected void initMessageHandler() {
		httpResponseHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Bundle data = msg.getData();
				String val = data.getString("result");
				Log.i("mylog", "请求结果-->" + val);
			}
		};
	}

}
