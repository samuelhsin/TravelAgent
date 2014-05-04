package com.fastdevelopment.travelagent.android.activity;

import java.util.List;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.fastdevelopment.travelagent.android.component.CusViewPager;
import com.fastdevelopment.travelagent.android.fragment.IFragment;

public class CusFramePagerAdapter extends FragmentPagerAdapter implements OnPageChangeListener, OnClickListener {
	private List<Fragment> fragmentsList;
	private CusViewPager viewPager;
	private LinearLayout llTab;

	public CusFramePagerAdapter(FragmentManager fm, List<Fragment> fragments, CusViewPager viewPager, LinearLayout llTab) {
		super(fm);
		this.fragmentsList = fragments;
		this.viewPager = viewPager;
		this.llTab = llTab;

		this.viewPager.setOnPageChangeListener(this);

		for (int i = 0; i < llTab.getChildCount(); i++) {
			llTab.getChildAt(i).setOnClickListener(this);
		}
	}

	public boolean changeFragement(int index) {

		viewPager.setCurrentItem(index);
		
		return true;
	}

	@Override
	public Fragment getItem(int position) {

		// Fragment fragment = new DummySectionFragment();
		// Bundle args = new Bundle();
		// args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position +
		// 1);
		// fragment.setArguments(args);

		return fragmentsList.get(position);
	}

	@Override
	public int getCount() {
		return fragmentsList.size();
	}

	// ******************OnPageChangeListener***************
	@Override
	public void onPageScrollStateChanged(int state) {
		System.out.println("onPageScrollStateChanged:" + state);
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		System.out.println("onPageScrolled:" + position + ">>" + positionOffset + ">>" + positionOffsetPixels);
	}

	@Override
	public void onPageSelected(int position) {
		System.out.println("onPageSelected:" + position);
		for (int i = 0; i < llTab.getChildCount(); i++) {
			if (position == i) {
				llTab.getChildAt(i).setBackgroundColor(Color.YELLOW);
			} else {
				llTab.getChildAt(i).setBackgroundColor(Color.BLUE);
			}
		}
	}

	// ******************OnClickListener***************

	@Override
	public void onClick(View view) {
		for (int i = 0; i < llTab.getChildCount(); i++) {

			if (view.equals(llTab.getChildAt(i))) {

				llTab.getChildAt(i).setBackgroundColor(Color.YELLOW);

				IFragment loseFocusfragment = (IFragment) getItem(viewPager.getCurrentItem());
				loseFocusfragment.onFocusChange(view, false);

				viewPager.setCurrentItem(i);

				IFragment focusfragment = (IFragment) getItem(i);
				focusfragment.onFocusChange(view, true);

			} else {
				llTab.getChildAt(i).setBackgroundColor(Color.BLUE);
			}
		}
	}

}
