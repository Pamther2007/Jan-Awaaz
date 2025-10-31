package com.example.CIRS.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mongodb")
public class MongoProperties {
    private String username;
    private String password;
    private String cluster;
    private String database;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUri() {
        return String.format(
            "mongodb+srv://%s:%s@%s/%s?retryWrites=true&w=majority&authSource=admin",
            username, password, cluster, database
        );
    }
}