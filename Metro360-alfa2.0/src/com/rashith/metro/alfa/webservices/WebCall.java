package com.rashith.metro.alfa.webservices;

import java.io.InputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;

import android.util.Log;

public class WebCall {

	private static final String tag = "Metro360°";

	public static String getPlaces(double radious) {

//		HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
//
//		DefaultHttpClient client = new DefaultHttpClient();
//
//		SchemeRegistry registry = new SchemeRegistry();
//		SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
//		socketFactory
//				.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
//		registry.register(new Scheme("https", socketFactory, 443));
//		SingleClientConnManager mgr = new SingleClientConnManager(
//				client.getParams(), registry);
//		DefaultHttpClient httpClient = new DefaultHttpClient(mgr,
//				client.getParams());
//
//		// Set verifier
//		HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);

		
		
		// String uri =
		// "https://maps.googleapis.com/maps/api/place/search/json?location=6.969576,79.909327&radius=500&sensor=false&key=AIzaSyBYApAXNOVnGRjPjwaP_VasuWTVfTyf7T8";
		String uri = "http://www.webservicex.net/periodictable.asmx?WSDL";
		Log.d(tag, uri);

		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(uri);

		HttpResponse response = null;

		try {

			response = client.execute(post);
			int statusCode = response.getStatusLine().getStatusCode();

			switch (statusCode) {
			case HttpStatus.SC_OK:
				InputStream is = response.getEntity().getContent();
				return StreamHelper.convertInputStreamToString(is);
			default:
				Log.d(tag, "Error occured tringit ot aces web service.");
				break;
			}

		} catch (Exception e) {
			Log.d(tag, e.getMessage());
		}

		return null;
	}
}
