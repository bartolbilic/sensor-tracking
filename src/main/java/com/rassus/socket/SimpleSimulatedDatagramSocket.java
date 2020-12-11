package com.rassus.socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleSimulatedDatagramSocket extends DatagramSocket {
    private final double lossRate;
    private final int averageDelay;
    private final Random random;

    public SimpleSimulatedDatagramSocket(int port, double lossRate, int averageDelay) throws SocketException, IllegalArgumentException {
        super(port);
        random = new Random();

        this.lossRate = lossRate;
        this.averageDelay = averageDelay;

        super.setSoTimeout(0);
    }

    @Override
    public void send(DatagramPacket packet) {
        if (random.nextDouble() >= lossRate) {
            new Thread(new OutgoingDatagramPacket(packet, (long) (2 * averageDelay * random.nextDouble()))).start();
        }
    }

    private class OutgoingDatagramPacket implements Runnable {
        private final DatagramPacket packet;
        private final long time;

        private OutgoingDatagramPacket(DatagramPacket packet, long time) {
            this.packet = packet;
            this.time = time;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(time);
                SimpleSimulatedDatagramSocket.super.send(packet);
            } catch (InterruptedException e) {
                Thread.interrupted();
            } catch (IOException ex) {
                Logger.getLogger(SimpleSimulatedDatagramSocket.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
