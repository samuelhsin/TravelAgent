package com.fastdevelopment.travelagent.android.common;

import java.util.ArrayList;
import java.util.List;

import com.fastdevelopment.travelagent.android.common.ServerConstants.ModelType;
import com.fastdevelopment.travelagent.android.model.DistanceModel;
import com.fastdevelopment.travelagent.android.model.IModel;
import com.fastdevelopment.travelagent.android.model.PlaceModel;
import com.fastdevelopment.travelagent.android.thirdparty.data.GoogleDistance;
import com.fastdevelopment.travelagent.android.thirdparty.data.GoogleDistanceElement;
import com.fastdevelopment.travelagent.android.thirdparty.data.GoogleDistanceMetrix;

public abstract class PlaceTimeFactory {

	public static List<IModel> calculatePlaceTimePath(GoogleDistanceMetrix metrixData) throws Exception {
		List<IModel> result = null;

		if (metrixData != null) {
			List<String> orgins = metrixData.getOrigin_addresses();
			List<String> destinations = metrixData.getDestination_addresses();
			List<GoogleDistance> distances = metrixData.getRows();
			if (!orgins.isEmpty() && !destinations.isEmpty() && !distances.isEmpty()) {

				int currentPlacePosition = -1;
				List<Integer> schedulePlaces = new ArrayList<Integer>();
				result = new ArrayList<IModel>();

				// put first place
				IModel placeModel = new PlaceModel(ModelType.PLACE);
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
						IModel distanceModel = new DistanceModel(ModelType.DISTANCE);
						distanceModel.setName(distanceStr);
						result.add(distanceModel);

						// put next place
						placeModel = new PlaceModel(ModelType.PLACE);
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
					IModel distanceModel = new DistanceModel(ModelType.DISTANCE);
					distanceModel.setName(distanceStr);
					result.add(distanceModel);

					// put first place (return to first place)
					placeModel = new PlaceModel(ModelType.PLACE);
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
