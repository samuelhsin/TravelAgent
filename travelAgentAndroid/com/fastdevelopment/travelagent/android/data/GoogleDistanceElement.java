package com.fastdevelopment.travelagent.android.data;

import com.fastdevelopment.travelagent.android.json2pojo.IPojoData;
import com.fastdevelopment.travelagent.android.json2pojo.JsonDataMethodAnnotation;
import com.fastdevelopment.travelagent.android.json2pojo.JsonObject;

public class GoogleDistanceElement extends JsonObject implements IPojoData {

	@JsonDataMethodAnnotation
	TextValue distance;

	@JsonDataMethodAnnotation
	TextValue duration;

	@JsonDataMethodAnnotation
	String status;

	public TextValue getDistance() {
		return distance;
	}

	public void setDistance(TextValue distance) {
		this.distance = distance;
	}

	public TextValue getDuration() {
		return duration;
	}

	public void setDuration(TextValue duration) {
		this.duration = duration;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
