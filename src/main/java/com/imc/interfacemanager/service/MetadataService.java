package com.imc.interfacemanager.service;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MetadataService {

    private final EntityManager entityManager;

    @Value("${metadata.schema-name:public}")
    private String targetSchema;

    /** DBMS 종류에 상관없이 테이블 목록 조회 (JDBC 표준) */
    @Transactional(readOnly = true)
    public List<String> getTableList() {
        List<String> tables = new ArrayList<>();
        
        // 하이버네이트 세션을 통해 JDBC Connection 획득
        Session session = entityManager.unwrap(Session.class);
        session.doWork(connection -> {
            DatabaseMetaData metaData = connection.getMetaData();
            // TABLE_TYPE을 "TABLE"로 지정하여 뷰(VIEW) 등을 제외한 순수 테이블만 추출
            try (ResultSet rs = metaData.getTables(null, targetSchema, "%", new String[]{"TABLE"})) {
                while (rs.next()) {
                    tables.add(rs.getString("TABLE_NAME"));
                }
            }
        });
        
        return tables;
    }

    /** DBMS 종류에 상관없이 특정 테이블의 컬럼 목록 조회 (JDBC 표준) */
    @Transactional(readOnly = true)
    public List<String> getColumnList(String tableName) {
        List<String> columns = new ArrayList<>();

        Session session = entityManager.unwrap(Session.class);
        session.doWork(connection -> {
            DatabaseMetaData metaData = connection.getMetaData();
            // 스키마와 테이블명을 인자로 전달 (PostgreSQL은 보통 소문자, Oracle은 대문자임에 유의)
            try (ResultSet rs = metaData.getColumns(null, targetSchema, tableName, "%")) {
                while (rs.next()) {
                    columns.add(rs.getString("COLUMN_NAME"));
                }
            }
        });

        return columns;
    }
}