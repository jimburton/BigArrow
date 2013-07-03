package uk.ac.brighton.ci360.bigarrow.places;
/**
 * A data transfer object for a set of results returned from the Places API.
 * 
 * Copyright (c) 2013 The BigArrow authors (see the file AUTHORS).
 * See the file LICENSE for copying permission.
 * 
 * @author jb259
 **/
import java.io.Serializable;
import java.util.List;
 
import com.google.api.client.util.Key;

public class PlacesList implements Serializable {
 
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Key
    public String status;
 
    @Key
    public List<Place> results;
 
}
