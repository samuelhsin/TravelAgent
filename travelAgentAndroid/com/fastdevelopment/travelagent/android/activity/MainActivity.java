package com.fastdevelopment.travelagent.android.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.widget.LinearLayout;

import com.fastdevelopment.travelagent.android.R;
import com.fastdevelopment.travelagent.android.fragment.NewsFragment;
import com.fastdevelopment.travelagent.android.fragment.PlansFragment;
import com.fastdevelopment.travelagent.android.fragment.ScheduleFragment;
import com.fastdevelopment.travelagent.android.fragment.SettingsFragment;

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
		fragmentsList.add(new PlansFragment());
		fragmentsList.add(new NewsFragment());
		fragmentsList.add(new SettingsFragment());

		tags = new String[] { this.getResources().getString(R.string.schedule), this.getResources().getString(R.string.plan), this.getResources().getString(R.string.news),
				this.getResources().getString(R.string.settings) };

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
