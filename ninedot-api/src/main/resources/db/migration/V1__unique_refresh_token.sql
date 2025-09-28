SET @idx_exists := (SELECT COUNT(1)
                    FROM INFORMATION_SCHEMA.STATISTICS
                    WHERE table_schema = DATABASE()
                      AND table_name = 'refresh_token'
                      AND index_name = 'uq_refresh_token_user');

SET @ddl := IF(
        @idx_exists = 0,
        'ALTER TABLE refresh_token ADD UNIQUE INDEX uq_refresh_token_user (user_id)',
        'SELECT 1'
            );

PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;