package com.middleware.internal.service.letterbox;

import com.middleware.internal.model.message.AbstractMessage;
import com.middleware.internal.model.message.client.asynchronous.AbstractAsynchronousClientMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LetterBoxService {
    // Attributes
    private List<AbstractMessage> box;

    // Constructors
    public LetterBoxService() {
        this.box = new CopyOnWriteArrayList<>();
    }

    // Getters & Setters

    // Methods
    public void addMessage(AbstractMessage message) {
        this.box.add(message);
    }

    public List<AbstractMessage> getAllMessages() {
        return new ArrayList<>(this.box);
    }

    public List<AbstractMessage> getAndDeleteAllMessages() {
        List<AbstractMessage> list = this.getAllMessages();
        this.box.clear();
        return list;
    }

    public List<AbstractMessage> getAllMessagesFrom(int id) {
        List<AbstractMessage> list = new ArrayList<>();
        for (AbstractMessage message : this.box) {
            if (message instanceof AbstractAsynchronousClientMessage clientMessage) {
                if (clientMessage.getFrom() == id) {
                    list.add(message);
                }
            }
        }
        return list;
    }

    public List<AbstractMessage> getAndDeleteAllMessagesFrom(int id) {
        List<AbstractMessage> list = this.getAllMessagesFrom(id);
        this.box.clear();
        return list;
    }

    public boolean isEmpty() {
        return this.box.isEmpty();
    }

    public int size() {
        return this.box.size();
    }
}
