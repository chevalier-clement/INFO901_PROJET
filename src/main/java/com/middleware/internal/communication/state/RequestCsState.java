package com.middleware.internal.communication.state;

import com.middleware.internal.model.message.AbstractMessage;
import com.middleware.internal.model.message.system.token.SystemTokenMessage;

public class RequestCsState extends AbstractState {
    // Attributes
    private int me;

    // Constructors
    public RequestCsState(int id) {
        super();
        this.me = id;

    }

    // Getters & Setters

    // Methods
    @Override
    public AbstractState handleMessage(AbstractMessage message) {
        if (message instanceof SystemTokenMessage tokenMessage) {
            if (tokenMessage.getDest() == this.me) {
                return new HandlingCsState();
            }
        }
        return this;
    }
}
