package com.fastdevelopment.travelagent.android.json2pojo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;

public class Json2PojoParser implements IJson2PojoParserParser {

	private final static Map<String, Map<String, Class<?>>> classMetadataMap = new HashMap<String, Map<String, Class<?>>>();

	public Json2PojoParser() {
		super();
	}

	/**
	 * get attrName Type, is like Integer, String...
	 * 
	 * @param data
	 * @param attrName
	 * @return
	 * @throws Exception
	 */
	protected final Class<?> findAttributeMetadata(IPojoData data, String attrName) throws Exception {
		Map<String, Class<?>> map = getMetadataLookup(data);
		return map.get(attrName);
	}

	/**
	 * init class's field and type metadata
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	private Map<String, Class<?>> getMetadataLookup(IPojoData data) throws Exception {
		Class<?> dataClass = data.getClass();
		String className = dataClass.getName();
		HashMap<String, Class<?>> map = (HashMap<String, Class<?>>) classMetadataMap.get(className);

		if (map == null) {
			synchronized (classMetadataMap) {
				if (map == null) {
					map = new HashMap<String, Class<?>>();
					Field fieldlist[] = dataClass.getDeclaredFields();
					for (int i = 0; i < fieldlist.length; i++) {
						Field f = fieldlist[i];
						Annotation annos[] = f.getAnnotations();
						for (Annotation anno : annos) {
							if (anno instanceof JsonDataMethodAnnotation) {
								String name = f.getName();
								Class<?> aClass = f.getType();
								map.put(name, aClass);
								break;
							}
						}
					}
					classMetadataMap.put(className, map);
				}
			}
		}
		return map;
	}

	public List<IPojoData> parsingJsonArrayToPojo(String pojoPackage, JSONArray jsonArray, Class<? extends IPojoData> classObject) throws Exception {
		if (jsonArray == null) {
			return null;
		}

		List<IPojoData> returnList = new ArrayList<IPojoData>();

		int arraySize = jsonArray.length();

		for (int i = 0; i < arraySize; i++) {

			Object item = jsonArray.get(i);

			if (item instanceof JSONObject) {

				IPojoData pojo = classObject.newInstance();

				pojo = parsingJsonValueToPojo(pojoPackage, (JSONObject) item, pojo);

				// sorting insert
				int index = Collections.binarySearch(returnList, pojo);
				if (index < 0) {
					// Add the non-existent item to the list
					int newIndex = -index - 1;
					returnList.add(newIndex, pojo);
				} else if (index == 0) {
					returnList.add(pojo);
				}

			} else if (item instanceof JSONArray) {
				// do nothings, now not support heterogeneous
			}

		}

		return returnList;
	}

	@SuppressWarnings({ "rawtypes" })
	public IPojoData parsingJsonValueToPojo(String pojoPackage, JSONObject json, IPojoData pojo) throws Exception {

		if (json == null || pojo == null) {
			return null;
		}

		Field[] fields = pojo.getClass().getDeclaredFields();

		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			String javaFieldName = field.getName();
			String jsonFieldName = null;
			String cleanFunctionName = null;
			boolean isJsonObjectValue = false;
			boolean isJsonArrayValue = false;
			boolean isFloatValue = false;

			IPojoData data = pojo;
			Object value = null;
			JSONObject jsonData = json;

			// field type
			if (field.getAnnotation(JsonDataMethodAnnotation.class) == null) {
				// skip
				continue;
			}

			Class<?> aClass = field.getType();
			String className = aClass.getSimpleName();
			Class<?>[] types = new Class[] { aClass };
			Type genericType = field.getGenericType();

			// "is" will be replace in eclipse generating, like 'isDelete', we
			// just want to get 'Delete'
			if (javaFieldName.startsWith("is") && javaFieldName.length() >= 3 && Character.isUpperCase(javaFieldName.charAt(2))) {
				cleanFunctionName = javaFieldName.replace("is", "");
			} else {
				cleanFunctionName = javaFieldName;
			}

			// replace '__' to '-', because java field name cant use '-' to represent
			// function name, but in json object '-' is legal.
			jsonFieldName = javaFieldName.replace("__", "-");

			// getter
			String getterMethodName;
			if (JsonObject.class.isAssignableFrom(aClass)) {
				getterMethodName = "JSONObject";
				isJsonObjectValue = true;
			} else if (List.class.isAssignableFrom(aClass)) {
				getterMethodName = "JSONArray";
				isJsonArrayValue = true;
			} else if (Float.class.isAssignableFrom(aClass)) {
				// because JSONObject is no getFloat method
				getterMethodName = "Double";
				isFloatValue = true;
			} else {
				// normal type, like Integer, Boolean, Long...
				if (className.startsWith("I")) {
					// because JSONObject no getInteger() function
					getterMethodName = "Int";
				} else {
					// getterMethodName =
					// Character.toUpperCase(className.charAt(0)) +
					// className.substring(1);
					getterMethodName = className;
				}

			}

			// String.class means first input paramenter is String type
			if (jsonData.has(jsonFieldName)) {
				Method getter = jsonData.getClass().getMethod("get" + getterMethodName, String.class);
				value = getter.invoke(jsonData, jsonFieldName);
			}

			// parsing json value to pojo
			if (isJsonObjectValue == true) {

				Class<?> subPojoClassName = null;

				subPojoClassName = Class.forName(pojoPackage + "." + className);

				if (value != null) {
					IPojoData subPojo = (IPojoData) subPojoClassName.newInstance();
					JSONObject subJson = (JSONObject) value;
					value = parsingJsonValueToPojo(pojoPackage, subJson, subPojo);
				}

				// IAdapterData setter
				String setterMethodName = Character.toUpperCase(cleanFunctionName.charAt(0)) + cleanFunctionName.substring(1);
				Method setter = data.getClass().getMethod("set" + setterMethodName, types);
				setter.invoke(data, new Object[] { value });

			} else if (isJsonArrayValue == true) {

				ParameterizedType paraType = (ParameterizedType) genericType;
				Type type = paraType.getActualTypeArguments()[0];
				List valueList = fillValueInListObject(pojoPackage, type, value);

				// setter
				String setterMethodName = Character.toUpperCase(cleanFunctionName.charAt(0)) + cleanFunctionName.substring(1);
				Method setter = data.getClass().getMethod("set" + setterMethodName, types);
				setter.invoke(data, new Object[] { valueList });

			} else {

				// Double -> Float
				if (value != null && isFloatValue) {
					value = (Float) ((Double) value).floatValue();
				}

				// object setter
				String setterMethodName = Character.toUpperCase(cleanFunctionName.charAt(0)) + cleanFunctionName.substring(1);
				Method setter = data.getClass().getMethod("set" + setterMethodName, types);
				setter.invoke(data, new Object[] { value });
			}

		}

		return pojo;

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected List fillValueInListObject(String pojoPackage, Type type, Object value) throws Exception {

		List valueList = null;

		if (type == null) {
			return null;
		}

		if (type instanceof Class<?>) {

			// List<?>
			Class<?> listOjbectClass = (Class<?>) type;
			if (value != null) {
				JSONArray jsonArray = (JSONArray) value;
				if (IPojoData.class.isAssignableFrom(listOjbectClass)) {
					// List<IAdapterData>
					IPojoData subPojo = (IPojoData) listOjbectClass.newInstance();
					List<IPojoData> pojoResult = parsingJsonArrayToPojo(pojoPackage, jsonArray, subPojo.getClass());
					valueList = new ArrayList();
					int resultSize = pojoResult.size();
					for (int index = 0; index < resultSize; index++) {
						valueList.add(listOjbectClass.cast(pojoResult.get(index)));
					}

				} else if (String.class.isAssignableFrom(listOjbectClass) || Integer.class.isAssignableFrom(listOjbectClass) || Long.class.isAssignableFrom(listOjbectClass)
						|| Boolean.class.isAssignableFrom(listOjbectClass) || Double.class.isAssignableFrom(listOjbectClass) || Float.class.isAssignableFrom(listOjbectClass)
						|| Date.class.isAssignableFrom(listOjbectClass)) {
					// List<String>,List<Integer>,List<Long>,List<Boolean>,List<Float>,List<Double>,List<Date>
					valueList = new ArrayList();
					int jsonArrayLength = jsonArray.length();
					for (int index = 0; index < jsonArrayLength; index++) {
						valueList.add(listOjbectClass.cast(jsonArray.get(index)));
					}
				}
			}
		} else if (type.toString().contains("java.util.List<")) {
			// List<List<?>>
			valueList = new ArrayList();
			JSONArray jsonArray = (JSONArray) value;
			if (jsonArray.length() > 0) {
				ParameterizedType paraType = (ParameterizedType) type;
				Type innerListType = paraType.getActualTypeArguments()[0];
				valueList.add(fillValueInListObject(pojoPackage, innerListType, jsonArray.get(0)));
			}
		}

		return valueList;

	}

	@SuppressLint("SimpleDateFormat")
	protected final Object buildAttributeValue(Class<?> aClass, String strValue) throws Exception {
		if (aClass == String.class) {
			return strValue;
		} else if (aClass == Integer.class) {
			return new Integer(strValue);
		} else if (aClass == Long.class) {
			return new Long(strValue);
		} else if (aClass == Date.class) {
			String[] date = strValue.split(" ");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			Date dateTime = sdf.parse(date[0]);
			return dateTime;
		} else if (aClass == Boolean.class) {
			return new Boolean(strValue.equalsIgnoreCase("true"));
		} else if (aClass == Double.class) {
			return new Double(strValue);
		} else if (aClass == Float.class) {
			return new Float(strValue);
		}
		return null;
	}

	public int[] randomSequential(int randomRange) {
		// 要排序的一组数字
		if (randomRange > 0) {
			int[] seed = new int[randomRange];
			for (int i = 0; i < randomRange; i++) {
				seed[i] = i;
			}
			int len = seed.length;
			int[] result = new int[len];
			Random random = new Random();
			for (int i = 0; i < len; i++) {
				// 得到一个位置
				int r = random.nextInt(len - i);
				// 得到那个位置的数值
				result[i] = seed[r];
				// 将最后一个未用的数字放到这里
				seed[r] = seed[len - 1 - i];
			}
			return result;
		} else {
			return new int[] {};
		}

	}

}
