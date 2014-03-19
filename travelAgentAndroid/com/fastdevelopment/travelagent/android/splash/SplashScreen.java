package com.fastdevelopment.travelagent.android.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

import com.fastdevelopment.travelagent.android.R;
import com.fastdevelopment.travelagent.android.orm.DatabaseHelper;
import com.j256.ormlite.android.apptools.OpenHelperManager;

public class SplashScreen extends Activity {

	/**
	 * The thread to process splash screen events
	 */
	private Thread mSplashThread;

	private DatabaseHelper databaseHelper = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		final SplashScreen sPlashScreen = this;

		mSplashThread = new Thread() {
			@Override
			public void run() {
				try {
					int waited = 0;
					while (waited < 5000) {
						sleep(100);
						waited += 100;
					}
				} catch (Exception e) {
					// do nothing
				} finally {
					finish();
					// Intent i = new Intent();
					// i.setClass(sPlashScreen, WelcomeActivity.class);
					// startActivity(i);
					startActivity(new Intent("com.fastdevelopment.travelagent.android.WelcomeActivity"));
				}
			}
		};
		mSplashThread.start();
	}

	/**
	 * Processes splash screen touch events
	 */
	@Override
	public boolean onTouchEvent(MotionEvent evt) {
		if (evt.getAction() == MotionEvent.ACTION_DOWN) {
			synchronized (mSplashThread) {
				mSplashThread.notifyAll();
			}
		}
		return true;
	}

	private DatabaseHelper getDBHelper() {
		if (databaseHelper == null) {
			databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
		}
		return databaseHelper;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (databaseHelper != null) {
			OpenHelperManager.releaseHelper();
			databaseHelper = null;
		}
	}

}
