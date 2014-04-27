package com.fastdevelopment.travelagent.android.model;

import com.fastdevelopment.travelagent.android.common.ServerConstants.ModelType;

public interface IModel {
	public String getName();

	public void setName(String name);

	public ModelType getModelType();

	public void setModelType(ModelType modelType);
}
