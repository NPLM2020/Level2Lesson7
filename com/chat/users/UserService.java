package com.chat.users;

import com.chat.entity.User;
import com.chat.server.DatabaseManager;

public class UserService implements UserManager {
    private DatabaseManager databaseService;

    public UserService(DatabaseManager databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    public void changeName(User user, String newName) {
        databaseService.updateUsername(user.getEmail(), newName);
    }
}
