package com.middleware.internal.model.message.system.ack;

import com.middleware.internal.model.message.system.AbstractSystemMessage;

public class SystemAcknowledgementMessage extends AbstractSystemAcknowledgementMessage {
    // Attributes
    private int from;
    private int dest;

    // Constructors
    public SystemAcknowledgementMessage(int from, int dest) {
        super();
        this.from = from;
        this.dest = dest;
    }

    // Getters & Setters
    public int getFrom() {
        return this.from;
    }

    public int getDest() {
        return this.dest;
    }

    // Methods
}
