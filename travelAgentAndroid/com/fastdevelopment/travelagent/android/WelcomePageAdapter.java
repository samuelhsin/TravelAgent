package com.fastdevelopment.travelagent.android;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout;

public class WelcomePageAdapter extends PagerAdapter {

	/**
	 * 装ImageView数组
	 */
	private RelativeLayout[] layouts;

	public WelcomePageAdapter(RelativeLayout[] layouts) {
		super();
		this.layouts = layouts;
	}

	@Override
	public int getCount() {
		return layouts.length;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView(layouts[position % layouts.length]);

	}

	/**
	 * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
	 */
	@Override
	public Object instantiateItem(View container, int position) {
		try {
			((ViewPager) container).addView(layouts[position % layouts.length], 0);
		} catch (Exception e) {

		}
		return layouts[position % layouts.length];
	}

}
