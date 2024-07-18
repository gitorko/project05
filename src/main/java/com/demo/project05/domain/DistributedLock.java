package com.demo.project05.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "distributed_lock")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DistributedLock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lockId;
    private String lockName;
    private LocalDateTime lockUntil;
    private LocalDateTime lockAt;
    private String lockBy;
    @Version
    private Long lockVersion;
}
