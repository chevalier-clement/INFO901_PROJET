package com.middleware.internal.model.message.client.synchronous;

public class SynchronousDedicatedMessage extends AbstractSynchronousClientMessage {
    // Attributes
    private int dest;

    // Constructors
    public SynchronousDedicatedMessage(int from, Object o, int dest, int lamportValue) {
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
