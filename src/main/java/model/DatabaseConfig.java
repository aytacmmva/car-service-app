package model;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConfig {
    private static HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        // Bazanın ünvanı, istifadəçi adı və şifrəsi
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/car_service_db");
        config.setUsername("postgres");
        config.setPassword("12345");

        // Pool tənzimləmələri
        config.setMaximumPoolSize(10); // Eyni anda maksimum 10 bağlantı
        config.setIdleTimeout(300000); // 5 dəqiqə boş qalsa bağlantını bağla

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}