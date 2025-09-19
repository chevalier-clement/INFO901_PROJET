package com.middleware.internal.model.message.client;

import com.middleware.internal.model.message.AbstractMessage;

public abstract class AbstractClientMessage extends AbstractMessage {
    // Attributes
    private int from;
    private int lamportValue;

    // Constructors
    public AbstractClientMessage(int from, Object o, int lamportValue) {
        super(o);
        this.from = from;
        this.lamportValue = lamportValue;
    }

    // Getters & Setters
    public int getFrom() {
        return this.from;
    }

    public int getLamportValue() {
        return this.lamportValue;
    }

    public void setLamportValue(int value) {
        this.lamportValue = value;
    }

    // Methods
}
