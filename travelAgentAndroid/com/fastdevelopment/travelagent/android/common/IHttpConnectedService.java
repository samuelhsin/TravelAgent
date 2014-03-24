package com.fastdevelopment.travelagent.android.common;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;

public interface IHttpConnectedService {

	public void initHttpClient(int servicePort) throws Exception;

	public HttpClient getHttpClient() throws Exception;

	public boolean shutdown() throws Exception;

	public String getUrlContents(String theUrl) throws Exception;

	public String doGetByHttpClientAndReturnJsonStr(String url) throws Exception;

	public String doPostByHttpClientAndReturnJsonStr(String url, HttpEntity reqEntity, boolean isJsonEntity) throws Exception;

	public String doPostByUrlConnection(String sURL, String data, String cookie, String referer, String charset, String keystorePath, String keystorePass) throws Exception;

	public String doGetByUrlConnection(String sURL, String cookie, String referer, String charset) throws Exception;

}
