package com.example.db1.Models;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ADMIN,PARTICIPANT,COMPANY;

    @Override
    public String getAuthority() {
        return name();
    }
}
