package uk.ac.brighton.ci360.bigarrow.classes;

import com.google.android.gms.maps.model.LatLng;

import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationManager;
import uk.ac.brighton.ci360.bigarrow.places.Place.Detail;

public class Utils
{
	public static final String NO_DATA = "Not present";
	
	public static String format(String detail) {
		return detail == null ? NO_DATA : detail;
	}
	
	public static String format(double detail) {
		return detail == 0.0 ? NO_DATA : Double.toString(detail);
	}
	
	@SuppressLint("DefaultLocale")
	public static String format(Detail detail) {
		return detail.name().toLowerCase().replace('_', ' ');
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
	public static Location getMyLocation(LocationManager lm) {
		if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
			return lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		
		return lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	}

	/**
	 * @param ll1 - first pair of coords
	 * @param ll2 - second pair of coors
	 * @return Approximate distance in meters between 2 pairs of lat
	 * and lng coordinates
	 */
	public static float distanceBetween(LatLng ll1, LatLng ll2) {
		Location loc1 = new Location("");
		loc1.setLatitude(ll1.latitude);
		loc1.setLongitude(ll1.longitude);
		
		Location loc2 = new Location("");
		loc2.setLatitude(ll2.latitude);
		loc2.setLongitude(ll2.longitude);
		
		return loc1.distanceTo(loc2);
	}
}
