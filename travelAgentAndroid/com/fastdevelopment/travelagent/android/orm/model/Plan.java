package com.fastdevelopment.travelagent.android.orm.model;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

public class Plan implements IModel {

	@DatabaseField(generatedId = true, dataType = DataType.INTEGER)
	@SerializedName("id")
	private int id;
	@DatabaseField(dataType = DataType.STRING)
	@SerializedName("name")
	private String name;
	@DatabaseField(dataType = DataType.STRING)
	@SerializedName("content")
	private String content;

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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
