package uk.ac.brighton.ci360.bigarrow;

/**
 * This activity shows the nearest n places on a map.
 * 
 * @author jb259
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.ac.brighton.ci360.bigarrow.places.Place;
import uk.ac.brighton.ci360.bigarrow.places.PlaceDetails;
import uk.ac.brighton.ci360.bigarrow.places.PlacesList;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MyMapActivity extends PlaceSearchActivity implements LocationListener,
		PlaceSearchRequester {

	protected static final String TAG = "MyMapActivity";
	MapFragment mapView;
	List<Overlay> mapOverlays;
	AddItemizedOverlay itemizedOverlay;

	GeoPoint geoPoint;
	MapController mc;

	double latitude;
	double longitude;
	OverlayItem overlayitem;
	private LocationManager locationManager;
	private Location myLocation;
	private LatLng myLatLng;
	private Marker myMarker;
	private GoogleMap map;
	private LatLngBounds.Builder llbBuilder;
	private PlacesAPISearch pSearch;
	private HashMap<String, String> refsLookup;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		myLocation = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (myLocation == null) {
			myLocation = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				10000, 50, this);
				
				
		/**IF GPS DISABLED myLocation is still null and app will crash**/		
		
		myLatLng = new LatLng(myLocation.getLatitude(),
				myLocation.getLongitude());

		setUpMapIfNeeded();

		map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 15));
		map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(Marker marker) {
				String title = marker.getTitle();
				if(refsLookup.containsKey(title)) {
					String ref = refsLookup.get(title);
					Log.d(TAG, ref);
					Intent in = new Intent(getApplicationContext(), PlaceDetailActivity.class);
	                in.putExtra(PlaceDetails.KEY_REFERENCE, ref);
	                startActivity(in);
				}
			}
		});

		pSearch = new PlacesAPISearch(this);
		SearchEstab e = SharedPrefsActivity.getSearchType(this);
		pSearch.search(myLocation, new SearchEstab[] { e },
				SearchType.MANY);

	}

	private void setUpMapIfNeeded() {
		if (map == null) {
			map = ((MapFragment) getFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			if (map != null) {
				// The Map is verified. It is now safe to manipulate the map.
			}
		}
	}

	@Override
	public void updateNearestPlace(Place place, Location location,
			float distance) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNearestPlaces(PlacesList places) {
		map.clear();
		LatLng ll;
		String marker_label = getResources().getString(R.string.my_marker_label);
		MarkerOptions mOpt = new MarkerOptions().position(myLatLng).title(
				marker_label);
		mOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.mark_red));
		myMarker = map.addMarker(mOpt);
		BitmapDescriptor bmd = BitmapDescriptorFactory
				.fromResource(R.drawable.mark_blue);
		refsLookup = new HashMap<String, String>();
		if (places.results != null) {
			// loop through all the places
			llbBuilder = new LatLngBounds.Builder();
			llbBuilder.include(myLatLng);
			for (Place place : places.results) {
				ll = new LatLng(place.geometry.location.lat,
						place.geometry.location.lng); // longitude
				mOpt = new MarkerOptions().position(ll).title(place.name)
						.snippet(distanceBetweenFormatted(place, myLocation))
						.icon(bmd);
				map.addMarker(mOpt);
				llbBuilder.include(ll);
				refsLookup.put(place.name, place.reference);
			}
			map.animateCamera(CameraUpdateFactory.newLatLngBounds(
					llbBuilder.build(), 20));
		}
		myMarker.showInfoWindow();
	}

	@Override
	public void updatePlaceDetails(PlaceDetails details) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(Location location) {
		myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
		SearchEstab e = SharedPrefsActivity.getSearchType(this);
		//This needs to be done relative to the bounds of the map
		pSearch.search(myLocation, new SearchEstab[] { e },
				SearchType.MANY);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResume() {
		super.onResume();
		setUpMapIfNeeded();
	}

	@Override
	public void updatePhotos(ArrayList<Bitmap> results) {
		// TODO Auto-generated method stub
		
	}

}
