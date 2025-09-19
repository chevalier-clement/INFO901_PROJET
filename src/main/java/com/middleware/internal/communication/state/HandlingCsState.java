package com.middleware.internal.communication.state;

import com.middleware.internal.model.message.AbstractMessage;

public class HandlingCsState extends AbstractState {
    // Constants
    private static final int DURATION_MS = 5000;

    // Attributes
    private volatile boolean released = false;
    private final Object lock = new Object();

    // Constructors
    public HandlingCsState() {
        startClock();
    }

    private void startClock() {
        Thread clock = new Thread(() -> {
            try {
                Thread.sleep(DURATION_MS);
                synchronized (lock) {
                    released = true;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        clock.setDaemon(true);
        clock.start();
    }

    @Override
    public AbstractState handleMessage(AbstractMessage message) {

        // TODO -> Pour l'instant, en cas de timeout, le token n'est relâché que si un message est reçu (appel de handleMessage pour vérifier la valeur de released).
        synchronized (lock) {
            if (released) {
                return new ReleaseCsState(); // Passage à l'état suivant
            } else {
                return this;
            }
        }
    }
}
