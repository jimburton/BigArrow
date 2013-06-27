package uk.ac.brighton.ci360.bigarrow.places;

import java.io.Serializable;

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
	public String formatted_address;

	@Key
	public String formatted_phone_number;

	@Override
	public String toString() {
		return name + " - " + id + " - " + reference;
	}

	public static class Geometry implements Serializable {
		@Key
		public Location location;
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
