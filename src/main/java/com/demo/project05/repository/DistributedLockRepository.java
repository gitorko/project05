package com.demo.project05.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import com.demo.project05.domain.DistributedLock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface DistributedLockRepository extends CrudRepository<DistributedLock, Long> {
    @Query("SELECT s FROM DistributedLock s WHERE s.lockName = :lockName AND (s.lockUntil IS NULL OR s.lockUntil <= :currentTime)")
    Optional<DistributedLock> findUnlocked(@Param("lockName") String lockName, @Param("currentTime") LocalDateTime currentTime);

    Optional<DistributedLock> findByLockName(String lockName);
}
