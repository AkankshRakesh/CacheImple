public class CacheImpl<K, V> implements Cache<K, V> {
    private final int capacity;
    private final Map<K, V> storage;
    private final EvictionPolicy<K> evictionPolicy;
    
    public CacheImpl(int capacity, EvictionPolicy<K> evictionPolicy) {
        this.capacity = capacity;
        this.storage = new HashMap<>();
        this.evictionPolicy = evictionPolicy;
    }
    
    @Override
    public void put(K key, V value) {
        boolean isUpdate = storage.containsKey(key);
        
        if (!isUpdate && storage.size() >= capacity) {
            K evicted = evictionPolicy.evict();
            storage.remove(evicted);
            evictionPolicy.onRemove(evicted);
        }
        
        storage.put(key, value);
        if (isUpdate) {
            evictionPolicy.onAccess(key);
        } else {
            evictionPolicy.onPut(key);
        }
    }
    
    @Override
    public Optional<V> get(K key) {
        V value = storage.get(key);
        if (value != null) {
            evictionPolicy.onAccess(key);
        }
        return Optional.ofNullable(value);
    }
    
    @Override
    public void remove(K key) {
        if (storage.remove(key) != null) {
            evictionPolicy.onRemove(key);
        }
    }
    
    @Override
    public void clear() {
        storage.clear();
        evictionPolicy.clear();
    }
    
    @Override
    public int size() {
        return storage.size();
    }
    
    @Override
    public boolean containsKey(K key) {
        return storage.containsKey(key);
    }
}
