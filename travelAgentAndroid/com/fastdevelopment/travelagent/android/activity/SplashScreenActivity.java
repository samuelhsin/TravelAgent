package com.fastdevelopment.travelagent.android.activity;

import java.sql.SQLException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

import com.fastdevelopment.travelagent.android.R;
import com.fastdevelopment.travelagent.android.common.ServerConfig;
import com.fastdevelopment.travelagent.android.orm.DatabaseHelper;
import com.fastdevelopment.travelagent.android.orm.model.User;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

public class SplashScreenActivity extends Activity {

	/**
	 * The thread to process splash screen events
	 */
	private Thread mSplashThread;

	private DatabaseHelper databaseHelper = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		// init ServerConfig
		ServerConfig.init(getResources());

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

					User user = getSelf();
					if (user.isFirstLoad()) {
						startActivity(new Intent("com.fastdevelopment.travelagent.android.activity.WelcomeActivity"));
					} else {
						startActivity(new Intent("com.fastdevelopment.travelagent.android.activity.MainActivity"));
					}

				}
			}
		};
		mSplashThread.start();
	}

	private User getSelf() {

		User user = null;

		try {
			Dao<User, Integer> userDao = getDBHelper().getDao(User.class);
			List<User> users = userDao.queryForAll();
			if (users != null && !users.isEmpty()) {
				// just one user
				user = users.get(0);
				user.setFirstLoad(false);
				userDao.update(user);
			} else {
				user = new User();
				user.setFirstLoad(true);
				userDao.create(user);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return user;
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
