package ru.ifmo.rain.dolgikh.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HelloUDPClient implements HelloClient {
    @Override
    public void run(String host, int port, String prefix, int threads, int requests) {
        SocketAddress destinationSocketAddress = new InetSocketAddress(host, port);
        ExecutorService workers = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            int finalI = i;
            workers.submit(() -> handleRequests(destinationSocketAddress, prefix, requests, finalI));
        }
        workers.shutdown();
        try {
            workers.awaitTermination(threads * requests, TimeUnit.MINUTES);
        } catch (InterruptedException ignored) {
        }
    }

    private static void handleRequests(SocketAddress socketAddress, String prefix, int requestsNum, int threadId) {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(20);
            for (int i = 0; i < requestsNum; ++i) {
                String requestMessage = prefix + threadId + "_" + i;
                DatagramPacket request = PackingUtils.getPacketToSend(socketAddress, requestMessage);
                while (!socket.isClosed()) {

                    try {
                        socket.send(request);
                        System.out.println("Request sent: " + requestMessage);
                    } catch (IOException e) {
                        System.err.println("Error sending packet: " + e.getMessage());
                    }

                    DatagramPacket response = PackingUtils.getPacketToReceive(socket.getReceiveBufferSize());
                    try {
                        socket.receive(response);
                    } catch (SocketTimeoutException ignored) {
                    } catch (IOException e) {
                        System.err.println("Error receiving packet: " + e.getMessage());
                    }

                    String responseMessage = new String(
                            response.getData(),
                            response.getOffset(),
                            response.getLength(),
                            StandardCharsets.UTF_8
                    );

                    if (responseMessage.equals("Hello, " + requestMessage)) {
                        System.out.println("Response received: " + responseMessage);
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            System.err.println("Error opening socket to server");
        }
    }


    public static void main(String[] args) {
        if (args == null || args.length != 5) {
            System.err.println("5 arguments expected");
            return;
        }
        for (String arg : args) {
            if (arg == null) {
                System.err.println("Non-null arguments expected");
                return;
            }
        }
        try {
            String address = args[0];
            int port = Integer.parseInt(args[1]);
            String prefix = args[2];
            int parallelNum = Integer.parseInt(args[3]);
            int perThread = Integer.parseInt(args[4]);
            new HelloUDPClient().run(address, port, prefix, parallelNum, perThread);
        } catch (NumberFormatException e) {
            System.err.println("Integer arguments expected");
        }
    }
}
