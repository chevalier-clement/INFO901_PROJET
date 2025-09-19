package com.middleware.internal.service.eventbus;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class EventBusService {
    private static EventBusService instance = null;
    private AtomicInteger nbProcess = new AtomicInteger(0);

    private EventBus eventBus = null;

    private EventBusService() {
        eventBus = new AsyncEventBus(Executors.newCachedThreadPool());
    }

    public static EventBusService getInstance() {
        if (instance == null) {
            instance = new EventBusService();
        }
        return instance;
    }

    public int getNbProcess() {
        return this.nbProcess.get();
    }

    public int registerSubscriber(Object subscriber) {
        eventBus.register(subscriber);
        return this.nbProcess.getAndIncrement();
    }

    public void unRegisterSubscriber(Object subscriber) {
        eventBus.unregister(subscriber);
        this.nbProcess.decrementAndGet();
    }

    public void postEvent(Object e) {
        try {
            eventBus.post(e);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
