package com.momotenko.os.lab1.manager;

import com.momotenko.os.lab1.Server;

import java.lang.reflect.InvocationTargetException;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Scanner;

public abstract class ServerManagerAbstract {
    private static Class<? extends ServerManagerAbstract> classInstance;
    protected Server server;
    protected String hostname;
    protected int portF;
    protected int portG;

    protected List<SimpleEntry<Double, Long>> results;

    public static void main(String[] argc) throws IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter x: ");
        int x = input.nextInt();

        if (argc.length == 0){
            System.out.println("Wrong input, please provide class name");
        }
        else{
            classInstance = (Class<? extends ServerManagerAbstract>) Class.forName("com.momotenko.os.lab1.manager."+argc[0]);
        }

        ServerManagerAbstract manager = (ServerManagerAbstract) classInstance.getConstructors()[0].newInstance("localhost", 4040, 4050);
        manager.run(x);

        Runtime.getRuntime().halt(0);
    }

    public ServerManagerAbstract(String hostname, int portF, int portG) {
        this.hostname = hostname;
        this.portF = portF;
        this.portG = portG;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> stopExecHooked()));
    }

    public abstract void run(int x);

    protected void processResults(boolean finished) {
        results = server.getResult();

        SimpleEntry<Double, Long> fValue = results.get(0);
        SimpleEntry<Double, Long> gValue = results.get(1);

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

        printResult(fValue.getKey() * gValue.getKey());
    }

    private void printResult(Double result) {
        System.out.println("Result: " + result);
    }

    private void stopExecHooked() {
        processResults(false);
    }
}
