package uk.ac.brighton.ci360.bigarrow;

/**
 * This class encapsulates calls to the Places API.
 * 
 * Copyright (c) 2013 The BigArrow authors (see the file AUTHORS).
 * See the file LICENSE for copying permission.
 * 
 * @author jb259
 */

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import uk.ac.brighton.ci360.bigarrow.PlaceSearchRequester.SearchEstab;
import uk.ac.brighton.ci360.bigarrow.PlaceSearchRequester.SearchType;
import uk.ac.brighton.ci360.bigarrow.places.Place;
import uk.ac.brighton.ci360.bigarrow.places.Place.Photo;
import uk.ac.brighton.ci360.bigarrow.places.PlaceDetails;
import uk.ac.brighton.ci360.bigarrow.places.PlacesList;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

@SuppressLint("DefaultLocale")
public class PlacesAPISearch {

	private Location location;
	private String estabType;
	private String detailsReference;
	private SearchType searchType;

	private static final String TAG = "PubSearch";
	private PlaceSearchRequester requester;
	private String apiKey;
	private String placesSearchURL;
	private String placesDetailsURL;
	private String placesPhotoURL;
	private ArrayList<Photo> photoRefs;
	//private ArrayList<Bitmap> photoResults;
	private CopyOnWriteArrayList<Bitmap> photoResults;
	
	private ExecutorService executorService;

	public PlacesAPISearch(PlaceSearchRequester requester) {
		this.requester = requester;
		Properties prop = new Properties();

		try {
			prop.load(PlacesAPISearch.class.getClassLoader()
					.getResourceAsStream("config.properties"));
			apiKey = prop.getProperty("apikey_places");
			placesSearchURL = prop.getProperty("places_endpoint_search");
			placesDetailsURL = prop.getProperty("places_endpoint_details");
			placesPhotoURL = prop.getProperty("places_endpoint_photo");
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

	public void getPhotos(Photo[] photoRefsArr) {
	    executorService = Executors.newFixedThreadPool(5);
		photoRefs = new ArrayList<Photo>(Arrays.asList(photoRefsArr));
		photoResults = new CopyOnWriteArrayList<Bitmap>();	
		for (Photo p : photoRefs) {
		    executorService.execute(new PhotoTask(p.photo_reference));
		}
	}
	
	class PhotoTask implements Runnable {

	    private final String photoRef;

        PhotoTask(String photoRef) {
            this.photoRef = photoRef;
        }
        
        private Bitmap getBitmap() {
            try {
                StringBuilder url = new StringBuilder(placesPhotoURL);
                url.append("maxwidth=400");
                url.append("&photoreference=").append(photoRef);
                url.append("&sensor=true");
                url.append("&key=").append(apiKey);
                
                HttpUriRequest request = new HttpGet(url.toString());
                HttpClient httpClient = new DefaultHttpClient();
                HttpResponse response = httpClient.execute(request);
         
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    byte[] bytes = EntityUtils.toByteArray(entity);
         
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0,
                            bytes.length);
                    return bitmap;
                } else {
                    throw new IOException("Download failed, HTTP response code "
                            + statusCode + " - " + statusLine.getReasonPhrase());
                }
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
        
        @Override
        public void run() {
            photoResults.add(getBitmap());           
            if (photoRefs.size() == photoResults.size()) {
                requester.updatePhotos(new ArrayList<Bitmap>(photoResults));
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

	@SuppressWarnings("unused")
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
