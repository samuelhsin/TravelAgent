package com.fastdevelopment.travelagent.android.orm.model;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

public class User implements IOrmModel {
	@DatabaseField(generatedId = true, dataType = DataType.INTEGER)
	@SerializedName("id")
	private int id;
	@DatabaseField(dataType = DataType.STRING)
	@SerializedName("name")
	private String name;
	@DatabaseField(dataType = DataType.BOOLEAN)
	@SerializedName("firstLoad")
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
