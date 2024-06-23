package com.javadeveloperzone.service.AuthenticationService;

import org.springframework.security.core.userdetails.User;

public class UserExtend extends User {
    private final Long id;
    public UserExtend(User user, Long id) {
        super(user.getUsername(), user.getPassword(),user.getAuthorities());
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
