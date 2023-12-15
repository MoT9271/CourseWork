package com.matnik.game.fxcoursework.client.module;

import java.io.Serializable;
import java.security.SecureRandomParameters;
import java.util.List;

public class Hero implements Serializable {

    private final HeroType heroType;
    private int hp;
    private int damage;
    private List<Item> items;

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
    public HeroType getHeroType() {
        return heroType;
    }

    public int getHp() {
        return hp;
    }

    public int getDamage() {
        return damage;
    }

    public List<Item> getItems() {
        return items;
    }

    public Hero(int hp, int damage, List<Item> items, HeroType heroType) {
        this.hp = hp;
        this.heroType = heroType;
        this.damage = damage;
        this.items = items;
    }
}
