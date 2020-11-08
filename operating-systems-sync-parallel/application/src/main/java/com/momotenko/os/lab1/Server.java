package com.momotenko.os.lab1;

import com.momotenko.os.lab1.utils.Pair;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

public class Server {
    private Selector selector;
    private ServerSocketChannel serverSocketChannelF;
    private ServerSocketChannel serverSocketChannelG;
    Pair<InetSocketAddress, Boolean> fWrote;
    Pair<InetSocketAddress, Boolean> gWrote;

    private ByteBuffer buffer;
    private SelectionKey key;

    int count;

    private List<Pair<Double, Long>> result;

    Process compileF;
    Process compileG;

    public Server(String hostname, int portF, int portG) {
        result = new ArrayList<>();
        result.add(null);
        result.add(null);

        try {
            selector = Selector.open();
            serverSocketChannelF = ServerSocketChannel.open();
            fWrote = new Pair<>(new InetSocketAddress(hostname, portF), false);
            serverSocketChannelF.bind(fWrote.getLeft());
            serverSocketChannelF.configureBlocking(false);
            serverSocketChannelF.register(selector, SelectionKey.OP_ACCEPT);

            serverSocketChannelG = ServerSocketChannel.open();
            gWrote = new Pair<>(new InetSocketAddress(hostname, portG), false);
            serverSocketChannelG.bind(gWrote.getLeft());
            serverSocketChannelG.configureBlocking(false);
            serverSocketChannelG.register(selector, SelectionKey.OP_ACCEPT);

            buffer = ByteBuffer.allocate(Integer.BYTES + Double.BYTES + Long.BYTES);

            count = 0;
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

    private void stopClients() {
        compileF.destroy();
        compileG.destroy();
    }

    public void run(int x) {
        startClients();

        try {
            while (count != 2) {
                selector.select();

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();

                while (iterator.hasNext()) {
                    key = iterator.next();

                    if (key.isAcceptable()) {
                        if (key.channel().equals(serverSocketChannelF)) {
                            register(selector, serverSocketChannelF);
                        } else if (key.channel().equals(serverSocketChannelG)) {
                            register(selector, serverSocketChannelG);
                        }
                    }

                    if (key.isWritable()) {
                        SocketChannel client = (SocketChannel) key.channel();

                        if (!fWrote.getRight() && fWrote.getLeft().equals(client.getLocalAddress())) {
                            sendX(client, x);
                            fWrote.setRight(true);
                        }

                        if (!gWrote.getRight() && gWrote.getLeft().equals(client.getLocalAddress())) {
                            sendX(client, x);
                            gWrote.setRight(true);
                        }

                        boolean f = true;

                        if (gWrote.getLeft()
                                .equals(((SocketChannel) key.channel()).getLocalAddress())) {
                            f = false;
                        }

                        if (!readX(client, x, f)) {
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

    public void sendX(SocketChannel client, int x) throws IOException {
        buffer.clear();
        buffer.putInt(x);
        buffer.rewind();
        client.write(buffer);
    }

    private boolean readX(SocketChannel client, int x, boolean f) throws IOException {
        buffer.clear();
        client.read(buffer);
        buffer.rewind();

        Integer assertRes = buffer.getInt();
        Double res = buffer.getDouble();
        Long time = buffer.getLong();

        if (assertRes.equals(((InetSocketAddress) client.getLocalAddress()).getPort())) {
            ++count;

            if (f) {
                result.set(0, new Pair(res, time));
            } else {
                result.set(1, new Pair(res, time));
            }

            key.channel().close();

            if (res == 0.0) {
                return false;
            }
        }

        return true;
    }

    public List<Pair<Double, Long>> getResult() {
        return result;
    }

    protected void register(Selector selector, ServerSocketChannel serverSocketChannel) throws IOException {
        SocketChannel client = serverSocketChannel.accept();
        System.out.println("Client connected");
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_WRITE);
    }
}
