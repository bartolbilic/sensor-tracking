package com.rassus.utils;

import org.apache.commons.lang3.ArrayUtils;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.stream.Collectors;

public class DatagramPacketConverter {

    public static DatagramPacket toDatagramPacket(String s, int port) throws UnknownHostException {
        Byte[] bytes = s.chars()
                .mapToObj(c -> (byte) c)
                .collect(Collectors.toList())
                .toArray(new Byte[s.length()]);

        byte[] primitives = ArrayUtils.toPrimitive(bytes);

        return new DatagramPacket(primitives, bytes.length,
                InetAddress.getByName("localhost"), port);
    }

    public static String fromDatagramPacket(DatagramPacket packet) {
        return new String(packet.getData(), packet.getOffset(), packet.getLength());
    }
}
