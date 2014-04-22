package com.fastdevelopment.travelagent.android.view;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fastdevelopment.travelagent.android.R;
import com.fastdevelopment.travelagent.android.component.DragGridView;
import com.fastdevelopment.travelagent.android.model.IModel;

public class ScheduleGridView extends DragGridView {

	private String TAG = this.getClass().getSimpleName();

	protected ImageView trashCan;
	protected LinearLayout parentLayout;

	public ScheduleGridView(Context context) {
		super(context);
	}

	public ScheduleGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ScheduleGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setTrashCan(ImageView trashCan) {
		this.trashCan = trashCan;
	}

	protected boolean deleteGridItem(int itemPosition) {
		ScheduleGridAdapter adapter = (ScheduleGridAdapter) getAdapter();
		IModel dragSrcItem = adapter.getItem(itemPosition);
		adapter.remove(dragSrcItem);
		return true;
	}

	@Override
	protected void onDropInExceedTop() {
		// do nothing
		this.setDragDesPosition(-1);
	}

	@Override
	protected void onDropInExceedButtom() {
		// do nothing
		this.setDragDesPosition(-1);
	}

	@Override
	protected void onDropInOverDiffObject() {
		ScheduleGridAdapter adapter = (ScheduleGridAdapter) getAdapter();
		IModel dragSrcItem = adapter.getItem(this.getDragSrcPosition());
		IModel dragTargetItem = adapter.getItem(this.getDragDesPosition());

		adapter.remove(dragSrcItem);
		adapter.insert(dragSrcItem, this.getDragDesPosition());

		adapter.remove(dragTargetItem);
		adapter.insert(dragTargetItem, this.getDragSrcPosition());

		System.out.println("srcPosition=" + this.getDragSrcPosition() + "  dragPosition=" + this.getDragDesPosition());
		// Toast.makeText(getContext(), adapter.getList().toString(), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDrop(MotionEvent ev) {
		super.onDrop(ev);

		// 移除物件
		Rect rc = new Rect();
		trashCan.getDrawingRect(rc);
		int[] location = new int[2];
		trashCan.getLocationOnScreen(location);
		rc.set(location[0], location[1], location[0] + rc.right, location[1] + rc.bottom);
		if (rc.contains((int) ev.getRawX(), (int) ev.getRawY())) {
			ScheduleGridAdapter adapter = (ScheduleGridAdapter) getAdapter();
			final int dragSrcPosition = this.getDragSrcPosition();
			IModel dragSrcItem = adapter.getItem(dragSrcPosition);
			Builder comfirm = new AlertDialog.Builder(this.getContext());
			comfirm.setTitle(R.string.delete_spot);
			comfirm.setMessage(R.string.delete_spot_confirm);
			comfirm.setIcon(android.R.drawable.ic_dialog_alert);
			comfirm.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
					deleteGridItem(dragSrcPosition);
				};
			});
			comfirm.setNegativeButton(R.string.no, null);
			comfirm.show();
		}
	}

}
