package com.chat.server;

import com.chat.auth.AuthenticationService;

public interface Server {
    void broadcastMessage(String message, String from);

    void sendPrivateMessage(String message, String from);

    boolean isLoggedIn(String nickname);

    void subscribe(ClientHandler client);

    void unsubscribe(ClientHandler client);

    AuthenticationService getAuthenticationService();
}
