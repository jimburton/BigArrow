package uk.ac.brighton.ci360.bigarrow.cache;
/**
 * Implements a cache for parsed responses that originated as JSON responses from the Places API. Code 
 * taken from https://github.com/thest1/LazyList.
 * 
 * Copyright (c) 2013 Fedor Vlasov <thest2@gmail.com> and The BigArrow authors (see the file AUTHORS).
 * See the file LICENSE for copying permission.
 * 
 * @author jb259
 */
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import android.graphics.Bitmap;
import android.util.Log;

public class MemoryCache {

    private static final String TAG = "MemoryCache";
    private Map<String, Bitmap> cache=Collections.synchronizedMap(
            new LinkedHashMap<String, Bitmap>(10,1.5f,true));//Last argument true for LRU ordering
    private long size=0;//current allocated size
    private long limit=1000000;//max memory in bytes

    public MemoryCache(){
        //use 25% of available heap size
        setLimit(Runtime.getRuntime().maxMemory()/4);
    }
    
    /**
     * Set the limit on this MemoryCache
     * @param new_limit
     */
    public void setLimit(long new_limit){
        limit=new_limit;
        Log.i(TAG, "MemoryCache will use up to "+limit/1024./1024.+"MB");
    }

    /**
     * Get the Bitmap associated with id.
     * @param id
     * @return
     */
    public Bitmap get(String id){
        try{
            if(!cache.containsKey(id))
                return null;
            //NullPointerException sometimes happen here http://code.google.com/p/osmdroid/issues/detail?id=78 
            return cache.get(id);
        }catch(NullPointerException ex){
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Put a new key:value pair into the cache.
     * @param id
     * @param bitmap
     */
    public void put(String id, Bitmap bitmap){
        try{
            if(cache.containsKey(id))
                size-=getSizeInBytes(cache.get(id));
            cache.put(id, bitmap);
            size+=getSizeInBytes(bitmap);
            checkSize();
        }catch(Throwable th){
            th.printStackTrace();
        }
    }
    
    /**
     * Calculate the size of the cache.
     */
    private void checkSize() {
        Log.i(TAG, "cache size="+size+" length="+cache.size());
        if(size>limit){
            Iterator<Entry<String, Bitmap>> iter=cache.entrySet().iterator();//least recently accessed item will be the first one iterated  
            while(iter.hasNext()){
                Entry<String, Bitmap> entry=iter.next();
                size-=getSizeInBytes(entry.getValue());
                iter.remove();
                if(size<=limit)
                    break;
            }
            Log.i(TAG, "Clean cache. New size "+cache.size());
        }
    }

    /**
     * Clear the cache.
     */
    public void clear() {
        try{
            //NullPointerException sometimes happen here http://code.google.com/p/osmdroid/issues/detail?id=78 
            cache.clear();
            size=0;
        }catch(NullPointerException ex){
            ex.printStackTrace();
        }
    }

    /**
     * Calculate the size of a Bitmap.
     * @param bitmap
     * @return
     */
    long getSizeInBytes(Bitmap bitmap) {
        if(bitmap==null)
            return 0;
        return bitmap.getRowBytes() * bitmap.getHeight();
    }
}
