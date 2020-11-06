package com.momotenko.os.lab1.controller;


import com.momotenko.os.lab1.Server;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.List;

public class ServerController implements KeyListener {
    private Server server;
    private String hostname;
    private int portF;
    private int portG;

    public ServerController(String hostname, int portF, int portG){
        this.hostname = hostname;
        this.portF = portF;
        this.portG = portG;
    }

    public void run() {
        server = new Server(hostname, portF, portG);
        server.run();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
