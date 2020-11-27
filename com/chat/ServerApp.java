package com.chat;

import com.chat.database.PostgresService;
import com.chat.server.ChatServer;

public class ServerApp {
    public static void main(String[] args) {
        new ChatServer(new PostgresService());
    }
}
