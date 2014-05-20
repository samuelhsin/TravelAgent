package com.fastdevelopment.travelagent.android.model;

import java.util.ArrayList;
import java.util.List;

import com.fastdevelopment.travelagent.android.common.ServerConstants.OrmModelType;
import com.fastdevelopment.travelagent.android.common.ServerConstants.PojoModelType;
import com.fastdevelopment.travelagent.android.orm.model.IOrmModel;
import com.fastdevelopment.travelagent.android.orm.model.Plan;

public abstract class PojoModelFactory {

	public static List<IPojoModel> generatePojoModels(OrmModelType ormModelType, List<? extends IOrmModel> ormModelList) {
		List<IPojoModel> result = null;

		if (ormModelList != null) {
			result = new ArrayList<IPojoModel>();

			for (int i = 0; i < ormModelList.size(); i++) {
				IPojoModel pojoModel = createPojoModel(ormModelType, ormModelList.get(i));
				result.add(pojoModel);
			}
		}

		return result;

	}

	private static IPojoModel createPojoModel(OrmModelType ormModelType, IOrmModel ormModel) {

		if (ormModel == null) {
			return null;
		}
		PlanModel planModel = null;

		switch (ormModelType) {
		case PLAN:
			Plan plan = (Plan) ormModel;
			planModel = new PlanModel(PojoModelType.PLAN);
			planModel.setId(plan.getId());
			planModel.setName(plan.getName());
			planModel.setContent(plan.getContent());
			break;
		default:
		}
		return planModel;
	}
}
