package com.fastdevelopment.travelagent.android.common;

public abstract class ServerConstants {

	public static enum PojoModelType {
		PLACE, DISTANCE, PLAN
	}

	public static enum OrmModelType {
		USER, PLAN
	}

	public static enum CountryCode {
		FR, US, TW;
		public String getCountryName() {
			switch (this) {
			case FR:
				return "France";
			case US:
				return "United State";
			case TW:
				return "Taiwan";
			default:
				throw new IllegalArgumentException();
			}
		}

		public String getString() {
			switch (this) {
			case FR:
				return "fr";
			case US:
				return "us";
			case TW:
				return "tw";
			default:
				throw new IllegalArgumentException();
			}
		}
	}

	public static enum Locale {
		EN_US;
		public String toString() {
			switch (this) {
			case EN_US:
				return "en-US";
			default:
				throw new IllegalArgumentException();
			}
		}
	}

	public static enum Encode {
		UTF_8;
		public String toString() {
			switch (this) {
			case UTF_8:
				return "UTF-8";
			default:
				throw new IllegalArgumentException();
			}
		}
	}

	public static interface IBundleDataKey {
		public static final String GOOGLE_DISTANCE_METRIX = "googleDistanceMetrix";
	}

	public static interface IFactualTableName {
		public static final String PLACES = "places";
		public static final String COUNTRY = "country";
	}

	public interface IJson2PojoConst {
		public static final String JSON2POJO_DATA_PACKAGE = "com.fastdevelopment.travelagent.android.thirdparty.data";
	}

	public interface IFactualApiConst {

	}

	public interface IGoogleDirectionApiConst {
		public static final String JSON_URL = "https://maps.googleapis.com/maps/api/directions/json";
		public static final String PARAM_ORIGIN = "origin";
		public static final String PARAM_DESTINATION = "destination";
		public static final String PARAM_WAYPOINTS = "waypoints";
		public static final String PARAM_MODE = "mode";
		public static final String PARAM_KEY = "key";
		public static final String PARAM_SENOR = "sensor";
	}

	public interface IGoogleDistanceMetrixApiConst {
		public static final String JSON_URL = "http://maps.googleapis.com/maps/api/distancematrix/json";
		public static final String PARAM_ORIGINS = "origins";
		public static final String PARAM_DESTINATIONS = "destinations";
		public static final String PARAM_MODE = "mode";
		public static final String PARAM_LANGUAGE = "language";
		public static final String PARAM_SENOR = "sensor";
	}

	public static enum GoogleDistanceMetrixMode {
		BICYCLING, WALKING;
		public String toString() {
			switch (this) {
			case BICYCLING:
				return "bicycling";
			case WALKING:
				return "walking";
			default:
				throw new IllegalArgumentException();
			}
		}
	}

}
