package com.fastdevelopment.travelagent.android.fragment;

import android.view.View.OnFocusChangeListener;

public interface IFragment extends OnFocusChangeListener {
	public void passValuesByFocus(Object... objects) throws Exception;
}
