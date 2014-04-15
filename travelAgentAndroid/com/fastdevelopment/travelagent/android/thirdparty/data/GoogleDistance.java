package com.fastdevelopment.travelagent.android.thirdparty.data;

import java.util.List;

import com.fastdevelopment.travelagent.android.json2pojo.JsonDataMethodAnnotation;
import com.fastdevelopment.travelagent.android.json2pojo.JsonObject;

public class GoogleDistance extends JsonObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5336773562434993177L;
	@JsonDataMethodAnnotation
	List<GoogleDistanceElement> elements;

	public List<GoogleDistanceElement> getElements() {
		return elements;
	}

	public void setElements(List<GoogleDistanceElement> elements) {
		this.elements = elements;
	}

}
