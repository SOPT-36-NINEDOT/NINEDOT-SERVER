ALTER TABLE mandalart
    ADD COLUMN completed_at TIMESTAMP NULL;

UPDATE mandalart
SET completed_at = created_at
WHERE completed_at IS NULL;

CREATE INDEX idx_mandalart_completed_at
    ON mandalart (completed_at);