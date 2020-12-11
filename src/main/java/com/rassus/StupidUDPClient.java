package com.rassus;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.rassus.models.Message;
import com.rassus.socket.SimpleSimulatedDatagramSocket;
import com.rassus.utils.DatagramPacketConverter;
import com.rassus.utils.EmulatedSystemClock;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.rassus.constants.SocketManagerConstants.*;

@Slf4j
public class StupidUDPClient {

    private int[] vectorTime = {0, 0, 0, 0};

    @Getter
    private final Map<String, Message> confirmedMessages = new HashMap<>();

    @Getter
    private final Map<String, Message> sentMessages = new HashMap<>();
    private final DatagramSocket socket;
    private final EmulatedSystemClock clock;
    private final Gson gson = new Gson();

    public StupidUDPClient() throws IOException {
        socket = new SimpleSimulatedDatagramSocket(PORT, LOSS_RATE, AVERAGE_DELAY);
        this.clock = new EmulatedSystemClock();
    }

    public void saveMessage(Message message) {
        sentMessages.put(message.getId(), message);
    }

    public void confirm(String id) {
        confirmedMessages.put(id, sentMessages.remove(id));
    }

    public Message toMessage(int measurement, int port) {
        return Message.builder()
                .id(UUID.randomUUID().toString())
                .host("localhost")
                .port(port)
                .measurement(measurement)
                .scalarTime(scalarTime())
                .vectorTime(vectorTime)
                .build();
    }

    public void sendMessage(Message message) throws IOException {
        if (sentMessages.containsKey(message.getId())) {
            log.info("[" + PORT + "] Repeating " + message.getId() + " to " + message.getPort());
        } else {
            log.info("[" + PORT + "] Sending " + message.getId() + " to " + message.getPort());
        }
        vectorTime[index()]++;
        this.socket
                .send(DatagramPacketConverter.toDatagramPacket(gson.toJson(message),
                        message.getPort()));
    }

    public void sendConfirmation(String id, int port) throws IOException {
        log.info("Confirming " + id + " to " + port);
        vectorTime[index()]++;
        this.socket.send(DatagramPacketConverter.toDatagramPacket(id, port));
    }

    public boolean isNewMessage(Message message) {
        return !confirmedMessages.containsKey(message.getId());
    }

    private long scalarTime() {
        return clock.currentTimeMillis();
    }

    public int index() {
        for (int i = 0; i < PORTS.length; i++) {
            if (PORTS[i] == PORT) {
                return i;
            }
        }
        throw new RuntimeException("Check your PORTS array. Didn't find " + PORT);
    }

    private void updateVectorTime(int[] vectorTime) {
        int myTime = this.vectorTime[index()];
        myTime += 1;

        this.vectorTime = vectorTime;
        this.vectorTime[index()] = myTime;
    }

    public void startServer() {
        Thread thread = new Thread(() -> {
            byte[] rcvBuf = new byte[256];

            while (true) {
                DatagramPacket rcvPacket = new DatagramPacket(rcvBuf, rcvBuf.length);
                try {
                    socket.receive(rcvPacket);

                    try {
                        Message message = gson.fromJson(
                                DatagramPacketConverter.fromDatagramPacket(rcvPacket),
                                Message.class);

                        if (isNewMessage(message)) {
                            log.info("[ " + PORT + " ] Received NEW MESSAGE " + message.getId());
                            confirmedMessages.put(message.getId(), message);
                        } else {
                            log.info("[ " + PORT + " ] Received OLD MESSAGE " + message.getId());
                        }

                        updateVectorTime(message.getVectorTime());
                        sendConfirmation(message.getId(), rcvPacket.getPort());
                    } catch (JsonSyntaxException e) {
                        String id = DatagramPacketConverter.fromDatagramPacket(rcvPacket);
                        log.info("[ " + PORT + " ] Received CONFIRMATION " + id);
                        confirm(id);
                    }
                } catch (IOException e) {
                    log.error(e.getMessage());
                    break;
                }
            }
            socket.close();
        });

        thread.start();
    }
}
