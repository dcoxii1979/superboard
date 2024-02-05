package org.blinksd.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class ListedMap<K, V> {
    private final List<ListEntry<K, V>> entries = new ArrayList<>();

    public boolean containsKey(K key) {
        return indexOfKey(key) >= 0;
    }

    public boolean containsValue(V value) {
        return indexOfValue(value) >= 0;
    }

    public void clear() {
        entries.clear();
    }

    public void putAll(Map<K, V> other) {
        for (Map.Entry<K, V> entry : other.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public V get(K key) {
        int index = indexOfKey(key);
        if (index >= 0) {
            return entries.get(index).value;
        }

        return null;
    }

    public void put(K key, V value) {
        int index = indexOfKey(key);

        if (index >= 0) {
            entries.set(index, new ListEntry<>(key, value));
        } else {
            entries.add(new ListEntry<>(key, value));
        }
    }

    public List<K> keyList() {
        List<K> keys = new ArrayList<>(size());

        for (ListEntry<K, V> entry : entries) {
            keys.add(entry.key);
        }

        return keys;
    }

    public int indexOfKey(K key) {
        for (int i = 0; i < size(); i++) {
            ListEntry<K, V> entry = entries.get(i);
            if (entry.key.equals(key)) {
                return i;
            }
        }

        return -1;
    }

    public int indexOfValue(V value) {
        for (int i = 0; i < size(); i++) {
            ListEntry<K, V> entry = entries.get(i);
            if (entry.value.equals(value)) {
                return i;
            }
        }

        return -1;
    }

    public int size() {
        return entries.size();
    }

    public K getKeyByIndex(int index) {
        if (index >= 0 && size() > index) {
            return entries.get(index).key;
        }

        return null;
    }

    public K getKeyByValue(V value) {
        return getKeyByIndex(indexOfValue(value));
    }

    private static class ListEntry<K, V> {
        final K key;
        final V value;

        private ListEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}
