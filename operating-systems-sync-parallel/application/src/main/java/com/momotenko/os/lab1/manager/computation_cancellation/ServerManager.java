package com.momotenko.os.lab1.manager.computation_cancellation;

import com.momotenko.os.lab1.Server;
import com.momotenko.os.lab1.manager.ServerManagerAbstract;


public class ServerManager extends ServerManagerAbstract {
    public ServerManager(String hostname, int portF, int portG)  {
        super(hostname, portF, portG);
    }

    @Override
    public void run(int x) {
        server = new Server(hostname, portF, portG, x);
        server.run();

        processResults(true);

        return;
    }
}
