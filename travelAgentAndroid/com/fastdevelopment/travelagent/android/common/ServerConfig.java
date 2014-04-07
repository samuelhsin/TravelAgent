package com.fastdevelopment.travelagent.android.common;

import android.content.res.Resources;

public abstract class ServerConfig {

	public static Resources resource;

	public final static void init(Resources resource) {
		ServerConfig.resource = resource;
	}

}
