package com.rassus;

import com.google.gson.Gson;
import com.rassus.models.Message;
import com.rassus.socket.SimpleSimulatedDatagramSocket;
import com.rassus.utils.DatagramPacketConverter;
import com.rassus.utils.EmulatedSystemClock;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

@Slf4j
public class StupidUDPClient {
    private static final int PORT = 4000;
    private static final double LOSS_RATE = 0.3;
    private static final int AVERAGE_DELAY = 1000;
    private static final int[] PORTS = {4000, 5000, 6000, 7000};
    private int[] vectorTime = {0, 0, 0, 0};

    private final DatagramSocket socket;
    private final EmulatedSystemClock clock;
    private final Gson gson = new Gson();

    public StupidUDPClient() throws IOException {
        socket = new SimpleSimulatedDatagramSocket(PORT, LOSS_RATE, AVERAGE_DELAY);
        this.clock = new EmulatedSystemClock();
    }

    public static void main(String args[]) {
        try {
            StupidUDPClient udpClient = new StupidUDPClient();
            udpClient.startServer();

            for (int i = 0; i < 5000; i++) {
                udpClient.send(564);
                Thread.sleep(1000L);
            }
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage());
        }
    }

    public void send(int measurement) throws IOException {
        vectorTime[index()]++;
        Message message = new Message(measurement, scalarTime(), vectorTime);
        this.socket.send(DatagramPacketConverter.toDatagramPacket(gson.toJson(message), PORT));
    }

    private long scalarTime() {
        return clock.currentTimeMillis();
    }

    private int index() {
        for (int i = 0; i < PORTS.length; i++) {
            if (PORTS[i] == PORT) {
                return i;
            }
        }
        throw new RuntimeException("Check your PORTS array. Didn't find " + PORT);
    }

    private void startServer() {
        Thread thread = new Thread(() -> {
            byte[] rcvBuf = new byte[256];

            while (true) {
                // create a datagram packet for receiving data
                DatagramPacket rcvPacket = new DatagramPacket(rcvBuf, rcvBuf.length);
                try {
                    socket.receive(rcvPacket);
                } catch (IOException e) {
                    log.error(e.getMessage());
                    break;
                }

                Message message = gson.fromJson(DatagramPacketConverter.fromDatagramPacket(rcvPacket),
                        Message.class);
                int time = ++this.vectorTime[index()];
                this.vectorTime = message.getVectorTime();
                this.vectorTime[index()] = time;
                log.info("Measurement: " + message.getMeasurement());
                log.info("Vector time: " + message.getVectorTime());
                log.info("Scalar time: " + message.getMeasurement());
            }
            socket.close();
        });

        thread.start();
    }
}
