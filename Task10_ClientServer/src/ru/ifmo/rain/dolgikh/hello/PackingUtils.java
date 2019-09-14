package ru.ifmo.rain.dolgikh.hello;

import java.net.DatagramPacket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;

class PackingUtils {

    private PackingUtils() {
    }

    static DatagramPacket getPacketToSend(SocketAddress address, String message) {
        final byte[] buff = new byte[1024];
        DatagramPacket datagramPacket = new DatagramPacket(buff, buff.length, address);
        datagramPacket.setData(message.getBytes(StandardCharsets.UTF_8));
        return datagramPacket;
    }

    static DatagramPacket getPacketToReceive(int buffSize) {
        final byte[] buff = new byte[buffSize];
        return new DatagramPacket(buff, buff.length);
    }
}
