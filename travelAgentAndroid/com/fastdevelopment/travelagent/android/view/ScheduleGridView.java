package com.fastdevelopment.travelagent.android.view;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.fastdevelopment.travelagent.android.R;
import com.fastdevelopment.travelagent.android.activity.MainActivity;
import com.fastdevelopment.travelagent.android.common.PlaceTimeFactory;
import com.fastdevelopment.travelagent.android.common.ServerConfig;
import com.fastdevelopment.travelagent.android.common.ServerConstants.FragmentEvent;
import com.fastdevelopment.travelagent.android.common.ServerConstants.FragmentIndex;
import com.fastdevelopment.travelagent.android.common.ServerConstants.IIntentDataKey;
import com.fastdevelopment.travelagent.android.common.ServerConstants.IStartActivityRequestCode;
import com.fastdevelopment.travelagent.android.common.ServerConstants.PojoModelType;
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
	protected ImageView imgAddPalce;
	protected View parentView;
	private int planId = -1;
	protected Resources resource = ServerConfig.resources;
	protected ScheduleFragment fragment;
	protected MainActivity activity;
	private Pojo2JsonParser pojo2JsonParser = new Pojo2JsonParser();
	private Dao<Plan, Integer> planDao = null;
	private String startCountryCode = null;
	private String endCountryCode = null;

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

		ScheduleGridAdapter adapter = (ScheduleGridAdapter) getAdapter();

		List<String> newPlacesOrder = new ArrayList<String>();

		List<IPojoModel> items = adapter.getItems();

		// 0. get place list
		for (IPojoModel model : items) {
			if (PojoModelType.PLACE == model.getPojoModelType()) {
				newPlacesOrder.add(model.getName());
			}
		}

		// 1. follow new items' order to rearrange distances
		List<IPojoModel> newItems = PlaceTimeFactory.calculatePlaceTimePathByOrder(newPlacesOrder, adapter.getGoogleDistanceMetrix());

		// 2. renew adapter
		ScheduleGridAdapter newAdapter = new ScheduleGridAdapter(adapter.getContext(), newItems, adapter.getGoogleDistanceMetrix());
		setAdapter(newAdapter);

		// 3. change values array with your new data then update the adapter
		newAdapter.notifyDataSetChanged();

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

		// avoid to change first and last item
		if (this.getDragSrcPosition() == 0 || this.getDragDesPosition() == 0) {
			return;
		}

		IPojoModel dragSrcItem = adapter.getItem(this.getDragSrcPosition());
		IPojoModel dragTargetItem = adapter.getItem(this.getDragDesPosition());

		// change position
		if (PojoModelType.PLACE == dragSrcItem.getPojoModelType() && PojoModelType.PLACE == dragTargetItem.getPojoModelType()) {

			adapter.remove(dragSrcItem);
			adapter.insert(dragSrcItem, this.getDragDesPosition());

			adapter.remove(dragTargetItem);
			adapter.insert(dragTargetItem, this.getDragSrcPosition());

			boolean isSuccess = recalculateGridItem();

			if (!isSuccess) {
				// rollback
				adapter.remove(dragSrcItem);
				adapter.insert(dragSrcItem, this.getDragSrcPosition());
				adapter.remove(dragTargetItem);
				adapter.insert(dragTargetItem, this.getDragDesPosition());
			}
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
		// check drop is on trash can
		onDelete(ev);

	}

	protected void settingImgViews() {
		// get add img
		ImageView imgAddPlace = (ImageView) this.parentView.findViewById(R.id.imgAdd);
		this.imgAddPalce = imgAddPlace;

		// get save img
		ImageView imgSave = (ImageView) this.parentView.findViewById(R.id.imgSave);
		this.imgSave = imgSave;

		// get delete img
		ImageView imgTrashCan = (ImageView) this.parentView.findViewById(R.id.imgTrashCan);
		this.imgTrashCan = imgTrashCan;

		imgAddPlace.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				ScheduleGridAdapter adapter = (ScheduleGridAdapter) getAdapter();
				GoogleDistanceMetrix googleDistanceMetrix = adapter.getGoogleDistanceMetrix();

				// add place
				// Intent intent = new Intent(activity, MapActivity.class);
				Intent intent = new Intent("com.fastdevelopment.travelagent.android.activity.MapActivity");
				intent.putExtra(IIntentDataKey.GOOGLE_DISTANCE_METRIX, googleDistanceMetrix);
				intent.putExtra(IIntentDataKey.START_COUNTRY_CODE, startCountryCode);
				intent.putExtra(IIntentDataKey.END_COUNTRY_CODE, endCountryCode);
				intent.putExtra(IIntentDataKey.PLAN_ID, planId);
				// activity.startActivity(intent);
				activity.startActivityForResult(intent, IStartActivityRequestCode.PICK_PLACES);
			}
		});

		imgSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (planId == -1) {
					// new plan

					AlertDialog.Builder editDialog = new AlertDialog.Builder(activity);
					editDialog.setTitle("--- " + resource.getString(R.string.ti_add_plan) + " ---");

					final EditText editText = new EditText(activity);
					editText.setHint(resource.getString(R.string.add_plan_hint));
					editDialog.setView(editText);

					editDialog.setPositiveButton(resource.getString(R.string.ti_add), new DialogInterface.OnClickListener() {
						// do something when the button is clicked
						public void onClick(DialogInterface arg0, int arg1) {

							String inputText = editText.getText().toString();

							if (!inputText.isEmpty()) {
								addPlan(inputText);
							} else {
								Toast toast = Toast.makeText(parentView.getContext(), resource.getString(R.string.empty_input), Toast.LENGTH_LONG);
								toast.show();
							}

						}
					});
					editDialog.setNegativeButton(resource.getString(R.string.ti_cancel), new DialogInterface.OnClickListener() {
						// do something when the button is clicked
						public void onClick(DialogInterface arg0, int arg1) {

						}
					});
					editDialog.show();

				} else {
					// existed plan
					savePlan(planId);
				}

			}
		});

		imgTrashCan.setClickable(true);
		imgTrashCan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// load plan object
				String planName = null;
				if (planId != -1) {
					Plan plan = getPlan(planId);
					if (plan != null) {
						planName = plan.getName();
					} else {
						planName = resource.getString(R.string.none_saved);
					}
				} else {
					planName = resource.getString(R.string.none_saved);
				}

				// return to schedule input
				Builder comfirm = new AlertDialog.Builder(activity);
				comfirm.setTitle(R.string.delete_plan);
				comfirm.setMessage(String.format(resource.getString(R.string.delete_plan_confirm), planName));
				comfirm.setIcon(android.R.drawable.ic_dialog_alert);
				comfirm.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						removePlan(planId);
					};
				});
				comfirm.setNegativeButton(R.string.no, null);
				comfirm.show();

			}
		});

	}

	protected boolean addPlan(String planName) {

		boolean isSuccess = false;

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
			plan.setName(planName);
			plan.setContent(jsonStr);
			plan.setStartCountryCode(startCountryCode);
			plan.setEndCountryCode(endCountryCode);
			int row = planDao.create(plan);
			if (row > 0) {
				planId = plan.getId();
				toast = Toast.makeText(parentView.getContext(), resource.getString(R.string.add_success), Toast.LENGTH_LONG);
				// change to plan fragment
				activity.changeFragement(FragmentIndex.PLAN, FragmentEvent.RELOAD);
				isSuccess = true;
			} else {
				toast = Toast.makeText(parentView.getContext(), resource.getString(R.string.add_failed), Toast.LENGTH_LONG);
				isSuccess = false;
			}

			toast.show();

		} catch (Exception e) {
			Log.e(TAG, ExceptionUtils.getStackTrace(e));
			Toast toast = Toast.makeText(parentView.getContext(), resource.getString(R.string.add_failed), Toast.LENGTH_LONG);
			toast.show();
		}
		return isSuccess;
	}

	protected boolean savePlan(int planId) {

		boolean isSuccess = false;
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
			Plan plan = getPlan(planId);
			if (plan != null) {
				plan.setContent(jsonStr);
				plan.setStartCountryCode(startCountryCode);
				plan.setEndCountryCode(endCountryCode);
				row = planDao.update(plan);
			}

			if (row > 0) {
				planId = plan.getId();
				toast = Toast.makeText(parentView.getContext(), resource.getString(R.string.save_success), Toast.LENGTH_LONG);
				// change to plan fragment
				activity.changeFragement(FragmentIndex.PLAN, FragmentEvent.RELOAD);
				isSuccess = true;
			} else {
				toast = Toast.makeText(parentView.getContext(), resource.getString(R.string.save_failed), Toast.LENGTH_LONG);
				isSuccess = false;
			}

			toast.show();

		} catch (Exception e) {
			Log.e(TAG, ExceptionUtils.getStackTrace(e));
			Toast toast = Toast.makeText(parentView.getContext(), resource.getString(R.string.save_failed), Toast.LENGTH_LONG);
			toast.show();
		}

		return isSuccess;

	}

	protected boolean removePlan(int planId) {

		boolean isSuccess = false;

		try {
			try {
				Toast toast = null;
				if (planId != -1) {
					// delete plan to db.
					int row = planDao.deleteById(planId);
					if (row > 0) {
						toast = Toast.makeText(parentView.getContext(), resource.getString(R.string.delete_success), Toast.LENGTH_LONG);
						isSuccess = true;
					} else {
						toast = Toast.makeText(parentView.getContext(), resource.getString(R.string.delete_failed), Toast.LENGTH_LONG);
						isSuccess = false;
					}
					// change to plan fragment
					activity.changeFragement(FragmentIndex.PLAN, FragmentEvent.RELOAD);
				} else {
					toast = Toast.makeText(parentView.getContext(), resource.getString(R.string.delete_success), Toast.LENGTH_LONG);
					fragment.loadScheduleInput();
					isSuccess = true;
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

		return isSuccess;

	}

	protected Plan getPlan(int planId) {

		Plan plan = null;
		try {
			if (planId != -1) {

				plan = planDao.queryForId(planId);

			}
		} catch (Exception e) {
			Log.e(TAG, ExceptionUtils.getStackTrace(e));
		}
		return plan;
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

	public String getStartCountryCode() {
		return startCountryCode;
	}

	public void setStartCountryCode(String startCountryCode) {
		this.startCountryCode = startCountryCode;
	}

	public String getEndCountryCode() {
		return endCountryCode;
	}

	public void setEndCountryCode(String endCountryCode) {
		this.endCountryCode = endCountryCode;
	}

}
