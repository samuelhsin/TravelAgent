package com.fastdevelopment.travelagent.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class WelcomeActivity extends Activity implements OnPageChangeListener {

	/**
	 * ViewPager
	 */
	private ViewPager viewPager;

	/**
	 * 装点点的ImageView数组
	 */
	private ImageView[] tips;

	/**
	 * 装ImageView数组
	 */
	private RelativeLayout[] layouts;

	/**
	 * 图片资源id
	 */
	private int[] imgIdArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		ViewGroup group = (ViewGroup) findViewById(R.id.viewGroup);
		viewPager = (ViewPager) findViewById(R.id.viewPager);

		// 载入图片资源ID
		imgIdArray = new int[] { R.drawable.welcome1, R.drawable.welcome2, R.drawable.welcome3, R.drawable.welcome4 };

		// 将点点加入到ViewGroup中
		tips = new ImageView[imgIdArray.length];
		for (int i = 0; i < tips.length; i++) {
			ImageView imageView = new ImageView(this);
			imageView.setLayoutParams(new LayoutParams(10, 10));
			tips[i] = imageView;
			if (i == 0) {
				tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
			} else {
				tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
			}

			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			layoutParams.leftMargin = 5;
			layoutParams.rightMargin = 5;
			group.addView(imageView, layoutParams);
		}

		// 将图片装载到数组中
		layouts = new RelativeLayout[imgIdArray.length];
		for (int i = 0; i < layouts.length; i++) {
			RelativeLayout layout = new RelativeLayout(this);
			layouts[i] = layout;
			ImageView imgView = new ImageView(this);
			layout.addView(imgView);
			imgView.setBackgroundResource(imgIdArray[i]);
			if (i == (layouts.length - 1)) {
				// last one
				final ImageView imgButton = new ImageView(this);
				imgButton.setBackgroundResource(R.drawable.btn_into_main_up);
				imgButton.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View arg0, MotionEvent arg1) {
						switch (arg1.getAction()) {
						case MotionEvent.ACTION_DOWN: {
							imgButton.setBackgroundResource(R.drawable.btn_into_main_down);

							redirectToMainActivity();

							break;
						}
						case MotionEvent.ACTION_CANCEL: {
							imgButton.setBackgroundResource(R.drawable.btn_into_main_up);
							break;
						}
						}
						return true;
					}
				});

				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
				params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
				params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
				params.bottomMargin = 150;
				layout.addView(imgButton, params);
			}
		}

		// 设置Adapter
		viewPager.setAdapter(new WelcomePageAdapter(layouts));
		// 设置监听，主要是设置点点的背景
		viewPager.setOnPageChangeListener(this);

		viewPager.setCurrentItem(0);

	}

	protected void redirectToMainActivity() {

		Handler handler = new Handler();

		// sleep 2 sec
		handler.postDelayed(new Runnable() {
			public void run() {
				finish();
				startActivity(new Intent("com.fastdevelopment.travelagent.android.MainActivity"));
			}
		}, 2000);

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		setImageBackground(arg0 % layouts.length);
	}

	/**
	 * 设置选中的tip的背景
	 * 
	 * @param selectItems
	 */
	private void setImageBackground(int selectItems) {
		for (int i = 0; i < tips.length; i++) {
			if (i == selectItems) {
				tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
			} else {
				tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
			}
		}
	}

}
