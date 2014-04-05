package com.fastdevelopment.travelagent.android.common;

public class Request {

	private Object[] data;

	public Request(Object... data) {
		this.setData(data);
	}

	public Object[] getData() {
		return data;
	}

	public void setData(Object[] data) {
		this.data = data;
	}

}
