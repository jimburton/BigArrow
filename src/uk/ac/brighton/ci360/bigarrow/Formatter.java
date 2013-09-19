package uk.ac.brighton.ci360.bigarrow;

public abstract class Formatter {
    
    /**
     * Used by {@code Cache} to format reference keys
     * to cached objects
     * 
     * @param key
     *            raw key
     * @return
     *          formatted key
     */
    public abstract String formatKey(String key);
}
