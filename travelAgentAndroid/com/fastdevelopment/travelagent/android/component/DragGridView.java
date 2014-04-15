package com.fastdevelopment.travelagent.android.component;

import org.apache.commons.lang.exception.ExceptionUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

public class DragGridView extends GridView {

	private String TAG = this.getClass().getSimpleName();

	// 定义基本的成员变量
	private ImageView dragImageView; // 拖动的物件（以圖型方式catch）
	private int dragSrcPosition;// 原始对应位置
	private int dragDesPosition;// 拖动到坐标对应的位置

	// x,y坐标的计算
	private int dragPointX; // 按下坐标相对于当前项位置-- 相对
	private int dragPointY;
	private int dragOffsetX;// 当前窗体和屏幕的距离 --绝对
	private int dragOffsetY;
	private WindowManager windowManager;// 窗口控制类
	private WindowManager.LayoutParams windowParams; // 用于控制拖动项的显示参数

	// private int scaledTouchSlop; //判断滑动的距离
	private int upScrollBounce; // 拖动时候,开始向上滚动的边界
	private int downScrollBounce;// 拖动时候,开始向下滚动的边界

	public DragGridView(Context context) {
		super(context);
	}

	public DragGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public DragGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	// 触控拦截事件
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			int x = (int) ev.getX();
			int y = (int) ev.getY();

			// 选中数据项位置 ,
			dragSrcPosition = dragDesPosition = pointToPosition(x, y);
			if (dragDesPosition == AdapterView.INVALID_POSITION) {// 无效位置(超出边蛸,分割线)
				return super.onInterceptTouchEvent(ev);
			}
			Log.i(TAG, "[onInterceptTouchEvent] dragSrcPosition:" + dragSrcPosition + ",getFirstVisiblePosition():" + getFirstVisiblePosition());
			// getFirstVisiblePosition()返回第一个display在界面的view在adapter的位置 可能是0,也可能是4
			ViewGroup itemView = (ViewGroup) getChildAt(dragDesPosition - getFirstVisiblePosition());
			// 计算按下的坐标相对当前项的位置
			dragPointX = x - itemView.getLeft();// 在当前项的X位置
			dragPointY = y - itemView.getTop();
			// 当前窗体和屏幕的距离
			dragOffsetX = (int) (ev.getRawX() - x);
			dragOffsetY = (int) (ev.getRawY() - y);
			Log.i(TAG, "[onInterceptTouchEvent] [x:" + x + ",y:" + y + "],[rawX:" + ev.getRawX() + ",rawY:" + ev.getRawY() + "]");
			Log.i(TAG, "[onInterceptTouchEvent] [dragPointX:" + dragPointX + ",dragPointY:" + dragPointY + "],[dragOffsetX:" + dragOffsetX + ",dragOffsetY:" + dragOffsetY + "]");
			//
			// upScrollBounce = Math.min(y-scaledTouchSlop, getHeight()/4);
			// downScrollBounce = Math.max(y+scaledTouchSlop, getHeight()*3/4);

			upScrollBounce = Math.min(y, getHeight() / 4);// 向上可以滚动的距离
			downScrollBounce = Math.max(y, getHeight() * 3 / 4);// 向下可以滚动的距离

			itemView.setDrawingCacheEnabled(true);
			Bitmap bm = Bitmap.createBitmap(itemView.getDrawingCache());
			startDrag(bm, x, y);
		}

		return super.onInterceptTouchEvent(ev);

	}

	// 开始拖动
	@SuppressWarnings("static-access")
	public void startDrag(Bitmap bm, int x, int y) {
		stopDrag();
		windowParams = new WindowManager.LayoutParams();
		windowParams.gravity = Gravity.TOP | Gravity.LEFT;
		windowParams.x = x - dragPointX + dragOffsetX;// 计算当前项Left离窗体的距离
		windowParams.y = y - dragPointY + dragOffsetY;// 计算当前项Top离窗体的距离

		windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;

		windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

		windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

		windowParams.format = PixelFormat.TRANSLUCENT;

		windowParams.windowAnimations = 0;

		ImageView imageView = new ImageView(getContext());
		imageView.setImageBitmap(bm);
		windowManager = (WindowManager) getContext().getSystemService(getContext().WINDOW_SERVICE);
		windowManager.addView(imageView, windowParams);
		dragImageView = imageView;
	}

	// 停止拖到
	public void stopDrag() {
		if (dragImageView != null) {
			windowManager.removeView(dragImageView);
			dragImageView = null;
		}
	}

	// 拖动
	public void onDrag(MotionEvent ev) {

		int x = (int) ev.getX();
		int y = (int) ev.getY();

		if (dragImageView != null) {
			windowParams.alpha = 0.9f;
			windowParams.x = x - dragPointX + dragOffsetX;
			windowParams.y = y - dragPointY + dragOffsetY;
			windowManager.updateViewLayout(dragImageView, windowParams);

		}
		int tempPosition = pointToPosition(x, y);
		if (tempPosition != INVALID_POSITION) {
			dragDesPosition = tempPosition;
		}

		if (y < upScrollBounce || y > downScrollBounce) {
			setSelection(dragDesPosition);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (dragImageView != null && dragDesPosition != INVALID_POSITION) {
			int action = ev.getAction();
			switch (action) {
			case MotionEvent.ACTION_UP:
				int upx = (int) ev.getX();
				int upY = (int) ev.getY();
				stopDrag();
				onDrop(ev);
				break;
			case MotionEvent.ACTION_MOVE:
				onDrag(ev);
				break;
			}
			return true;
		}

		return super.onTouchEvent(ev);
	}

	protected void onDropInExceedTop() {
		dragDesPosition = 0;
	}

	protected void onDropInExceedButtom() {
		dragDesPosition = getAdapter().getCount() - 1;
	}

	protected void onDropInOverDiffObject() {

	}

	// 拖到落下
	public void onDrop(MotionEvent ev) {

		try {

			int x = (int) ev.getX();
			int y = (int) ev.getY();

			int tempPosition = pointToPosition(x, y);
			if (tempPosition != INVALID_POSITION) {
				dragDesPosition = tempPosition;
			}
			// 超出上边界
			if (y < getChildAt(0).getTop()) {
				onDropInExceedTop();

			} else if (y > getChildAt(getChildCount() - 1).getBottom() || (y > getChildAt(getChildCount() - 1).getTop() && x > getChildAt(getChildCount() - 1).getRight())) {
				// 超出下边界
				onDropInExceedButtom();
			}

			// 数据交换当前拖动的于拖到到位置上的图片交换
			if (dragDesPosition != -1 && dragDesPosition != dragSrcPosition && dragDesPosition > -1 && dragDesPosition < getAdapter().getCount()) {
				onDropInOverDiffObject();
			}
		} catch (Exception e) {
			Log.e(TAG, ExceptionUtils.getStackTrace(e));
		}
	}

	protected double calDistance(int x1, int y1, int x2, int y2) {
		return Math.sqrt(Math.abs(x1 - x2) * Math.abs(x1 - x2) + Math.abs(y1 - y2) * Math.abs(y1 - y2));
	}

	public int getDragSrcPosition() {
		return dragSrcPosition;
	}

	public void setDragSrcPosition(int dragSrcPosition) {
		this.dragSrcPosition = dragSrcPosition;
	}

	public int getDragDesPosition() {
		return dragDesPosition;
	}

	public void setDragDesPosition(int dragDesPosition) {
		this.dragDesPosition = dragDesPosition;
	}

}
