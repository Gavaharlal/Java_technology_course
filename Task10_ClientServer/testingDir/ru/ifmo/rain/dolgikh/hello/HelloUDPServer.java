package ru.ifmo.rain.dolgikh.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HelloUDPServer implements HelloServer {
    private DatagramSocket socket;
    private ExecutorService workers;
    private ExecutorService listener;

    @Override
    public void start(int port, int threads) {
        int receivingBufferSize;
        try {
            socket = new DatagramSocket(port);
            receivingBufferSize = socket.getReceiveBufferSize();
        } catch (SocketException e) {
            System.err.println("Error creating socking, port:" + port);
            return;
        }
        listener = Executors.newSingleThreadExecutor();
        workers = Executors.newFixedThreadPool(threads);
        listener.submit(() -> {
            while (!socket.isClosed()) {
                DatagramPacket datagramPacket = PackingUtils.getPacketToReceive(receivingBufferSize);
                try {
                    socket.receive(datagramPacket);
                } catch (IOException ignored) {
                }
                workers.submit(() -> sendResponse(datagramPacket));
            }
        });
    }

    private void sendResponse(DatagramPacket msg) {
        String receivedMessage = new String(msg.getData(), msg.getOffset(), msg.getLength(), StandardCharsets.UTF_8);
        String responseMessage = "Hello, " + receivedMessage;
        DatagramPacket responsePacket = PackingUtils.getPacketToSend(msg.getSocketAddress(), responseMessage);
        try {
            socket.send(responsePacket);
        } catch (IOException ignored) {
        }

    }

    @Override
    public void close() {
        socket.close();
        listener.shutdownNow();
        workers.shutdownNow();
        try {
            workers.awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
        }
    }

    public static void main(String[] args) {
        if (args == null || args.length != 2 || args[0] == null || args[1] == null) {
            System.err.println("Incorrect arguments");
            return;
        }
        try {
            int port = Integer.parseInt(args[0]);
            int num = Integer.parseInt(args[1]);
            new HelloUDPServer().start(port, num);
        } catch (NumberFormatException e) {
            System.err.println("Integer arguments expected");
        }
    }
}
