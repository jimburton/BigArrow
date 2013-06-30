package uk.ac.brighton.ci360.bigarrow.classes;

import com.google.android.gms.maps.model.LatLng;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import uk.ac.brighton.ci360.bigarrow.places.Place;
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
	
	/**
	 * @param place - place
	 * @param location - location to which you need to know the distance
	 * @return a distance in meters between place to the given location
	 */
	public static float distanceBetween(Place place, Location location) {
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
	public static String distanceBetweenFormatted(Place place, Location location) {
		return String.format("(%.2f m)", distanceBetween(place, location));
	}
	
	/**
	 * A specific alert dialog. If we will require more of those
	 * we can easily make it a general by passing a string
	 * @return - alert dialog saying that the application
	 * needs GPS enabled to proceed. After user taps OK
	 * he's redirected to the OS Location Settings
	 */
	public static AlertDialog getLocationServicesAlertDialog(Context context)
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
