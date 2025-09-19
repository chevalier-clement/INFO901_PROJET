package com.middleware.core;

import com.middleware.internal.communication.Communicator;
import com.middleware.internal.model.message.AbstractMessage;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Represents a distributed process in the middleware system.
 * <p>
 * Each process is identified by a UUID and manages message communication,
 * critical section access using Lamport clocks, and message handling through a Communicator.
 * <p>
 * This class implements {@code Runnable} and must be initialized using {@link #init()} before it starts execution.
 * Communication includes synchronous and asynchronous sending/receiving, as well as Lamport-based critical section entry.
 * <p>
 * Usage:
 * <pre>
 *     Process p = new Process();
 *     String id = p.init();
 *     p.broadcast("Hello");
 *     ...
 * </pre>
 */
public class Process implements Runnable {
    // Attributes
    private Thread thread;
    private boolean alive;
    private Communicator communicator;
    private UUID uuid;

    // Constructors
    public Process() {
        this.thread = new Thread(this);
        this.alive = false;
        this.communicator = new Communicator();
        this.uuid = UUID.randomUUID();
    }

    // Getters & Setters

    // Methods
    @Override
    public void run() {
        int loop = 0;
        this.wait(200);
        while (this.alive) {
            // Exemple
            if (this.getIdWithStringUUID(this.me()) == 0) {
                // Envoi asynchrone
                if (loop == 100000) {
                    String secondProcessStringUUID = this.getStringUUIDWithId(1);
                    this.sendTo("Hello World", secondProcessStringUUID);
                }
            } else {
                String firstProcessStringUUID = this.getStringUUIDWithId(0);
                // Reception asynchrone via boite aux lettres
                List<AbstractMessage> myLetterBox = this.getAllMessagesFrom(firstProcessStringUUID);
                System.out.println("Nombre de messages aysnchrones reçus dans la BaL: " + this.getNbMessages());

                // Reception asynchrone pour un message unique
                CompletableFuture<AbstractMessage> futureMessage = this.receiveFrom(firstProcessStringUUID);
                futureMessage.thenAccept(message -> {
                    System.out.println(message.toString());
                });
                futureMessage.orTimeout(30, TimeUnit.SECONDS);
            }
            loop++;
            System.out.println(this.getLamportValue());
        }
    }


    // ---- PUBLIC ----

    public void printRoutingTable() {
        this.communicator.printRoutingTable();
    }


    /**
     * Create a new Process
     *
     * @return Created process's id
     */
    public String init() {
        this.communicator.init(this);
        this.alive = true;
        this.thread.start();
        return this.me();
    }

    /**
     * Stop the process
     */
    private void stop() {
        this.alive = false;
        this.communicator.stop();
    }

    /**
     * Send given object to everyone
     *
     * @param o The object to send to all processes
     */
    public void broadcast(Object o) {
        this.communicator.broadcast(o);
    }

    /**
     * Send given object to everyone and wait for everyone to receive the object
     *
     * @param o The object to be sent to all processes
     */
    public void broadcastSync(Object o) {
        this.communicator.broadcastSync(o);
    }

    /**
     * Send a given object to a given process
     *
     * @param o The object to send
     * @param dest The UUID of the destination process (as a string)
     */
    public void sendTo(Object o, String dest) {
        this.communicator.sendTo(o, dest);
    }

    /**
     * Send a given object to a given process and wait for the process to receive the object
     *
     * @param o The object to send
     * @param dest The UUID of the destination process (as a string)
     */
    public void sendToSync(Object o, String dest) {
        this.communicator.sendToSync(o, dest);
    }

    public CompletableFuture<AbstractMessage> receiveFrom(String from) {
        return this.communicator.receiveFrom(from);
    }

    /**
     * Waits until a message is received from the specified process
     */
    public AbstractMessage receiveFromSync(String from) {
        return this.communicator.receiveFromSync(from);
    }

    /**
     * Make the process wait to get the critical section access token
     */
    public void requestCS() {
        this.communicator.requestCS();
    }

    /**
     * Free the critical section access token
     */
    public void releaseCS() {
        this.communicator.releaseCS();
    }

    /**
     *
     */
    public void synchronize() {
        this.communicator.synchronize();
    }

    /**
     *
     * @return The process's id
     */
    public String me() {
        return this.uuid.toString();
    }

    /**
     * Retrieve the number of messages from the process's letterbox
     *
     * @return The number of messages
     */
    public int getNbMessages() {
        return this.communicator.getNbMessages();
    }

    /**
     * Retrieves all asynchronous messages received
     * The messages are NOT deleted from the letterbox
     *
     * @return A copy of asynchronous messages received
     */
    public List<AbstractMessage> getAllMessages() {
        return this.communicator.getAllMessages();
    }

    /**
     * Retrieves all asynchronous messages received
     * The messages ARE deleted from the letterbox
     *
     * @return All asynchronous message received
     */
    public List<AbstractMessage> getAndDeleteAllMessages() {
        return this.communicator.getAndDeleteAllMessages();
    }

    /**
     * Retrieves all asynchronous messages received from the specified process.
     * The messages are NOT deleted from the letterbox.
     *
     * @param from The UUID (as a string) of the sender process
     * @return A list of messages from the given sender
     */
    public List<AbstractMessage> getAllMessagesFrom(String from) {
        return this.communicator.getAllMessagesFrom(from);
    }

    /**
     * Retrieves all asynchronous messages received from the specified process.
     * The messages ARE deleted from the letterbox.
     *
     * @param from The UUID (as a string) of the sender process
     * @return A list of messages from the given sender
     */
    public List<AbstractMessage> getAndDeleteAllMessagesFrom(String from) {
        return this.communicator.getAndDeleteAllMessagesFrom(from);
    }

    /**
     * Adds the given message to the process's letterbox.
     * If a message from the same sender already exists, it will be duplicated.
     *
     * @param message The message to add to the letterbox
     */
    public void addMessage(AbstractMessage message) {
        this.communicator.addMessage(message);
    }

    /**
     * Increment the process's lamport clock
     *
     * @return The updated clock's value
     */
    public int inc_clock() {
        return this.communicator.inc_clock();
    }

    /**
     * Retrieve the associated UUID with the given id in the routing table
     *
     * @param id
     * @return The UUID (as a string), null if not found
     */
    public String getStringUUIDWithId(int id) {
        return this.communicator.getStringUUIDWithId(id);
    }

    /**
     * Retrieves the UUID (as a string) corresponding to the given process ID from the routing table
     *
     * @param uuid
     * @return process's id, null if not found
     */
    public int getIdWithStringUUID(String uuid) {
        return this.communicator.getIdWithStringUUID(uuid);
    }

    /**
     *
     * @return Retrieves the value of lamport clock
     */
    public int getLamportValue() {
        return this.communicator.getLamportValue();
    }

    /**
     * Sleep thread for the given time in ms
     *
     * @param waitTimeMs
     */
    public void wait(int waitTimeMs) {
        try {
            Thread.sleep(waitTimeMs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---- PRIVATE ----
    private void waitStoped() {
        while (this.alive) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
