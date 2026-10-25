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
        ensureColumnExists("must_change_password", "BOOLEAN DEFAULT FALSE");
        ensureColumnExists("default_password_generated_at", "DATETIME NULL");
        ensureDefaultHotelRow();
    }

    private void ensureDefaultHotelRow() {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'hotels'",
                    Integer.class
            );
            if (count != null && count > 0) {
                Integer hotelCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM hotels WHERE id = 1", Integer.class);
                if (hotelCount == null || hotelCount == 0) {
                    jdbcTemplate.execute("""
                        INSERT INTO hotels (id, name, email, phone, address, city, state, country, zip_code, total_rooms, currency, is_active, created_at, updated_at)
                        VALUES (1, 'HMS Cloud - Main Hotel', 'admin@hmscloud.com', '9999999999', '100 Hospitality Way', 'Metropolis', 'Central', 'United States', '10001', 100, 'USD', 1, NOW(), NOW())
                        ON DUPLICATE KEY UPDATE id = id
                    """);
                    log.info("Ensured default hotel with ID: 1 in database via SQL.");
                }
            }
        } catch (Exception e) {
            log.debug("ensureDefaultHotelRow check: {}", e.getMessage());
        }
    }

    private void ensureColumnExists(String columnName, String columnDefinition) {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM information_schema.columns
                WHERE table_schema = DATABASE()
                  AND table_name = 'users'
                  AND column_name = ?
                """,
                Integer.class,
                columnName
        );

        if (count != null && count > 0) return;

        try {
            jdbcTemplate.execute("ALTER TABLE users ADD COLUMN " + columnName + " " + columnDefinition);
            log.info("Added missing users.{} column", columnName);
        } catch (Exception e) {
            log.debug("users.{} column migration check: {}", columnName, e.getMessage());
        }
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
