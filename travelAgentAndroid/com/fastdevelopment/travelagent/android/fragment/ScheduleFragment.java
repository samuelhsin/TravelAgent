package com.fastdevelopment.travelagent.android.fragment;

import org.apache.commons.lang.exception.ExceptionUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.fastdevelopment.travelagent.android.R;
import com.fastdevelopment.travelagent.android.common.ThirdPartyHandler;
import com.google.android.gms.maps.GoogleMap;

public class ScheduleFragment extends Fragment {

	private GoogleMap map;
	private Handler httpResponseHandler;
	private Context context = this.getActivity();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_schedule, container, false);

		// map = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.support_map_fragment)).getMap();

		// init message handler
		initMessageHandler();

		Spinner spinner = (Spinner) v.findViewById(R.id.spinner_where);

		// 建立一個ArrayAdapter物件，並放置下拉選單的內容
		String[] values = { "Select Country", "Taiwan", "France", "United States" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), R.layout.simple_spinner_item, values);

		// 設定下拉選單的樣式
		adapter.setDropDownViewResource(R.layout.simple_spinner_item);
		spinner.setAdapter(adapter);

		// 設定項目被選取之後的動作
		spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
				// Toast.makeText(context, "您選擇" + adapterView.getSelectedItem().toString(), Toast.LENGTH_LONG).show();

				String selected = adapterView.getSelectedItem().toString();

				if (!selected.equals("Select Country")) {

					String countryCode = "tw";
					if (selected.equals("France")) {
						countryCode = "fr";
					} else if (selected.equals("United States")) {
						countryCode = "us";
					}

					try {
						ThirdPartyHandler tp = ThirdPartyHandler.getInstance();
						tp.invokeDistanceTimeEvent(httpResponseHandler, countryCode);
					} catch (Exception e) {
						Log.e(this.getClass().getSimpleName(), ExceptionUtils.getStackTrace(e));
					}
				}

			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// Toast.makeText(context, "您沒有選擇任何項目", Toast.LENGTH_LONG).show();
			}
		});

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
