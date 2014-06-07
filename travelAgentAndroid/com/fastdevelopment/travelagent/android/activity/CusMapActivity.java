package com.fastdevelopment.travelagent.android.activity;

import java.util.ArrayList;

import org.apache.commons.lang.exception.ExceptionUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.fastdevelopment.travelagent.android.R;
import com.fastdevelopment.travelagent.android.common.ServerConfig;
import com.fastdevelopment.travelagent.android.common.ServerConstants;
import com.fastdevelopment.travelagent.android.common.ServerConstants.CountryCode;
import com.fastdevelopment.travelagent.android.common.ServerConstants.FragmentEvent;
import com.fastdevelopment.travelagent.android.common.ServerConstants.FragmentIndex;
import com.fastdevelopment.travelagent.android.common.ServerConstants.ICountryLatLng;
import com.fastdevelopment.travelagent.android.common.ServerConstants.IIntentDataKey;
import com.fastdevelopment.travelagent.android.thirdparty.data.GoogleDistanceMetrix;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class CusMapActivity extends Activity implements OnMarkerDragListener {

	private static final String TAG = CusMapActivity.class.getSimpleName();
	private GoogleMap map;
	private Context context;
	private Resources resources;
	private Marker mapMarker;
	private ArrayList<String> places;
	private CusMapActivity activity;
	private String startCountryCode;
	private String endCountryCode;
	private int planId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		try {

			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_mapv2);

			map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapv2)).getMap();

			if (map != null) {

				activity = this;

				context = this;

				resources = ServerConfig.resources;

				loadIntentData();

				LatLng country;

				country = getCountryLatLng(startCountryCode);

				mapMarker = map.addMarker(new MarkerOptions().position(country).title(resources.getString(R.string.ti_marker)).draggable(true));

				// Move the camera instantly to country with a zoom of 5.
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(country, 5));

				map.setOnMarkerDragListener(this);

			}

		} catch (Exception e) {
			Log.e(TAG, ExceptionUtils.getStackTrace(e));
		}

	}

	private LatLng getCountryLatLng(String countryCodeStr) throws Exception {

		CountryCode countryCode = CountryCode.valueOf(countryCodeStr);

		LatLng countryLatLng = null;

		switch (countryCode) {
		case US:
			countryLatLng = ICountryLatLng.US;
			break;
		case FR:
			countryLatLng = ICountryLatLng.FR;
			break;
		default:
		}

		return countryLatLng;
	}

	private boolean loadIntentData() {

		try {

			Bundle extraBundle = this.getIntent().getExtras();

			if (extraBundle == null) {
				return false;
			}

			GoogleDistanceMetrix googleDistanceMetrix = (GoogleDistanceMetrix) extraBundle.get(IIntentDataKey.GOOGLE_DISTANCE_METRIX);

			if (googleDistanceMetrix != null) {
				places = new ArrayList<String>();
				places.addAll(googleDistanceMetrix.getOrigin_addresses());
			}

			startCountryCode = extraBundle.getString(IIntentDataKey.START_COUNTRY_CODE);
			endCountryCode = extraBundle.getString(IIntentDataKey.END_COUNTRY_CODE);
			
			planId = extraBundle.getInt(IIntentDataKey.PLAN_ID);
			
			return true;
		} catch (Exception e) {
			Log.e(TAG, ExceptionUtils.getStackTrace(e));
			return false;
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

		loadIntentData();
	}

	@Override
	public void onMarkerDrag(Marker marker) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMarkerDragEnd(final Marker marker) {

		Builder comfirm = new AlertDialog.Builder(context);
		comfirm.setTitle(R.string.ti_add_palce);
		comfirm.setMessage(R.string.add_place_confirm);
		comfirm.setIcon(android.R.drawable.ic_dialog_alert);
		comfirm.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				try {

					String newPlace = marker.getPosition().latitude + "," + marker.getPosition().longitude;

					if (places != null && places.size() > 0) {
						places.add(1, newPlace);
					} else {
						places = new ArrayList<String>();
						places.add(newPlace);
						places.add(newPlace);
					}

					// reopen old activity
					// Intent intent = new Intent(context, MainActivity.class);
					// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					Intent intent = new Intent();
					intent.putStringArrayListExtra(IIntentDataKey.PLACES, places);
					intent.putExtra(IIntentDataKey.START_FRAGMENT_INDEX, FragmentIndex.SCHEDULE);
					intent.putExtra(IIntentDataKey.FRAGMENT_EVENT_ID, FragmentEvent.SCHEDULE_NEW_PLACES);
					intent.putExtra(IIntentDataKey.START_COUNTRY_CODE, startCountryCode);
					intent.putExtra(IIntentDataKey.END_COUNTRY_CODE, endCountryCode);
					intent.putExtra(IIntentDataKey.PLAN_ID, planId);
					
					setResult(RESULT_OK, intent);
					finish();
					// activity.startActivityForResult(intent, IStartActivityRequestCode.PICK_PLACES);

				} catch (Exception e) {
					Log.e(TAG, ExceptionUtils.getStackTrace(e));
					Toast toast = Toast.makeText(context, resources.getString(R.string.add_failed), Toast.LENGTH_LONG);
					toast.show();
				}
			};
		});
		comfirm.setNegativeButton(R.string.no, null);
		comfirm.show();

	}

	@Override
	public void onMarkerDragStart(Marker marker) {
		// TODO Auto-generated method stub

	}

}
