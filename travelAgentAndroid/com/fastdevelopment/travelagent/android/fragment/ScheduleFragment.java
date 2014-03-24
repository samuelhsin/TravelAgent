package com.fastdevelopment.travelagent.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fastdevelopment.travelagent.android.R;
import com.google.android.gms.maps.GoogleMap;

public class ScheduleFragment extends Fragment {

	private GoogleMap map;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_schedule, container, false);

		// map = ((SupportMapFragment)
		// getFragmentManager().findFragmentById(R.id.support_map_fragment)).getMap();
		String directionUrl = "http://maps.googleapis.com/maps/api/directions/json?origin=Chicago,IL&destination=Los+Angeles,CA&waypoints=Joplin,MO|Oklahoma+City,OK&sensor=false&key=%s";
		String googleMapApiKey = getResources().getString(R.string.google_map_api_v2_api_key);
		String.format(directionUrl, googleMapApiKey);
		
		return v;
	}

}
