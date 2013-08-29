package uk.ac.brighton.ci360.bigarrow;

import java.util.LinkedList;
import java.util.WeakHashMap;

import uk.ac.brighton.ci360.bigarrow.places.PlacesList;

/**
 * This class saves information about searches
 * so that same search instead of loading data over network
 * will read it from memory
 * 
 * Copyright (c) 2013 The BigArrow authors (see the file AUTHORS).
 * See the file LICENSE for copying permission.
 * 
 * @author Almas (ab607)
 * 
 */
public class Cache {

    /**
     * Holds the Singleton instance of Cache.
     */
    private static final Cache instance = new Cache();
    
    /**
     * Maximum number of key-value mappings that can be held in cache
     */
    public static final int CACHE_SIZE_LIMIT = 30;
    
    /**
     * Holds formatted query string as key which maps to places list value
     * which would be returned if query was executed
     */
    private final WeakHashMap<String, PlacesList> cache = new WeakHashMap<String, PlacesList>(CACHE_SIZE_LIMIT);
    
    /**
     * Provides means of identifying the least-recently accessed cache item
     * 
     * First (head) element of this queue is the key
     * of the least-recently cached entry
     */
    private final LinkedList<String> queue = new LinkedList<String>();
    
    /**
     * Do not let anyone instantiate this class
     */
    private Cache() {}
    
    /**
     * Returns the single {@code Cache} instance.
     *
     * @return the {@code Cache} object for the current application.
     */
    public static Cache getInstance() {
        return instance;
    }

    /**
     * Stores the formatted search URL and its result in cache
     * 
     * @param queryString search URL
     * @param response search result
     */
    public void store(String queryString, PlacesList response) {
        if (queue.size() == CACHE_SIZE_LIMIT) {
            cache.remove(queue.poll());
        }
        
        String key = format(queryString);
        queue.add(key);
        cache.put(key, response);
    }
    
    /**
     * Retrieves search result of given query
     * 
     * @param queryString
     * @return search result or null if no such query key exists in cache
     */
    public PlacesList get(String queryString) {
        String key = format(queryString);
        queue.remove(key);
        queue.add(key);
        return cache.get(key);
    }
    
    /**
     * Checks whether the given query string has been found in the cache
     * 
     * @param queryString
     * @return true if query string exists, false otherwise
     */
    public boolean contains(String queryString) {
        return cache.containsKey(format(queryString));
    }
    
    /**
     * Formats the query string used in URL as follows:
     * "type1,type2&latitude,longitude"
     * 
     * @param queryString
     * @return formatted query string
     */
    private String format(String queryString) {
        String location = queryString.substring(queryString.indexOf("location=")+9);
        location = location.substring(0, location.indexOf("&"));
        String lat = location.substring(0, location.indexOf(","));
        String lng = location.substring(location.indexOf(",") + 1);
        
        lat = String.format("%.3f", Double.parseDouble(lat));
        lng = String.format("%.3f", Double.parseDouble(lng));
        
        String type = queryString.substring(queryString.indexOf("types=")+6);

        return type + "&" + lat + "," + lng;
    }
}
