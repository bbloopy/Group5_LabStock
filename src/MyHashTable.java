public class MyHashTable<K, V> {

    private class Entry {
        K key;
        V value;
        Entry next;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }
    }

    private Object[] buckets;
    private int size;

    public MyHashTable() {
        buckets = new Object[16];
        size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private int indexFor(K key) {
        int hash = key.hashCode();
        if (hash < 0) {
            hash = -hash;
        }
        return hash % buckets.length;
    }

    @SuppressWarnings("unchecked")
    public void put(K key, V value) {
        if ((double) size / buckets.length >= 0.75) {
            resize();
        }

        int index = indexFor(key);
        Entry current = (Entry) buckets[index];

        while (current != null) {
            if (current.key.equals(key)) {
                current.value = value;
                return;
            }
            current = current.next;
        }

        Entry entry = new Entry(key, value);
        entry.next = (Entry) buckets[index];
        buckets[index] = entry;
        size++;
    }

    @SuppressWarnings("unchecked")
    public V get(K key) {
        int index = indexFor(key);
        Entry current = (Entry) buckets[index];

        while (current != null) {
            if (current.key.equals(key)) {
                return current.value;
            }
            current = current.next;
        }
        return null;
    }

    public boolean containsKey(K key) {
        return get(key) != null;
    }

    @SuppressWarnings("unchecked")
    public V remove(K key) {
        int index = indexFor(key);
        Entry current = (Entry) buckets[index];
        Entry previous = null;

        while (current != null) {
            if (current.key.equals(key)) {
                if (previous == null) {
                    buckets[index] = current.next;
                } else {
                    previous.next = current.next;
                }
                size--;
                return current.value;
            }
            previous = current;
            current = current.next;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Object[] oldBuckets = buckets;
        buckets = new Object[oldBuckets.length * 2];
        size = 0;

        for (int i = 0; i < oldBuckets.length; i++) {
            Entry current = (Entry) oldBuckets[i];
            while (current != null) {
                put(current.key, current.value);
                current = current.next;
            }
        }
    }
}