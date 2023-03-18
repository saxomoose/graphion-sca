package io.github.saxomoose.sca.web.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class JDBCUserDetailsService implements UserDetailsService {
    private final UserService userService;

    @Autowired
    public JDBCUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userService.read(username);
    }
}
