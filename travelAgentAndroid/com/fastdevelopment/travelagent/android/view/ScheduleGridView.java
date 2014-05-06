package com.fastdevelopment.travelagent.android.view;

import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fastdevelopment.travelagent.android.R;
import com.fastdevelopment.travelagent.android.common.PlaceTimeFactory;
import com.fastdevelopment.travelagent.android.common.ServerConfig;
import com.fastdevelopment.travelagent.android.component.DragGridView;
import com.fastdevelopment.travelagent.android.model.IModel;

public class ScheduleGridView extends DragGridView {

	private String TAG = this.getClass().getSimpleName();

	private ScheduleGridView instance = this;
	protected ImageView imgTrashCan;
	protected ImageView imgSave;
	protected ImageView imgAdd;
	protected LinearLayout parentLayout;
	protected Resources resource = ServerConfig.resource;

	public ScheduleGridView(Context context) {
		super(context);
		init(context);
	}

	public ScheduleGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public ScheduleGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	protected void init(Context context) {
	}
	
	protected boolean recalculateGridItem() throws Exception {
		// TODO
		return true;
	}

	protected boolean deleteGridItem(int itemPosition) throws Exception {

		// 0. check this item is not last item
		ScheduleGridAdapter adapter = (ScheduleGridAdapter) getAdapter();
		if (adapter.getGoogleDistanceMetrix().getOrigin_addresses().size() > 1) {
			// 1. delete grid item
			IModel dragSrcItem = adapter.getItem(itemPosition);
			adapter.remove(dragSrcItem);

			// 2. regenerate item
			PlaceTimeFactory.removePlace(adapter.getGoogleDistanceMetrix(), dragSrcItem.getName());
			List<IModel> newModelList = PlaceTimeFactory.calculatePlaceTimePath(adapter.getGoogleDistanceMetrix());

			// 3. renew adapter
			ScheduleGridAdapter newAdapter = new ScheduleGridAdapter(adapter.getContext(), newModelList, adapter.getGoogleDistanceMetrix());
			setAdapter(newAdapter);

			// 4. change values array with your new data then update the adapter
			newAdapter.notifyDataSetChanged();

			return true;
		} else {
			return false;
		}
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
	protected void onDropInOverDiffObject() throws Exception {
		ScheduleGridAdapter adapter = (ScheduleGridAdapter) getAdapter();
		IModel dragSrcItem = adapter.getItem(this.getDragSrcPosition());
		IModel dragTargetItem = adapter.getItem(this.getDragDesPosition());

		adapter.remove(dragSrcItem);
		adapter.insert(dragSrcItem, this.getDragDesPosition());

		adapter.remove(dragTargetItem);
		adapter.insert(dragTargetItem, this.getDragSrcPosition());

		System.out.println("srcPosition=" + this.getDragSrcPosition() + "  dragPosition=" + this.getDragDesPosition());
		// Toast.makeText(getContext(), adapter.getList().toString(), Toast.LENGTH_SHORT).show();

		boolean isSuccess = recalculateGridItem();

		if (!isSuccess) {
			// TODO rollback
		}

	}

	public void onDelete(MotionEvent ev) {
		if (imgTrashCan != null) {
			Rect rc = new Rect();
			imgTrashCan.getDrawingRect(rc);
			int[] location = new int[2];
			imgTrashCan.getLocationOnScreen(location);
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
						try {
							boolean isSuccess = deleteGridItem(dragSrcPosition);

							if (isSuccess) {
								Toast.makeText(getContext(), resource.getString(R.string.delete_success), Toast.LENGTH_LONG);
							} else {
								Toast.makeText(getContext(), resource.getString(R.string.delete_failed), Toast.LENGTH_LONG);
							}

						} catch (Exception e) {
							Log.e(TAG, ExceptionUtils.getStackTrace(e));
						}
					};
				});
				comfirm.setNegativeButton(R.string.no, null);
				comfirm.show();
			}
		}
	}

	public void setImgTrashCan(ImageView imgTrashCan) {
		this.imgTrashCan = imgTrashCan;
	}

	public void setImgSave(ImageView imgSave) {
		this.imgSave = imgSave;
		//TODO onclick event
	}

	public void setImgAdd(ImageView imgAdd) {
		this.imgAdd = imgAdd;
		//TODO onclick event
	}

	@Override
	public void onDrop(MotionEvent ev) {
		super.onDrop(ev);

		// 移除物件
		onDelete(ev);

	}

	public void onAddPlace(View v) {
		String a = "test";
	}

}
