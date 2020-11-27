package com.chat.server;

import com.chat.entity.User;

public interface DatabaseManager {
    User getUserByEmail(String email);

    void updateUsername(String email, String newName);
}
