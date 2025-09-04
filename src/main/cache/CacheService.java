package cache;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public interface CacheService {
    void put(String key, Object value);
    void put(String key, Object value, long timeout, TimeUnit timeUnit);
    Optional<Object> get(String key);
    <T> Optional<T> get(String key, Class<T> type);
    boolean exists(String key);
    void delete(String key);
    void clear();
    long size();
    void expire(String key, long timeout, TimeUnit timeUnit);
    long getTimeToLive(String key);
}