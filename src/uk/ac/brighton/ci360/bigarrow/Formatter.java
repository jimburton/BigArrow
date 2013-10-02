package uk.ac.brighton.ci360.bigarrow;

/**
 * Users may use this interface to format cache
 * keys appropriately
 * 
 * Copyright (c) 2013 The BigArrow authors (see the file AUTHORS).
 * See the file LICENSE for copying permission.
 * 
 * @author Almas Baimagambetov (ab607)
 */
public interface Formatter {
    
    /**
     * Used by {@code Cache} to format reference keys
     * to cached objects
     * 
     * @param key
     *            raw key
     * @return
     *          formatted key
     */
    public String formatKey(String key);
}
