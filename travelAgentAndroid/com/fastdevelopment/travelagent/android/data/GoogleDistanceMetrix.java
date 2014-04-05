package com.fastdevelopment.travelagent.android.data;

import java.util.List;

import com.fastdevelopment.travelagent.android.json2pojo.IPojoData;
import com.fastdevelopment.travelagent.android.json2pojo.JsonDataMethodAnnotation;
import com.fastdevelopment.travelagent.android.json2pojo.JsonObject;

public class GoogleDistanceMetrix extends JsonObject implements IPojoData {

	@JsonDataMethodAnnotation
	List<String> destination_addresses;

	@JsonDataMethodAnnotation
	List<String> origin_addresses;

	@JsonDataMethodAnnotation
	List<GoogleDistance> rows;

	@JsonDataMethodAnnotation
	String status;

	public List<String> getDestination_addresses() {
		return destination_addresses;
	}

	public void setDestination_addresses(List<String> destination_addresses) {
		this.destination_addresses = destination_addresses;
	}

	public List<String> getOrigin_addresses() {
		return origin_addresses;
	}

	public void setOrigin_addresses(List<String> origin_addresses) {
		this.origin_addresses = origin_addresses;
	}

	public List<GoogleDistance> getRows() {
		return rows;
	}

	public void setRows(List<GoogleDistance> rows) {
		this.rows = rows;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
