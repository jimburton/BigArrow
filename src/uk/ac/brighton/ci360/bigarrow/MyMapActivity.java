package uk.ac.brighton.ci360.bigarrow;

import java.util.HashMap;
import java.util.List;

import uk.ac.brighton.ci360.bigarrow.classes.Utils;
import uk.ac.brighton.ci360.bigarrow.places.Place;
import uk.ac.brighton.ci360.bigarrow.places.PlaceDetails;
import uk.ac.brighton.ci360.bigarrow.places.PlacesList;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
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

public class MyMapActivity extends PlaceSearchActivity implements PlaceSearchRequester {

	protected static final String TAG = null;
	MapFragment mapView;
	List<Overlay> mapOverlays;
	AddItemizedOverlay itemizedOverlay;

	GeoPoint geoPoint;
	MapController mc;

	double latitude;
	double longitude;
	OverlayItem overlayitem;
	private LatLng myLatLng;
	private Marker myMarker;
	private GoogleMap map;
	private LatLngBounds.Builder llbBuilder;
	private PlaceSearch pSearch;
	
	private float minDistance = 50.0f;
	private long minTime = 300000;	//5 min
	
	/**
	 * Contains marker and the reference of the place associated with that marker
	 * According to HashMap keys they'll be overwritten when using new places
	 * Still need to check with new markers/places in theory should work
	 */
	private HashMap<Marker, String> markerReference = new HashMap<Marker, String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		setUpMapIfNeeded();
		map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(Marker marker) {
				Intent in = new Intent(getApplicationContext(), PlaceDetailActivity.class);
                in.putExtra(PlaceDetails.KEY_REFERENCE, markerReference.get(marker));
                startActivity(in);
			}
		});
		pSearch = new PlaceSearch(this);
		//if open 1st time, update, if null handle in resume
		onLocationChanged(myLocation);
	}

	private void setUpMapIfNeeded() {
		if (map == null) {
			map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			if (map != null) {
				// The Map is verified. It is now safe to manipulate the map.
			}
		}
	}

	@Override
	public void updateNearestPlace(Place place, Location location, float distance) {
		// TODO Auto-generated method stub
	}

	@Override
	public void updateNearestPlaces(PlacesList places) {
		map.clear();
		String my_marker_label = getResources().getString(R.string.my_marker_label);
		MarkerOptions mOpt = new MarkerOptions().position(myLatLng).title(my_marker_label);
		mOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.mark_red));
		myMarker = map.addMarker(mOpt);
		BitmapDescriptor bmd = BitmapDescriptorFactory.fromResource(R.drawable.mark_blue);
		
		if (places.results != null) {
			// loop through all the places
			llbBuilder = new LatLngBounds.Builder();
			llbBuilder.include(myLatLng);
			for (Place place : places.results) {
				mOpt = new MarkerOptions().position(place.getLatLng())
						.title(place.name + String.format(" (%.2f m)", place.distanceTo(myLocation)))
						.icon(bmd);
				markerReference.put(map.addMarker(mOpt), place.reference);
				llbBuilder.include(place.getLatLng());
			}
			LatLngBounds llb = llbBuilder.build();
			map.animateCamera(CameraUpdateFactory.newLatLngBounds(llb, 20));
			//set min distance for updates as 1/4 of the dist between bounds
			minDistance = Utils.distanceBetween(llb.northeast, llb.southwest) / 4.0f;
		}
		myMarker.showInfoWindow();
	}

	@Override
	public void onLocationChanged(Location location) {
		if (location != null) {
			myLocation = location;	//if app hasn't been closed, we need to change our location
			myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 15));
			pSearch.search(myLocation, new SearchEstab[] {Prefs.getSearchType(this)}, SearchType.MANY);
		}
	}
	
	@Override
	public void onProviderEnabled(String provider) {
		//provider has been enabled, get location
		onLocationChanged(Utils.getMyLocation(locationManager));
	}

	@Override
	public void onResume() {
		super.onResume();
		setUpMapIfNeeded();
		
		//if null ask user to change settings
		if (myLocation == null)
			getLocationServicesAlertDialog().show();

		//register listener with more efficient callbacks
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, this);
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
