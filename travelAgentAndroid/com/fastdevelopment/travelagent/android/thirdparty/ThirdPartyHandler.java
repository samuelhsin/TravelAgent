package com.fastdevelopment.travelagent.android.thirdparty;

import java.net.URLEncoder;
import java.util.ArrayList;
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
import com.fastdevelopment.travelagent.android.common.BundleDataFactory;
import com.fastdevelopment.travelagent.android.common.IHttpConnectedService;
import com.fastdevelopment.travelagent.android.common.Request;
import com.fastdevelopment.travelagent.android.common.RequestQueue;
import com.fastdevelopment.travelagent.android.common.RestConnectedService;
import com.fastdevelopment.travelagent.android.common.ServerConfig;
import com.fastdevelopment.travelagent.android.common.ServerConstants.CountryCode;
import com.fastdevelopment.travelagent.android.common.ServerConstants.Encode;
import com.fastdevelopment.travelagent.android.common.ServerConstants.GoogleDistanceMetrixMode;
import com.fastdevelopment.travelagent.android.common.ServerConstants.IFactualTableName;
import com.fastdevelopment.travelagent.android.common.ServerConstants.IGoogleDirectionApiConst;
import com.fastdevelopment.travelagent.android.common.ServerConstants.IGoogleDistanceMetrixApiConst;
import com.fastdevelopment.travelagent.android.common.ServerConstants.IJson2PojoConst;
import com.fastdevelopment.travelagent.android.json2pojo.Json2PojoParser;
import com.fastdevelopment.travelagent.android.thirdparty.data.FactualPlace;
import com.fastdevelopment.travelagent.android.thirdparty.data.FactualQuery;
import com.fastdevelopment.travelagent.android.thirdparty.data.GoogleDistanceMetrix;

public class ThirdPartyHandler {

	private static ThirdPartyHandler cache = null;
	private static final RequestQueue queue = new RequestQueue();
	private static final Vector<ThirdPartyHandlerThread> workers = new Vector<ThirdPartyHandlerThread>();
	private static final Json2PojoParser parser = new Json2PojoParser();

	public boolean invokeDistanceTimeEvent(final Handler httpResponseHandler, final CountryCode countryCode) throws Exception {

		ThirdPartyHandlerWorkObject work = new ThirdPartyHandlerWorkObject() {
			@Override
			public void toDo() throws Exception {

				FactualQuery factualQuery = querySpotsToFactualApi(countryCode);

				GoogleDistanceMetrix googleDistanceMetrix = queryDistanceMetrixToGoogleApiByFactualQueryObject(factualQuery, factualQuery);

				Message msg = new Message();
				Bundle data = BundleDataFactory.createBundleData(googleDistanceMetrix, countryCode.toString(), countryCode.toString());
				msg.setData(data);
				httpResponseHandler.sendMessage(msg);

			}
		};

		queue.put(new Request(work));

		return true;

	}

	public boolean invokeDistanceTimeEvent(final Handler httpResponseHandler, final ArrayList<String> places, final CountryCode countryCode) throws Exception {

		ThirdPartyHandlerWorkObject work = new ThirdPartyHandlerWorkObject() {
			@Override
			public void toDo() throws Exception {

				StringBuffer spots = new StringBuffer();
				for (Iterator<String> iterator = places.iterator(); iterator.hasNext();) {
					String place = iterator.next();
					spots.append(place);
					if (iterator.hasNext()) {
						spots.append("|");
					}
				}

				GoogleDistanceMetrix googleDistanceMetrix = queryDistanceMetrixToGoogleApi(spots.toString(), spots.toString());

				Message msg = new Message();
				Bundle data = BundleDataFactory.createBundleData(googleDistanceMetrix, countryCode.toString(), countryCode.toString());
				msg.setData(data);
				httpResponseHandler.sendMessage(msg);

			}
		};

		queue.put(new Request(work));

		return true;

	}

	protected FactualQuery querySpotsToFactualApi(CountryCode countryCode) throws Exception {
		//
		// http request.
		//

		// Create an authenticated handle to Factual
		Factual factual = new Factual(ServerConfig.resources.getString(R.string.factual_key), ServerConfig.resources.getString(R.string.factual_secret));

		// get 2 random records from Factual's Places table:
		// String jsonStrResponse = factual.fetch(IFactualTableName.PLACES, new Query().limit(2)).getJson();

		Query q = new Query();
		q.and(q.field(IFactualTableName.COUNTRY).isEqual(countryCode.getLowerCaseString())).limit(3);
		String jsonStrResponse = factual.fetch(IFactualTableName.PLACES, q).getJson();

		FactualQuery factualQuery = new FactualQuery();
		try {
			JSONObject jsonResponse = new JSONObject(jsonStrResponse);

			parser.parsingJsonValueToPojo(IJson2PojoConst.JSON2POJO_DATA_PACKAGE, jsonResponse, factualQuery);

		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), ExceptionUtils.getStackTrace(e));
		}

		return factualQuery;

	}

	protected GoogleDistanceMetrix queryDistanceMetrixToGoogleApiByFactualQueryObject(FactualQuery factualQueryForOrgins, FactualQuery factualQueryForDestinations) throws Exception {

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

		GoogleDistanceMetrix googleDistanceMetrix = queryDistanceMetrixToGoogleApi(originSpot.toString(), destinationSpot.toString());

		return googleDistanceMetrix;
	}

	protected GoogleDistanceMetrix queryDistanceMetrixToGoogleApi(String originSpot, String destinationSpot) throws Exception {
		//
		// http request.
		//

		StringBuffer url = new StringBuffer();
		try {

			// google distance matrix api : (for distance time)
			url.append(IGoogleDistanceMetrixApiConst.JSON_URL);
			url.append("?");
			url.append(IGoogleDistanceMetrixApiConst.PARAM_ORIGINS);
			url.append("=");
			url.append(URLEncoder.encode(originSpot, Encode.UTF_8.toString()));
			url.append("&");
			url.append(IGoogleDistanceMetrixApiConst.PARAM_DESTINATIONS);
			url.append("=");
			url.append(URLEncoder.encode(destinationSpot, Encode.UTF_8.toString()));
			url.append("&");
			url.append(IGoogleDistanceMetrixApiConst.PARAM_MODE);
			url.append("=");
			url.append(URLEncoder.encode(GoogleDistanceMetrixMode.BICYCLING.toString(), Encode.UTF_8.toString()));
			url.append("&");
			url.append(IGoogleDistanceMetrixApiConst.PARAM_LANGUAGE);
			url.append("=");
			url.append(URLEncoder.encode(ServerConfig.resources.getConfiguration().locale.toString(), Encode.UTF_8.toString()));
			url.append("&");
			url.append(IGoogleDistanceMetrixApiConst.PARAM_SENOR);
			url.append("=");
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
				parser.parsingJsonValueToPojo(IJson2PojoConst.JSON2POJO_DATA_PACKAGE, json, googleDistanceMetrix);
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

		String googleApisServerKey = ServerConfig.resources.getString(R.string.google_apis_android_server_key);

		// OAuthHmacSigner a;

		StringBuffer url = new StringBuffer();
		try {
			// google direction api : (for navi)
			url.append(IGoogleDirectionApiConst.JSON_URL);
			url.append("?");
			url.append(IGoogleDirectionApiConst.PARAM_ORIGIN);
			url.append("=");
			url.append(URLEncoder.encode("Chicago,IL", Encode.UTF_8.toString()));
			url.append("&");
			url.append(IGoogleDirectionApiConst.PARAM_DESTINATION);
			url.append("=");
			url.append(URLEncoder.encode("Los+Angeles,CA", Encode.UTF_8.toString()));
			url.append("&");
			url.append(IGoogleDirectionApiConst.PARAM_WAYPOINTS);
			url.append("=");
			url.append(URLEncoder.encode("Joplin,MO|Oklahoma+City,OK", Encode.UTF_8.toString()));
			url.append("&");
			url.append(IGoogleDirectionApiConst.PARAM_MODE);
			url.append("=");
			url.append(GoogleDistanceMetrixMode.WALKING);
			url.append("&");
			url.append(IGoogleDirectionApiConst.PARAM_SENOR);
			url.append("=");
			url.append(URLEncoder.encode("false", Encode.UTF_8.toString()));
			url.append("&");
			url.append(IGoogleDirectionApiConst.PARAM_KEY);
			url.append("=");
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
