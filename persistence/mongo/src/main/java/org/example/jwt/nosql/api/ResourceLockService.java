package org.example.jwt.nosql.api;

import java.time.Duration;

public interface ResourceLockService {
    boolean lock(String key, Duration howLong);

    void unlock(String key);
}
