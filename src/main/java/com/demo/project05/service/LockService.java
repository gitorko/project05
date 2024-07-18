package com.demo.project05.service;

import java.util.concurrent.TimeUnit;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LockService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean tryLock(long lockId, int expirySeconds) {
        String lockQuery = "SELECT pg_try_advisory_lock(:lockId)";
        Query query = entityManager.createNativeQuery(lockQuery);
        query.setParameter("lockId", lockId);

        Boolean lockAcquired = (Boolean) query.getSingleResult();

        if (lockAcquired != null && lockAcquired) {
            log.info("Acquired lock: {}", lockId);
            scheduleUnlock(lockId, expirySeconds);
            return lockAcquired;
        }
        return lockAcquired;
    }

    @Transactional
    public void unlock(long lockId) {
        String unlockQuery = "SELECT pg_advisory_unlock(:lockId)";
        Query query = entityManager.createNativeQuery(unlockQuery);
        query.setParameter("lockId", lockId);

        Boolean unlocked = (Boolean) query.getSingleResult();
        if (unlocked != null && unlocked) {
            log.info("Unlocking lock: {}", lockId);
        } else {
            log.error("Unlock Failed!");
            throw new RuntimeException("Unlock Failed!");
        }
    }

    /**
     * If the server dies then session closed so lock is auto released.
     */
    private void scheduleUnlock(long lockId, int expirySeconds) {
        Thread.startVirtualThread(() -> {
            try {
                TimeUnit.SECONDS.sleep(expirySeconds);
                unlock(lockId);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
}
