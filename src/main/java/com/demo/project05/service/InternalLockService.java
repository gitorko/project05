package com.demo.project05.service;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Optional;

import com.demo.project05.domain.DistributedLock;
import com.demo.project05.repository.DistributedLockRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Since @Transaction needs public scope we dont want to expose this class to other classes, hence it's an internal class.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InternalLockService {

    final DistributedLockRepository repository;
    final String lockedBy = getHostIdentifier();

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean tryLock(String lockName, int expirySeconds) {
        LocalDateTime lockUntil = LocalDateTime.now().plusSeconds(expirySeconds);
        LocalDateTime now = LocalDateTime.now();
        Optional<DistributedLock> lockOptional = repository.findUnlocked(lockName, now);
        if (lockOptional.isPresent()) {
            DistributedLock lock = lockOptional.get();
            lock.setLockUntil(lockUntil);
            lock.setLockAt(now);
            lock.setLockBy(lockedBy);
            repository.save(lock);
        } else {
            DistributedLock newLock = new DistributedLock();
            newLock.setLockName(lockName);
            newLock.setLockUntil(lockUntil);
            newLock.setLockAt(now);
            newLock.setLockBy(lockedBy);
            repository.save(newLock);
        }
        return true;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean unlock(String lockName) {
        Optional<DistributedLock> lockOptional = repository.findByLockName(lockName);
        if (lockOptional.isPresent()) {
            DistributedLock lock = lockOptional.get();
            //Only the node that locked will be able to unlock
            if (lockedBy.equals(lock.getLockBy())) {
                lock.setLockUntil(null);
                lock.setLockAt(null);
                lock.setLockBy(null);
                repository.save(lock);
            }
            return true;
        }
        return false;
    }

    @SneakyThrows
    private String getHostIdentifier() {
        // Get unique identifier for this instance, e.g., hostname or UUID
        return InetAddress.getLocalHost().getHostName();
    }
}
