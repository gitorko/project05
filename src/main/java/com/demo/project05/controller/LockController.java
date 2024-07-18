package com.demo.project05.controller;

import com.demo.project05.service.DistributedLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LockController {

    final DistributedLockService distributedLockService;

    @GetMapping("/try-lock")
    public String tryLock(@RequestParam String lockName, @RequestParam int expirySeconds) {
        boolean lockAcquired = distributedLockService.acquireLock(lockName, expirySeconds);
        return lockAcquired ? "Lock acquired!" : "Failed to acquire lock!";
    }

    @GetMapping("/unlock")
    public String unlock(@RequestParam String lockName) {
        distributedLockService.releaseLock(lockName);
        return "Lock released!";
    }
}
