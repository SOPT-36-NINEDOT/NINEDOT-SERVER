ALTER TABLE mandalart
    ADD COLUMN completed_at TIMESTAMP;

UPDATE mandalart
SET completed_at = created_at
WHERE completed_at IS NULL;

ALTER TABLE mandalart
    ALTER COLUMN completed_at SET NOT NULL;

CREATE INDEX idx_mandalart_completed_at
    ON mandalart (completed_at);