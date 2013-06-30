package uk.ac.brighton.ci360.bigarrow;

import uk.ac.brighton.ci360.bigarrow.classes.Utils;
import uk.ac.brighton.ci360.bigarrow.places.Place;
import uk.ac.brighton.ci360.bigarrow.places.PlaceDetails;
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
			myLocation = Utils.getMyLocation(locationManager);
			
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
		myLocation = Utils.getMyLocation(locationManager);
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
}
