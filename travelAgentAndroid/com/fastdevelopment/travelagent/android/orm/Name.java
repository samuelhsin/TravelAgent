package com.fastdevelopment.travelagent.android.orm;

import com.j256.ormlite.field.DatabaseField;

public class Name {
	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField
	private String name;

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

}
