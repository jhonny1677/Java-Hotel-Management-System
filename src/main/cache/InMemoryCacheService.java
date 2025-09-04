package cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.Optional;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class InMemoryCacheService implements CacheService {
    private final ConcurrentHashMap<String, CacheEntry> cache;
    private final ScheduledExecutorService scheduler;
    private final long defaultTtl;

    public InMemoryCacheService() {
        this(300, TimeUnit.SECONDS); // Default 5 minutes TTL
    }

    public InMemoryCacheService(long defaultTtl, TimeUnit timeUnit) {
        this.cache = new ConcurrentHashMap<>();
        this.scheduler = Executors.newScheduledThreadPool(2);
        this.defaultTtl = timeUnit.toMillis(defaultTtl);
        
        // Start cleanup task
        startCleanupTask();
    }

    @Override
    public void put(String key, Object value) {
        put(key, value, defaultTtl, TimeUnit.MILLISECONDS);
    }

    @Override
    public void put(String key, Object value, long timeout, TimeUnit timeUnit) {
        long expirationTime = System.currentTimeMillis() + timeUnit.toMillis(timeout);
        cache.put(key, new CacheEntry(value, expirationTime));
    }

    @Override
    public Optional<Object> get(String key) {
        CacheEntry entry = cache.get(key);
        if (entry == null || entry.isExpired()) {
            cache.remove(key);
            return Optional.empty();
        }
        return Optional.of(entry.getValue());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String key, Class<T> type) {
        Optional<Object> value = get(key);
        if (value.isPresent() && type.isInstance(value.get())) {
            return Optional.of((T) value.get());
        }
        return Optional.empty();
    }

    @Override
    public boolean exists(String key) {
        CacheEntry entry = cache.get(key);
        if (entry != null && entry.isExpired()) {
            cache.remove(key);
            return false;
        }
        return entry != null;
    }

    @Override
    public void delete(String key) {
        cache.remove(key);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public long size() {
        // Clean expired entries first
        cleanupExpiredEntries();
        return cache.size();
    }

    @Override
    public void expire(String key, long timeout, TimeUnit timeUnit) {
        CacheEntry entry = cache.get(key);
        if (entry != null && !entry.isExpired()) {
            long newExpirationTime = System.currentTimeMillis() + timeUnit.toMillis(timeout);
            cache.put(key, new CacheEntry(entry.getValue(), newExpirationTime));
        }
    }

    @Override
    public long getTimeToLive(String key) {
        CacheEntry entry = cache.get(key);
        if (entry == null || entry.isExpired()) {
            return -1;
        }
        return entry.getExpirationTime() - System.currentTimeMillis();
    }

    private void startCleanupTask() {
        scheduler.scheduleAtFixedRate(this::cleanupExpiredEntries, 60, 60, TimeUnit.SECONDS);
    }

    private void cleanupExpiredEntries() {
        cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        cache.clear();
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new ConcurrentHashMap<>();
        stats.put("size", cache.size());
        stats.put("maxSize", Integer.MAX_VALUE);
        stats.put("hitCount", 0); // Would need to implement hit/miss tracking
        stats.put("missCount", 0);
        return stats;
    }

    private static class CacheEntry {
        private final Object value;
        private final long expirationTime;

        public CacheEntry(Object value, long expirationTime) {
            this.value = value;
            this.expirationTime = expirationTime;
        }

        public Object getValue() {
            return value;
        }

        public long getExpirationTime() {
            return expirationTime;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }
    }
}