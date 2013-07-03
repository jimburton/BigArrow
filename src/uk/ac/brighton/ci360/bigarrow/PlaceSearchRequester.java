package uk.ac.brighton.ci360.bigarrow;
/**
 * This interface is to be implemented by any class that wants to request
 * a places search from an instance of PlacesAPISearch, e.g. any subclass of 
 * PlaceSearchActivity. It specifies the callback methods that PlacesAPISearch 
 * will call when the search request is done.
 * 
 * Copyright (c) 2013 The BigArrow authors (see the file AUTHORS).
 * See the file LICENSE for copying permission.
 * 
 * @author jb259
 */
import java.util.ArrayList;

import uk.ac.brighton.ci360.bigarrow.places.Place;
import uk.ac.brighton.ci360.bigarrow.places.PlaceDetails;
import uk.ac.brighton.ci360.bigarrow.places.PlacesList;
import android.graphics.Bitmap;
import android.location.Location;

public interface PlaceSearchRequester {
	public static enum SearchType {
		SINGLE, MANY, DETAIL
	}
	public static final String LABEL_BAR = "bar";
	public static final String LABEL_ATM = "atm";
	public static final String LABEL_CAFE = "cafe";
	public static final String LABEL_MOVIE_THEATER = "movie_theater";
	public static final String LABEL_RESTAURANT = "restaurant";
	public static final String LABEL_TAXI_STAND = "taxi_stand";
	
	public static enum SearchEstab {
		BAR(LABEL_BAR), 
		ATM(LABEL_ATM), 
		CAFE(LABEL_CAFE), 
		MOVIE_THEATER(LABEL_MOVIE_THEATER), 
		RESTAURANT(LABEL_RESTAURANT), 
		TAXI_STAND(LABEL_TAXI_STAND);
		private String label;
		private SearchEstab(String label) {
			this.label = label;
		}
		public String label() {
			return label;
		}
	}
	public void updateNearestPlace(Place place, Location location, float distance);
	public void updateNearestPlaces(PlacesList places);
	public void updatePlaceDetails(PlaceDetails details); 
	public void updatePhotos(ArrayList<Bitmap> results);
}
