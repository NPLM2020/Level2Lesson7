package com.chat.client;

public interface Logger {
    void addMessage(String message);

    String getLastMessages(int limit);
}
