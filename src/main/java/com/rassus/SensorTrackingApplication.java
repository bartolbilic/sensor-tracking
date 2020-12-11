package com.rassus;

import com.rassus.socket.managers.ClientSocketManager;
import com.rassus.socket.managers.ClientSocketManagerImpl;
import com.rassus.socket.managers.ServerSocketManager;
import com.rassus.socket.managers.SocketManager;
import com.rassus.utils.Stopwatch;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class SensorTrackingApplication {
    public static void main(String args[]) {
        Stopwatch.setStartTime(0);
        try {
            SocketManager socketManager = new SocketManager();

            ClientSocketManager clientSocketManager =
                    new ClientSocketManagerImpl(socketManager);

            ServerSocketManager serverSocketManager = new
                    ServerSocketManager(socketManager, clientSocketManager);

            serverSocketManager.start();
            clientSocketManager.start();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
