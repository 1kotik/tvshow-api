package com.javaprojects.tvshowapi.cache;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class EntityCache<K, V> {
    Map<K, V> cache = new ConcurrentHashMap<>();

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
