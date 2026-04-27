CREATE TABLE virtual_machines (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    os_type     VARCHAR(20)  NOT NULL,  -- WINDOWS_10 ou WINDOWS_11
    profile     VARCHAR(10)  NOT NULL,  -- WEAK, MEDIUM, STRONG
    status      VARCHAR(10)  NOT NULL,  -- RUNNING, STOPPED, PAUSED
    cpu_cores   INTEGER      NOT NULL,
    ram_gb      INTEGER      NOT NULL,
    disk_gb     INTEGER      NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);
