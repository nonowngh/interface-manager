CREATE USER interface_manager WITH PASSWORD 'meta1234';
CREATE SCHEMA interface_manager AUTHORIZATION interface_manager;
GRANT ALL PRIVILEGES ON SCHEMA interface_manager TO interface_manager;
ALTER ROLE interface_manager SET search_path TO interface_manager, public;
-- interface_manager.interface_info definition
-- DROP TABLE interface_manager.interface_info
CREATE TABLE interface_manager.interface_info (
    interface_id varchar(30) NOT NULL,
    interface_name varchar(100) NULL,
    cron_expression varchar(30) NULL,
    pattern_type bpchar(3) NOT NULL,
    send_system_code bpchar(3) NOT NULL,
    recv_system_code bpchar(3) NOT NULL,
    use_yn bpchar(1) DEFAULT 'N'::bpchar NOT NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
    updated_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
    created_by varchar(20) NULL,
    updated_by varchar(20) NULL,
    deploy_status bpchar(1) DEFAULT 'N'::bpchar NOT NULL,
    last_deploy_at timestamp NULL,
    last_deploy_by varchar(20) NULL,
    CONSTRAINT pk_interface_info PRIMARY KEY (interface_id),
    CONSTRAINT fkdgso1xpvpjbty7tcvab5fpcea FOREIGN KEY (pattern_type) REFERENCES interface_manager.pattern_info(pattern_code)
);

-- index
--CREATE UNIQUE INDEX pk_interface_info ON interface_manager.interface_info USING btree (interface_id);

-- interface_manager.interface_adapter_map definition
-- DROP TABLE interface_manager.interface_adapter_map;
CREATE TABLE interface_manager.interface_adapter_map (
    interface_id varchar(30) NOT NULL,
    adapter_id varchar(100) NOT NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
    created_by varchar(20) NULL,
    last_deploy_version bpchar(12) NULL,
    CONSTRAINT pk_interface_adapter_map PRIMARY KEY (interface_id, adapter_id),
    CONSTRAINT fk_adapter_map_interface FOREIGN KEY (interface_id) REFERENCES interface_manager.interface_info(interface_id) ON DELETE CASCADE
);

-- index
--CREATE UNIQUE INDEX pk_interface_adapter_map ON interface_manager.interface_adapter_map USING btree (interface_id, adapter_id);

-- interface_manager.interface_deploy_hist definition
-- DROP TABLE interface_manager.interface_deploy_hist;
CREATE TABLE interface_manager.interface_deploy_hist (
    deploy_seq serial4 NOT NULL,
    interface_id varchar(30) NOT NULL,
    deploy_version bpchar(12) NOT NULL,
    deploy_data jsonb NOT NULL,
    target_adapter varchar(50) NOT NULL,
    result_code varchar(1) DEFAULT 'S'::character varying NULL,
    result_msg text NULL,
    deployed_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
    deployed_by varchar(20) NULL,
    CONSTRAINT pk_interface_deploy_hist PRIMARY KEY (deploy_seq)
);
CREATE INDEX idx_deploy_hist_if_id ON interface_manager.interface_deploy_hist USING btree (interface_id);

-- interface_manager.pattern_info definition
-- DROP TABLE interface_manager.pattern_info;

CREATE TABLE interface_manager.pattern_info (
    pattern_code varchar(10) NOT NULL,
    pattern_name varchar(50) NOT NULL,
    interface_type varchar(10) NOT NULL,
    pattern_desc varchar(200) NULL,
    use_yn bpchar(1) DEFAULT 'Y'::bpchar NULL,
    sort_order int4 DEFAULT 0 NULL,
    CONSTRAINT pattern_info_interface_type_check CHECK (((interface_type)::text = ANY ((ARRAY['REALTIME'::character varying, 'BATCH'::character varying])::text[]))),
    CONSTRAINT pattern_info_pkey PRIMARY KEY (pattern_code)
);

--index
--CREATE UNIQUE INDEX pattern_info_pkey ON interface_manager.pattern_info USING btree (pattern_code);

-- 초기 데이터 샘플
INSERT INTO pattern_info (pattern_code, pattern_name, interface_type) VALUES ('P01', 'DB2DB', 'BATCH');
INSERT INTO pattern_info (pattern_code, pattern_name, interface_type) VALUES ('P02', 'API2DB', 'REALTIME');

-- interface_manager.interface_prop definition
-- DROP TABLE interface_manager.interface_prop;

CREATE TABLE interface_manager.interface_prop (
    interface_id varchar(30) NOT NULL,
    pattern_code varchar(10) NOT NULL,
    property_name varchar(100) NOT NULL,
    property_value varchar(1000) NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
    updated_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
    created_by varchar(50) NULL,
    updated_by varchar(50) NULL,
    CONSTRAINT pk_interface_prop_info PRIMARY KEY (interface_id, pattern_code, property_name),
    CONSTRAINT fk_interface_prop_pattern FOREIGN KEY (pattern_code) REFERENCES interface_manager.pattern_info(pattern_code) ON UPDATE CASCADE
);
CREATE INDEX idx_interface_prop_id ON interface_manager.interface_prop USING btree (interface_id);

-- interface_manager.interface_sql definition
-- DROP TABLE interface_manager.interface_sql;

CREATE TABLE interface_manager.interface_sql (
    interface_id varchar(30) NOT NULL,
    sql_id varchar(50) NOT NULL,
    sql_type varchar(6) NOT NULL,
    sql_query text NOT NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
    updated_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
    CONSTRAINT pk_interface_sql PRIMARY KEY (interface_id, sql_id),
    CONSTRAINT fk_interface_sql_master FOREIGN KEY (interface_id) REFERENCES interface_manager.interface_info(interface_id) ON DELETE CASCADE
);
CREATE INDEX idx_interface_sql_interface_id ON interface_manager.interface_sql USING btree (interface_id);

