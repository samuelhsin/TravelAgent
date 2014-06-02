package com.fastdevelopment.travelagent.android.fragment;

import android.view.View;
import android.view.View.OnFocusChangeListener;

public interface IFragment extends OnFocusChangeListener {
	public void passValuesByFocus(int fragmentEventId, Object... objects) throws Exception;

	public View getView() throws Exception;
}
