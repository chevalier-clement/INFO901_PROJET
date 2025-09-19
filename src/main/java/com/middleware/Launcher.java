package com.middleware;

import com.middleware.core.Process;

import java.util.ArrayList;

public class Launcher {
    public static void main(String[] args) {

        final int maxNbProcess = 2;

        ArrayList<Process> processes = new ArrayList<Process>();

        for (int i = 0; i < maxNbProcess; i++) {
            Process p = new Process();
            processes.add(p);
            p.init();
        }
    }
}
