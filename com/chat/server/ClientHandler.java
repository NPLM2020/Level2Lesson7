package com.chat.server;

import com.chat.entity.User;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientHandler {

    private static final String exitMessage = "-exit";
    private static final String privateMessage = "/w";
    private static final String authMessage = "-auth";
    private static final String changeNameMessage = "/cn";


    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private User user;

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            doListen();
        } catch (IOException e) {
            throw new RuntimeException("SWW", e);
        }
    }

    public String getName() {
        return user.getNickname();
    }

    private void doListen() {
        new Thread(() -> {
            try {
                doAuth();
                receiveMessage();
            } catch (Exception e) {
                throw new RuntimeException("SWW", e);
            } finally {
                server.unsubscribe(this);
            }
        }).start();
    }

    private void doAuth() {
        AtomicBoolean isAuthSucceed = new AtomicBoolean(false);

        try {

            while (!isAuthSucceed.get()) {

                String credentials = in.readUTF();
                /**
                 * Input credentials sample
                 * "-auth n1@mail.com 1"
                 */
                if (credentials.startsWith(authMessage)) {
                    /**
                     * After splitting sample
                     * array of ["-auth", "n1@mail.com", "1"]
                     */
                    String[] credentialValues = credentials.split("\\s");
                    server.getAuthenticationService()
                            .doAuth(credentialValues[1], credentialValues[2])
                            .ifPresentOrElse(
                                    user -> {
                                        if (!server.isLoggedIn(user.getNickname())) {
                                            sendMessage("cmd auth: Status OK");
                                            this.user = user;
                                            server.broadcastMessage(user.getNickname() + " is logged in.", user.getNickname());
                                            server.subscribe(this);
                                            isAuthSucceed.set(true);
                                        } else {
                                            sendMessage("Current user is already logged in.");
                                        }
                                    },
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            sendMessage("No a such user by email and password.");
                                        }
                                    }
                            );
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("SWW", e);
        }
    }

    /**
     * Receives input data from {@link ClientHandler#in} and then broadcast via {@link Server#broadcastMessage(String, String)}
     */
    private void receiveMessage() {
        try {
            while (true) {
                String message = in.readUTF();
                if (message.equals(exitMessage)) {
                    return;
                }
                if (message.startsWith(privateMessage)) {
                    server.sendPrivateMessage(message, user.getNickname());
                } else {
                    server.broadcastMessage(message, user.getNickname());
                }
                if (message.startsWith(changeNameMessage)) {
                    changeName(message);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("SWW", e);
        }
    }

    private void changeName(String message) {
        String[] messageValues = message.split("\\s");
        if (messageValues.length > 1) {
            server.getUserService().changeName(user, messageValues[1]);
            user.setNickname(messageValues[1]);
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            throw new RuntimeException("SWW", e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientHandler that = (ClientHandler) o;
        return Objects.equals(server, that.server) &&
                Objects.equals(socket, that.socket) &&
                Objects.equals(in, that.in) &&
                Objects.equals(out, that.out) &&
                Objects.equals(user.getNickname(), user.getNickname());
    }

    @Override
    public int hashCode() {
        return Objects.hash(server, socket, in, out, user.getNickname());
    }
}
