package com.momotenko.os.lab1;

import spos.lab1.demo.DoubleOps;

import java.io.IOException;
import java.util.Scanner;

public class FunctionF extends Client {
    public static void main(String[] args) throws IOException, InterruptedException {
        FunctionF f = new FunctionF("localhost", 4040);
        f.waitServer();
    }

    public FunctionF(String hostname, int port) {
        super(hostname, port);
    }

    @Override
    protected Double getResult(int x) throws InterruptedException {
        return DoubleOps.funcF(x);
    }
}
