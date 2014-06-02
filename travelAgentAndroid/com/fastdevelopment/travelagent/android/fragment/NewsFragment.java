package com.fastdevelopment.travelagent.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fastdevelopment.travelagent.android.R;

public class NewsFragment extends Fragment implements IFragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_news, container, false);

		return v;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub

	}

	@Override
	public void passValuesByFocus(int fragmentEventId, Object... objects) throws Exception {
		// TODO Auto-generated method stub

	}
}
