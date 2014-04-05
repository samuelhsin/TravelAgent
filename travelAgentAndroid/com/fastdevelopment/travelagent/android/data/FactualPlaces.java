package com.fastdevelopment.travelagent.android.data;

import java.util.List;

import com.fastdevelopment.travelagent.android.json2pojo.IPojoData;
import com.fastdevelopment.travelagent.android.json2pojo.JsonDataMethodAnnotation;
import com.fastdevelopment.travelagent.android.json2pojo.JsonObject;

public class FactualPlaces extends JsonObject implements IPojoData  {

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
