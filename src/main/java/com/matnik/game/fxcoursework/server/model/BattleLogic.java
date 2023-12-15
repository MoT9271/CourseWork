package com.matnik.game.fxcoursework.server.model;

import com.matnik.game.fxcoursework.client.module.Hero;
import com.matnik.game.fxcoursework.client.module.HeroType;
import com.matnik.game.fxcoursework.server.model.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class BattleLogic {
    private final ClientStreams player1;
    private final ClientStreams player2;
    private Hero hero1;
    private Hero hero2;

    public BattleLogic(ClientStreams player1, ClientStreams player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    public void handleFight() throws IOException, ClassNotFoundException {
        ObjectOutputStream player1Out = player1.out();
        ObjectInputStream player1In = player1.in();
        ObjectOutputStream player2Out = player2.out();
        ObjectInputStream player2In = player2.in();
        hero1 = (Hero) player1In.readObject();
        hero2 = (Hero) player2In.readObject();
        player1Out.writeObject(hero2);
        player2Out.writeObject(hero1);
        player1Out.writeObject("First");
        player2Out.writeObject("Second");
        while (hero1.getHp() > 0 && hero2.getHp() > 0) {
            hero2 = (Hero) player1In.readObject();
            player2Out.writeObject(hero2);
            hero1 = (Hero) player2In.readObject();
            player1Out.writeObject(hero1);
        }

    }

}
