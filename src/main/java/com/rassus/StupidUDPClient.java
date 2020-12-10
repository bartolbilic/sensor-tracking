package com.rassus;

import com.google.gson.Gson;
import com.rassus.models.Message;
import com.rassus.socket.SimpleSimulatedDatagramSocket;
import com.rassus.utils.EmulatedSystemClock;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.stream.Collectors;

public class StupidUDPClient {
    private static final int PORT = 4000;
    private static final double LOSS_RATE = 0.3;
    private static final int AVERAGE_DELAY = 1000;
    private static final int[] PORTS = {4000, 5000, 6000, 7000};
    private int[] vectorTime = {0, 0, 0, 0};

    private final DatagramSocket socket;
    private final EmulatedSystemClock clock;
    private Gson gson = new Gson();


    public StupidUDPClient() throws IOException {
        socket = new SimpleSimulatedDatagramSocket(PORT, LOSS_RATE, AVERAGE_DELAY);
        this.clock = new EmulatedSystemClock();
    }

    public static void main(String args[]) {
        try {
            String message = "message";
            System.out.println("Client sends: " + message);
            StupidUDPClient udpClient = new StupidUDPClient();
            udpClient.startServer();

            for (int i = 0; i < 5000; i++) {
                udpClient.send(564);
                Thread.sleep(1000L);
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Catched exception: " + e.getMessage());
        }
    }

    public void send(int measurement) throws IOException {
        vectorTime[index()]++;
        Message message = new Message(measurement, scalarTime(), vectorTime);
        this.socket.send(toDatagramPacket(gson.toJson(message)));
    }

    private DatagramPacket toDatagramPacket(String s) throws UnknownHostException {
        Byte[] bytes = s.chars()
                .mapToObj(c -> (byte) c)
                .collect(Collectors.toList())
                .toArray(new Byte[s.length()]);

        byte[] primitives = ArrayUtils.toPrimitive(bytes);

        return new DatagramPacket(primitives, bytes.length,
                InetAddress.getByName("localhost"), 5000);
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
                    System.out.println("Exception: " + e.getMessage());
                    break;
                }

                String received = new String(rcvPacket.getData(), rcvPacket.getOffset(), rcvPacket.getLength());
                Message message = gson.fromJson(received, Message.class);
                int time = ++this.vectorTime[index()];
                this.vectorTime = message.getVectorTime();
                this.vectorTime[index()] = time;
                System.out.println("Received " + received);
            }
            socket.close();
        });

        thread.start();
    }
}
