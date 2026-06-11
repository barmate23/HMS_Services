package com.hotelerp.userservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
@RequiredArgsConstructor
@Slf4j
public class LegacyUserSchemaMigrator implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    @Override
    public void run(ApplicationArguments args) throws SQLException {
        if (!isMySql()) return;
        makeLegacyColumnNullable("role");
        makeLegacyColumnNullable("department");
    }

    private boolean isMySql() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            return connection.getMetaData().getDatabaseProductName().toLowerCase().contains("mysql");
        }
    }

    private void makeLegacyColumnNullable(String columnName) {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM information_schema.columns
                WHERE table_schema = DATABASE()
                  AND table_name = 'users'
                  AND column_name = ?
                  AND is_nullable = 'NO'
                """,
                Integer.class,
                columnName
        );

        if (count == null || count == 0) return;

        jdbcTemplate.execute("ALTER TABLE users MODIFY " + columnName + " VARCHAR(100) NULL");
        log.info("Relaxed legacy users.{} column to allow FK based user mapping", columnName);
    }
}
