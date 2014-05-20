package com.fastdevelopment.travelagent.android.model;

import com.fastdevelopment.travelagent.android.common.ServerConstants.PojoModelType;

public abstract class BaseModel implements IPojoModel {

	private int id;
	private String name;
	private PojoModelType modelType;

	public BaseModel(PojoModelType modelType) {
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
	public PojoModelType getPojoModelType() {
		return modelType;
	}

	@Override
	public void setPojoModelType(PojoModelType modelType) {
		this.modelType = modelType;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
