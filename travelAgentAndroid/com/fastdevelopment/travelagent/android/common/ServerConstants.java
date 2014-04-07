package com.fastdevelopment.travelagent.android.common;

public abstract class ServerConstants {

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

	public static interface IFactualTableName {
		public static final String PLACES = "places";
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
