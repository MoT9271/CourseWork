package com.matnik.game.fxcoursework.server.model;

import com.matnik.game.fxcoursework.client.module.Hero;

import java.io.Serializable;

public record UserInfo(String username, String password, Hero hero) implements Serializable {
}
