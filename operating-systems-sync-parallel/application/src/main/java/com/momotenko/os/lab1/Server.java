package com.momotenko.os.lab1;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Server {
    private Selector selector;
    private ServerSocketChannel serverSocketChannelF;
    private ServerSocketChannel serverSocketChannelG;
    SimpleEntry<InetSocketAddress, Boolean> fWrote;
    SimpleEntry<InetSocketAddress, Boolean> gWrote;

    int x;

    private ByteBuffer buffer;
    private SelectionKey key;

    int count;

    private List<SimpleEntry<Double, Long>> result;

    Process compileF;
    Process compileG;

    public Server(String hostname, int portF, int portG, int x) {
        result = new ArrayList<>();
        result.add(null);
        result.add(null);
        this.x = x;

        try {
            selector = Selector.open();
            serverSocketChannelF = ServerSocketChannel.open();
            fWrote = new SimpleEntry<>(new InetSocketAddress(hostname, portF), false);
            serverSocketChannelF.bind(fWrote.getKey());
            serverSocketChannelF.configureBlocking(false);
            serverSocketChannelF.register(selector, SelectionKey.OP_ACCEPT);

            serverSocketChannelG = ServerSocketChannel.open();
            gWrote = new SimpleEntry<>(new InetSocketAddress(hostname, portG), false);
            serverSocketChannelG.bind(gWrote.getKey());
            serverSocketChannelG.configureBlocking(false);
            serverSocketChannelG.register(selector, SelectionKey.OP_ACCEPT);

            buffer = ByteBuffer.allocate(Integer.BYTES + Double.BYTES + Long.BYTES);

            count = 0;

            startClients();
            register();
            sendX();
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startClients() {
        String outFFolder =
                ".." + File.separator +
                        ".." + File.separator +
                        "f_function" + File.separator +
                        "target" + File.separator;

        String outGFolder =
                ".." + File.separator +
                        ".." + File.separator +
                        "g_function" + File.separator +
                        "target" + File.separator;

        String functionFCP =
                "com.momotenko.os.lab1.FunctionF";

        String functionGCP =
                "com.momotenko.os.lab1.FunctionG";

        try {
            compileF = new ProcessBuilder("java", "-cp",
                    outFFolder + "full-f_function-1.0.jar",
                    functionFCP).start();

            compileG = new ProcessBuilder("java", "-cp",
                    outGFolder + "full-g_function-1.0.jar",
                    functionGCP).start();
        } catch (IOException e) {
            System.out.println("Failed to start client");
        }
    }

    public void stopClients() {
        compileF.destroy();
        compileG.destroy();
    }

    public void run() {
        try {
            while (count != 2) {
                selector.select();

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();

                while (iterator.hasNext()) {
                    key = iterator.next();
                    if (key.isReadable()) {
                        SocketChannel keyChannel = (SocketChannel) key.channel();

                        boolean f = true;

                        if (gWrote.getKey()
                                .equals(keyChannel.getLocalAddress())) {
                            f = false;
                        }

                        if (!readX(keyChannel, f)) {
                            stopClients();
                            return;
                        }
                    }

                    iterator.remove();
                }
            }
        } catch (IOException e) {
            System.out.println("Client disconnected");
            e.printStackTrace();
        }

        stopClients();
    }

    private void sendX() throws IOException {
        int count = 0;
        while (count != 2) {
            selector.select();

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();

            while (iterator.hasNext()) {
                key = iterator.next();

                if (key.isWritable()) {

                    sendX((SocketChannel)key.channel());
                    ++count;
                }

                iterator.remove();
            }
        }
    }

    private void sendX(SocketChannel client) throws IOException {
        buffer.clear();
        buffer.putInt(x);
        buffer.rewind();
        client.write(buffer);
    }

    private boolean readX(SocketChannel client, boolean f) throws IOException {
        buffer.clear();
        client.read(buffer);
        buffer.rewind();

        Integer assertRes = buffer.getInt();
        Double res = buffer.getDouble();
        Long time = buffer.getLong();

        if (assertRes.equals(((InetSocketAddress) client.getLocalAddress()).getPort())) {
            ++count;

            if (f) {
                result.set(0, new SimpleEntry(res, time));
            } else {
                result.set(1, new SimpleEntry(res, time));
            }

            if (res == 0.0) {
                return false;
            }
        }

        return true;
    }

    public List<SimpleEntry<Double, Long>> getResult() {
        return result;
    }

    protected void register() throws IOException {
        boolean fRegistered = false;
        boolean gRegistered = false;

        while (!(fRegistered && gRegistered)) {
            selector.select();

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();

            while (iterator.hasNext()) {
                key = iterator.next();

                if (key.isAcceptable()) {
                    if (key.channel().equals(serverSocketChannelF) && !fRegistered) {
                        register(serverSocketChannelF);
                        fRegistered = true;
                    } else if (key.channel().equals(serverSocketChannelG) && !gRegistered) {
                        register(serverSocketChannelG);
                        gRegistered = true;
                    }

                    iterator.remove();
                    continue;
                }

                iterator.remove();
            }
        }
    }

    private void register(ServerSocketChannel serverSocketChannel) throws IOException {
        SocketChannel client = serverSocketChannel.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
    }
}
