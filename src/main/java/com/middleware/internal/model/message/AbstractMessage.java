package com.middleware.internal.model.message;

public abstract class AbstractMessage {
    // Attributes
    private Object payload;

    // Constructors
    public AbstractMessage(Object payload) {
        this.payload = payload;
    }

    // Getters & Setters
    public Object getPayload() {
        return this.payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }


    // Methods
}
