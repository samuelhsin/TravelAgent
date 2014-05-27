package com.fastdevelopment.travelagent.android.view;

import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.JSONObject;

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
import android.widget.Toast;

import com.fastdevelopment.travelagent.android.R;
import com.fastdevelopment.travelagent.android.activity.MainActivity;
import com.fastdevelopment.travelagent.android.common.PlaceTimeFactory;
import com.fastdevelopment.travelagent.android.common.ServerConfig;
import com.fastdevelopment.travelagent.android.component.DragGridView;
import com.fastdevelopment.travelagent.android.fragment.ScheduleFragment;
import com.fastdevelopment.travelagent.android.model.IPojoModel;
import com.fastdevelopment.travelagent.android.orm.model.Plan;
import com.fastdevelopment.travelagent.android.pojo2json.Pojo2JsonParser;
import com.fastdevelopment.travelagent.android.thirdparty.data.GoogleDistanceMetrix;
import com.j256.ormlite.dao.Dao;

public class ScheduleGridView extends DragGridView {

	private String TAG = this.getClass().getSimpleName();

	private ScheduleGridView instance = this;
	protected ImageView imgTrashCan;
	protected ImageView imgSave;
	protected ImageView imgAdd;
	protected View parentView;
	private int planId = -1;
	protected Resources resource = ServerConfig.resource;
	protected ScheduleFragment fragment;
	protected MainActivity activity;
	private Pojo2JsonParser pojo2JsonParser = new Pojo2JsonParser();
	private Dao<Plan, Integer> planDao = null;

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
		activity = (MainActivity) context;
		initDao();
	}

	private boolean initDao() {
		try {
			planDao = activity.getDBHelper().getDao(Plan.class);
		} catch (Exception e) {
			Log.e(TAG, ExceptionUtils.getStackTrace(e));
			return false;
		}
		return true;
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
			IPojoModel dragSrcItem = adapter.getItem(itemPosition);
			adapter.remove(dragSrcItem);

			// 2. regenerate item
			PlaceTimeFactory.removePlace(adapter.getGoogleDistanceMetrix(), dragSrcItem.getName());
			List<IPojoModel> newModelList = PlaceTimeFactory.calculatePlaceTimePath(adapter.getGoogleDistanceMetrix());

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
		IPojoModel dragSrcItem = adapter.getItem(this.getDragSrcPosition());
		IPojoModel dragTargetItem = adapter.getItem(this.getDragDesPosition());

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
				final Context context = adapter.getContext();
				IPojoModel dragSrcItem = adapter.getItem(dragSrcPosition);
				Builder comfirm = new AlertDialog.Builder(this.getContext());
				comfirm.setTitle(R.string.delete_spot);
				comfirm.setMessage(R.string.delete_spot_confirm);
				comfirm.setIcon(android.R.drawable.ic_dialog_alert);
				comfirm.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						try {
							boolean isSuccess = deleteGridItem(dragSrcPosition);
							Toast toast = null;
							if (isSuccess) {
								toast = Toast.makeText(context, resource.getString(R.string.delete_success), Toast.LENGTH_LONG);
							} else {
								toast = Toast.makeText(context, resource.getString(R.string.delete_failed), Toast.LENGTH_LONG);
							}
							toast.show();

						} catch (Exception e) {
							Log.e(TAG, ExceptionUtils.getStackTrace(e));
							Toast toast = Toast.makeText(parentView.getContext(), resource.getString(R.string.delete_failed), Toast.LENGTH_LONG);
							toast.show();
						}
					};
				});
				comfirm.setNegativeButton(R.string.no, null);
				comfirm.show();
			}
		}
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

	protected void settingImgViews() {
		// get add img
		ImageView imgAdd = (ImageView) this.parentView.findViewById(R.id.imgAdd);
		this.imgAdd = imgAdd;

		// get save img
		ImageView imgSave = (ImageView) this.parentView.findViewById(R.id.imgSave);
		this.imgSave = imgSave;

		// get delete img
		ImageView imgTrashCan = (ImageView) this.parentView.findViewById(R.id.imgTrashCan);
		this.imgTrashCan = imgTrashCan;

		if (planId == -1) {
			// new plan
			imgSave.setVisibility(View.INVISIBLE);
			imgSave.setClickable(false);
			imgAdd.setVisibility(View.VISIBLE);
			imgAdd.setClickable(true);
		} else {
			// old plan
			imgSave.setVisibility(View.VISIBLE);
			imgSave.setClickable(true);
			imgAdd.setVisibility(View.INVISIBLE);
			imgAdd.setClickable(false);
		}

		imgAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// add plan
				try {
					ScheduleGridAdapter adapter = (ScheduleGridAdapter) getAdapter();
					GoogleDistanceMetrix googleDistanceMetrix = adapter.getGoogleDistanceMetrix();
					// change pojo to string
					String jsonStr = null;
					JSONObject json = pojo2JsonParser.parsingPojoToJson(googleDistanceMetrix);
					if (json != null) {
						jsonStr = json.toString();
					}

					Toast toast = null;

					// add to db
					Plan plan = new Plan();
					plan.setName("test plan");
					plan.setContent(jsonStr);
					int row = planDao.create(plan);
					if (row > 0) {
						planId = plan.getId();
						toast = Toast.makeText(parentView.getContext(), resource.getString(R.string.add_success), Toast.LENGTH_LONG);
					} else {
						toast = Toast.makeText(parentView.getContext(), resource.getString(R.string.add_failed), Toast.LENGTH_LONG);
					}

					toast.show();

				} catch (Exception e) {
					Log.e(TAG, ExceptionUtils.getStackTrace(e));
					Toast toast = Toast.makeText(parentView.getContext(), resource.getString(R.string.add_failed), Toast.LENGTH_LONG);
					toast.show();
				}
			}
		});

		imgSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// save plan
				try {

					ScheduleGridAdapter adapter = (ScheduleGridAdapter) getAdapter();
					GoogleDistanceMetrix googleDistanceMetrix = adapter.getGoogleDistanceMetrix();
					// change pojo to string
					String jsonStr = null;
					JSONObject json = pojo2JsonParser.parsingPojoToJson(googleDistanceMetrix);
					if (json != null) {
						jsonStr = json.toString();
					}

					Toast toast = null;

					// save to db
					int row = -1;
					Plan plan = planDao.queryForId(planId);
					if (plan != null) {
						plan.setContent(jsonStr);
						row = planDao.update(plan);
					}

					if (row > 0) {
						planId = plan.getId();
						toast = Toast.makeText(parentView.getContext(), resource.getString(R.string.save_success), Toast.LENGTH_LONG);
					} else {
						toast = Toast.makeText(parentView.getContext(), resource.getString(R.string.save_failed), Toast.LENGTH_LONG);
					}

					toast.show();

				} catch (Exception e) {
					Log.e(TAG, ExceptionUtils.getStackTrace(e));
					Toast toast = Toast.makeText(parentView.getContext(), resource.getString(R.string.save_failed), Toast.LENGTH_LONG);
					toast.show();
				}
			}
		});

		imgTrashCan.setClickable(true);
		imgTrashCan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// return to schedule input
				Builder comfirm = new AlertDialog.Builder(activity);
				comfirm.setTitle(R.string.delete_plan);
				comfirm.setMessage(R.string.delete_plan_confirm);
				comfirm.setIcon(android.R.drawable.ic_dialog_alert);
				comfirm.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						try {
							try {
								Toast toast = null;
								if (planId != -1) {
									// delete plan to db.
									int row = planDao.deleteById(planId);
									if (row > 0) {
										toast = Toast.makeText(parentView.getContext(), resource.getString(R.string.delete_success), Toast.LENGTH_LONG);
									} else {
										toast = Toast.makeText(parentView.getContext(), resource.getString(R.string.delete_failed), Toast.LENGTH_LONG);
									}
									// change to plan fragment
									activity.changeFragement(1);
								} else {
									toast = Toast.makeText(parentView.getContext(), resource.getString(R.string.delete_success), Toast.LENGTH_LONG);
									fragment.loadScheduleInput();
								}

								toast.show();

							} catch (Exception e) {
								Log.e(TAG, ExceptionUtils.getStackTrace(e));
								Toast.makeText(parentView.getContext(), resource.getString(R.string.delete_failed), Toast.LENGTH_LONG);
							}
						} catch (Exception e) {
							Log.e(TAG, ExceptionUtils.getStackTrace(e));
							Toast toast = Toast.makeText(parentView.getContext(), resource.getString(R.string.delete_failed), Toast.LENGTH_LONG);
							toast.show();
						}
					};
				});
				comfirm.setNegativeButton(R.string.no, null);
				comfirm.show();

			}
		});

	}

	public View getParentView() {
		return parentView;
	}

	public void setParentView(View parentView) {
		this.parentView = parentView;
		settingImgViews();
	}

	public ScheduleFragment getFragment() {
		return fragment;
	}

	public void setFragment(ScheduleFragment fragment) {
		this.fragment = fragment;
	}

	public int getPlanId() {
		return planId;
	}

	public void setPlanId(int planId) {
		this.planId = planId;
	}

}
