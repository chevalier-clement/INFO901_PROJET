package com.middleware.internal.communication.state;

import com.middleware.internal.model.message.AbstractMessage;
import com.middleware.internal.model.message.system.ack.SystemAcknowledgementMessage;

public class WaitingForAckState extends AbstractState {
    // Attributes
    private int from;

    // Constructors
    public WaitingForAckState(int from) {
        super();
        this.from = from;
    }

    // Getters & Setters

    // Methods
    @Override
    public AbstractState handleMessage(AbstractMessage message) {
        if (message instanceof SystemAcknowledgementMessage acknowledgementMessage) {
            if (acknowledgementMessage.getFrom() == this.from) {
                return new NullState();
            }
        }
        return this;
    }
}
