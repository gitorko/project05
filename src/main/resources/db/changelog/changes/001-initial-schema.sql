CREATE TABLE distributed_lock
(
    lock_id      BIGSERIAL PRIMARY KEY,
    lock_name    VARCHAR(255) UNIQUE,
    lock_until   TIMESTAMP,
    lock_at      TIMESTAMP,
    lock_by      VARCHAR(255),
    lock_version BIGINT
);