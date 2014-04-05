package com.fastdevelopment.travelagent.android.common;

import java.text.DecimalFormat;

import android.util.Log;

public class ThirdPartyHandlerThread implements Runnable {

	private RequestQueue queue = null;
	private boolean active = false;
	private long retryTime = 10000;
	private DecimalFormat df = new DecimalFormat("#.##");
	private boolean getError = false;
	private boolean isWorkSuccess = true;
	private long id;
	private String threadName = "Third Party Handler Thread";
	private ThirdPartyHandlerWorkObject work;

	public ThirdPartyHandlerThread(RequestQueue queue) {
		this.queue = queue;
		this.active = true;
	}

	private void init(ThirdPartyHandlerWorkObject work) {
		this.work = work;
	}

	@Override
	public void run() {
		try {

			if (isWorkSuccess) {
				// get new work
				Request request = queue.get();
				init((ThirdPartyHandlerWorkObject) request.getData()[0]);
				isWorkSuccess = false;
				
			}
			if (work != null) {
				work.toDo();
			}
			
			//done for work
			getError = false;
			isWorkSuccess = true;

		} catch (Exception e) {

			getError = true;
			isWorkSuccess = false;

		} finally {

			try {

				if (getError || !isWorkSuccess) {
					// this is not success work, so retry after 10 sec.
					Log.e(this.getClass().getSimpleName(), "error happened, will retry by " + (df.format(retryTime / 1000) + " sec(s)."));
					Thread.sleep(retryTime);
				}

			} catch (Exception e) {

				Log.e(this.getClass().getSimpleName(), threadName + " ID: " + id + " error to sleep");
			}

			// recursive
			if (this.active) {
				run();
			}
		}

	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
