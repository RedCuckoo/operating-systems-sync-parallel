package com.momotenko.os.lab1.manager.periodic_prompt;

import com.momotenko.os.lab1.Server;
import com.momotenko.os.lab1.utils.Pair;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;

public class ServerManager {
    private Server server;
    private String hostname;
    private int portF;
    private int portG;

    private volatile Boolean cancel;

    private List<Pair<Double, Long>> results;

    public static void main(String[] argc) throws IOException, InterruptedException {
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

        cancel = false;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> stopExecHooked()));
    }


    public void run(int x) throws InterruptedException {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

        executorService.scheduleWithFixedDelay(() -> {
            synchronized (cancel) {
                if (cancel == true) {
                    executorService.shutdown();
                }

                Scanner in = new Scanner(System.in);
                System.out.println("Would you like to continue?\n" +
                        "a - continue, b - continue without prompt, c - cancel");
                String ans = in.nextLine();

                if (ans.equals("a")) {

                } else if (ans.equals("b")) {
                    executorService.shutdown();
                } else {
                    cancel = true;
                    Runtime.getRuntime().exit(1);
                }
            }
        }, 3, 3, SECONDS);


        server = new Server(hostname, portF, portG);
        server.run(x);

        synchronized (cancel) {
            processResults(!cancel);
        }

        return;
    }

    private void stopExecHooked() {
        processResults(false);
    }

    private void processResults(boolean finished) {
        results = server.getResult();

        Pair<Double, Long> fValue = results.get(0);
        Pair<Double, Long> gValue = results.get(1);

        if (finished) {
            System.out.println("Finished calculating");
        } else {
            System.out.println("Calculation was interrupted\n" +
                    "What wasn't calculated:");
        }

        if (fValue == null && gValue == null) {
            System.out.println("Nothing was calculated");
            return;
        }

        if (fValue == null && gValue != null) {
            if (finished) {
                printResult(0.0);
            } else {
                System.out.println("F function wasn't calculated");
            }

            return;
        }

        if (fValue != null && gValue == null) {
            if (finished) {
                printResult(0.0);
            } else {
                System.out.println("G function wasn't calculated");
            }

            return;
        }

        printResult(fValue.getLeft() * gValue.getLeft());
    }

    private void printResult(Double result) {
        System.out.println("Result: " + result);
    }
}
