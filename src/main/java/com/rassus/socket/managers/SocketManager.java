package com.rassus.socket.managers;

import com.google.gson.Gson;
import com.rassus.models.Message;
import com.rassus.socket.SimpleSimulatedDatagramSocket;
import com.rassus.utils.EmulatedSystemClock;
import lombok.Getter;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Map;

import static com.rassus.constants.SocketManagerConstants.*;

public class SocketManager {
    @Getter
    private final DatagramSocket socket;

    @Getter
    private final int[] vectorClocks = {0, 0, 0, 0};

    @Getter
    private final EmulatedSystemClock clock = new EmulatedSystemClock();

    @Getter
    private final Map<String, Message> confirmedMessages = new HashMap<>();

    @Getter
    private final Map<String, Message> sentMessages = new HashMap<>();

    public SocketManager() throws IOException {
        this.socket = new SimpleSimulatedDatagramSocket(PORT, LOSS_RATE, AVERAGE_DELAY);
    }

    public void setToSent(Message message) {
        sentMessages.put(message.getId(), message);
    }

    public void setToConfirmed(Message message) {
        confirmedMessages.put(message.getId(), message);
    }

    public void setToConfirmed(String id) {
        confirmedMessages.put(id, sentMessages.remove(id));
    }

    public boolean isNewMessage(Message message) {
        return !confirmedMessages.containsKey(message.getId());
    }

    public int getLogicalClock() {
        return vectorClocks[index()];
    }

    public void setLogicalClock(int value) {
        vectorClocks[index()] = value;
    }

    public void incrementLogicalClock() {
        setLogicalClock(getLogicalClock() + 1);
    }

    public long getPhysicalClock() {
        return clock.currentTimeMillis();
    }

    public void setVectorClocks(int[] vectorClocks) {
        for (int i = 0; i < vectorClocks.length; i++) {
            if (i != index()) {
                vectorClocks[i] = Math.max(this.vectorClocks[i], vectorClocks[i]);
            }
        }
        incrementLogicalClock();
    }

    public int index() {
        for (int i = 0; i < PORTS.length; i++) {
            if (PORTS[i] == PORT) {
                return i;
            }
        }
        throw new RuntimeException("Check your PORTS array. Didn't find " + PORT);
    }

}
