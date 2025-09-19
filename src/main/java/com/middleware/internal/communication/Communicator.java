package com.middleware.internal.communication;

import com.google.common.eventbus.Subscribe;
import com.middleware.core.Process;
import com.middleware.internal.communication.state.*;
import com.middleware.internal.model.message.AbstractMessage;
import com.middleware.internal.model.message.client.AbstractClientMessage;
import com.middleware.internal.model.message.client.asynchronous.AbstractAsynchronousClientMessage;
import com.middleware.internal.model.message.client.asynchronous.AsynchronousBroadcastMessage;
import com.middleware.internal.model.message.client.asynchronous.AsynchronousDedicatedMessage;
import com.middleware.internal.model.message.client.synchronous.AbstractSynchronousClientMessage;
import com.middleware.internal.model.message.client.synchronous.SynchronousBroadcastMessage;
import com.middleware.internal.model.message.client.synchronous.SynchronousDedicatedMessage;
import com.middleware.internal.model.message.system.AbstractSystemMessage;
import com.middleware.internal.model.message.system.ack.AbstractSystemAcknowledgementMessage;
import com.middleware.internal.model.message.system.ack.SystemAcknowledgementMessage;
import com.middleware.internal.model.message.system.ack.SystemAcknowledgementBroadcastMessage;
import com.middleware.internal.model.message.system.synchronize.SystemSynchronizeMessage;
import com.middleware.internal.model.message.system.token.SystemTokenMessage;
import com.middleware.internal.service.eventbus.EventBusService;
import com.middleware.internal.service.lamportclock.LamportClockService;
import com.middleware.internal.service.letterbox.LetterBoxService;
import com.middleware.internal.service.routingtable.RoutingTableService;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class Communicator {
    // Attributes
    private EventBusService busService;
    private RoutingTableService routingTableService;
    private int id;
    private Process process;
    private LetterBoxService letterBox;
    private AbstractState state;
    private Map<Integer, CompletableFuture<AbstractMessage>> waitingMessages;
    private Map<Integer, CompletableFuture<SystemAcknowledgementMessage>> waitingAck;
    private LamportClockService lamportClockService;
    private int synchronizeMessages;
    private int syncBroadcastMessage;

    // Constructors
    public Communicator() {
        this.busService = EventBusService.getInstance();
        this.routingTableService = RoutingTableService.getInstance();
        this.letterBox = new LetterBoxService();
        this.state = new NullState();
        this.waitingMessages = new ConcurrentHashMap<>();
        this.waitingAck = new ConcurrentHashMap<>();
        this.lamportClockService = new LamportClockService();
        this.synchronizeMessages = 0;
        this.syncBroadcastMessage = 0;
    }

    // Getters & Setters
    public int myId() {
        return this.id;
    }

    public int getIdWithStringUUID(String uuidString) {
        UUID uuid = UUID.fromString(uuidString);
        return this.routingTableService.getId(uuid);
    }

    public String getStringUUIDWithId(int id) {
        return (this.routingTableService.getUUID(id)).toString();
    }

    // Methods
    public void init(Process p) {
        this.process = p;
        this.id = this.busService.registerSubscriber(this);
        this.routingTableService.register(UUID.fromString(p.me()), this.id);
        this.initToken();
    }

    private void initToken() {
        if (!SystemTokenMessage.exist()) {
            SystemTokenMessage.getInstance();
            this.sendToken();
        }
    }

    // -- Test --

    /**
     * Display the process's routing table
     */
    public void printRoutingTable() {
        System.out.println(this.routingTableService.toString());
    }

    /**
     *
     * @return The process's lamport clock value
     */
    public int getLamportValue() {
        return this.lamportClockService.getValue();
    }

    // -- Messages --
    public void broadcast(Object o) {
        this.lamportClockService.send();

        int processId = this.getIdFromString(process.me());

        AsynchronousBroadcastMessage message = new AsynchronousBroadcastMessage(processId, o, this.lamportClockService.getValue());
        this.postMessage(message);
    }

    public void broadcastSync(Object o) {
        this.lamportClockService.send();

        int processId = this.getIdFromString(process.me());

        SynchronousBroadcastMessage message = new SynchronousBroadcastMessage(processId, o, this.lamportClockService.getValue());
        this.postMessage(message);

        int nbProcess = this.busService.getNbProcess();

        while (this.syncBroadcastMessage < nbProcess) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.syncBroadcastMessage = 0;
    }

    public CompletableFuture<AbstractMessage> receiveFrom(String from) {
        int fromId = this.getIdFromString(from);
        this.state = new WaitingToReceiveState(fromId);
        return this.waitForMessageFrom(fromId);
    }

    public AbstractMessage receiveFromSync(String from) {
        int fromId = this.getIdFromString(from);
        this.state = new WaitingToReceiveState(fromId);

        CompletableFuture<AbstractMessage> futureMessage = this.waitForMessageFrom(fromId);

        try {
            return futureMessage.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void sendTo(Object o, String dest) {
        this.lamportClockService.send();

        int processId = this.getIdFromString(process.me());
        int destId = this.getIdFromString(dest);

        AsynchronousDedicatedMessage message = new AsynchronousDedicatedMessage(processId, o, destId, this.lamportClockService.getValue());
        this.postMessage(message);
    }

    public void sendToSync(Object o, String dest) {
        this.lamportClockService.send();

        int processId = this.getIdFromString(process.me());
        int destId = this.getIdFromString(dest);

        SynchronousDedicatedMessage message = new SynchronousDedicatedMessage(processId, o, destId, this.lamportClockService.getValue());
        this.postMessage(message);

        this.state = new WaitingForAckState(destId);
        CompletableFuture<SystemAcknowledgementMessage> futureAck = this.waitForAcknowledgementFrom(destId);

        try {
            futureAck.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void requestCS() {
        this.state = new RequestCsState(this.id);
        while (this.state instanceof RequestCsState) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void releaseCS() {
        this.state = new ReleaseCsState();
    }

    public void synchronize() {
        SystemSynchronizeMessage synchronizeMessage = new SystemSynchronizeMessage();
        this.postMessage(synchronizeMessage);
        int nbProcess = this.busService.getNbProcess();
        while (this.synchronizeMessages < nbProcess) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.synchronizeMessages = 0;
    }

    // -- Bus --
    private void postMessage(AbstractMessage message) {
        this.busService.postEvent(message);
    }

    // -- LetterBox --
    public int getNbMessages() {
        return this.letterBox.size();
    }

    public List<AbstractMessage> getAllMessages() {
        return this.letterBox.getAllMessages();
    }

    public List<AbstractMessage> getAndDeleteAllMessages() {
        return this.letterBox.getAndDeleteAllMessages();
    }

    public List<AbstractMessage> getAllMessagesFrom(String from) {
        int fromId = this.getIdFromString(from);
        return this.letterBox.getAllMessagesFrom(fromId);
    }

    public List<AbstractMessage> getAndDeleteAllMessagesFrom(String from) {
        int fromId = this.getIdFromString(from);
        return this.letterBox.getAndDeleteAllMessagesFrom(fromId);
    }

    public void addMessage(AbstractMessage message) {
        this.letterBox.addMessage(message);
    }

    public int inc_clock() {
        return this.lamportClockService.increment();
    }

    public void stop() {
        this.busService.unRegisterSubscriber(this);
        // TODO -> verifications de sécurité (token, section critique)
        // TODO -> Mise à jour des ids des autres processus (et de la table de routage)
    }

    // - RECEIVE -

    @Subscribe
    public void onMessage(AbstractMessage message) {
        this.state = this.state.handleMessage(message);
        if (message instanceof AbstractClientMessage clientMessage) {
            this.handleClientMessage(clientMessage);
        } else if (message instanceof AbstractSystemMessage systemMessage) {
            this.handleSystemMessage(systemMessage);
        }
    }

    private void handleClientMessage(AbstractClientMessage message) {
        if (message.getFrom() != this.id) {
            // Update lamport clock
            this.lamportClockService.receive(message.getLamportValue());

            // Check if a message is waited from message author
            CompletableFuture<AbstractMessage> futureMessage = waitingMessages.get(message.getFrom());
            if (futureMessage != null) {
                futureMessage.complete(message);
                waitingMessages.remove(message.getFrom());
            }

            if (message instanceof AbstractSynchronousClientMessage synchronousClientMessage) {
                this.handleSynchronousClientMessage(synchronousClientMessage);
            } else if (message instanceof AbstractAsynchronousClientMessage asynchronousClientMessage) {
                this.handleAsynchronousClientMessage(asynchronousClientMessage);
            }
        }
    }

    private void handleSynchronousClientMessage(AbstractSynchronousClientMessage message) {
        int fromId = message.getFrom();
        AbstractMessage answerMessage = null;
        if (message instanceof SynchronousDedicatedMessage) {
            answerMessage = new SystemAcknowledgementMessage(this.id, fromId);
        } else if (message instanceof SynchronousBroadcastMessage) {
            answerMessage = new SystemAcknowledgementBroadcastMessage(fromId);
        }
        this.postMessage(answerMessage);
    }

    private void handleAsynchronousClientMessage(AbstractAsynchronousClientMessage message) {
        if (message instanceof AsynchronousBroadcastMessage broadcastMessage) {
            this.letterBox.addMessage(message);
        } else if (message instanceof AsynchronousDedicatedMessage dedicatedMessage) {
            if (dedicatedMessage.getDest() == this.id) {
                this.letterBox.addMessage(message);
            }
        }
    }

    private void handleSystemMessage(AbstractSystemMessage message) {
        if (message instanceof AbstractSystemAcknowledgementMessage acknowledgementMessage) {
            this.handleAckMessage(acknowledgementMessage);
        } else if (message instanceof SystemTokenMessage tokenMessage) {
            this.sendToken();
        } else if (message instanceof SystemSynchronizeMessage synchronizeMessage) {
            this.synchronizeMessages++;
        }
    }

    private void handleAckMessage(AbstractSystemAcknowledgementMessage message) {
        if (message instanceof SystemAcknowledgementMessage systemAcknowledgementMessage) {
            if (systemAcknowledgementMessage.getFrom() != this.id && systemAcknowledgementMessage.getDest() == this.id) {
                CompletableFuture<SystemAcknowledgementMessage> futureAck = waitingAck.get(systemAcknowledgementMessage.getFrom());
                if (futureAck != null) {
                    futureAck.complete(systemAcknowledgementMessage);
                    waitingAck.remove(systemAcknowledgementMessage.getFrom());
                }
            }
        } else if (message instanceof SystemAcknowledgementBroadcastMessage systemAcknowledgementBroadcastMessage) {
            this.syncBroadcastMessage++;
        }
    }

    // -- PRIVATE --
    private UUID getUUIDFromString(String s) {
        UUID uuid = null;
        try {
            uuid = UUID.fromString(s);
        } catch (IllegalArgumentException e) {
            System.err.println(e);
        }
        return uuid;
    }

    private int getIdFromString(String s) {
        UUID uuid = this.getUUIDFromString(s);
        return this.routingTableService.getId(uuid);
    }

    private CompletableFuture<AbstractMessage> waitForMessageFrom(int from) {
        return waitingMessages.computeIfAbsent(from, k -> new CompletableFuture<>());
    }

    private CompletableFuture<SystemAcknowledgementMessage> waitForAcknowledgementFrom(int from) {
        return waitingAck.computeIfAbsent(from, k -> new CompletableFuture<>());
    }

    private void sendToken() {
        if (this.state instanceof NullState) {
            int dest = this.suivant();
            SystemTokenMessage tokenMessage = SystemTokenMessage.getInstance();
            tokenMessage.setDest(dest);
            this.postMessage(SystemTokenMessage.getInstance());
        }
    }

    private int suivant() {
        return (this.id + 1) % this.busService.getNbProcess();
    }
}
