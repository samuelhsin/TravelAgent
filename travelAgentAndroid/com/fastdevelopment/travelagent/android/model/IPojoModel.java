package com.fastdevelopment.travelagent.android.model;

import com.fastdevelopment.travelagent.android.common.ServerConstants.PojoModelType;

public interface IPojoModel {
	public String getName();

	public void setName(String name);

	public PojoModelType getPojoModelType();

	public void setPojoModelType(PojoModelType pojoModelType);
}
