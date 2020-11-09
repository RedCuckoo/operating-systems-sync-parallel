package com.momotenko.os.lab1;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public abstract class Client {
    private SocketChannel clientSocketChannel;
    private int port;

    public Client(String hostname, int port) {
        this.port = port;

        try {
            clientSocketChannel = SocketChannel.open(new InetSocketAddress(hostname, port));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void waitServer() throws IOException, InterruptedException {
        ByteBuffer inputBuffer = ByteBuffer.allocate(Integer.BYTES + Double.BYTES + Long.BYTES);

        while (clientSocketChannel.read(inputBuffer) == 0) {};

        inputBuffer.rewind();
        int x = inputBuffer.getInt();

        inputBuffer.clear();

        final long start = System.currentTimeMillis();
        double result = getResult(x);
        final long end = System.currentTimeMillis();

        inputBuffer.putInt(port);
        inputBuffer.putDouble(result);
        inputBuffer.putLong(end - start);
        inputBuffer.rewind();

        clientSocketChannel.write(inputBuffer);
    }

    protected abstract Double getResult(int x) throws InterruptedException;
}
