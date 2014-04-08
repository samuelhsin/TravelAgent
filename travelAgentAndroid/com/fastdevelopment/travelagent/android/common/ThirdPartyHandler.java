package com.fastdevelopment.travelagent.android.common;

import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.factual.driver.Factual;
import com.factual.driver.Query;
import com.fastdevelopment.travelagent.android.R;
import com.fastdevelopment.travelagent.android.common.ServerConstants.Encode;
import com.fastdevelopment.travelagent.android.common.ServerConstants.GoogleDistanceMetrixMode;
import com.fastdevelopment.travelagent.android.common.ServerConstants.IFactualTableName;
import com.fastdevelopment.travelagent.android.common.ServerConstants.Locale;
import com.fastdevelopment.travelagent.android.data.FactualPlace;
import com.fastdevelopment.travelagent.android.data.FactualQuery;
import com.fastdevelopment.travelagent.android.data.GoogleDistanceMetrix;
import com.fastdevelopment.travelagent.android.json2pojo.Json2PojoParser;

public class ThirdPartyHandler {

	private static ThirdPartyHandler cache = null;
	private static final RequestQueue queue = new RequestQueue();
	private static final Vector<ThirdPartyHandlerThread> workers = new Vector<ThirdPartyHandlerThread>();
	private static final Json2PojoParser parser = new Json2PojoParser();

	public boolean invokeDistanceTimeEvent(final Handler httpResponseHandler, final String countryCode) throws Exception {

		ThirdPartyHandlerWorkObject work = new ThirdPartyHandlerWorkObject() {
			@Override
			public void toDo() throws Exception {

				FactualQuery factualQuery = querySpotsToFactualApi(countryCode);

				FactualQuery factualQuery2 = querySpotsToFactualApi(countryCode);

				GoogleDistanceMetrix googleDistanceMetrix = queryDistanceMetrixToGoogleApi(factualQuery, factualQuery2);

				Message msg = new Message();
				Bundle data = new Bundle();
				data.putString("result", "result");
				msg.setData(data);
				httpResponseHandler.sendMessage(msg);

			}
		};

		queue.put(new Request(work));

		return true;

	}

	protected FactualQuery querySpotsToFactualApi(String countryCode) throws Exception {
		//
		// http request.
		//

		// Create an authenticated handle to Factual
		Factual factual = new Factual(ServerConfig.resource.getString(R.string.factual_key), ServerConfig.resource.getString(R.string.factual_secret));

		// get 2 random records from Factual's Places table:
		// String jsonStrResponse = factual.fetch(IFactualTableName.PLACES, new Query().limit(2)).getJson();

		Query q = new Query();
		q.and(q.field("country").isEqual(countryCode)).limit(3);
		String jsonStrResponse = factual.fetch(IFactualTableName.PLACES, q).getJson();

		FactualQuery factualQuery = new FactualQuery();
		try {
			JSONObject jsonResponse = new JSONObject(jsonStrResponse);

			parser.parsingJsonValueToPojo("com.fastdevelopment.travelagent.android.data", jsonResponse, factualQuery);

		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), ExceptionUtils.getStackTrace(e));
		}

		return factualQuery;

	}

	protected GoogleDistanceMetrix queryDistanceMetrixToGoogleApi(FactualQuery factualQueryForOrgins, FactualQuery factualQueryForDestinations) throws Exception {
		//
		// http request.
		//

		StringBuffer url = new StringBuffer();
		try {

			StringBuffer originSpot = new StringBuffer();
			List<FactualPlace> orgins = factualQueryForOrgins.getResponse().getData();
			for (Iterator<FactualPlace> iterator = orgins.iterator(); iterator.hasNext();) {
				FactualPlace orgin = iterator.next();
				originSpot.append(orgin.getLatitude() + "," + orgin.getLongitude());
				if (iterator.hasNext()) {
					originSpot.append("|");
				}
			}

			StringBuffer destinationSpot = new StringBuffer();
			List<FactualPlace> destinations = factualQueryForDestinations.getResponse().getData();
			for (Iterator<FactualPlace> iterator = destinations.iterator(); iterator.hasNext();) {
				FactualPlace destination = iterator.next();
				destinationSpot.append(destination.getLatitude() + "," + destination.getLongitude());
				if (iterator.hasNext()) {
					destinationSpot.append("|");
				}
			}

			// google distance matrix api : (for distance time)
			url.append("http://maps.googleapis.com/maps/api/distancematrix/json?");
			url.append("origins=");
			url.append(URLEncoder.encode(originSpot.toString(), Encode.UTF_8.toString()));
			url.append("&destinations=");
			url.append(URLEncoder.encode(destinationSpot.toString(), Encode.UTF_8.toString()));
			url.append("&mode=");
			url.append(URLEncoder.encode(GoogleDistanceMetrixMode.BICYCLING.toString(), Encode.UTF_8.toString()));
			url.append("&language=");
			url.append(URLEncoder.encode(Locale.EN_US.toString(), Encode.UTF_8.toString()));
			url.append("&sensor=");
			url.append(URLEncoder.encode("false", Encode.UTF_8.toString()));

		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), ExceptionUtils.getStackTrace(e));
		}
		IHttpConnectedService httpService = new RestConnectedService();
		GoogleDistanceMetrix googleDistanceMetrix = new GoogleDistanceMetrix();
		JSONObject json = null;
		try {
			httpService.initHttpClient(443);
			String jsonStr = httpService.doGetByHttpClientAndReturnJsonStr(url.toString());
			if (jsonStr != null) {
				json = new JSONObject(jsonStr);
				parser.parsingJsonValueToPojo("com.fastdevelopment.travelagent.android.data", json, googleDistanceMetrix);
			}
		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), ExceptionUtils.getStackTrace(e));
		}

		return googleDistanceMetrix;

	}

	protected JSONObject queryDirectionsToGoogleApi(Handler httpResponseHandler) throws Exception {
		//
		// http request.
		//

		String googleApisServerKey = ServerConfig.resource.getString(R.string.google_apis_android_server_key);

		// OAuthHmacSigner a;

		StringBuffer url = new StringBuffer();
		try {
			// google direction api : (for navi)
			url.append("https://maps.googleapis.com/maps/api/directions/json?");
			url.append("origin=");
			url.append(URLEncoder.encode("Chicago,IL", Encode.UTF_8.toString()));
			url.append("&destination=");
			url.append(URLEncoder.encode("Los+Angeles,CA", Encode.UTF_8.toString()));
			url.append("&waypoints=");
			url.append(URLEncoder.encode("Joplin,MO|Oklahoma+City,OK", Encode.UTF_8.toString()));
			url.append("&mode=");
			url.append(GoogleDistanceMetrixMode.WALKING);
			url.append("&sensor=");
			url.append(URLEncoder.encode("false", Encode.UTF_8.toString()));
			url.append("&key=");
			url.append(URLEncoder.encode(googleApisServerKey, Encode.UTF_8.toString()));
		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), ExceptionUtils.getStackTrace(e));
		}

		IHttpConnectedService httpService = new RestConnectedService();
		JSONObject json = null;
		try {
			httpService.initHttpClient(443);
			String jsonStr = httpService.doGetByHttpClientAndReturnJsonStr(url.toString());
			if (jsonStr != null) {
				json = new JSONObject(jsonStr);
			}
		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), ExceptionUtils.getStackTrace(e));
		}

		return json;

	}

	// **************************************************************************
	//
	// The singleton functions
	//
	// **************************************************************************

	public static ThirdPartyHandler getInstance() throws Exception {
		if (cache == null) {
			return createCache();
		}
		return cache;
	}

	public static synchronized ThirdPartyHandler createCache() throws Exception {
		if (cache == null) {
			cache = new ThirdPartyHandler();
		}
		if (workers.isEmpty()) {
			ThirdPartyHandlerThread tp = new ThirdPartyHandlerThread(queue);
			new Thread(tp).start();
			workers.add(tp);
		}

		return cache;
	}
}
