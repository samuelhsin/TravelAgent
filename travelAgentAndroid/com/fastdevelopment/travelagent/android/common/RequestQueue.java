package com.fastdevelopment.travelagent.android.common;

import java.util.LinkedList;

import org.apache.commons.lang.exception.ExceptionUtils;

import android.util.Log;

public class RequestQueue {
	private final LinkedList<Request> requests = new LinkedList<Request>();

	public synchronized Request get() {
		while (requests.size() == 0) {
			try {
				wait();
			} catch (Exception e) {
				Log.e(this.getClass().getSimpleName(), ExceptionUtils.getStackTrace(e));
			}
		}
		return requests.removeFirst();
	}

	public synchronized void put(Request request) {
		requests.addLast(request);
		notifyAll();
	}

}
