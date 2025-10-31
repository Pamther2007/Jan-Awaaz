package com.example.CIRS.model.mongodb;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.CIRS.model.Role;

@Document(collection = "users")
public class MongoUser {

    @Id
    private String id;
    private String username;
    private String password;
    private Role role;

    public MongoUser() {}

    public MongoUser(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}