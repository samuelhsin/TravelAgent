package com.fastdevelopment.travelagent.android.fragment;

import com.fastdevelopment.travelagent.android.R;
import com.fastdevelopment.travelagent.android.R.layout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NewsFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_news, container, false);

		return v;
	}
}
