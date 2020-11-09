package com.momotenko.os.lab1.manager.periodic_prompt;

import com.momotenko.os.lab1.Server;
import com.momotenko.os.lab1.manager.ServerManagerAbstract;

import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;

public class ServerManager extends ServerManagerAbstract {
    private volatile Boolean cancel;


    public ServerManager(String hostname, int portF, int portG) {
        super(hostname, portF, portG);
        cancel = false;
    }



    @Override
    public void run(int x) {
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
}
