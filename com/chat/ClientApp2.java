package com.chat;

import com.chat.client.Client;
import com.chat.logging.TextLoggerService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientApp2 {
    public static void main(String[] args) {
        new Client("localhost", 8888, new TextLoggerService("E:\\Temp\\c2\\log.txt")).run();
    }
}
