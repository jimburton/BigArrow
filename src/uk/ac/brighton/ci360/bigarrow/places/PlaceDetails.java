package uk.ac.brighton.ci360.bigarrow.places;
/**
 * A data transfer object for a result returned from a detail search against the Places API.
 * 
 * Copyright (c) 2013 The BigArrow authors (see the file AUTHORS).
 * See the file LICENSE for copying permission.
 * 
 * @author jb259
 **/
import java.io.Serializable;

import com.google.api.client.util.Key;

public class PlaceDetails implements Serializable {

	private static final long serialVersionUID = 1L;

	public static String KEY_REFERENCE = "reference";
    public static String KEY_NAME = "name";
 
    @Key
    public String status;
     
    @Key
    public Place result;
 
    @Override
    public String toString() {
        if (result!=null) {
            return result.toString();
        }
        return super.toString();
    }
}
