package com.fastdevelopment.travelagent.android.model;

public abstract class BaseModel implements IModel {

	private String name;
	
	protected BaseModel() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
