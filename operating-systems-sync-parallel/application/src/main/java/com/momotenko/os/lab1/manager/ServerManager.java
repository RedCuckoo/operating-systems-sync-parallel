package com.momotenko.os.lab1.manager;

import com.momotenko.os.lab1.Server;
import com.momotenko.os.lab1.utils.Pair;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;


public class ServerManager {
    private Server server;
    private String hostname;
    private int portF;
    private int portG;

    private List<Pair<Double, Long>> results;

    public static void main(String[] argc) throws IOException {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter x: ");
        int x = input.nextInt();

        ServerManager controller = new ServerManager("localhost", 4040, 4050);
        controller.run(x);

        Runtime.getRuntime().halt(0);
    }

    public ServerManager(String hostname, int portF, int portG) throws IOException {
        this.hostname = hostname;
        this.portF = portF;
        this.portG = portG;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> stopExecHooked()));
    }

    public void run(int x) {
        server = new Server(hostname, portF, portG);
        server.run(x);

        processResults(true);

        return;
    }

    private void processResults(boolean finished) {
        results = server.getResult();

        Pair<Double, Long> fValue = results.get(0);
        Pair<Double, Long> gValue = results.get(1);

        if (finished) {
            System.out.println("Finished calculating");
        } else {
            System.out.println("Calculation was interrupted\n" +
                    "What was calculated");
        }

        if (fValue == null && gValue == null) {
            System.out.println("Nothing calculated");
            return;
        }

        if (fValue == null && gValue != null) {

            System.out.println((finished ? "f: stop calculating because g returned zero\n"
                    : "f: stop calculation due to cancellation\n") +
                    generateValue(false, gValue));

            if (finished) {
                printResult(0.0);
            }

            return;
        }

        if (fValue != null && gValue == null) {
            System.out.println(generateValue(true, fValue) + "\n" +
                    (finished ? "g: stop calculating because f is null" :
                            "g: stop calculating due to cancellation"));

            if (finished) {
                printResult(0.0);
            }

            return;
        }

        System.out.println(generateValue(true, fValue) + "\n" +
                generateValue(false, gValue));

        printResult(fValue.getLeft() * gValue.getLeft());
    }

    private void printResult(Double result) {
        System.out.println("Result: " + result);
    }

    private String generateValue(boolean f, Pair<Double, Long> value) {
        return (f ? "f" : "g") + ": " + value.getLeft() + " in " + value.getRight() + "ms";
    }

    private void stopExecHooked() {
        processResults(false);
    }
}
