package com.fastdevelopment.travelagent.android.fragment;

import java.net.URLEncoder;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.JSONObject;
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
import com.fastdevelopment.travelagent.android.common.IHttpConnectedService;
import com.fastdevelopment.travelagent.android.common.RestConnectedService;
import com.google.android.gms.maps.GoogleMap;

public class ScheduleFragment extends Fragment {

	private GoogleMap map;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_schedule, container, false);

		new Thread(httpRequestRunner).start();

		return v;
	}

	@SuppressLint("HandlerLeak")
	Handler httpResponseHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			String val = data.getString("value");
			Log.i("mylog", "请求结果-->" + val);
		}
	};

	Runnable httpRequestRunner = new Runnable() {
		@Override
		public void run() {
			//
			// http request.
			//
			// map = ((SupportMapFragment)
			// getFragmentManager().findFragmentById(R.id.support_map_fragment)).getMap();
			String googleApisServerKey = getResources().getString(R.string.google_apis_server_key);

			StringBuffer url = new StringBuffer();
			try {
				
				//google distance matrix api : (for distance time)
				url.append("http://maps.googleapis.com/maps/api/distancematrix/json?");
				url.append("origins=");
				url.append(URLEncoder.encode("Seattle", "UTF-8"));
				url.append("&destinations=");
				url.append(URLEncoder.encode("San+Francisco", "UTF-8"));
				url.append("&mode=");
				url.append(URLEncoder.encode("bicycling", "UTF-8"));
				url.append("&language=");
				url.append(URLEncoder.encode("en-US", "UTF-8"));
				url.append("&sensor=");
				url.append(URLEncoder.encode("false", "UTF-8"));
				
				//google direction api : (for navi)
				//url.append("https://maps.googleapis.com/maps/api/directions/json?");
				//url.append("origin=");
				//url.append(URLEncoder.encode("Chicago,IL", "UTF-8"));
				//url.append("&destination=");
				//url.append(URLEncoder.encode("Los+Angeles,CA", "UTF-8"));
				//url.append("&waypoints=");
				//url.append(URLEncoder.encode("Joplin,MO|Oklahoma+City,OK", "UTF-8"));
				//url.append("&mode=");
				//url.append("walking");
				//url.append("&sensor=");
				//url.append(URLEncoder.encode("false", "UTF-8"));
				//url.append("&key=");
				//url.append(URLEncoder.encode(googleApisServerKey, "UTF-8"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			IHttpConnectedService httpService = new RestConnectedService();
			JSONObject json = null;
			try {
				httpService.initHttpClient(443);
				String jsonStr = httpService.doGetByHttpClientAndReturnJsonStr(url.toString());
				if (jsonStr != null) {
					json = new JSONObject(jsonStr);
				}
			} catch (Exception e) {
				Log.e(this.getClass().getSimpleName(), ExceptionUtils.getStackTrace(e));
			}

			Message msg = new Message();
			Bundle data = new Bundle();
			data.putString("value", "请求结果");
			msg.setData(data);
			httpResponseHandler.sendMessage(msg);
		}
	};

}
