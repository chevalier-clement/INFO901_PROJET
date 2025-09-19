package com.middleware.internal.communication.state;

import com.middleware.internal.model.message.AbstractMessage;

public class ReleaseCsState extends AbstractState {
    // Attributes

    // Constructors

    // Getters & Setters

    // Methods
    @Override
    public AbstractState handleMessage(AbstractMessage message) {
        return new NullState();
    }
}
