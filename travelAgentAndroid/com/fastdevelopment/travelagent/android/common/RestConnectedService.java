package com.fastdevelopment.travelagent.android.common;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.util.Log;

@SuppressWarnings("deprecation")
public class RestConnectedService implements IHttpConnectedService {

	private HttpClient httpClient = null;

	@Override
	public void initHttpClient(int serviceSSLPort) throws Exception {

		httpClient = this.createHttpsClient(serviceSSLPort);

	}

	@Override
	public HttpClient getHttpClient() throws Exception {
		return httpClient;
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

			if (httpClient == null) {
				Log.w(this.getClass().getSimpleName(), "http client is null!");
				return null;
			}

			HttpGet httppost = new HttpGet(url);

			// content type
			httppost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

			// request
			HttpResponse response = httpClient.execute(httppost);

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

				}
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

			if (httpClient == null) {
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

			HttpResponse response = httpClient.execute(httppost);

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

				}
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

	protected HttpClient createHttpsClient(int port) throws Exception {
		HttpClient httpclient = null;

		try {

			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());

			trustStore.load(null, null);

			SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);

			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER); // 允许所有主机的验证

			HttpParams params = new BasicHttpParams();

			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
			HttpProtocolParams.setUseExpectContinue(params, true);

			// 设置连接管理器的超时
			ConnManagerParams.setTimeout(params, 10000);
			// 设置连接超时
			HttpConnectionParams.setConnectionTimeout(params, 10000);
			// 设置socket超时
			HttpConnectionParams.setSoTimeout(params, 10000);

			// 设置http https支持
			SchemeRegistry schReg = new SchemeRegistry();
			schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			schReg.register(new Scheme("https", sf, port));

			ClientConnectionManager conManager = new ThreadSafeClientConnManager(params, schReg);

			httpclient = new DefaultHttpClient(conManager, params);

		} catch (Exception ex) {

			Log.e(this.getClass().getSimpleName(), ex.getMessage());

			httpclient = null;
		}

		return httpclient;
	}

	private class SSLSocketFactoryEx extends SSLSocketFactory {

		SSLContext sslContext = SSLContext.getInstance("TLS");

		public SSLSocketFactoryEx(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {

				@Override
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {

				}

				@Override
				public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {

				}
			};

			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
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

				// Load CAs from an InputStream
				// (could be from a resource or ByteArrayInputStream or ...)
				CertificateFactory cf = CertificateFactory.getInstance("X.509");
				// From
				// https://www.washington.edu/itconnect/security/ca/load-der.crt
				InputStream caInput = new BufferedInputStream(new FileInputStream("load-der.crt"));
				Certificate ca;
				try {
					ca = cf.generateCertificate(caInput);
					System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
				} finally {
					caInput.close();
				}

				// Create a KeyStore containing our trusted CAs
				String keyStoreType = KeyStore.getDefaultType();
				KeyStore keyStore = KeyStore.getInstance(keyStoreType);
				keyStore.load(null, null);
				keyStore.setCertificateEntry("ca", ca);

				// Create a TrustManager that trusts the CAs in our KeyStore
				String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
				TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
				tmf.init(keyStore);

				// Create an SSLContext that uses our TrustManager
				SSLContext context = SSLContext.getInstance("TLS");
				context.init(null, tmf.getTrustManagers(), null);

				// Tell the URLConnection to use a SocketFactory from our
				// SSLContext
				HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
				urlConnection.setSSLSocketFactory(context.getSocketFactory());

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

	@Override
	public boolean shutdown() throws Exception {
		if (httpClient != null) {
			httpClient.getConnectionManager().shutdown();

		}

		return true;

	}

}
