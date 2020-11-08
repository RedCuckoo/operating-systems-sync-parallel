package com.momotenko.os.lab1;

import spos.lab1.demo.DoubleOps;

import java.io.IOException;

public class FunctionF extends Client {
    public static void main(String[] args) throws IOException {
        FunctionF f = new FunctionF("localhost", 4040);

        try {
            f.waitServer();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public FunctionF(String hostname, int port) {
        super(hostname, port);
    }

    @Override
    protected Double getResult(int x) throws InterruptedException {
        return DoubleOps.funcF(x);
    }
}
