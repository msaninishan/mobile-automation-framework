package com.nishan.mobile.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class User {

    private final String username;
    private final String password;
    private final String type;

    @JsonCreator
    public User(
            @JsonProperty("username") String username,
            @JsonProperty("password") String password,
            @JsonProperty("type")     String type) {
        this.username = username;
        this.password = password;
        this.type     = type;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getType()     { return type; }
}