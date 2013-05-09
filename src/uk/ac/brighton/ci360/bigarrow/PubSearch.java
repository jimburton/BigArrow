package uk.ac.brighton.ci360.bigarrow;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;

import uk.ac.brighton.ci360.bigarrow.places.Place;
import uk.ac.brighton.ci360.bigarrow.places.PlacesList;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.http.json.JsonHttpParser;
import com.google.api.client.json.jackson.JacksonFactory;

public class PubSearch {

	// Google API Key
	private static final String API_KEY = "AIzaSyAP8HSy-2r57WhvO8KcmEdw5rfMrcUtGLU";
	private static final String PLACES_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/search/json?";
	private static final String PLACES_TEXT_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/search/json?";
	private static final String PLACES_DETAILS_URL = "https://maps.googleapis.com/maps/api/place/details/json?";

	private Location location;
	private String types;

	private static final String TAG = "PubSearch";
	private MainActivity main;
	
	public PubSearch(MainActivity a) {
		this.main = a;
	}

	public void search(Location l, String types) {

		this.location = l;
		this.types = types;
		new PubSearchTask().execute();

	}

	class PubSearchTask extends AsyncTask<String, Void, PlacesList> {

		protected PlacesList doInBackground(String... urls) {
			PlacesList places = new PlacesList();
			BufferedReader in = null;
			try {
				HttpTransport transport = new ApacheHttpTransport();
				HttpRequestFactory httpRequestFactory = createRequestFactory(transport);
				HttpRequest request = httpRequestFactory
						.buildGetRequest(new GenericUrl(PLACES_SEARCH_URL));
				//request.getUrl().setPort(443);
				request.getUrl().put("key", API_KEY);
				request.getUrl().put("location", location.getLatitude() + "," + location.getLongitude());
				request.getUrl().put("rankby", "distance"); // in meters
				request.getUrl().put("sensor", "true");
				if (types != null)
					request.getUrl().put("types", types);
				Log.d(TAG, request.getUrl().toString());
				places = request.execute().parseAs(PlacesList.class);
				// Check log cat for places response status
				Log.d(TAG, "" + places.status);
				return places;
			} catch (HttpResponseException e) {
				Log.e("Error:", e.getMessage());
				return null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}

		protected void onPostExecute(PlacesList places) {
			Log.d(TAG, "AsyncTask is done");
			Log.d(TAG, "places is null?: "+(places == null));
			if (places != null && places.results != null) {
				Place p = places.results.get(0);
				Location l = new Location(p.name);
				l.setLatitude(p.geometry.location.lat);
				l.setLongitude(p.geometry.location.lng);
				Log.d(TAG, "Nearest pub:"+p.name);
				Log.d(TAG, "Distance:"+location.distanceTo(l));
				main.updateNearestPub(p, l, location.distanceTo(l));
			}
		}
	}

	private static HttpRequestFactory createRequestFactory(
			final HttpTransport transport) {
		return transport.createRequestFactory(new HttpRequestInitializer() {
			public void initialize(HttpRequest request) {
				GoogleHeaders headers = new GoogleHeaders();
				headers.setApplicationName("BigArrow");
				request.setHeaders(headers);
				JsonHttpParser parser = new JsonHttpParser(new JacksonFactory());
				request.addParser(parser);
			}
		});
	}

	private HttpClient sslClient(HttpClient client) {
		try {
			X509TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] xcs,
						String string) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] xcs,
						String string) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			SSLContext ctx = SSLContext.getInstance("TLS");
			ctx.init(null, new TrustManager[] { tm }, null);
			SSLSocketFactory ssf = new MySSLSocketFactory(ctx);
			ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			ClientConnectionManager ccm = client.getConnectionManager();
			SchemeRegistry sr = ccm.getSchemeRegistry();
			sr.register(new Scheme("https", ssf, 443));
			return new DefaultHttpClient(ccm, client.getParams());
		} catch (Exception ex) {
			return null;
		}
	}

	public class MySSLSocketFactory extends SSLSocketFactory {
		SSLContext sslContext = SSLContext.getInstance("TLS");

		public MySSLSocketFactory(KeyStore truststore)
				throws NoSuchAlgorithmException, KeyManagementException,
				KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};

			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		public MySSLSocketFactory(SSLContext context)
				throws KeyManagementException, NoSuchAlgorithmException,
				KeyStoreException, UnrecoverableKeyException {
			super(null);
			sslContext = context;
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port,
				boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host,
					port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}

}
