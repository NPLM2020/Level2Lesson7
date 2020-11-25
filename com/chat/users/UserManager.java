package com.chat.users;

import com.chat.entity.User;

public interface UserManager {
    void changeName(User user, String newName);
}
