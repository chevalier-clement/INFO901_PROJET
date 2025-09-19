package com.middleware.internal.service.lamportclock;

import java.util.concurrent.atomic.AtomicInteger;

public class LamportClockService {
    // Attributes
    private AtomicInteger value = new AtomicInteger(0);

    // Constructors
    public LamportClockService() {
    }

    // Getters & Setters

    public int getValue() {
        return value.get();
    }

    public void setValue(int value) {
        this.value.set(value);
    }

    // Methods
    public int increment() {
        return this.value.incrementAndGet();
    }

    public int decrement() {
        return this.value.decrementAndGet();
    }

    public int receive(int value) {
        int v = Math.max(value, this.value.get());
        this.value.set(v + 1);
        return this.getValue();
    }

    public int send() {
        return this.increment();
    }
}
