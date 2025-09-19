package com.middleware.internal.model.message.system.ack;

public class SystemAcknowledgementBroadcastMessage extends AbstractSystemAcknowledgementMessage {
    // Attributes
    private int dest;

    // Constructors
    public SystemAcknowledgementBroadcastMessage(int dest) {
        super();
        this.dest = dest;
    }

    // Getters & Setters

    public int getDest() {
        return this.dest;
    }

    // Methods

}
