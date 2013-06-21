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
import java.util.Properties;

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

import uk.ac.brighton.ci360.bigarrow.PlaceSearchRequester.SearchEstab;
import uk.ac.brighton.ci360.bigarrow.PlaceSearchRequester.SearchType;
import uk.ac.brighton.ci360.bigarrow.places.Place;
import uk.ac.brighton.ci360.bigarrow.places.PlaceDetails;
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

public class PlaceSearch {

	private Location location;
	private String estabType;
	private String detailsReference;
	private SearchType searchType;

	private static final String TAG = "PubSearch";
	private PlaceSearchRequester requester;
	private String apiKey;
	private String placesSearchURL;
	private String placesDetailsURL;

	public PlaceSearch(PlaceSearchRequester requester) {
		this.requester = requester;
		Properties prop = new Properties();
		 
    	try {
    		prop.load(PlaceSearch.class.getClassLoader().getResourceAsStream("config.properties"));
            apiKey = prop.getProperty("apikey_places");
            placesSearchURL = prop.getProperty("places_endpoint_search");
            placesDetailsURL = prop.getProperty("places_endpoint_details");
            Log.d(TAG, apiKey);
    	} catch (IOException ex) {
    		ex.printStackTrace();
        }
	}

	public void search(Location l, SearchEstab[] estabs, SearchType searchType) {
		this.location = l;
		StringBuffer estabsStr = new StringBuffer();
		int i = 0;
		for (; i < estabs.length - 1; i++) {
			estabsStr.append(estabs[i].label());
			estabsStr.append(",");
		}
		estabsStr.append(estabs[i]);
		this.estabType = estabsStr.toString();
		this.searchType = searchType;
		new PlaceSearchTask().execute();
	}
	
	public void getDetail(String reference) {
		this.detailsReference = reference;
		this.searchType = SearchType.DETAIL;
		new PlaceDetailsTask().execute();
	}

	class PlaceSearchTask extends AsyncTask<String, Void, PlacesList> {
		final SearchType st = searchType;
		protected PlacesList doInBackground(String... urls) {
			PlacesList places = new PlacesList();
			BufferedReader in = null;
			try {
				HttpTransport transport = new ApacheHttpTransport();
				HttpRequestFactory httpRequestFactory = createRequestFactory(transport);
				HttpRequest request = httpRequestFactory
						.buildGetRequest(new GenericUrl(placesSearchURL));
				// request.getUrl().setPort(443);
				request.getUrl().put("key", apiKey);
				request.getUrl().put("location",
						location.getLatitude() + "," + location.getLongitude());
				request.getUrl().put("rankby", "distance"); // in meters
				request.getUrl().put("sensor", "true");
				if (estabType != null)
					request.getUrl().put("types", estabType.toLowerCase());
				Log.d(TAG, request.getUrl().toString());
				places = request.execute().parseAs(PlacesList.class);
				// Check log cat for places response status
				Log.d(TAG, "" + places.status);
				return places;
			} catch (HttpResponseException e) {
				Log.e("Error:", e.getMessage());
				returnNoResult();
				return null;
			} catch (IOException e) {
				returnNoResult();
				e.printStackTrace();
				return null;
			}
		}

		protected void onPostExecute(PlacesList places) {
			Log.d(TAG, "AsyncTask is done");
			Log.d(TAG, "SearchType: " + searchType);
			if (places != null && places.results != null) {
				if (places.results.size() > 0) {
					if (this.st == SearchType.MANY) {
						requester.updateNearestPlaces(places);
					} else {
						Place p = places.results.get(0);
						Location l = new Location(p.name);
						l.setLatitude(p.geometry.location.lat);
						l.setLongitude(p.geometry.location.lng);
						Log.d(TAG, "Nearest pub:" + p.name);
						Log.d(TAG, "Distance:" + location.distanceTo(l));
						requester.updateNearestPlace(p, l,
								location.distanceTo(l));
					}
				} else {
					returnNoResult();
				}
			} else {
				returnNoResult();
			}
		}
	}

	class PlaceDetailsTask extends AsyncTask<String, Void, PlaceDetails> {

		protected PlaceDetails doInBackground(String... urls) {
			PlaceDetails details = new PlaceDetails();
			BufferedReader in = null;
			try {
				HttpTransport transport = new ApacheHttpTransport();
				HttpRequestFactory httpRequestFactory = createRequestFactory(transport);
				HttpRequest request = httpRequestFactory
						.buildGetRequest(new GenericUrl(placesDetailsURL));
				// request.getUrl().setPort(443);
				request.getUrl().put("key", apiKey);
				request.getUrl().put("reference", detailsReference);
				request.getUrl().put("sensor", "true");
				Log.d(TAG, request.getUrl().toString());
				details = request.execute().parseAs(PlaceDetails.class);
				// Check log cat for places response status
				Log.d(TAG, "" + details.status);
				return details;
			} catch (HttpResponseException e) {
				Log.e("Error:", e.getMessage());
				returnNoResult();
				return null;
			} catch (IOException e) {
				returnNoResult();
				e.printStackTrace();
				return null;
			}
		}

		protected void onPostExecute(PlaceDetails details) {
			Log.d(TAG, "AsyncTask is done");
			Log.d(TAG, "places is null?: " + (details == null));
			if (details != null) {
				requester.updatePlaceDetails(details);
			} else {
				returnNoResult();
			}
		}
	}

	public void getPlaceDetails(String reference) throws Exception {
		detailsReference = reference;
		new PlaceDetailsTask().execute();
	}

	private void returnNoResult() {
		Log.d(TAG, "NO RESULT");
		if (searchType == SearchType.MANY) {
			requester.updateNearestPlaces(new PlacesList());
		} else {
			Place p = new Place();
			p.name = "No results";
			p.id = Place.NO_RESULT;
			requester.updateNearestPlace(p, null, 0f);
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
