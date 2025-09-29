ALTER TABLE refresh_token
    MODIFY COLUMN expires_at TIMESTAMP(6) NOT NULL COMMENT '토큰 만료 시간';