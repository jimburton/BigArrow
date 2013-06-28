package uk.ac.brighton.ci360.bigarrow.places;

import java.io.Serializable;
import com.google.android.gms.maps.model.LatLng;
import com.google.api.client.util.Key;

/**
 * Implement this class from "Serializable" So that you can pass this class
 * Object to another using Intents Otherwise you can't pass to another actitivy
 * */
public class Place implements Serializable {
	
	public final static String NO_RESULT = "NO_RESULT";

	@Key
	public String id;

	@Key
	public String name;

	@Key
	public String reference;

	@Key
	public String icon;

	@Key
	public String vicinity;

	/**Careful, the array might be NULL if a place has no photos**/
	@Key
	public Photo[] photos;
	
	@Key
	public Geometry geometry;
	
	@Key
	public double rating;
	
	@Key
	public OpeningHours opening_hours;

	@Key
	public String formatted_address;

	@Key
	public String formatted_phone_number;
	
	/**
	 * @return a pair of latitude and longitude coordinates of this place
	 */
	public LatLng getLatLng() {
		return new LatLng(geometry.location.lat, geometry.location.lng);
	}

	/**
	 * Since we have defined our on location I had to use
	 * android.location.Location. Once I really get into it
	 * I will probably replace the inner class with something different
	 * to demystify the code
	 * @param location - location to which you need to know the distance
	 * @return a distance in meters to the given location
	 */
	public float distanceTo(android.location.Location location) {
		android.location.Location thisPlaceLocation = new android.location.Location("");
		thisPlaceLocation.setLatitude(geometry.location.lat);
		thisPlaceLocation.setLongitude(geometry.location.lng);
		return thisPlaceLocation.distanceTo(location);
	}
	
	@Override
	public String toString() {
		return name + " - " + id + " - " + reference;
	}

	public static class Geometry implements Serializable {
		@Key
		public Location location;
	}
	
	public static class OpeningHours implements Serializable {
		@Key
		public boolean open_now;
	}
	
	public static class Photo implements Serializable {
		@Key
		public int height;
		
		@Key
		public String[] html_attributions;
		
		@Key
		public String photo_reference;
		
		@Key
		public int width;
	}

	public static class Location implements Serializable {
		@Key
		public double lat;

		@Key
		public double lng;
	}

}
