package com.middleware.internal.model.message.system.token;

import com.middleware.internal.model.message.system.AbstractSystemMessage;

public class SystemTokenMessage extends AbstractSystemMessage {
    // Attributes
    private static SystemTokenMessage instance;
    private int dest;

    // Constructors
    private SystemTokenMessage() {
        super("TokenMessage");
        this.dest = 0;
    }

    // Getters & Setters
    public static SystemTokenMessage getInstance() {
        if (instance == null) {
            instance = new SystemTokenMessage();
        }
        return instance;
    }

    public int getDest() {
        return this.dest;
    }

    public void setDest(int dest) {
        this.dest = dest;
    }

    // Methods
    public static boolean exist() {
        return instance != null;
    }
}
