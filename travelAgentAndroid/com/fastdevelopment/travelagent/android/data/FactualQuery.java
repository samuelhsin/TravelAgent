package com.fastdevelopment.travelagent.android.data;

import com.fastdevelopment.travelagent.android.json2pojo.IPojoData;
import com.fastdevelopment.travelagent.android.json2pojo.JsonDataMethodAnnotation;
import com.fastdevelopment.travelagent.android.json2pojo.JsonObject;

public class FactualQuery extends JsonObject implements IPojoData {

	@JsonDataMethodAnnotation
	Integer version;

	@JsonDataMethodAnnotation
	String status;

	@JsonDataMethodAnnotation
	FactualPlaces response;

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public FactualPlaces getResponse() {
		return response;
	}

	public void setResponse(FactualPlaces response) {
		this.response = response;
	}

}
