package com.matnik.game.fxcoursework.server.model;

import java.io.Serializable;

public record UserInfo(String username, String password) implements Serializable {
}
