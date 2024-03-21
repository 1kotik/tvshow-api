package com.javaprojects.tvshowapi.cache;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class EntityCache<K, V> {
    Map<K, V> cache = new HashMap<>();

    private static final int CAPACITY = 200;

    public V get(K key) {
        return cache.get(key);
    }


    public void put(K key, V value) {
        if (cache.size() >= CAPACITY) cache.clear();
        cache.put(key, value);
    }

    public void remove(K key) {
        cache.remove(key);
    }
}
