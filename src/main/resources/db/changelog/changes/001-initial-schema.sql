CREATE TABLE distributed_locks
(
    lock_id     BIGINT PRIMARY KEY,
    expiry_time TIMESTAMP
);