package com.fastdevelopment.travelagent.android.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.fastdevelopment.travelagent.android.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class CusMapActivity extends Activity implements OnMarkerDragListener {

	static final LatLng NKUT = new LatLng(23.979548, 120.696745);
	private GoogleMap map;
	private Context context;
	private Marker mapMarker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mapv2);

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapv2)).getMap();

		if (map != null) {

			context = this.getBaseContext();

			mapMarker = map.addMarker(new MarkerOptions().position(NKUT).title("南開科技大學").snippet("數位生活創意系").draggable(true));

			// Move the camera instantly to NKUT with a zoom of 16.
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(NKUT, 16));

			map.setOnMarkerDragListener(this);
		}

	}

	@Override
	public void onMarkerDrag(Marker marker) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMarkerDragEnd(Marker marker) {

		Toast.makeText(this.context, marker.getPosition().latitude + ", " + marker.getPosition().longitude, Toast.LENGTH_LONG).show();

	}

	@Override
	public void onMarkerDragStart(Marker marker) {
		// TODO Auto-generated method stub

	}

}
