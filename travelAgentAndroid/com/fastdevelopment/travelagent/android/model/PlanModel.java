package com.fastdevelopment.travelagent.android.model;

import com.fastdevelopment.travelagent.android.common.ServerConstants.PojoModelType;

public class PlanModel extends BaseModel {

	private String startCountryCode;
	private String endCountryCode;
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
