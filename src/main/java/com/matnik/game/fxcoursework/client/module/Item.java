package com.matnik.game.fxcoursework.client.module;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.Texture;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Item implements Serializable {


    String type;

    Texture image;
    List<Integer> stats;

    public Item(String type, List<Integer> stats, Texture texture) {
        this.type = type;
        this.stats = stats;
        this.image = texture;
    }

    public Item() {
        this("Wraith Band", Arrays.asList(2, 5, 2, 0, 2, 5, 700), FXGL.texture("heart.png"));
    }

    public void setImage(Texture image) {
        this.image = image;
    }

    public Texture getImage() {
        return image;
    }

    public String getType() {
        return type;
    }

    public List<Integer> getStats() {
        return stats;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStats(List<Integer> stats) {
        this.stats = stats;
    }

    @Override
    public String toString() {
        return "Item" +
                "type='" + type + '\'' +
                ", stats: " + "strength " + stats.get(0) + ", agility " + stats.get(1) + ", intelligence " + stats.get(2)
                + ", movement speed " + stats.get(3) + ", armor" + stats.get(4) + ", attack speed " + stats.get(5) + ", cost " + stats.get(6) + '\n';
    }
}