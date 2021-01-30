package com.chat.logging;

import com.chat.client.Logger;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;


public class TextLoggerService implements Logger {
    private String path;

    public TextLoggerService(String path) {
        this.path = path;
    }

    @Override
    public void addMessage(String message) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path), true))) {
            bw.write(message);
            bw.newLine();
            bw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getLastMessages(int limit) {
        StringBuilder text = new StringBuilder();
        File file = new File(path);
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                List<String> collection = br.lines().collect(Collectors.toList());
                if (collection.size() >= limit) {
                    collection.stream().skip(collection.size() - limit).forEach(line -> text.append(line + "\n"));
                } else {
                    collection.forEach(line -> text.append(line + "\n"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return text.toString();
    }
}
