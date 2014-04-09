package com.fastdevelopment.travelagent.android.fragment;

import org.apache.commons.lang.exception.ExceptionUtils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.fastdevelopment.travelagent.android.R;
import com.fastdevelopment.travelagent.android.common.ThirdPartyHandler;
import com.google.android.gms.maps.GoogleMap;

public class ScheduleFragment extends Fragment {

	private ProgressBar progressBar;
	private GoogleMap map;
	private Handler httpResponseHandler;
	private Context context = this.getActivity();
	private View thisView;
	private LinearLayout thisLinearLayout;
	private int shortAnimationDuration = 1000;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		thisView = inflater.inflate(R.layout.fragment_schedule, container, false);

		// map = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.support_map_fragment)).getMap();

		// init message handler
		initMessageHandler();
		
		thisLinearLayout = (LinearLayout) thisView.findViewById(R.id.llt_fragment_schedule);
		final Spinner spinner = (Spinner) thisView.findViewById(R.id.spinner_where);
		progressBar = (ProgressBar) thisView.findViewById(R.id.progressBar_in_fragment_schedule);
		progressBar.setVisibility(View.GONE);

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
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// Toast.makeText(context, "您沒有選擇任何項目", Toast.LENGTH_LONG).show();
			}
		});

		Button btnSchedule = (Button) thisView.findViewById(R.id.btn_schedule);

		btnSchedule.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {

				String selectedCountry = (String) spinner.getSelectedItem();

				if (!selectedCountry.equals("Select Country")) {
					load(true);
					String countryCode = "tw";
					if (selectedCountry.equals("France")) {
						countryCode = "fr";
					} else if (selectedCountry.equals("United States")) {
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

		});

		return thisView;
	};

	protected void load(boolean isLoad) {

		if (isLoad) {
			fade(false, this.thisLinearLayout);
			fade(true, this.progressBar);
		} else {
			fade(false, this.progressBar);
			fade(true, this.thisLinearLayout);
		}

	}

	protected void fade(boolean in, final View v) {
		if (in) {
			// Set the content view to 0% opacity but visible, so that it is visible
			// (but fully transparent) during the animation.
			v.setAlpha(0f);
			v.setVisibility(View.VISIBLE);

			// Animate the content view to 100% opacity, and clear any animation
			// listener set on the view.
			v.animate().alpha(1f).setDuration(shortAnimationDuration).setListener(null);

		} else {
			// Animate the loading view to 0% opacity. After the animation ends,
			// set its visibility to GONE as an optimization step (it won't
			// participate in layout passes, etc.)
			v.animate().alpha(0f).setDuration(shortAnimationDuration).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					v.setVisibility(View.GONE);
				}
			});
		}
	}

	@SuppressLint("HandlerLeak")
	protected void initMessageHandler() {
		httpResponseHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Bundle data = msg.getData();
				String val = data.getString("result");
				Log.i("mylog", "请求结果-->" + val);
				load(false);
			}
		};
	}

}
