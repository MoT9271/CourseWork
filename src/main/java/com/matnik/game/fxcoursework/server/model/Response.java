package com.matnik.game.fxcoursework.server.model;

import java.io.Serializable;

public record Response(boolean success, String message) implements Serializable {
}
