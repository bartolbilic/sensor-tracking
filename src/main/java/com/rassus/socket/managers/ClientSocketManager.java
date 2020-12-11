package com.rassus.socket.managers;

import com.rassus.models.Message;

import java.io.IOException;

public interface ClientSocketManager {
    void send(Message message) throws IOException;
    void start();
}
