package com.chat.server;

import com.chat.auth.AuthenticationService;
import com.chat.auth.BasicAuthenticationService;
import com.chat.database.PostgresService;
import com.chat.users.UserService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ChatServer implements Server {
    private Set<ClientHandler> clients;
    private AuthenticationService authenticationService;
    private UserService userService;


    public ChatServer(DatabaseManager databaseService) {
        try {
            System.out.println("Server is starting up...");
            ServerSocket serverSocket = new ServerSocket(8888);
            clients = new HashSet<>();
            authenticationService = new BasicAuthenticationService(databaseService);
            userService = new UserService(databaseService);
            System.out.println("Server is started up...");

            while (true) {
                System.out.println("Server is listening for clients...");
                Socket socket = serverSocket.accept();
                System.out.println("Client accepted: " + socket);
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            throw new RuntimeException("SWW", e);
        }
    }

   // 2. * Реализовать личные сообщения, если клиент пишет «/w nick3 Привет»,
   // то только клиенту с ником nick3 должно прийти сообщение «Привет»
    @Override
    public synchronized void sendPrivateMessage(String message, String from) {
        String nickname = "[private]" + from;
        String[] privateMessageValues = message.split("\\s");
        if (privateMessageValues.length > 2) {
            if (isLoggedIn(privateMessageValues[1])) {
                String content = String.join("\s",
                        Arrays.copyOfRange(privateMessageValues, 2, privateMessageValues.length));
                clients.stream().
                        filter(clientHandler -> clientHandler.getName().equals(privateMessageValues[1])).
                        findFirst().
                        get().
                        sendMessage(buildMessage(content, nickname));
            }
        }
    }

    @Override
    public synchronized void broadcastMessage(String message, String from) {
        clients.forEach(client -> client.sendMessage(buildMessage(message, from)));
    }

    @Override
    public synchronized boolean isLoggedIn(String nickname) {
        return clients.stream()
                .filter(clientHandler -> clientHandler.getName().equals(nickname))
                .findFirst()
                .isPresent();
    }

    @Override
    public synchronized void subscribe(ClientHandler client) {
        clients.add(client);
    }

    @Override
    public synchronized void unsubscribe(ClientHandler client) {
        clients.remove(client);
    }

    @Override
    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    @Override
    public UserService getUserService() {
        return userService;
    }

    private String buildMessage(String message, String nickname) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")) +
                " " + nickname + ": " + message;
    }

}
