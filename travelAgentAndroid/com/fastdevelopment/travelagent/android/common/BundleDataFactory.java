package com.fastdevelopment.travelagent.android.common;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;

import com.fastdevelopment.travelagent.android.common.ServerConstants.IBundleDataKey;
import com.fastdevelopment.travelagent.android.json2pojo.JsonObject;
import com.fastdevelopment.travelagent.android.thirdparty.data.GoogleDistanceMetrix;

public abstract class BundleDataFactory {

	public static Bundle createBundleData(GoogleDistanceMetrix googleDistanceMetrix, String startCountryCode, String endCountryCode) throws Exception {
		Bundle data = new Bundle();
		data.putSerializable(IBundleDataKey.GOOGLE_DISTANCE_METRIX, googleDistanceMetrix);
		data.putCharSequence(IBundleDataKey.START_COUNTRY_CODE, startCountryCode);
		data.putCharSequence(IBundleDataKey.END_COUNTRY_CODE, endCountryCode);
		return data;
	}

	@SuppressLint("Recycle")
	protected Parcel autoSettingJsonObjectToParcelObject(JsonObject jsonObject) {

		if (jsonObject == null) {
			Log.e("BundleDataFactory", "input value is null!");
			return null;
		}

		Parcel parcel = Parcel.obtain();

		Field[] fields = jsonObject.getClass().getDeclaredFields();

		for (int i = 0; i < fields.length; i++) {

			Field field = fields[i];
			String javaFieldName = field.getName();
			String cleanFunctionName = null;

			Class<?> aClass = field.getType();
			String className = aClass.getSimpleName();
			Class<?>[] types = new Class[] { aClass };

			// "is" will be replace in eclipse generating, like 'isDelete', we
			// just want to get 'Delete'
			if (javaFieldName.startsWith("is") && javaFieldName.length() >= 3 && Character.isUpperCase(javaFieldName.charAt(2))) {
				cleanFunctionName = javaFieldName.replace("is", "");
			} else {
				cleanFunctionName = javaFieldName;
			}

			String getterMethodName = null;
			String setterMethodName = null;
			try {
				// get value
				getterMethodName = Character.toUpperCase(cleanFunctionName.charAt(0)) + cleanFunctionName.substring(1);
				Method getter = jsonObject.getClass().getMethod("get" + getterMethodName);
				Object value = getter.invoke(jsonObject);

				// object setter
				if (className.equals("String")) {
					setterMethodName = "writeString";
				} else if (className.contains("List")) {
					setterMethodName = "writeList";
				} else if (className.equals("Float")) {
					setterMethodName = "writeFloat";
				} else if (className.equals("Double")) {
					setterMethodName = "writeDouble";
				} else if (className.equals("Long")) {
					setterMethodName = "writeLong";
				} else if (className.contains("Int")) {
					setterMethodName = "writeInt";
				} else if (className.equals("Object")) {
					setterMethodName = "writeObject";
				}

				Method setter = parcel.getClass().getMethod(setterMethodName, types);
				setter.invoke(parcel, new Object[] { value });
			} catch (Exception e) {
				String typeName = null;
				if (aClass != null) {
					typeName = aClass.getSimpleName();
				}
				Log.e("BundleDataFactory", "auto setting error-> json name: " + jsonObject.getClass().getSimpleName() + " getterMethodName: get" + getterMethodName + ", setterMethodName: set"
						+ setterMethodName + ", setter type: " + typeName);
			}

		}

		return parcel;
	}

}
