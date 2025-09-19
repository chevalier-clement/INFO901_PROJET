package com.middleware.internal.communication.state;

import com.middleware.internal.model.message.AbstractMessage;

public abstract class AbstractState {
    // Attributes

    // Constructors

    // Getters & Setters

    // Methods
    public abstract AbstractState handleMessage(AbstractMessage message);

}
