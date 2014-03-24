package com.fastdevelopment.travelagent.android.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyStore;
import java.security.Security;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.util.Log;

@SuppressWarnings("deprecation")
public class RestConnectedService implements IHttpConnectedService {

	private HttpClient httpclient = null;

	@Override
	public void initHttpClient(int servicePort) throws Exception {
		String keystorePath = null;
		String keystorePass = null;

		httpclient = this.createHttpsClient(servicePort, keystorePath, keystorePass);

	}

	@Override
	public HttpClient getHttpClient() throws Exception {
		return httpclient;
	}

	@Override
	public String getUrlContents(String theUrl) throws Exception {
		StringBuilder content = new StringBuilder();

		// many of these calls can throw exceptions, so i've just
		// wrapped them all in one try/catch statement.
		try {
			// create a url object
			URL url = new URL(theUrl);

			// create a urlconnection object
			URLConnection urlConnection = url.openConnection();

			// wrap the urlconnection in a bufferedreader
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

			String line;

			// read from the urlconnection via the bufferedreader
			while ((line = bufferedReader.readLine()) != null) {
				content.append(line + "\n");
			}
			bufferedReader.close();
		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), "connection error!");
		}
		return content.toString();
	}

	@Override
	public String doGetByHttpClientAndReturnJsonStr(String url) throws Exception {
		String jsonStr = null;

		try {

			if (httpclient == null) {
				Log.w(this.getClass().getSimpleName(), "http client is null!");
				return null;
			}

			HttpGet httppost = new HttpGet(url);

			// content type
			httppost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

			// request
			HttpResponse response = httpclient.execute(httppost);

			// response
			try {
				HttpEntity resEntity = response.getEntity();

				if (resEntity != null) {

					BufferedReader reader = new BufferedReader(new InputStreamReader(resEntity.getContent(), "UTF-8"));
					StringBuilder builder = new StringBuilder();
					for (String line = null; (line = reader.readLine()) != null;) {
						builder.append(line).append("\n");
					}
					jsonStr = builder.toString();
					// virtuoso 2013 plus will append "for(;;);" in return json
					// value;

					if (jsonStr != null && jsonStr.startsWith("for(;;);")) {

						jsonStr = jsonStr.replace("for(;;);", "");

					}

				}
				EntityUtils.consume(resEntity);
			} finally {

			}

		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), "connection error!");
			jsonStr = null;
		}

		return jsonStr;
	}

	@Override
	public String doPostByHttpClientAndReturnJsonStr(String url, HttpEntity reqEntity, boolean isJsonEntity) throws Exception {
		String jsonStr = null;

		try {

			if (httpclient == null) {
				Log.w(this.getClass().getSimpleName(), "http client is null!");
				return null;
			}

			HttpPost httppost = new HttpPost(url);

			// content type
			if (isJsonEntity) {
				httppost.setHeader("Content-Type", "application/json;charset=UTF-8");
			} else {
				httppost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			}

			// entity
			httppost.setEntity(reqEntity);

			// request

			HttpResponse response = httpclient.execute(httppost);

			// response
			try {

				if (null == response) {
					return null;
				}

				HttpEntity resEntity = response.getEntity();

				if (resEntity != null) {

					BufferedReader reader = new BufferedReader(new InputStreamReader(resEntity.getContent(), "UTF-8"));
					StringBuilder builder = new StringBuilder();
					for (String line = null; (line = reader.readLine()) != null;) {
						builder.append(line).append("\n");
					}

					jsonStr = builder.toString();

					if (jsonStr != null) {
						jsonStr = jsonStr.replace("for(;;);", "");
					}

				}
				EntityUtils.consume(resEntity);
			} finally {

			}

		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), "connection error!");
			jsonStr = null;
		}

		return jsonStr;
	}

	protected HttpClient createHttpClient(int port) throws Exception {
		HttpClient httpclient = null;

		try {

			CusSchemeSocketFactory socketFactory = new CusSchemeSocketFactory();

			Scheme httpsScheme = new Scheme("http", port, socketFactory);

			httpclient = new DefaultHttpClient();
			httpclient.getConnectionManager().getSchemeRegistry().register(httpsScheme);

		} catch (Exception ex) {

			Log.e(this.getClass().getSimpleName(), ex.getMessage());

			httpclient = null;
		}

		return httpclient;
	}

	protected HttpClient createHttpsClient(int port, String keystorePath, String keystorePass) throws Exception {
		HttpClient httpclient = null;

		try {
			java.lang.System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
			CusX509TrustManager tm = new CusX509TrustManager(keystorePath, keystorePass);
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, new CusX509TrustManager[] { tm }, null);

			SSLSocketFactory ssf = new SSLSocketFactory(context, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			// Register our new socket factory with the typical SSL port and the
			// correct protocol name.
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			// SSLSocketFactory ssf = new SSLSocketFactory(trustStore,
			// keystorePass);

			schemeRegistry.register(new Scheme("https", port, ssf));

			final HttpParams httpParams = new BasicHttpParams();

			ThreadSafeClientConnManager mgr = new ThreadSafeClientConnManager(schemeRegistry);

			httpclient = new DefaultHttpClient(mgr, httpParams);

		} catch (Exception ex) {

			Log.e(this.getClass().getSimpleName(), ex.getMessage());

			httpclient = null;
		}

		return httpclient;
	}

	@Override
	public String doPostByUrlConnection(String sURL, String data, String cookie, String referer, String charset, String keystorePath, String keystorePass) throws Exception {
		StringBuffer sb = new StringBuffer();
		java.io.BufferedWriter wr = null;
		boolean isSSL = false;
		HttpURLConnection URLConn = null;
		try {

			if (sURL.indexOf("https") != -1) {
				isSSL = true;
			}

			URL url = new URL(sURL);

			if (isSSL) {
				javax.net.ssl.SSLSocketFactory sslsocketfactory = createEasySSLContext(keystorePath, keystorePass).getSocketFactory();
				URLConn = (HttpsURLConnection) url.openConnection();
				((HttpsURLConnection) URLConn).setSSLSocketFactory(sslsocketfactory);
			} else {
				URLConn = (HttpURLConnection) url.openConnection();
			}

			URLConn.setDoOutput(true);
			URLConn.setDoInput(true);
			URLConn.setRequestMethod("POST");
			URLConn.setUseCaches(false);
			URLConn.setAllowUserInteraction(true);
			HttpURLConnection.setFollowRedirects(true);
			URLConn.setInstanceFollowRedirects(true);

			URLConn.setRequestProperty("User-agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; zh-TW; rv:1.9.1.2) " + "Gecko/20090729 Firefox/3.5.2 GTB5 (.NET CLR 3.5.30729)");
			URLConn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			URLConn.setRequestProperty("Accept-Language", "zh-tw,en-us;q=0.7,en;q=0.3");
			URLConn.setRequestProperty("Accept-Charse", "Big5,utf-8;q=0.7,*;q=0.7");
			if (cookie != null)
				URLConn.setRequestProperty("Cookie", cookie);
			if (referer != null)
				URLConn.setRequestProperty("Referer", referer);

			URLConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			URLConn.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));

			java.io.DataOutputStream dos = new java.io.DataOutputStream(URLConn.getOutputStream());
			dos.writeBytes(data);

			java.io.BufferedReader rd = new java.io.BufferedReader(new java.io.InputStreamReader(URLConn.getInputStream(), charset));
			String line;

			// output
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}

			rd.close();
		} catch (Exception e) {

			Log.e(this.getClass().getSimpleName(), "connection error!");

		} finally {
			if (wr != null) {
				try {
					wr.close();
				} catch (java.io.IOException ex) {
					ex.printStackTrace();
					Log.e(this.getClass().getSimpleName(), ex.getMessage());
				}
				wr = null;
			}
		}

		return sb.toString();
	}

	@Override
	public String doGetByUrlConnection(String sURL, String cookie, String referer, String charset) throws Exception {
		StringBuffer sb = new StringBuffer();
		BufferedReader in = null;
		try {
			URL url = new URL(sURL);
			HttpURLConnection URLConn = (HttpURLConnection) url.openConnection();
			URLConn.setRequestProperty("User-agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; zh-TW; rv:1.9.1.2) " + "Gecko/20090729 Firefox/3.5.2 GTB5 (.NET CLR 3.5.30729)");
			URLConn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			URLConn.setRequestProperty("Accept-Language", "zh-tw,en-us;q=0.7,en;q=0.3");
			URLConn.setRequestProperty("Accept-Charse", "Big5,utf-8;q=0.7,*;q=0.7");

			if (cookie != null)
				URLConn.setRequestProperty("Cookie", cookie);
			if (referer != null)
				URLConn.setRequestProperty("Referer", referer);
			URLConn.setDoInput(true);
			URLConn.setDoOutput(true);
			URLConn.connect();
			URLConn.getOutputStream().flush();
			in = new BufferedReader(new InputStreamReader(URLConn.getInputStream(), charset));

			String line;
			while ((line = in.readLine()) != null) {
				sb.append(line);
				// System.out.println(line);
			}

		} catch (IOException e) {
			Log.e(this.getClass().getSimpleName(), "connection error!");
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (java.io.IOException ex) {
					ex.printStackTrace();
					Log.e(this.getClass().getSimpleName(), ex.getMessage());
				}
				in = null;

			}
		}

		return sb.toString();
	}

	protected SSLContext createEasySSLContext(String keystorePath, String keystorePass) throws Exception {
		try {

			// set ssl env
			System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
			Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

			// read .keystore file
			InputStream in = new FileInputStream(new File(keystorePath));

			KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
			try {
				ks.load(in, keystorePass.toCharArray());
			} finally {
				try {
					in.close();
				} catch (Exception ignore) {
				}
			}

			// set ssl cert in url connection
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(ks);
			X509TrustManager defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];

			SSLContext context = SSLContext.getInstance("SSL");

			context.init(null, new TrustManager[] { defaultTrustManager }, null);

			return context;
		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), "create ssl context error!");
			throw new Exception(e);
		}

	}

	@Override
	public boolean shutdown() throws Exception {
		if (httpclient != null) {
			httpclient.getConnectionManager().shutdown();

		}

		return true;

	}

}
