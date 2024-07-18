package com.demo.project05.controller;

import com.demo.project05.service.LockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LockController {

    final LockService lockService;

    @GetMapping("/try-lock")
    public String tryLock(@RequestParam Long lockId, @RequestParam int expirySeconds) {
        boolean lockAcquired = lockService.tryLock(lockId, expirySeconds);
        return lockAcquired ? "Lock acquired!" : "Failed to acquire lock.";
    }

    @GetMapping("/unlock")
    public String unlock(@RequestParam Long lockId) {
        lockService.unlock(lockId);
        return "Lock released!";
    }
}
