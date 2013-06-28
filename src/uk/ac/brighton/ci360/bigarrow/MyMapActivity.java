package uk.ac.brighton.ci360.bigarrow;

import java.util.HashMap;
import java.util.List;

import uk.ac.brighton.ci360.bigarrow.places.Place;
import uk.ac.brighton.ci360.bigarrow.places.PlaceDetails;
import uk.ac.brighton.ci360.bigarrow.places.PlacesList;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

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

	protected static final String TAG = null;
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
	private PlaceSearch pSearch;
	
	/**
	 * If we won't be needing that much info in the future, I will restrict it with <Marker, String>
	 * According to HashMap keys they'll be overwritten when using new places
	 * Still need to check with new markers/places in theory should work
	 */
	private HashMap<Marker, Place> markerPlace = new HashMap<Marker, Place>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 50, this);

		setUpMapIfNeeded();

		map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(Marker marker) {
				String reference = markerPlace.get(marker).reference;
				Intent in = new Intent(getApplicationContext(), PlaceDetailActivity.class);
                in.putExtra(PlaceDetails.KEY_REFERENCE, reference);
                startActivity(in);
			}
		});

		pSearch = new PlaceSearch(this);
	}
	
	/**
	 * Returns the current location of the requester
	 * using best available provider
	 * May return null if no providers available or
	 * providers cannot be used under current conditions
	 * Checking result for null is compulsory
	 * @param lm - location manager
	 * @return current location
	 */
	private Location getMyLocation(LocationManager lm) 
	{
		if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
			return lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		
		if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
			return lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
		return null;
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
		
		if (places.results != null) {
			// loop through all the places
			llbBuilder = new LatLngBounds.Builder();
			llbBuilder.include(myLatLng);
			for (Place place : places.results) {
				ll = place.getLatLng();
				mOpt = new MarkerOptions().position(ll)
						.title(place.name + String.format(" (%.2f m)", place.distanceTo(myLocation)))
						.icon(bmd);
				markerPlace.put(map.addMarker(mOpt), place);
				llbBuilder.include(ll);
			}
			map.animateCamera(CameraUpdateFactory.newLatLngBounds(llbBuilder.build(), 20));
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
		//move map because location may have changed significantly
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 15));
		SearchEstab e = Prefs.getSearchType(this);
		
		//This needs to be done relative to the bounds of the map
		pSearch.search(myLocation, new SearchEstab[] { e }, SearchType.MANY);
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
		//app resumed, location or setting may have changed,
		myLocation = getMyLocation(locationManager);
		
		//if null ask user to change settings, otherwise everything ready for search
		if (myLocation == null)
			getLocationServicesAlertDialog().show();
		else
			onLocationChanged(myLocation);
	}
	
	/**
	 * A specific alert dialog. If we will require more of those
	 * we can easily make it a general by passing a string
	 * @return - alert dialog saying that the application
	 * needs GPS enabled to proceed. After user taps OK
	 * he's redirected to the OS Location Settings
	 */
	public AlertDialog getLocationServicesAlertDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Location Services Not Active");
		builder.setMessage("Please enable Location Services");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				// Show location settings when the user acknowledges the alert dialog
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivityForResult(intent, 0);
			}
		});
		return builder.create();
	}
}
