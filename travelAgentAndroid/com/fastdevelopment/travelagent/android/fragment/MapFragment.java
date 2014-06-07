package com.fastdevelopment.travelagent.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fastdevelopment.travelagent.android.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements IFragment {

	/** The view. */
	private static View view;

	/** The g map. */
	private static GoogleMap gMap;

	/** The bangalore. */
	private final LatLng BANGALORE = new LatLng(12.971689, 77.594504);

	/** The zoom. */
	private float zoom = 11.0f;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}

		if (view != null) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null)
				parent.removeView(view);
		}
		try {
			view = inflater.inflate(R.layout.activity_mapv1, container, false);
			setUpMapIfNeeded();
		} catch (InflateException e) {
			// Log.wtf("S*****", e.getMessage());
		}
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		gMap = null;
		if (gMap != null)
			setUpMap();

		if (gMap == null) {
			gMap = ((SupportMapFragment) this.getActivity().getSupportFragmentManager().findFragmentById(R.id.mapv2)).getMap();
			if (gMap != null)
				setUpMap();
		}
	}

	/***** Sets up the map if it is possible to do so *****/
	public void setUpMapIfNeeded() {

		if (gMap == null) {
			gMap = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.mapv2)).getMap();
			if (gMap != null)
				setUpMap();
		}
	}

	/**
	 * This is where we can add markers or lines, add listeners or move the camera. This should only be called once and when we are sure that {@link #gMap} is not null.
	 */
	private void setUpMap() {
		gMap.addMarker(new MarkerOptions().position(BANGALORE).title("My Home").snippet("Home Address"));
		gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(BANGALORE, zoom));
	}

	@Override
	public void passValuesByFocus(int fragmentEventId, Object... objects) throws Exception {
		// TODO Auto-generated method stub
	}

	/****
	 * The mapfragment's id must be removed from the FragmentManager or else if the same it is passed on the next time then app will crash
	 ****/
	// @Override
	// public void onDestroyView() {
	// super.onDestroyView();
	// FragmentManager fm = getActivity().getSupportFragmentManager();
	// Fragment fragment = (fm.findFragmentById(R.id.mapd));
	// FragmentTransaction ft = fm.beginTransaction();
	// ft.remove(fragment);
	// ft.commit();
	// }
}
