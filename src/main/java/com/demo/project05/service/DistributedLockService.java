package com.demo.project05.service;

import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DistributedLockService {

    final InternalLockService internalLockService;

    public boolean acquireLock(String lockName, int expirySeconds) {
        log.info("Attempting to acquire lock: {}", lockName);
        try {
            boolean lockStatus = internalLockService.tryLock(lockName, expirySeconds);
            scheduleUnlock(lockName, expirySeconds);
            return lockStatus;
        } catch (ObjectOptimisticLockingFailureException ex) {
            log.error("Unable to acquire lock due to concurrent request");
            return false;
        } catch (DataIntegrityViolationException ex) {
            log.error("Lock already exists");
            return false;
        } catch (Exception ex) {
            log.error("Failed to acquire lock");
            return false;
        }
    }

    public boolean releaseLock(String lockName) {
        try {
            log.info("Attempting to release lock: {}", lockName);
            return internalLockService.unlock(lockName);
        } catch (Exception ex) {
            log.error("Failed to release lock");
            return false;
        }
    }

    /**
     * Auto cleanup job.
     * Code will work even if this fails. But this will set the lock to null to make it more clear that it is unused.
     * Even if server dies the lock will be released based on lock_until time
     */
    private void scheduleUnlock(String lockName, int expirySeconds) {
        Thread.startVirtualThread(() -> {
            try {
                TimeUnit.SECONDS.sleep(expirySeconds);
                releaseLock(lockName);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
}
