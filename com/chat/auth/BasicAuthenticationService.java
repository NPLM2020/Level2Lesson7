package com.chat.auth;

import com.chat.entity.User;
import com.chat.server.DatabaseManager;

import java.util.List;
import java.util.Optional;

public class BasicAuthenticationService implements AuthenticationService {
    private DatabaseManager databaseService;

    public BasicAuthenticationService(DatabaseManager databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    public Optional<User> doAuth(String email, String password) {
        User user = databaseService.getUserByEmail(email);
        if (user != null) {
            if (user.getPassword().equals(password)) return Optional.of(user);
        }
        return Optional.empty();
    }
}
