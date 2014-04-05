package com.fastdevelopment.travelagent.android.data;

import java.util.List;

import com.fastdevelopment.travelagent.android.json2pojo.IPojoData;
import com.fastdevelopment.travelagent.android.json2pojo.JsonDataMethodAnnotation;
import com.fastdevelopment.travelagent.android.json2pojo.JsonObject;

public class GoogleDistance extends JsonObject implements IPojoData {

	@JsonDataMethodAnnotation
	List<GoogleDistanceElement> elements;

	public List<GoogleDistanceElement> getElements() {
		return elements;
	}

	public void setElements(List<GoogleDistanceElement> elements) {
		this.elements = elements;
	}

}
