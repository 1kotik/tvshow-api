package com.javaprojects.tvshowapi.cache;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Getter
@Setter
public class EntityCache<K, V> {
    private Map<K, V> cache;

    public EntityCache() {
        this.cache = new HashMap<>();
    }

    private static final int CAPACITY = 200;

    public V get(final K key) {
        return cache.get(key);
    }


    public void put(final K key, final V value) {
        if (cache.size() >= CAPACITY) {
            cache.clear();
        }
        cache.put(key, value);
    }

    public void remove(final K key) {
        cache.remove(key);
    }
}
