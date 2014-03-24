package com.fastdevelopment.travelagent.android.activity;

import android.app.Activity;
import android.os.Bundle;

import com.fastdevelopment.travelagent.android.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends Activity {

	static final LatLng NKUT = new LatLng(23.979548, 120.696745);
	private GoogleMap map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapv2)).getMap();
		if (map != null) {
			Marker nkut = map.addMarker(new MarkerOptions().position(NKUT).title("南開科技大學").snippet("數位生活創意系"));

			// Move the camera instantly to NKUT with a zoom of 16.
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(NKUT, 16));
		}

	}
}
