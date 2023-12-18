package com.matnik.game.fxcoursework.client.module;

import java.io.Serializable;

public class Item implements Serializable {

    private String type;
    private String imagePath; // Store the file path instead of Texture
    private int hp;
    private int damage;
    private int armor;
    private int magicResistance;

    public Item(String type, String imagePath, int hp, int damage, int armor, int magicResistance) {
        this.type = type;
        this.imagePath = imagePath;
        this.hp = hp;
        this.damage = damage;
        this.armor = armor;
        this.magicResistance = magicResistance;
    }
    public Item(){

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getArmor() {
        return armor;
    }

    public void setArmor(int armor) {
        this.armor = armor;
    }

    public int getMagicResistance() {
        return magicResistance;
    }

    public void setMagicResistance(int magicResistance) {
        this.magicResistance = magicResistance;
    }
}
