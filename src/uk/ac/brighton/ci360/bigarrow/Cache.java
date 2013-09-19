package uk.ac.brighton.ci360.bigarrow;

import java.util.LinkedList;
import java.util.WeakHashMap;

/**
 * This class saves information about searches
 * so that same search instead of loading data over network
 * will read it from memory
 * 
 * Copyright (c) 2013 The BigArrow authors (see the file AUTHORS).
 * See the file LICENSE for copying permission.
 * 
 * @author Almas Baimagambetov (ab607)
 * 
 */
public class Cache<V extends Cacheable> {

    /**
     * Maximum number of key-value mappings that can be held in cache
     * This value is used by default when the limit is not specified
     */
    private static final int DEFAULT_CACHE_SIZE_LIMIT = 30;
    
    /**
     * Maximum number of key-value mappings that can be held in this cache
     */
    private int sizeLimit;
    
    /**
     * Formatter used to format cache keys
     */
    private Formatter formatter;
    
    /**
     * Actual key and value "holder"
     */
    private WeakHashMap<String, V> cache;
    
    /**
     * Provides means of identifying the least-recently accessed cache item
     * 
     * First (head) element of this queue is the key
     * of the least-recently cached entry
     */
    private LinkedList<String> queue = new LinkedList<String>();
    
    /**
     * Constructs cache with given formatter
     * and default size limit
     * 
     * @param formatter
     */
    public Cache(Formatter formatter) {
        this(DEFAULT_CACHE_SIZE_LIMIT, formatter);
    }
    
    /**
     * Constructs cache with given formatter
     * and given size limit
     * 
     * @param limit
     * @param formatter
     */
    public Cache(int limit, Formatter formatter) {
        sizeLimit = limit;
        this.formatter = formatter;
        cache = new WeakHashMap<String, V>(sizeLimit);
    }

    /**
     * Maps the specified key to the specified value
     *
     * @param key
     * @param value
     */
    public void store(String key, V value) {
        key = formatter.formatKey(key);
        if (queue.size() == sizeLimit) {
            cache.remove(queue.poll());
        }
        
        queue.add(key);
        cache.put(key, value);
    }
    
    /**
     * Retrieve the value of the mapping with the specified key
     *
     * @param key
     * @return the value of the mapping with the specified key, or {@code null}
     *         if no mapping for the specified key is found
     */
    public V get(String key) {
        key = formatter.formatKey(key);
        queue.remove(key);
        queue.add(key);
        return cache.get(key);
    }
    
    /**
     * Returns whether this cache contains the specified key
     *
     * @param key the key to search for
     * @return {@code true} if this map contains the specified key,
     *         {@code false} otherwise
     */
    public boolean contains(String key) {
        key = formatter.formatKey(key);
        return cache.containsKey(key);
    }
    
    /**
     * @return number of items currently held in cache
     */
    public int getSize() {
        return cache.size();
    }
    
    /**
     * @return maximum number of items that can be held in this cache
     */
    public int getLimit() {
        return sizeLimit;
    }
    
    /**
     * Removes all data held in cache
     * Consequently on the first following GC sweep
     * GC will attempt to free up the memory used by old objects
     */
    public void free() {
        queue.clear();
        cache.clear();
    }
}
