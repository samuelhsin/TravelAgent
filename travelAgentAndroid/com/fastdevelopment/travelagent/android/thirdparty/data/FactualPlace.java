package com.fastdevelopment.travelagent.android.thirdparty.data;

import java.util.List;

import com.fastdevelopment.travelagent.android.json2pojo.JsonDataMethodAnnotation;
import com.fastdevelopment.travelagent.android.json2pojo.JsonObject;

public class FactualPlace extends JsonObject {

	// schema: http://www.factual.com/data/t/places/schema

	/**
	 * 
	 */
	private static final long serialVersionUID = 5622902148733732782L;

	@JsonDataMethodAnnotation
	String address;

	@JsonDataMethodAnnotation
	String address_extended;

	@JsonDataMethodAnnotation
	String admin_region;

	@JsonDataMethodAnnotation
	List<Integer> category_ids;

	@JsonDataMethodAnnotation
	List<List<String>> category_labels;

	@JsonDataMethodAnnotation
	String country;

	@JsonDataMethodAnnotation
	String factual_id;

	@JsonDataMethodAnnotation
	String fax;

	@JsonDataMethodAnnotation
	String hours;

	@JsonDataMethodAnnotation
	String hours_display;

	@JsonDataMethodAnnotation
	Float latitude;

	@JsonDataMethodAnnotation
	String locality;

	@JsonDataMethodAnnotation
	Float longitude;

	@JsonDataMethodAnnotation
	String name;

	@JsonDataMethodAnnotation
	String chain_name;

	@JsonDataMethodAnnotation
	String chain_id;

	@JsonDataMethodAnnotation
	List<String> neighborhood;

	@JsonDataMethodAnnotation
	String postcode;

	@JsonDataMethodAnnotation
	String post_town;

	@JsonDataMethodAnnotation
	String po_box;

	@JsonDataMethodAnnotation
	String region;

	@JsonDataMethodAnnotation
	String status;

	@JsonDataMethodAnnotation
	String tel;

	@JsonDataMethodAnnotation
	String website;

	public String getAddress_extended() {
		return address_extended;
	}

	public void setAddress_extended(String address_extended) {
		this.address_extended = address_extended;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public List<Integer> getCategory_ids() {
		return category_ids;
	}

	public void setCategory_ids(List<Integer> category_ids) {
		this.category_ids = category_ids;
	}

	public List<List<String>> getCategory_labels() {
		return category_labels;
	}

	public void setCategory_labels(List<List<String>> category_labels) {
		this.category_labels = category_labels;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getFactual_id() {
		return factual_id;
	}

	public void setFactual_id(String factual_id) {
		this.factual_id = factual_id;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getHours() {
		return hours;
	}

	public void setHours(String hours) {
		this.hours = hours;
	}

	public String getHours_display() {
		return hours_display;
	}

	public void setHours_display(String hours_display) {
		this.hours_display = hours_display;
	}

	public Float getLatitude() {
		return latitude;
	}

	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public Float getLongitude() {
		return longitude;
	}

	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getNeighborhood() {
		return neighborhood;
	}

	public void setNeighborhood(List<String> neighborhood) {
		this.neighborhood = neighborhood;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getAdmin_region() {
		return admin_region;
	}

	public void setAdmin_region(String admin_region) {
		this.admin_region = admin_region;
	}

	public String getChain_name() {
		return chain_name;
	}

	public void setChain_name(String chain_name) {
		this.chain_name = chain_name;
	}

	public String getChain_id() {
		return chain_id;
	}

	public void setChain_id(String chain_id) {
		this.chain_id = chain_id;
	}

	public String getPost_town() {
		return post_town;
	}

	public void setPost_town(String post_town) {
		this.post_town = post_town;
	}

	public String getPo_box() {
		return po_box;
	}

	public void setPo_box(String po_box) {
		this.po_box = po_box;
	}

}
