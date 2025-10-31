package com.example.CIRS.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class DatabaseConfig {

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPass;

    @Value("${app.mysql.host:127.0.0.1}")
    private String host;

    @Value("${app.mysql.port:3306}")
    private String port;

    @Value("${app.db.name:cirsdb}")
    private String dbName;

    @Bean
    @Primary
    public DataSource dataSource() throws SQLException {
        // Connect to MySQL server (no default database) and (re)create the application database
        String adminUrl = String.format("jdbc:mysql://%s:%s/?serverTimezone=UTC&allowPublicKeyRetrieval=true", host, port);
        try (Connection conn = DriverManager.getConnection(adminUrl, dbUser, dbPass);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DROP DATABASE IF EXISTS " + dbName);
            stmt.executeUpdate("CREATE DATABASE " + dbName);
        }

        // Return DataSource pointing to the newly created database
        String appUrl = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC", host, port, dbName);
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setUrl(appUrl);
        ds.setUsername(dbUser);
        ds.setPassword(dbPass);
        return ds;
    }
}
