package com.rassus;

import com.rassus.constants.SocketManagerConstants;
import com.rassus.models.Message;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class SensorTrackingApplication {
    public static void main(String args[]) {
        try {
            StupidUDPClient udpClient = new StupidUDPClient();
            udpClient.startServer();

            while (true) {
                for (int i = 0; i < 4; i++) {
                    if (i != udpClient.index()) {
                        Message message = udpClient.toMessage(544,
                                SocketManagerConstants.PORTS[i]);
                        udpClient.sendMessage(message);
                        udpClient.saveMessage(message);
                    }
                }
                Thread.sleep(5000);

                for (Message message : udpClient.getSentMessages().values()) {
                    udpClient.sendMessage(message);
                }
            }
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage());
        }
    }
}
