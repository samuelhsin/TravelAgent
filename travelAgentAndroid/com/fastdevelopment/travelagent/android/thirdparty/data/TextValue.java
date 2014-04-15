package com.fastdevelopment.travelagent.android.thirdparty.data;

import com.fastdevelopment.travelagent.android.json2pojo.JsonDataMethodAnnotation;
import com.fastdevelopment.travelagent.android.json2pojo.JsonObject;

public class TextValue extends JsonObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6872147004580353757L;

	@JsonDataMethodAnnotation
	String text;

	@JsonDataMethodAnnotation
	String value;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
