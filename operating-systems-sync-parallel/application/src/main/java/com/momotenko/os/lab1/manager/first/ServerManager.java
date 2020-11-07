package com.momotenko.os.lab1.manager.first;


import com.momotenko.os.lab1.Server;

import java.io.File;
import java.io.IOException;


public class ServerManager {
    private Server server;
    private String hostname;
    private int portF;
    private int portG;

    public static void main(String[] argc) throws IOException {
        ServerManager controller = new ServerManager("localhost", 4040, 4050);
        controller.run();
    }

    public ServerManager(String hostname, int portF, int portG) throws IOException {
        this.hostname = hostname;
        this.portF = portF;
        this.portG = portG;

//        String outFolder =
//                "out" + File.separator +
//
//
//        Process compile = new ProcessBuilder("java","-cp",).start();
    }

    public void run() {
        server = new Server(hostname, portF, portG);
        server.run();


    }

}
