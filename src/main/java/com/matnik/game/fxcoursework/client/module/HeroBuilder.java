package com.matnik.game.fxcoursework.client.module;

import com.matnik.game.fxcoursework.client.view.BuildView;
import com.matnik.game.fxcoursework.client.view.StartMenu;

public class HeroBuilder {
    public static Hero buildHero() {
        switch (StartMenu.heroType){
            case Archer -> {
                return new Hero(HeroType.Archer, BuildView.build);
            }
            case Knight -> {
                return new Hero(HeroType.Knight,BuildView.build);
            }
            case Mage -> {
                return new Hero(HeroType.Mage, BuildView.build);
            }
        }
        return null;
    }

}
