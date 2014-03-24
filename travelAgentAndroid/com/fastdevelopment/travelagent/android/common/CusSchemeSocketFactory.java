package com.fastdevelopment.travelagent.android.common;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class CusSchemeSocketFactory implements SchemeSocketFactory {

	@Override
	public Socket connectSocket(Socket socket, InetSocketAddress remoteAddress, InetSocketAddress localAddress, HttpParams params) throws IOException, UnknownHostException, ConnectTimeoutException {

		if (localAddress != null) {
			socket.setReuseAddress(HttpConnectionParams.getSoReuseaddr(params));
			socket.bind(localAddress);
		}
		int connTimeout = HttpConnectionParams.getConnectionTimeout(params);
		int soTimeout = HttpConnectionParams.getSoTimeout(params);

		try {
			socket.setSoTimeout(soTimeout);
			socket.connect(remoteAddress, connTimeout);
		} catch (SocketTimeoutException ex) {
			throw new ConnectTimeoutException("Connect to " + remoteAddress + " timed out");
		}

		return socket;
	}

	@Override
	public boolean isSecure(Socket socket) throws IllegalArgumentException {
		return false;
	}

	@Override
	public Socket createSocket(HttpParams arg0) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
