CREATE USER interface_manager WITH PASSWORD 'meta1234';
CREATE SCHEMA interface_manager AUTHORIZATION interface_manager;
GRANT ALL PRIVILEGES ON SCHEMA interface_manager TO interface_manager;
ALTER ROLE interface_manager SET search_path TO interface_manager, public;

CREATE TABLE interface_info (
    interface_id      VARCHAR(30)  NOT NULL,
    interface_name    VARCHAR(100),
    cron_expression   VARCHAR(30),
    pattern_type      CHAR(3)      NOT NULL,
    send_system_code  CHAR(3)      NOT NULL,
    recv_system_code  CHAR(3)      NOT NULL,
    use_yn            CHAR(1)      NOT NULL DEFAULT 'N',
    created_at        TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    created_by        VARCHAR(20),
    updated_by        VARCHAR(20),
    
    -- 기본키(PK) 설정 (필요 시 수정하세요)
    CONSTRAINT pk_interface_info PRIMARY KEY (interface_id)
);

-- 트리거 생성
CREATE OR REPLACE FUNCTION update_modified_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- 트리거 주입
CREATE TRIGGER update_interface_info_modtime
    BEFORE UPDATE ON interface_info
    FOR EACH ROW
    EXECUTE PROCEDURE update_modified_column();


CREATE TABLE pattern_info (
    pattern_code    VARCHAR(10) PRIMARY KEY,
    pattern_name    VARCHAR(50) NOT NULL,
    -- 연계 유형: CHECK 제약 조건으로 값을 고정
    interface_type  VARCHAR(10) NOT NULL 
                    CHECK (interface_type IN ('REALTIME', 'BATCH')), 
    pattern_desc    VARCHAR(200),
    use_yn          CHAR(1) DEFAULT 'Y',
    sort_order      INTEGER DEFAULT 0
);

-- 초기 데이터 샘플
INSERT INTO pattern_info (pattern_code, pattern_name, interface_type) VALUES ('P01', 'DB2DB', 'BATCH');
INSERT INTO pattern_info (pattern_code, pattern_name, interface_type) VALUES ('P02', 'API2DB', 'REALTIME');

