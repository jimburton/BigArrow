package uk.ac.brighton.ci360.bigarrow;

import uk.ac.brighton.ci360.bigarrow.places.Place;
import uk.ac.brighton.ci360.bigarrow.places.PlacesList;
import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public abstract class PlaceSearchActivity extends Activity implements
		LocationListener, PlaceSearchRequester {

	protected final boolean PLACES_SEARCH_ON = true;

	protected LocationManager locationManager;
	protected Location myLocation;
	protected PlaceSearch pSearch;

	protected SearchType firstSearchType;
	protected String placeReference;
	protected SearchEstab estab;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		estab = Prefs.getSearchType(this);
		String estabStr = getReadableLabel(estab);
		setTitle("Nearest " + estabStr);
		if (PLACES_SEARCH_ON) {
			pSearch = new PlaceSearch(this);
			locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
			myLocation = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (myLocation == null) {
				myLocation = locationManager
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			}
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 10000, 10, this);
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
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,// or
				// NETWORK_PROVIDER
				10000, 50, this);
	}

	@Override
	public void onPause() {
		super.onPause();
		locationManager.removeUpdates(this);
	}

	@Override
	public void onLocationChanged(Location location) {
		if (PLACES_SEARCH_ON) {
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
	public abstract void updateNearestPlace(Place place, Location location,
			float distance);
	
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

}
