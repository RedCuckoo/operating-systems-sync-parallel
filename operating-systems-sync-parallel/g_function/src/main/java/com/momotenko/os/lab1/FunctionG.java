package com.momotenko.os.lab1;

import spos.lab1.demo.DoubleOps;

import java.io.IOException;

public class FunctionG extends Client {
    public static void main(String[] args) throws IOException {
        FunctionG g = new FunctionG("localhost", 4050);

        try {
            g.waitServer();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public FunctionG(String hostname, int port) {
        super(hostname, port);
    }

    @Override
    protected Double getResult(int x) throws InterruptedException {
        return DoubleOps.funcG(x);
    }
}
