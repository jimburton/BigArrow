package uk.ac.brighton.ci360.bigarrow;

import uk.ac.brighton.ci360.bigarrow.places.Place;
import uk.ac.brighton.ci360.bigarrow.places.PlaceDetails;
import uk.ac.brighton.ci360.bigarrow.places.PlacesList;
import android.location.Location;

public interface PlaceSearchRequester {
	public static enum SearchType {
		SINGLE, MANY, DETAIL
	}
	public static enum SearchEstab {
		BAR("bar");
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
}
