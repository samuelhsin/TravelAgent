package com.fastdevelopment.travelagent.android.common;

import java.net.URLEncoder;
import java.util.Vector;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.factual.driver.Factual;
import com.factual.driver.Query;
import com.fastdevelopment.travelagent.android.data.FactualPlace;
import com.fastdevelopment.travelagent.android.data.FactualQuery;
import com.fastdevelopment.travelagent.android.data.GoogleDistanceMetrix;
import com.fastdevelopment.travelagent.android.json2pojo.Json2PojoParser;

public class ThirdPartyHandler {

	private static ThirdPartyHandler cache = null;
	private static final RequestQueue queue = new RequestQueue();
	private static final Vector<ThirdPartyHandlerThread> workers = new Vector<ThirdPartyHandlerThread>();
	private static final Json2PojoParser parser = new Json2PojoParser();

	public boolean invokeDistanceTimeEvent(final Handler httpResponseHandler) throws Exception {

		ThirdPartyHandlerWorkObject work = new ThirdPartyHandlerWorkObject() {
			@Override
			public void toDo() throws Exception {

				FactualQuery factualQuery = querySpotsToFactualApi();

				GoogleDistanceMetrix googleDistanceMetrix = queryDistanceMetrixToGoogleApi(factualQuery);

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

	protected JSONObject queryDirectionsToGoogleApi(String googleApisServerKey, Handler httpResponseHandler) throws Exception {
		//
		// http request.
		//

		// String googleApisServerKey = getResources().getString(R.string.google_apis_server_key);

		// OAuthHmacSigner a;

		StringBuffer url = new StringBuffer();
		try {
			// google direction api : (for navi)
			url.append("https://maps.googleapis.com/maps/api/directions/json?");
			url.append("origin=");
			url.append(URLEncoder.encode("Chicago,IL", "UTF-8"));
			url.append("&destination=");
			url.append(URLEncoder.encode("Los+Angeles,CA", "UTF-8"));
			url.append("&waypoints=");
			url.append(URLEncoder.encode("Joplin,MO|Oklahoma+City,OK", "UTF-8"));
			url.append("&mode=");
			url.append("walking");
			url.append("&sensor=");
			url.append(URLEncoder.encode("false", "UTF-8"));
			url.append("&key=");
			url.append(URLEncoder.encode(googleApisServerKey, "UTF-8"));
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

	protected FactualQuery querySpotsToFactualApi() throws Exception {
		//
		// http request.
		//

		// Create an authenticated handle to Factual
		Factual factual = new Factual("7yBtAv0khO2jUxvs9PjR3peCzSPZorUZaZQsmIDA", "W2KImgXXxvPGxWIfKls3kwWAlBqoqf3yOHIiXvuk");

		// get 2 random records from Factual's Places table:
		String jsonStrResponse = factual.fetch("places", new Query().limit(2)).getJson();
		FactualQuery factualQuery = new FactualQuery();
		try {
			JSONObject jsonResponse = new JSONObject(jsonStrResponse);

			parser.parsingJsonValueToPojo("com.fastdevelopment.travelagent.android.data", jsonResponse, factualQuery);

		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), ExceptionUtils.getStackTrace(e));
		}

		return factualQuery;

	}

	protected GoogleDistanceMetrix queryDistanceMetrixToGoogleApi(FactualQuery factualQuery) throws Exception {
		//
		// http request.
		//

		StringBuffer url = new StringBuffer();
		try {

			FactualPlace orgin = factualQuery.getResponse().getData().get(0);
			String originSpot = orgin.getLatitude() + "," + orgin.getLongitude();
			FactualPlace destination = factualQuery.getResponse().getData().get(1);
			String destinationSpot = destination.getLatitude() + "," + destination.getLongitude();

			// google distance matrix api : (for distance time)
			url.append("http://maps.googleapis.com/maps/api/distancematrix/json?");
			url.append("origins=");
			url.append(URLEncoder.encode(originSpot, "UTF-8"));
			url.append("&destinations=");
			url.append(URLEncoder.encode(destinationSpot, "UTF-8"));
			url.append("&mode=");
			url.append(URLEncoder.encode("bicycling", "UTF-8"));
			url.append("&language=");
			url.append(URLEncoder.encode("en-US", "UTF-8"));
			url.append("&sensor=");
			url.append(URLEncoder.encode("false", "UTF-8"));

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
