package com.rassus.socket.managers;

import com.google.gson.Gson;
import com.rassus.models.Message;
import com.rassus.utils.DatagramPacketConverter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.UUID;

import static com.rassus.constants.SocketManagerConstants.INTERVAL;
import static com.rassus.constants.SocketManagerConstants.PORTS;

@Slf4j
public class ClientSocketManagerImpl implements ClientSocketManager {
    private final SocketManager socketManager;
    private final Gson gson = new Gson();

    public ClientSocketManagerImpl(SocketManager socketManager) {
        this.socketManager = socketManager;
    }

    @Override
    public void send(Message message) throws IOException {
        socketManager.getSocket()
                .send(DatagramPacketConverter.toDatagramPacket(gson.toJson(message),
                        message.getPort()));
    }

    private void send(int data, int port) throws IOException {
        Message message = toMessage(data, port);
        send(message);
        socketManager.setToSent(message);
        socketManager.incrementLogicalClock();
    }

    private void sendMessagesToPeers() throws IOException {
        for (int i = 0; i < 4; i++) {
            if (i != socketManager.index()) {
                send(544, PORTS[i]);
            }
        }
    }

    private void resendMessagesToPeers() throws IOException {
        for (Message message : socketManager.getSentMessages().values()) {
            send(message);
        }
    }

    private void run() {
        while (true) {
            try {
                sendMessagesToPeers();
                Thread.sleep(INTERVAL);
                resendMessagesToPeers();
            } catch (IOException | InterruptedException e) {
                log.error(e.getMessage());
            }
        }
    }

    private Message toMessage(int measurement, int port) {
        return Message.builder()
                .id(UUID.randomUUID().toString())
                .host("localhost")
                .port(port)
                .measurement(measurement)
                .scalarTime(socketManager.getPhysicalClock())
                .vectorTime(socketManager.getVectorClocks())
                .build();
    }

    @Override
    public void start() {
        new Thread(this::run).start();
    }
}
