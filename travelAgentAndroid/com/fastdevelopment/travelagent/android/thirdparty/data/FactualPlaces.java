package com.fastdevelopment.travelagent.android.thirdparty.data;

import java.util.List;

import com.fastdevelopment.travelagent.android.json2pojo.JsonDataMethodAnnotation;
import com.fastdevelopment.travelagent.android.json2pojo.JsonObject;

public class FactualPlaces extends JsonObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7926774668045348573L;

	@JsonDataMethodAnnotation
	List<FactualPlace> data;

	@JsonDataMethodAnnotation
	Integer included_rows;

	public List<FactualPlace> getData() {
		return data;
	}

	public void setData(List<FactualPlace> data) {
		this.data = data;
	}

	public Integer getIncluded_rows() {
		return included_rows;
	}

	public void setIncluded_rows(Integer included_rows) {
		this.included_rows = included_rows;
	}

}
