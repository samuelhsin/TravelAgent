package com.fastdevelopment.travelagent.android;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.widget.LinearLayout;

public class MainActivity extends FragmentActivity {

	private LinearLayout llTabSwipPager;
	private TabSwipPager tabSwipPager;
	private List<Fragment> fragmentsList;
	private String[] tags;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}

	private void initView() {
		initData();

		llTabSwipPager = (LinearLayout) findViewById(R.id.llTabSwipPager);

		tabSwipPager = new TabSwipPager(getApplicationContext(), getSupportFragmentManager(), llTabSwipPager);
		if (!tabSwipPager.setFragmentList(fragmentsList, tags)) {
			System.out.println("初始化失敗");
			finish();
		}
	}

	private void initData() {
		fragmentsList = new ArrayList<Fragment>();
		fragmentsList.add(new ScheduleFragment());
		fragmentsList.add(new Fragment());
		fragmentsList.add(new Fragment());
		fragmentsList.add(new Fragment());

		tags = new String[] { "Schedule", "Plans", "News", "Settings" };

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
