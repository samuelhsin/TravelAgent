package com.fastdevelopment.travelagent.android.model;

import com.fastdevelopment.travelagent.android.common.ServerConstants.PojoModelType;

public class PlanModel extends BaseModel {

	private String content;

	public PlanModel(PojoModelType modelType) {
		super(modelType);
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
