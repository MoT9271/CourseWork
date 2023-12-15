package com.matnik.game.fxcoursework.client.module;

import com.matnik.game.fxcoursework.client.view.StartMenu;

public class HeroBuilder {
    public static Hero buildHero() {
        switch (StartMenu.heroType){
            case Archer -> {
                return new Hero(10, 3, null, HeroType.Archer);
            }
            case Knight -> {
                return new Hero(15, 2, null, HeroType.Knight);
            }
            case Mage -> {
                return new Hero(7, 5, null, HeroType.Mage);
            }
        }
        return null;
    }
}
