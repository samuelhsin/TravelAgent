package com.fastdevelopment.travelagent.android.model;

import com.fastdevelopment.travelagent.android.common.ServerConstants.ModelType;

public abstract class BaseModel implements IModel {

	private String name;
	private ModelType modelType;

	public BaseModel(ModelType modelType) {
		super();
		this.modelType = modelType;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public ModelType getModelType() {
		return modelType;
	}

	@Override
	public void setModelType(ModelType modelType) {
		this.modelType = modelType;
	}

}
