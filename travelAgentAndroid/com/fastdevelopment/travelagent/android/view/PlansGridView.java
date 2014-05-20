package com.fastdevelopment.travelagent.android.view;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

import com.fastdevelopment.travelagent.android.activity.MainActivity;
import com.fastdevelopment.travelagent.android.common.ServerConfig;
import com.fastdevelopment.travelagent.android.fragment.PlansFragment;

public class PlansGridView extends GridView {

	private PlansGridView instance = this;
	protected View parentView;
	protected Resources resource = ServerConfig.resource;
	protected PlansFragment fragment;
	protected MainActivity activity;

	public PlansGridView(Context context) {
		super(context);
		init(context);
	}

	public PlansGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public PlansGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	protected void init(Context context) {
		activity = (MainActivity) context;
	}
}
