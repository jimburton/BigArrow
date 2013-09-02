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
 * @author Almas (ab607)
 * 
 */
public class Cache2<K, V extends Cacheable> {
    
    /**
     * Maximum number of key-value mappings that can be held in cache
     * This value is used by default when the limit is not specified
     */
    private static final int DEFAULT_CACHE_SIZE_LIMIT = 30;
    
    /**
     * Maximum number of key-value mappings that can be held in cache
     */
    private final int sizeLimit;
    
    /**
     * Holds formatted query string as key which maps to places list value
     * which would be returned if query was executed
     */
    private final WeakHashMap<K, V> cache;
    
    /**
     * Provides means of identifying the least-recently accessed cache item
     * 
     * First (head) element of this queue is the key
     * of the least-recently cached entry
     */
    private final LinkedList<K> queue = new LinkedList<K>();
    
    /**
     * Constructs cache with given limit
     * 
     * @param limit size of cache
     */
    public Cache2(int limit) {
        sizeLimit = limit;
        cache = new WeakHashMap<K, V>(sizeLimit);
    }
    
    /**
     * Constructs cache with default size limit
     */
    public Cache2() {
        this(DEFAULT_CACHE_SIZE_LIMIT);
    }

    /**
     * Maps the specified key to the specified value
     *
     * @param key
     * @param value
     */
    public void store(K key, V value) {
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
    public V get(K key) {
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
    public boolean contains(K key) {
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
