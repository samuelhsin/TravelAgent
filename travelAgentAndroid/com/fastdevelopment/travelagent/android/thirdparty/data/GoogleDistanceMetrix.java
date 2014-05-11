package com.fastdevelopment.travelagent.android.thirdparty.data;

import java.util.List;
import java.util.Map;

import com.fastdevelopment.travelagent.android.json2pojo.JsonDataMethodAnnotation;
import com.fastdevelopment.travelagent.android.json2pojo.JsonObject;

public class GoogleDistanceMetrix extends JsonObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -422979959581538915L;

	@JsonDataMethodAnnotation
	List<String> destination_addresses;

	@JsonDataMethodAnnotation
	List<String> origin_addresses;

	@JsonDataMethodAnnotation
	List<GoogleDistance> rows;

	/**
	 * OK
	 */
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
