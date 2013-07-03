package uk.ac.brighton.ci360.bigarrow.places;
/**
 * A data transfer object for a result returned from the Places API.
 * 
 * Copyright (c) 2013 The BigArrow authors (see the file AUTHORS).
 * See the file LICENSE for copying permission.
 * 
 * @author jb259
 **/

import java.io.Serializable;
import java.util.LinkedHashMap;

import android.annotation.SuppressLint;

import com.google.android.gms.maps.model.LatLng;
import com.google.api.client.util.Key;

public class Place implements Serializable {

	private static final long serialVersionUID = -1518642766553991067L;

	public final static String NO_RESULT = "NO_RESULT";
	public static final String NO_DATA = "Not present";
	
	/**
	 * Details about this place we're interested in to show
	 * on place detail activity. The code is dynamic, only
	 * need to add link between enum and actual field in the
	 * getDetails() method
	 */
	public enum Detail {
		NAME, ADDRESS, PHONE, LOCATION, RATING, OPENING_HOURS
	};
	
	/**
	 * Careful if accessing fields directly
	 * Any of the keys below can be null
	 * It is advised that you use getDetails() instead
	 */

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
	 * The returned hashmap contains all details
	 * we need to display on the place detail activity
	 * The key is stringified Detail enum, value is string representation of this detail
	 * It is advised to declare the map as "final" where you plan to use it
	 * as it is only a very convenient data wrapper
	 * Since it's linked, using keySet() will return keys in the order they were put
	 * The map is safe to use as all of the values ARE NOT null,
	 * so there is no need to check for null
	 * Whenever you need more details, simply expand the map
	 * @return details of this place
	 */
	public LinkedHashMap<String, String> getDetails() {
		LinkedHashMap<String, String> details = new LinkedHashMap<String, String>();
		details.put(format(Detail.NAME), name);	//put name
		details.put(format(Detail.ADDRESS), formatted_address);	//put address
		details.put(format(Detail.PHONE), formatted_phone_number);	//put phone
		details.put(format(Detail.LOCATION), getLatLng().toString());	//put location
		details.put(format(Detail.RATING), format(rating));	//put rating
		details.put(format(Detail.OPENING_HOURS),
				opening_hours == null ? NO_DATA : (opening_hours.open_now ? "open now" : "closed"));
		
		return details;
	}
	
	/**
	 * Assuming that geometry != null since google maps could find it
	 * using some sort of coordintates
	 * @return a pair of latitude and longitude coordinates of this place
	 */
	public LatLng getLatLng() {
		return new LatLng(geometry.location.lat, geometry.location.lng);
	}
	
	@Override
	public String toString() {
		return name + " - " + id + " - " + reference;
	}

	@SuppressWarnings("serial")
	public static class Geometry implements Serializable {
		@Key
		public Location location;
	}
	
	@SuppressWarnings("serial")
	public static class OpeningHours implements Serializable {
		@Key
		public boolean open_now;
	}
	
	@SuppressWarnings("serial")
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

	@SuppressWarnings("serial")
	public static class Location implements Serializable {
		@Key
		public double lat;

		@Key
		public double lng;
	}
	
	public String format(String detail) {
		return detail == null ? NO_DATA : detail;
	}
	
	public String format(double detail) {
		return detail == 0.0 ? NO_DATA : Double.toString(detail);
	}
	
	@SuppressLint("DefaultLocale")
	public String format(Detail detail) {
		return detail.name().toLowerCase().replace('_', ' ');
	}
}
