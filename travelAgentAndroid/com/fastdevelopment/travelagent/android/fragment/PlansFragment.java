package com.fastdevelopment.travelagent.android.fragment;

import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;

import com.fastdevelopment.travelagent.android.R;
import com.fastdevelopment.travelagent.android.activity.MainActivity;
import com.fastdevelopment.travelagent.android.common.ServerConstants.FragmentEvent;
import com.fastdevelopment.travelagent.android.common.ServerConstants.FragmentIndex;
import com.fastdevelopment.travelagent.android.common.ServerConstants.IJson2PojoConst;
import com.fastdevelopment.travelagent.android.common.ServerConstants.OrmModelType;
import com.fastdevelopment.travelagent.android.common.ServerConstants.PojoModelType;
import com.fastdevelopment.travelagent.android.json2pojo.Json2PojoParser;
import com.fastdevelopment.travelagent.android.model.IPojoModel;
import com.fastdevelopment.travelagent.android.model.PlanModel;
import com.fastdevelopment.travelagent.android.model.PojoModelFactory;
import com.fastdevelopment.travelagent.android.orm.model.Plan;
import com.fastdevelopment.travelagent.android.thirdparty.data.GoogleDistanceMetrix;
import com.fastdevelopment.travelagent.android.view.PlansGridAdapter;
import com.fastdevelopment.travelagent.android.view.PlansGridView;
import com.j256.ormlite.dao.Dao;

public class PlansFragment extends Fragment implements IFragment {

	private String TAG = this.getClass().getSimpleName();

	private Dao<Plan, Integer> planDao = null;
	private MainActivity mainActivity = null;
	private PlansGridView plansGridView = null;
	private FrameLayout wholeView;
	private static final Json2PojoParser parser = new Json2PojoParser();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		wholeView = (FrameLayout) inflater.inflate(R.layout.fragment_plans, container, false);
		mainActivity = (MainActivity) this.getActivity();
		initDao();
		List<IPojoModel> plans = getAllPlans();
		plansGridView = (PlansGridView) wholeView.findViewById(R.id.plans_grid);
		PlansGridAdapter adapter = new PlansGridAdapter(this.mainActivity, plans);
		plansGridView.setAdapter(adapter);

		plansGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				try {
					onGridItemClick(parent, view, position, id);
				} catch (Exception e) {
					Log.e(TAG, ExceptionUtils.getStackTrace(e));
				}
			}

		});

		return wholeView;
	}

	private void onGridItemClick(AdapterView<?> parent, View view, int position, long id) throws Exception {
		PlansGridAdapter adapter = (PlansGridAdapter) plansGridView.getAdapter();
		IPojoModel pojoModel = adapter.getItem(position);
		if (pojoModel != null && PojoModelType.PLAN == pojoModel.getPojoModelType()) {
			PlanModel planModel = (PlanModel) pojoModel;
			String jsonStr = planModel.getContent();
			if (jsonStr != null) {
				JSONObject json = new JSONObject(jsonStr);
				GoogleDistanceMetrix googleDistanceMetrix = new GoogleDistanceMetrix();
				parser.parsingJsonValueToPojo(IJson2PojoConst.JSON2POJO_DATA_PACKAGE, json, googleDistanceMetrix);
				mainActivity.changeFragement(FragmentIndex.SCHEDULE, FragmentEvent.LOAD_PLAN, planModel.getId(), googleDistanceMetrix, planModel.getStartCountryCode(), planModel.getEndCountryCode());
			}

		}
	}

	private boolean initDao() {
		try {
			planDao = mainActivity.getDBHelper().getDao(Plan.class);
		} catch (Exception e) {
			Log.e(TAG, ExceptionUtils.getStackTrace(e));
			return false;
		}
		return true;
	}

	protected List<IPojoModel> getAllPlans() {
		List<IPojoModel> planModels = null;
		try {
			List<Plan> plans = planDao.queryForAll();
			planModels = PojoModelFactory.generatePojoModels(OrmModelType.PLAN, plans);

		} catch (Exception e) {
			Log.e(TAG, ExceptionUtils.getStackTrace(e));
		}
		return planModels;
	}

	protected boolean refreshGridView() {

		try {

			PlansGridAdapter adapter = (PlansGridAdapter) plansGridView.getAdapter();
			adapter.clear();
			adapter.addAll(getAllPlans());

		} catch (Exception e) {
			Log.e(TAG, ExceptionUtils.getStackTrace(e));
			return false;
		}
		return true;
	}

	@Override
	public void passValuesByFocus(int fragmentEventId, Object... objects) throws Exception {
		switch (fragmentEventId) {
		case FragmentEvent.RELOAD:
			refreshGridView();
			break;
		case FragmentEvent.CLICK_FOCUS:
			boolean hasFocus = (Boolean) objects[0];
			if (hasFocus) {
				refreshGridView();
			}
			break;
		case FragmentEvent.NONE:
			break;
		default:
		}

	}
}
