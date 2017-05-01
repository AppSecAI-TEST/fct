package com.fct.common.utils;

import java.util.HashMap;

/**
 * @author ningyang
 */
public class MapUtils<K, V> extends HashMap<K, V> {

    public static MapUtils<String, Object> builder() {
        return new MapUtils();
    }

    public MapUtils<K, V> putObj(K key, V value) {
        super.put(key, value);
        return this;
    }
}
