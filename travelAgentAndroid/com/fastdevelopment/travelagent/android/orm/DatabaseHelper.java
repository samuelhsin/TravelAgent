package com.fastdevelopment.travelagent.android.orm;

import org.apache.commons.lang.exception.ExceptionUtils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.fastdevelopment.travelagent.android.orm.model.Plan;
import com.fastdevelopment.travelagent.android.orm.model.User;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static final String DATABASE_NAME = "ormlite.db";
	private static final int DATABASE_VERSION = 2;
	private static final String TAG = DatabaseHelper.class.getSimpleName();

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {

			// create tables
			TableUtils.createTable(connectionSource, User.class);
			TableUtils.createTable(connectionSource, Plan.class);

		} catch (Exception e) {
			Log.e(TAG, ExceptionUtils.getStackTrace(e));
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int arg2, int arg3) {
		try {
	        Log.i(DatabaseHelper.class.getName(), "onUpgrade");
	        TableUtils.dropTable(connectionSource, User.class, true);
	        // after we drop the old databases, we create the new ones
	        onCreate(db, connectionSource);
	    } catch (Exception e) {
	        Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
	        throw new RuntimeException(e);
	    }

	}

}
