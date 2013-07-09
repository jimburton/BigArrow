package uk.ac.brighton.ci360.bigarrow;

/**
 * This is the base class for activities that want to carry out a Places API search.
 * The constructor contains some useful config and setuop that is common to the concrete
 * implementations.
 * 
 * Copyright (c) 2013 The BigArrow authors (see the file AUTHORS).
 * See the file LICENSE for copying permission.
 * 
 * @author jb259
 */
import uk.ac.brighton.ci360.bigarrow.places.Place;
import uk.ac.brighton.ci360.bigarrow.places.PlaceDetails;
import uk.ac.brighton.ci360.bigarrow.places.PlacesList;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

import com.google.android.gms.maps.model.LatLng;

@SuppressLint("DefaultLocale")
public abstract class PlaceSearchActivity extends Activity implements
		LocationListener, PlaceSearchRequester {

	protected final boolean PLACES_SEARCH_ON = false;

	protected LocationManager locationManager;
	protected Location myLocation;
	protected PlacesAPISearch pSearch;

	protected SearchType firstSearchType;
	protected String placeReference;
	protected SearchEstab estab;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		estab = SharedPrefsActivity.getSearchType(this);
		String estabStr = getReadableLabel(estab);
		setTitle("Nearest " + estabStr);
		if (PLACES_SEARCH_ON) {
			pSearch = new PlacesAPISearch(this);
			locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
			myLocation = getMyLocation(locationManager);
			
			if(firstSearchType == SearchType.DETAIL) {
				getDetail();
			} else {
				getNearest();
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		//register listeners in derived classes
		//app resumed, manually re-check where we are, can be null - handle in derived classes!
		//update the location for all derived classes 
		myLocation = getMyLocation(locationManager);
	}

	@Override
	public void onPause() {
		super.onPause();
		//if remove listener, location doesnt get updated
		//when provider is enabled
		//perhaps there's a better way
		//locationManager.removeUpdates(this);
	}

	@Override
	public void onLocationChanged(Location location) {
		if (PLACES_SEARCH_ON) {
			myLocation = location;	//update our location
			getNearest();
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onProviderEnabled(String provider) {
		if (PLACES_SEARCH_ON) {
			myLocation = locationManager.getLastKnownLocation(provider);
			getNearest();
		}
	}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}

	private void getNearest() {
		if (myLocation != null) {
		  pSearch.search(myLocation, new SearchEstab[] { estab }, firstSearchType);
		}	
	}
	
	private void getDetail() {
		pSearch.getDetail(placeReference);
	}

	@Override
	public abstract void updateNearestPlaces(PlacesList places);
	
	@Override
	public abstract void updateNearestPlace(Place place, Location location, float distance);
	
	@Override
	public void updatePlaceDetails(PlaceDetails details) {
		
	}
	
	public String getReadableLabel(SearchEstab estab) {
		String label = estab.label();
		String res = null;
		if(label.equals(LABEL_BAR)) {
			res = getResources().getString(R.string.readable_label_bar); 
		} else if (label.equals(LABEL_ATM)) {
			res = getResources().getString(R.string.readable_label_atm);
		} else if (label.equals(LABEL_CAFE)) {
			res = getResources().getString(R.string.readable_label_cafe);
		} else if (label.equals(LABEL_MOVIE_THEATER)) {
			res = getResources().getString(R.string.readable_label_movie_theater);
		} else if (label.equals(LABEL_RESTAURANT)) {
			res = getResources().getString(R.string.readable_label_restaurant);
		} else if (label.equals(LABEL_TAXI_STAND)) {
			res = getResources().getString(R.string.readable_label_taxi_stand);
		}
		return res;
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
	public Location getMyLocation(LocationManager lm) {
		if (lm != null && lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
			return lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		
		return (lm == null) ? null : lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	}

	/**
	 * @param ll1 - first pair of coords
	 * @param ll2 - second pair of coors
	 * @return Approximate distance in meters between 2 pairs of lat
	 * and lng coordinates
	 */
	public float distanceBetween(LatLng ll1, LatLng ll2) {
		Location loc1 = new Location("");
		loc1.setLatitude(ll1.latitude);
		loc1.setLongitude(ll1.longitude);
		
		Location loc2 = new Location("");
		loc2.setLatitude(ll2.latitude);
		loc2.setLongitude(ll2.longitude);
		
		return loc1.distanceTo(loc2);
	}
	
	/**
	 * @param place - place
	 * @param location - location to which you need to know the distance
	 * @return a distance in meters between place to the given location
	 */
	public float distanceBetween(Place place, Location location) {
		return distanceBetween(place.getLatLng(), new LatLng(location.getLatitude(), location.getLongitude()));
	}
	
	/**
	 * Returns formatted distance between place and location
	 * as a float value with 2 decimal places followed by the letter 'm'
	 * everything in brackets
	 * @param place
	 * @param location
	 * @return - distance between place and location
	 */
	@SuppressLint("DefaultLocale")
	public String distanceBetweenFormatted(Place place, Location location) {
		return String.format("(%.2f m)", distanceBetween(place, location));
	}
	
	/**
	 * A specific alert dialog. If we will require more of those
	 * we can easily make it a general by passing a string
	 * @return - alert dialog saying that the application
	 * needs GPS enabled to proceed. After user taps OK
	 * he's redirected to the OS Location Settings
	 */
	public AlertDialog getLocationServicesAlertDialog(Context context)
	{
		final Activity caller = (Activity) context;
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Location Services Not Active");
		builder.setMessage("Please enable Location Services");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				// Show location settings when the user acknowledges the alert dialog
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				caller.startActivityForResult(intent, 0);
			}
		});
		return builder.create();
	}
}
