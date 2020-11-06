package com.momotenko.os.lab1;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public abstract class Client {
    private SocketChannel clientSocketChannel;
    private ByteBuffer buffer;

    public Client(String hostname, int port) {
        try {
            clientSocketChannel = SocketChannel.open(new InetSocketAddress(hostname, port));
            buffer = ByteBuffer.allocate(1024);
            System.out.println("Connected to the server");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void stop() {
        try {
            clientSocketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        buffer = null;
    }

    public void waitServer() throws IOException, InterruptedException {
        ByteBuffer inputBuffer = ByteBuffer.allocate(1024);

        clientSocketChannel.read(inputBuffer);
        inputBuffer.rewind();
        int x = inputBuffer.getInt();

        inputBuffer.clear();

        double result = getResult(x);
        inputBuffer.putDouble(result);
        inputBuffer.rewind();
        clientSocketChannel.write(inputBuffer);
    }

    protected abstract Double getResult(int x) throws InterruptedException;
}
