package com.fastdevelopment.travelagent.android.common;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import android.util.Log;

public class CusX509TrustManager implements X509TrustManager {

	X509TrustManager pkixTrustManager;

	public CusX509TrustManager(String keystorePath, String keystorePass) throws Exception {

		// String certFile = "/certificates/MyCertFile.cer";
		// Certificate myCert =
		// CertificateFactory.getInstance("X509").generateCertificate(this.getClass().getResourceAsStream(valicertFile));

		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		FileInputStream instream = new FileInputStream(keystorePath);
		try {
			trustStore.load(instream, keystorePass.toCharArray());
			Log.d(this.getClass().getSimpleName(), "Truststore has " + trustStore.size() + " keys");
		} finally {
			instream.close();
		}

		TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("PKIX");
		trustManagerFactory.init(trustStore);

		TrustManager trustManagers[] = trustManagerFactory.getTrustManagers();

		for (TrustManager trustManager : trustManagers) {
			if (trustManager instanceof X509TrustManager) {
				pkixTrustManager = (X509TrustManager) trustManager;
				return;
			}
		}

		throw new Exception("Couldn't initialize");
	}

	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		pkixTrustManager.checkServerTrusted(chain, authType);
	}

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		pkixTrustManager.checkServerTrusted(chain, authType);
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return pkixTrustManager.getAcceptedIssuers();
	}
}
