package com.middleware.internal.communication.state;

import com.middleware.internal.model.message.AbstractMessage;
import com.middleware.internal.model.message.client.AbstractClientMessage;

public class WaitingToReceiveState extends AbstractState {
    // Attributes
    private int from;

    // Constructors
    public WaitingToReceiveState(int from) {
        super();
        this.from = from;
    }

    // Getters & Setters

    // Methods
    @Override
    public AbstractState handleMessage(AbstractMessage message) {
        if (message instanceof AbstractClientMessage clientMessage) {
            if (clientMessage.getFrom() == this.from) {
                return new NullState();
            }
        }
        return this;
    }
}
