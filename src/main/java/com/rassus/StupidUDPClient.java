package com.rassus;

import com.rassus.socket.SimpleSimulatedDatagramSocket;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.net.*;
import java.util.stream.Collectors;

public class StupidUDPClient {

    static final int PORT = 4000;
    private final DatagramSocket socket;

    public StupidUDPClient() throws IOException {
        socket = new SimpleSimulatedDatagramSocket(PORT, 0.0, 0);
    }

    public static void main(String args[]) {
        try {
            String message = "message";
            System.out.print("Client sends: " + message);
            StupidUDPClient udpClient = new StupidUDPClient();
            udpClient.startServer(); // drugi thread

            for(int i = 0; i < 5000; i++) {
                DatagramPacket packet = udpClient.toDatagramPacket(message);
                udpClient.send(packet);
            }
        } catch(IOException e) {
            System.out.println("Catched exception: " + e.getMessage());
        }
    }

    public void send(DatagramPacket packet) throws IOException{
        this.socket.send(packet);
    }

    private DatagramPacket toDatagramPacket(String s) throws UnknownHostException {
        Byte[] bytes = s.chars()
                .mapToObj(c -> (byte) c)
                .collect(Collectors.toList())
                .toArray(new Byte[s.length()]);

        byte[] primitives = ArrayUtils.toPrimitive(bytes);

        return new DatagramPacket(primitives, bytes.length,
                InetAddress.getByName("localhost"), PORT);
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
                System.out.println("Received " + received);
            }
            socket.close();
        });

        thread.start();
    }
}
