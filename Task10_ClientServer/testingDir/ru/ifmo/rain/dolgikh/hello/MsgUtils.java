package ru.ifmo.rain.dolgikh.hello;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;

public class MsgUtils {


    public static DatagramPacket makeMsgToSend(final SocketAddress dst, final int buffSize) {
        final byte[] buff = new byte[buffSize];
        return new DatagramPacket(buff, buffSize, dst);
    }

    public static DatagramPacket makeMsgToReceive(final int buffSize) {
        final byte[] buff = new byte[buffSize];
        return new DatagramPacket(buff, buff.length);
    }
}
