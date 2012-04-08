package com.rashith.metro.alfa.webservices;

import java.io.InputStream;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;


import android.util.Log;

public class WebCall {

	private static final String tag = "Metro360°";

	public static String getPlaces(double radious) {

//		SchemeRegistry schemeRegistry = new SchemeRegistry();
//		schemeRegistry.register(new Scheme("http", PlainSocketFactory
//				.getSocketFactory(), 80));
//		schemeRegistry.register(new Scheme("mxiss", SSLSocketFactory
//				.getSocketFactory(), 443));
//
//		HttpParams params = new BasicHttpParams();
//		int timeoutConnection = 5000;
//		HttpConnectionParams.setConnectionTimeout(params, timeoutConnection);
//		int timeoutSocket = 10000;
//		HttpConnectionParams.setSoTimeout(params, timeoutSocket);
//		params.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 30);
//		params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE,
//				new ConnPerRouteBean(30));
//		params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
//		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
//
//		ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(
//				params, schemeRegistry);

		String uri = "https://maps.googleapis.com/maps/api/place/search/json?location=6.969576,79.909327&radius=500&sensor=false&key=AIzaSyBYApAXNOVnGRjPjwaP_VasuWTVfTyf7T8";
		//String uri = "http://www.webservicex.net/periodictable.asmx?WSDL";

		Log.d(tag, uri);

		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(uri);

		HttpResponse response = null;

		try {
			// URL u = new URL(uri);
			// HttpsURLConnection posts = (HttpsURLConnection)
			// u.openConnection();
			// InputStream i = posts.getInputStream();

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
