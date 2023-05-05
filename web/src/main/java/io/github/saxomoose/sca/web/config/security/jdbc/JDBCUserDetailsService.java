package io.github.saxomoose.sca.web.config.security.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import io.github.saxomoose.sca.web.services.UserService;

@Service
public class JDBCUserDetailsService implements UserDetailsService {
    private final UserService userService;

    @Autowired
    public JDBCUserDetailsService(UserService userService) 
    {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) 
    {
        return userService.select(username);
    }
}
