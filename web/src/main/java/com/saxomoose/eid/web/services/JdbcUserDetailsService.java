package com.saxomoose.eid.web.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JdbcUserDetailsService implements UserDetailsService {
    private final UserService userService;

    @Autowired
    public JdbcUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        final var user = userService.findByUsername(username);
        if(user.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }

        return user.get();
    }
}
