package com.momotenko.os.lab1;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Server {
    private Selector selector;
    private ServerSocketChannel serverSocketChannelF;
    private ServerSocketChannel serverSocketChannelG;
    private ByteBuffer buffer;
    private volatile SelectionKey key;
    private volatile boolean running = false;

    private List<Double> result;

    public Server(String hostname, int portF, int portG) {
        result = new ArrayList<>(Arrays.asList(new Double[2]));

        try {
            selector = Selector.open();
            serverSocketChannelF = ServerSocketChannel.open();
            serverSocketChannelF.bind(new InetSocketAddress(hostname, portF));
            serverSocketChannelF.configureBlocking(false);
            serverSocketChannelF.register(selector, SelectionKey.OP_ACCEPT);

            serverSocketChannelG = ServerSocketChannel.open();
            serverSocketChannelG.bind(new InetSocketAddress(hostname, portG));
            serverSocketChannelG.configureBlocking(false);
            serverSocketChannelG.register(selector, SelectionKey.OP_ACCEPT);

            buffer = ByteBuffer.allocate(1024);
            running = true;
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Future<?> future = executorService.submit(() -> {
            try {
                while (running) {
                    selector.select();

                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectedKeys.iterator();

                    while (iterator.hasNext()) {
                        key = iterator.next();

                        if (key.isAcceptable()) {
                            System.out.println(key.channel());

                            if (key.channel().equals(serverSocketChannelF)) {
                                register(selector, serverSocketChannelF);
                            } else if (key.channel().equals(serverSocketChannelG)) {
                                register(selector, serverSocketChannelG);
                            }
                        }

                        if (key.isWritable()) {
                            boolean f = true;

                            if (serverSocketChannelG.getLocalAddress()
                                    .equals(((SocketChannel) key.channel()).getLocalAddress())) {
                                f = false;
                            }

                            sendX(buffer, f);
                        }

                        iterator.remove();
                    }
                }
            } catch (IOException e) {
                System.out.println("Client disconnected");
            }
        });

        executorService.shutdown();
    }

    public void stop() {
        running = false;
    }

    public boolean sendX(ByteBuffer buffer, boolean f) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();

        buffer.clear();
        buffer.putInt(0);
        buffer.rewind();
        client.write(buffer);

        buffer.clear();

        client.read(buffer);
        buffer.rewind();
        Double res = buffer.getDouble();

        synchronized (result) {
            if (f) {
                result.add(0, res);
            } else {
                result.add(1, res);
            }
        }

        key.channel().close();

        if (res == 0.0) {
            return false;
        }

        return true;
    }

    public synchronized boolean resultsReady() {
        synchronized (result) {
            if (result.get(0) != null && result.get(0) == 0){
                return true;
            }
            else if (result.get(1) != null && result.get(1) == 0){
                return true;
            }

            return result.get(0) != null && result.get(1) != null;
        }
    }

    public synchronized List<Double> getResult() {
        synchronized (result){
            return result;
        }
    }

    public boolean getRunning(){
        return running;
    }

    protected void register(Selector selector, ServerSocketChannel serverSocketChannel) throws IOException {
        SocketChannel client = serverSocketChannel.accept();
        System.out.println("Client connected");
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_WRITE);
    }
}
