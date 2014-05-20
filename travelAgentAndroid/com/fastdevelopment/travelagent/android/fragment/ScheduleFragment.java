package com.fastdevelopment.travelagent.android.fragment;

import java.util.List;

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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.fastdevelopment.travelagent.android.R;
import com.fastdevelopment.travelagent.android.common.PlaceTimeFactory;
import com.fastdevelopment.travelagent.android.common.ServerConstants.CountryCode;
import com.fastdevelopment.travelagent.android.common.ServerConstants.IBundleDataKey;
import com.fastdevelopment.travelagent.android.model.IPojoModel;
import com.fastdevelopment.travelagent.android.thirdparty.ThirdPartyHandler;
import com.fastdevelopment.travelagent.android.thirdparty.data.GoogleDistanceMetrix;
import com.fastdevelopment.travelagent.android.view.ScheduleGridAdapter;
import com.fastdevelopment.travelagent.android.view.ScheduleGridView;
import com.google.android.gms.maps.GoogleMap;

public class ScheduleFragment extends Fragment implements IFragment {

	private String TAG = this.getClass().getSimpleName();
	private ProgressBar progressBar;
	private GoogleMap map;
	private Handler httpResponseHandler;
	private Context context;
	private FrameLayout wholeView;
	private LinearLayout formView;
	private int shortAnimationDuration = 1000;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		context = this.getActivity();

		wholeView = (FrameLayout) inflater.inflate(R.layout.fragment_schedule, container, false);
		formView = (LinearLayout) wholeView.findViewById(R.id.llt_fragment_schedule);
		// map = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.support_map_fragment)).getMap();

		// init message handler
		initMessageHandler();

		final Spinner spinner = (Spinner) wholeView.findViewById(R.id.spinner_where);
		progressBar = (ProgressBar) wholeView.findViewById(R.id.progressBar_in_fragment_schedule);
		progressBar.setVisibility(View.GONE);

		// 建立一個ArrayAdapter物件，並放置下拉選單的內容
		String[] values = { context.getResources().getString(R.string.select_country), CountryCode.TW.getCountryName(), CountryCode.FR.getCountryName(), CountryCode.US.getCountryName() };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), R.layout.simple_spinner_item, values);

		// 設定下拉選單的樣式
		adapter.setDropDownViewResource(R.layout.simple_spinner_item);
		spinner.setAdapter(adapter);

		// 設定項目被選取之後的動作
		spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

				Object selected = adapterView.getSelectedItem();

				if (context != null && selected != null) {
					if (!selected.toString().equals(context.getResources().getString(R.string.select_country))) {
						Toast.makeText(context, context.getResources().getString(R.string.you_selected) + " " + adapterView.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
					}
				}
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				if (context != null) {
					Toast.makeText(context, context.getResources().getString(R.string.no_item_selected), Toast.LENGTH_LONG).show();
				}
			}
		});

		Button btnSchedule = (Button) wholeView.findViewById(R.id.btn_schedule);

		btnSchedule.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {

				String selectedCountry = (String) spinner.getSelectedItem();

				if (!selectedCountry.equals(context.getResources().getString(R.string.select_country))) {
					load(true);
					CountryCode countryCode = CountryCode.TW;
					if (selectedCountry.equals(CountryCode.FR.getCountryName())) {
						countryCode = CountryCode.FR;
					} else if (selectedCountry.equals(CountryCode.US.getCountryName())) {
						countryCode = CountryCode.US;
					}

					try {
						ThirdPartyHandler tp = ThirdPartyHandler.getInstance();
						tp.invokeDistanceTimeEvent(httpResponseHandler, countryCode);
					} catch (Exception e) {
						Log.e(TAG, ExceptionUtils.getStackTrace(e));
					}
				} else {
					Toast.makeText(context, context.getResources().getString(R.string.no_item_selected), Toast.LENGTH_LONG).show();
				}

			}

		});

		// on fragment focus

		return wholeView;
	};

	protected void load(boolean isLoad) {

		if (isLoad) {
			fade(false, this.formView);
			fade(true, this.progressBar);
		} else {
			fade(false, this.progressBar);
			fade(true, this.formView);
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

	public void loadScheduleInput() throws Exception {

		wholeView.removeViewAt(0);

		wholeView.addView(formView, 0);

	}

	public void loadScheduleResult(int planId, GoogleDistanceMetrix result) throws Exception {

		wholeView.removeViewAt(0);

		// LinearLayout
		View scheduleView = LayoutInflater.from(this.context).inflate(R.layout.layout_schedule, null);

		ScheduleGridView scheduleGridView = (ScheduleGridView) scheduleView.findViewById(R.id.schedule_grid);
		scheduleGridView.setPlanId(planId);
		scheduleGridView.setParentView(scheduleView);
		scheduleGridView.setFragment(this);

		// init schedule list
		List<IPojoModel> modelList = PlaceTimeFactory.calculatePlaceTimePath(result);
		ScheduleGridAdapter adapter = new ScheduleGridAdapter(this.context, modelList, result);
		scheduleGridView.setAdapter(adapter);

		wholeView.addView(scheduleView, 0);

	}

	@SuppressLint("HandlerLeak")
	protected void initMessageHandler() {
		httpResponseHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Bundle data = msg.getData();
				if (data != null) {
					GoogleDistanceMetrix result = (GoogleDistanceMetrix) data.getSerializable(IBundleDataKey.GOOGLE_DISTANCE_METRIX);
					Log.i(TAG, "scheduling from third party return status-->" + result.getStatus());
					load(false);
					try {
						// planId: -1 , is not save into db.
						loadScheduleResult(-1, result);
					} catch (Exception e) {
						Log.e(TAG, ExceptionUtils.getStackTrace(e));
					}
				} else {
					Toast.makeText(context, context.getResources().getString(R.string.return_error), Toast.LENGTH_LONG).show();
				}

			}
		};
	}

	protected void reloadView() {

	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus) {
			if (wholeView.getChildAt(0) != formView) {
				wholeView.removeViewAt(0);
				wholeView.addView(formView, 0);
			}
		}
	}

	@Override
	public void passValuesByFocus(Object... objects) throws Exception {
		if (objects != null) {
			int planId = (Integer) objects[0];
			GoogleDistanceMetrix googleDistanceMetrix = (GoogleDistanceMetrix) objects[1];
			loadScheduleResult(planId, googleDistanceMetrix);
		}

	}

}
