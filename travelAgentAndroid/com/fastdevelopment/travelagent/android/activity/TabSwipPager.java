package com.fastdevelopment.travelagent.android.activity;

import java.util.List;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;

import com.fastdevelopment.travelagent.android.component.CusViewPager;

public class TabSwipPager {

	private CustomViewManager customView;

	private Context context;
	private LinearLayout parentView, llTab;
	private FragmentManager fm;
	private CusViewPager viewPager;
	private CusFramePagerAdapter pagerAdapter;

	public TabSwipPager(Context context, FragmentManager fm, LinearLayout parentView) {
		this.context = context;
		this.fm = fm;
		this.parentView = parentView;
	}

	public boolean changeFragement(int index, int fragmentEventId, Object... objects) throws Exception {
		return pagerAdapter.changeFragement(index, fragmentEventId, objects);
	}

	public boolean setFragmentList(List<Fragment> fragmentsList, String[] tags) {
		if (tags.length != fragmentsList.size()) {
			return false;
		}

		customView = new CustomViewManager(context, tags);
		llTab = customView.getTabView();
		viewPager = (CusViewPager) customView.getViewPager();

		pagerAdapter = new CusFramePagerAdapter(fm, fragmentsList, viewPager, llTab);
		viewPager.setAdapter(pagerAdapter);

		parentView.addView(customView.getCustomTabView(), new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		return true;
	}

}
