package com.fastdevelopment.travelagent.android.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.util.Log;

import com.fastdevelopment.travelagent.android.R;
import com.fastdevelopment.travelagent.android.common.ServerConstants.PojoModelType;
import com.fastdevelopment.travelagent.android.model.DistanceModel;
import com.fastdevelopment.travelagent.android.model.IPojoModel;
import com.fastdevelopment.travelagent.android.model.PlaceModel;
import com.fastdevelopment.travelagent.android.thirdparty.data.GoogleDistance;
import com.fastdevelopment.travelagent.android.thirdparty.data.GoogleDistanceElement;
import com.fastdevelopment.travelagent.android.thirdparty.data.GoogleDistanceMetrix;

public abstract class PlaceTimeFactory {

	private static final String TAG = PlaceTimeFactory.class.getSimpleName();

	public static GoogleDistanceMetrix removePlace(GoogleDistanceMetrix metrixData, String placeName) throws Exception {

		int index = -1;

		int placeNumber = metrixData.getOrigin_addresses().size();
		for (int i = 0; i < placeNumber; i++) {
			String temp = metrixData.getOrigin_addresses().get(i);
			if (temp != null && temp.equals(placeName)) {
				index = i;
				break;
			}
		}

		if (index != -1) {
			metrixData.getOrigin_addresses().remove(index);
			metrixData.getDestination_addresses().remove(index);
			metrixData.getRows().remove(index);

			for (Iterator<GoogleDistance> iterator = metrixData.getRows().iterator(); iterator.hasNext();) {
				GoogleDistance googleDistance = iterator.next();
				googleDistance.getElements().remove(index);
			}
		} else {
			Log.w(TAG, "cant find deleted place name in grid's item list");
		}

		return metrixData;
	}

	public static List<IPojoModel> calculatePlaceTimePathByOrder(List<String> placeNameOrder, GoogleDistanceMetrix metrixData) throws Exception {
		List<IPojoModel> result = null;

		if (placeNameOrder != null && !placeNameOrder.isEmpty()) {

			List<String> orgins = metrixData.getOrigin_addresses();
			List<String> destinations = metrixData.getDestination_addresses();
			List<GoogleDistance> distances = metrixData.getRows();

			result = new ArrayList<IPojoModel>();

			if (!orgins.isEmpty() && !destinations.isEmpty() && !distances.isEmpty()) {

				int placeNumber = placeNameOrder.size();

				for (int i = 0; i < placeNumber; i++) {

					String placeName = placeNameOrder.get(i);

					int metrixDataIndexForPlaceName = orgins.indexOf(placeName);

					if (metrixDataIndexForPlaceName != -1) {
						// put place
						IPojoModel placeModel = new PlaceModel(PojoModelType.PLACE);
						placeModel.setName(orgins.get(metrixDataIndexForPlaceName));
						result.add(placeModel);

						int nextIndex = i + 1;

						if (nextIndex < placeNumber) {
							// put distance
							GoogleDistance googleDistanceInCurrentPlace = distances.get(metrixDataIndexForPlaceName);

							if (googleDistanceInCurrentPlace != null) {
								List<GoogleDistanceElement> googleDistanceElement = googleDistanceInCurrentPlace.getElements();
								int nextMetrixDataIndexForPlaceName = orgins.indexOf(placeNameOrder.get(nextIndex));
								GoogleDistanceElement distance = googleDistanceElement.get(nextMetrixDataIndexForPlaceName);
								String distanceStr = null;
								if (distance != null && "OK".equals(distance.getStatus())) {
									distanceStr = distance.getDistance().getText();
								} else {
									distanceStr = ServerConfig.resources.getString(R.string.unknown);
								}
								IPojoModel distanceModel = new DistanceModel(PojoModelType.DISTANCE);
								distanceModel.setName(distanceStr);
								result.add(distanceModel);
							}

						}
					}

				}
			}

		}

		return result;
	}

	public static List<IPojoModel> calculatePlaceTimePath(GoogleDistanceMetrix metrixData) throws Exception {
		List<IPojoModel> result = null;

		if (metrixData != null) {
			List<String> orgins = metrixData.getOrigin_addresses();
			List<String> destinations = metrixData.getDestination_addresses();
			List<GoogleDistance> distances = metrixData.getRows();
			if (!orgins.isEmpty() && !destinations.isEmpty() && !distances.isEmpty()) {

				int currentPlacePosition = -1;
				List<Integer> schedulePlaces = new ArrayList<Integer>();
				result = new ArrayList<IPojoModel>();

				// put first place
				IPojoModel placeModel = new PlaceModel(PojoModelType.PLACE);
				placeModel.setName(orgins.get(0));
				result.add(placeModel);
				currentPlacePosition = 0;
				schedulePlaces.add(0);

				// ---begin schedule---
				int placeNumber = orgins.size() - 1;

				// get currentPlacePosition's distance row and compare elements
				// choose minimum distance place and refresh currentPlacePosition and schedulePlaces
				for (int i = 0; i < placeNumber; i++) {
					GoogleDistance row = metrixData.getRows().get(currentPlacePosition);
					List<GoogleDistanceElement> elements = row.getElements();
					long benchmark = -1;
					int benchmarkIndex = -1;
					String distanceStr = null;
					// calculate minimum distance
					for (int j = 0; j < elements.size(); j++) {
						GoogleDistanceElement element = elements.get(j);
						if ("OK".equals(element.getStatus())) {
							long temp = Long.valueOf(element.getDistance().getValue());
							if (temp != 0 && (temp < benchmark || benchmark == -1) && !schedulePlaces.contains(j)) {
								benchmark = temp;
								benchmarkIndex = j;
								distanceStr = element.getDistance().getText();
							}
						}

					}

					if (benchmarkIndex != -1) {
						// put distance
						IPojoModel distanceModel = new DistanceModel(PojoModelType.DISTANCE);
						distanceModel.setName(distanceStr);
						result.add(distanceModel);

						// put next place
						placeModel = new PlaceModel(PojoModelType.PLACE);
						placeModel.setName(orgins.get(benchmarkIndex));
						result.add(placeModel);
						currentPlacePosition = benchmarkIndex;
						schedulePlaces.add(benchmarkIndex);
					}

				}

				// ---end schedule---

				// put distance
				GoogleDistance row = metrixData.getRows().get(currentPlacePosition);
				List<GoogleDistanceElement> elements = row.getElements();
				GoogleDistanceElement element = elements.get(0);
				if ("OK".equals(element.getStatus())) {
					String distanceStr = element.getDistance().getText();
					IPojoModel distanceModel = new DistanceModel(PojoModelType.DISTANCE);
					distanceModel.setName(distanceStr);
					result.add(distanceModel);

					// put first place (return to first place)
					placeModel = new PlaceModel(PojoModelType.PLACE);
					placeModel.setName(orgins.get(0));
					result.add(placeModel);
					currentPlacePosition = 0;
					schedulePlaces.add(0);
				}

			}

		}

		return result;
	}
}
