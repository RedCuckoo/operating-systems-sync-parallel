package com.momotenko.os.lab1.manager;

import com.momotenko.os.lab1.Server;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class ServerManager {
    private Server server;
    private String hostname;
    private int portF;
    private int portG;
    private Process compileF;
    private Process compileG;
    private List<Double> results;

    public static void main(String[] argc) throws IOException {
        ServerManager controller = new ServerManager("localhost", 4040, 4050);
        controller.run();

        Runtime.getRuntime().halt(0);
    }

    public ServerManager(String hostname, int portF, int portG) throws IOException {
        this.hostname = hostname;
        this.portF = portF;
        this.portG = portG;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> stopExecHooked()));
    }

    public void run() {
        server = new Server(hostname, portF, portG);
        server.run();

        startClients();

        while (server.resultsReady() != true) {
            results = server.getResult();
        }

        results = server.getResult();

        processResults(true);

        stopExec();

        return;
    }

    private void processResults(boolean finished) {
        Double fX = results.get(0);
        Double gX = results.get(1);

        if (fX != null && fX == 0 ||
                gX != null && gX == 0) {
            processNull(fX, gX, finished);
        } else {
            processNotNull(fX, gX, finished);

        }
    }

    private void processNotNull(Double fX, Double gX, boolean finished) {
        if (finished == true){
            System.out.println("Finished");
        }

        System.out.println("f: " + fX + "\n" +
                "g: " + gX + "\n");
    }

    private void processNull(Double fX, Double gX, boolean finished) {
        if (finished == true){
            System.out.println("Finished\n");
        }

        if (fX != null && fX == 0) {
            System.out.println("f: 0.0\n" +
                    "g: stop calculating because f is null");
        } else if (gX != null && gX == 0) {
            System.out.println("f: stop calculating because g is null\n" +
                    "g: 0.0");
        }
    }

    private void stopExecHooked() {
        stopExec();
        System.out.println("Calculation was interrupted\n" +
                "The results received before interruption\n");

        processResults(false);

    }

    private void stopExec() {
        compileF.destroy();
        compileG.destroy();
        server.stop();
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

}
