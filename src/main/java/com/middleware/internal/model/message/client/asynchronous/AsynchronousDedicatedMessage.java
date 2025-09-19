package com.middleware.internal.model.message.client.asynchronous;

public class AsynchronousDedicatedMessage extends AbstractAsynchronousClientMessage {
    // Attributes
    private int dest;

    // Constructors
    public AsynchronousDedicatedMessage(int from, Object o, int dest, int lamportValue) {
        super(from, o, lamportValue);
        this.dest = dest;
    }

    // Getters & Setters
    public int getDest() {
        return this.dest;
    }

    public void setDest(int dest) {
        this.dest = dest;
    }

    // Methods
}
