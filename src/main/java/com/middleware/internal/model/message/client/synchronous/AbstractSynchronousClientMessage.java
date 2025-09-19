package com.middleware.internal.model.message.client.synchronous;

import com.middleware.internal.model.message.client.AbstractClientMessage;

public abstract class AbstractSynchronousClientMessage extends AbstractClientMessage {
    // Attributes

    // Constructors
    public AbstractSynchronousClientMessage(int from, Object o, int lamportValue) {
        super(from, o, lamportValue);
    }

    // Getters & Setters
}
