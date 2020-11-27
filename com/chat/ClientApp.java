package com.chat;

import com.chat.client.Client;
import com.chat.logging.TextLoggerService;

public class ClientApp {
    public static void main(String[] args) {
        new Client("localhost", 8888, new TextLoggerService("E:\\Temp\\c1\\log.txt")).run();
    }
}
