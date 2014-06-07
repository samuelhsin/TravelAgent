package com.fastdevelopment.travelagent.android.fragment;

import android.view.View;

public interface IFragment {
	public void passValuesByFocus(int fragmentEventId, Object... objects) throws Exception;

	public View getView() throws Exception;
}
