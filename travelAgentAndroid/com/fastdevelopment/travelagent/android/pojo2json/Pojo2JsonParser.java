package com.fastdevelopment.travelagent.android.pojo2json;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fastdevelopment.travelagent.android.json2pojo.IPojoData;
import com.fastdevelopment.travelagent.android.json2pojo.JsonDataMethodAnnotation;
import com.fastdevelopment.travelagent.android.json2pojo.JsonObject;

public class Pojo2JsonParser implements IPojo2JsonParser {

	public Pojo2JsonParser() {
		super();
	}

	public JSONArray parsingPojoArray2JsonArray(List<?> listObject) throws Exception {
		if (listObject == null) {
			return null;
		}
		JSONArray jsonArray = new JSONArray();

		if (listObject.size() > 0) {
			Object innerValue = listObject.get(0);

			boolean isInnerJsonObjectValue = false;
			boolean isInnerJsonArrayValue = false;

			if (JsonObject.class.isAssignableFrom(innerValue.getClass())) {
				isInnerJsonObjectValue = true;
			} else if (List.class.isAssignableFrom(innerValue.getClass())) {
				isInnerJsonArrayValue = true;
			}

			for (int j = 0; j < listObject.size(); j++) {
				Object arrayValue = null;
				innerValue = listObject.get(j);
				if (isInnerJsonObjectValue) {
					IPojoData innerPojoValue = (IPojoData) innerValue;
					arrayValue = this.parsingPojoToJson(innerPojoValue);
				} else if (isInnerJsonArrayValue) {
					arrayValue = this.parsingPojoArray2JsonArray(listObject);
				} else {
					arrayValue = innerValue;
				}

				jsonArray.put(arrayValue);
			}
		}

		return jsonArray;
	}

	public JSONObject parsingPojoToJson(IPojoData pojo) throws Exception {
		if (pojo == null) {
			return null;
		}
		JSONObject json = new JSONObject();

		Field[] fields = pojo.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			
			Field field = fields[i];
			
			String javaFieldName = field.getName();
			String jsonFieldName = null;
			
			boolean isJsonObjectValue = false;
			boolean isJsonArrayValue = false;
			boolean isBooleanValue = false;
			
			Object value = null;

			// field type
			if (field.getAnnotation(JsonDataMethodAnnotation.class) == null) {
				// skip
				continue;
			}

			Class<?> aClass = field.getType();

			// replace '-' to '__', because java field name cant use '-' to represent
			// function name, but in json object '-' is legal.
			jsonFieldName = javaFieldName.replace("-", "__");

			if (JsonObject.class.isAssignableFrom(aClass)) {
				isJsonObjectValue = true;
			} else if (List.class.isAssignableFrom(aClass)) {
				isJsonArrayValue = true;
			} else if (Boolean.class.isAssignableFrom(aClass)) {
				isBooleanValue = true;
			}

			// getter
			if (isJsonObjectValue) {
				// 1. is a json object
				String functionName = Character.toUpperCase(javaFieldName.charAt(0)) + javaFieldName.substring(1);
				String getterMethodName = "get" + functionName;
				Method getter = pojo.getClass().getMethod(getterMethodName);
				Object temp = getter.invoke(pojo);
				if (temp != null) {
					IPojoData innerValue = (IPojoData) temp;
					value = this.parsingPojoToJson(innerValue);
				}

			} else if (isJsonArrayValue) {
				// 2. is a json array object
				String functionName = Character.toUpperCase(javaFieldName.charAt(0)) + javaFieldName.substring(1);
				String getterMethodName = "get" + functionName;
				Method getter = pojo.getClass().getMethod(getterMethodName);
				Object temp = getter.invoke(pojo);
				if (temp != null) {
					List<?> innerList = (List<?>) temp;
					value = parsingPojoArray2JsonArray(innerList);
				}

			} else {
				// 3. is a normal object
				String getterMethodName = null;
				if (javaFieldName.startsWith("is") && javaFieldName.length() >= 3 && Character.isUpperCase(javaFieldName.charAt(2))) {
					javaFieldName = javaFieldName.replace("is", "");
				}
				String functionName = Character.toUpperCase(javaFieldName.charAt(0)) + javaFieldName.substring(1);
				if (isBooleanValue) {
					getterMethodName = "is" + functionName;
				} else {
					getterMethodName = "get" + functionName;
				}
				Method getter = pojo.getClass().getMethod(getterMethodName);
				value = getter.invoke(pojo);
			}

			// setter
			Method setter = json.getClass().getMethod("put", new Class[] { String.class, Object.class });
			setter.invoke(json, jsonFieldName, value);

		}

		return json;
	}

}
