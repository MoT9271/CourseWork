package com.matnik.game.fxcoursework.server.model;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

// Modify the ClientStreams class
public class ClientStreams {
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private final Socket socket;

    public Socket getSocket() {
        return socket;
    }

    private volatile boolean active; // Add a flag to determine the activity status

    public ClientStreams(ObjectInputStream in, ObjectOutputStream out, Socket  socket) {
        this.socket = socket;
        this.in = in;
        this.out = out;
        this.active = true;
    }

    public ObjectInputStream in() {
        return in;
    }

    public ObjectOutputStream out() {
        return out;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
