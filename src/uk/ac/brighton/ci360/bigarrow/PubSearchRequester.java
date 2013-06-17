package uk.ac.brighton.ci360.bigarrow;

import java.util.List;

import uk.ac.brighton.ci360.bigarrow.places.Place;
import android.location.Location;

public interface PubSearchRequester {
	public void updateNearestPub(Place place, Location location, float distance);
	public void updateNearestPubs(List<Place> places);
}
