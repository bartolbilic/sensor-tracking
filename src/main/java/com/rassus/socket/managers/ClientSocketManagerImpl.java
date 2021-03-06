package com.rassus.socket.managers;

import com.google.gson.Gson;
import com.rassus.models.Message;
import com.rassus.models.Type;
import com.rassus.utils.DatagramPacketConverter;
import com.rassus.utils.MeasurementReader;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicIntegerArray;

import static com.rassus.constants.SocketManagerConstants.*;

@Slf4j
public class ClientSocketManagerImpl implements ClientSocketManager {
    private final SocketManager socketManager;
    private final Gson gson = new Gson();
    private final MeasurementReader measurementReader;

    public ClientSocketManagerImpl(SocketManager socketManager) {
        this.socketManager = socketManager;
        this.measurementReader = new MeasurementReader();
    }

    @Override
    public void send(Message message) throws IOException {
        //log.info("[" + PORT + "] SEND " + message.getType() + "-" + message.getId() + " TO " + message.getPort());
        //log.info("[" + PORT + "] SEND: " + socketManager.getSentMessages().size() +
        //        ", CONFIRMED: " + socketManager.getConfirmedMessages().size() +
        //        ", RATIO: " + ((double) socketManager.getConfirmedMessages().size() / (socketManager.getSentMessages().size() + 1)));
        socketManager.getSocket()
                .send(DatagramPacketConverter.toDatagramPacket(gson.toJson(message),
                        message.getPort()));
    }

    private void send(float data, int port) throws IOException {
        socketManager.incrementLogicalClock();
        Message message = toMessage(data, port);
        send(message);
        socketManager.setToSent(message);
    }

    private void sendMessagesToPeers() throws IOException {
        for (int i = 0; i < 4; i++) {
            if (i != socketManager.index()) {
                send(measurementReader.getMeasurement("CO").getValue(), PORTS[i]);
            }
        }
    }

    private void resendMessagesToPeers() throws IOException {
        for (Message message : socketManager.getSentMessages().values()) {
            socketManager.incrementLogicalClock();
            send(message);
        }
    }

    private synchronized void run() {
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

    private Message toMessage(float measurement, int port) {
        return Message.builder()
                .id(UUID.randomUUID().toString())
                .host(HOST)
                .port(port)
                .type(Type.REQUEST)
                .measurement(measurement)
                .scalarTime(socketManager.getPhysicalClock())
                .vectorTime(copy(socketManager.getVectorClocks()))
                .build();
    }

    private int[] copy(AtomicIntegerArray array) {
        int[] copy = new int[array.length()];
        for (int i = 0; i < array.length(); i++) {
            copy[i] = array.get(i);
        }
        return copy;
    }

    @Override
    public void start() {
        new Thread(this::run).start();
    }
}
