package com.matnik.game.fxcoursework.client.module;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.security.SecureRandomParameters;
import java.util.List;

public class Hero implements Serializable {

    private HeroType heroType;
    private int hp;
    private int damage;
    private int armor;
    private int magicResistance;
    private List<Item> items;

    public Hero(HeroType heroType, List<Item> items) {
        this.heroType = heroType;
        this.items = items;
        calculateStats();
    }


    private void calculateStats() {
        this.hp = heroType.getBaseHp();
        this.damage = heroType.getBaseDamage();
        this.armor = heroType.getBaseArmor();
        this.magicResistance = heroType.getBaseMagicResistance();
        for (Item item : items) {
            this.damage += item.getDamage();
            this.armor += item.getArmor();
            this.hp += item.getHp();
            this.magicResistance += item.getMagicResistance();
        }


    }
    public Hero() {
    }


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

    public void setMagicResistance(int magicResistance) {
        this.magicResistance = magicResistance;
    }

    public void setArmor(int armor) {
        this.armor = armor;
    }

    public int getArmor() {
        return armor;
    }

    public int getMagicResistance() {
        return magicResistance;
    }


}
