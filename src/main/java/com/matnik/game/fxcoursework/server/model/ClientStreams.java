package com.matnik.game.fxcoursework.server.model;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

// Modify the ClientStreams class
public class ClientStreams {
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private volatile boolean active; // Add a flag to determine the activity status

    public ClientStreams(ObjectInputStream in, ObjectOutputStream out) {
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
