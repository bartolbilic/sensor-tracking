package com.rassus.socket.managers;

import com.google.gson.Gson;
import com.rassus.models.Message;
import com.rassus.models.Type;
import com.rassus.utils.DatagramPacketConverter;
import com.rassus.utils.VectorTimeComparator;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.rassus.constants.SocketManagerConstants.PORT;

@Slf4j
public class ServerSocketManager {
    private final SocketManager socketManager;
    private final ClientSocketManager clientSocketManager;
    private final byte[] receiveBuffer = new byte[256];
    private final Gson gson = new Gson();

    public ServerSocketManager(SocketManager socketManager, ClientSocketManager clientSocketManager) {
        this.socketManager = socketManager;
        this.clientSocketManager = clientSocketManager;
    }

    private Message receiveMessage() throws IOException {
        DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        socketManager.getSocket().receive(packet);

        Message message = gson.fromJson(
                DatagramPacketConverter.fromDatagramPacket(packet), Message.class);

        message.setPort(packet.getPort());
        return message;
    }

    private void processRequest(Message message) throws IOException {
        if (socketManager.isNewMessage(message)) {
            //log.info("[" + PORT + "] RECV NEW MESSAGE-" + message.getId() +
            //       " Measurement: " + message.getMeasurement());
            socketManager.setToConfirmed(message);
        } else {
            //log.info("[" + PORT + "] RECV OLD MESSAGE-" + message.getId());
        }

        clientSocketManager.send(Message.builder()
                .id(message.getId())
                .type(Type.CONFIRMATION)
                .port(message.getPort())
                .vectorTime(message.getVectorTime())
                .scalarTime(message.getScalarTime())
                .build());
    }

    private void processConfirmation(Message message) {
        //log.info("[" + PORT + "] RECV CONFIRMATION-" + message.getId());
        socketManager.setToConfirmed(message.getId());
    }

    private void processMessage(Message message) throws IOException {
        socketManager.setVectorClocks(message.getVectorTime());
        if (message.getType() == Type.REQUEST) {
            processRequest(message);
        }

        if (message.getType() == Type.CONFIRMATION) {
            processConfirmation(message);
        }
    }

    private void run() {
        while (true) {
            try {
                processMessage(receiveMessage());
            } catch (IOException e) {
                log.error(e.getMessage());
                break;
            }
        }
        socketManager.getSocket().close();
    }

    private void sortByPhysicalClock() {
        List<Message> allMessages = new ArrayList<>(socketManager.getConfirmedMessages().values());
        allMessages.addAll(socketManager.getSentMessages().values());

        List<Message> sorted = allMessages.stream()
                .filter(t -> t != null)
                .filter(t -> t.getVectorTime() != null)
                .sorted(Comparator.comparingLong(Message::getScalarTime))
                .collect(Collectors.toList());

        log.info("[" + PORT + "] 5 secs passed, physical clock sorting\n");
    }

    private void sortByLogicalClock() {
        List<Message> allMessages = new ArrayList<>();
        allMessages.addAll(socketManager.getConfirmedMessages().values());
        allMessages.addAll(socketManager.getSentMessages().values());

        VectorTimeComparator comparator = new VectorTimeComparator();
        List<Message> sorted = allMessages.stream()
                .filter(t -> t != null)
                .filter(t -> t.getVectorTime() != null)
                .sorted((t1, t2) -> comparator.compare(t1.getVectorTime(), t2.getVectorTime()))
                .collect(Collectors.toList());

        log.info("[" + PORT + "] 5 secs passed, logical clock sorting\n");
    }

    private void sort() {
        while (true) {
            try {
                Thread.sleep(5000);
                sortByPhysicalClock();
                sortByLogicalClock();
                socketManager.clearConfirmedMessages();
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        }
    }

    public void start() {
        new Thread(this::sort).start();
        new Thread(this::run).start();
    }
}
