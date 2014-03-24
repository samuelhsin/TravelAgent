package com.fastdevelopment.travelagent.android.orm.model;

import com.j256.ormlite.field.DatabaseField;

public class User {
	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField
	private String name;
	@DatabaseField
	private boolean firstLoad;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isFirstLoad() {
		return firstLoad;
	}

	public void setFirstLoad(boolean firstLoad) {
		this.firstLoad = firstLoad;
	}

}
