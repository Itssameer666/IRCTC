package com.railnova.dto;

import com.railnova.entity.Role;
import com.railnova.entity.User;

public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private UserDto user;

    public AuthResponse() {
    }

    public AuthResponse(String token, User user) {
        this.token = token;
        this.type = "Bearer";
        this.user = new UserDto(user);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }
}
