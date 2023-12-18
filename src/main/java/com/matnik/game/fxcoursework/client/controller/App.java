package com.matnik.game.fxcoursework.client.controller;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.matnik.game.fxcoursework.client.module.HeroType;
import com.matnik.game.fxcoursework.client.module.Item;
import com.matnik.game.fxcoursework.client.view.BuildView;
import com.matnik.game.fxcoursework.client.view.StartMenu;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameScene;
import static com.almasb.fxgl.dsl.FXGLForKtKt.texture;

public class App extends GameApplication {

    Pane root = new Pane();

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(700);
        settings.setTitle("FXGL FXML Menu Example");
        settings.setVersion("1.0");
        settings.setMainMenuEnabled(true);
        settings.setSceneFactory(new SceneFactory() {
            @Override
            public FXGLMenu newMainMenu() {
                return new StartMenu();
            }

        });

    }

    @Override
    protected void initGame() {


    }

    @Override
    protected void initUI() {

        List<Item> items = createHeroItems();
        root.getChildren().add(new BuildView(items, StartMenu.heroType));
        getGameScene().addUINode(root);

    }

    @Override
    protected void initInput() {

    }

    public static void main(String[] args) {
        launch(args);
    }

    private List<Item> createHeroItems() {
        List<Item> items = new ArrayList<>();
        if (StartMenu.heroType == HeroType.Knight) {
            items.add(new Item("sword", "armorSword.png", 0, 3, 2, 0));
            items.add(new Item("sword", "katana.png", 0, 4, 0, 0));
            items.add(new Item("sword", "resistSword.png", 0, 3, 0, 3));
            items.add(new Item("shield", "buckler.png", 1, 1, 1, 1));
            items.add(new Item("shield", "heavyShield.png", 2, 0, 2, 0));
            items.add(new Item("shield", "magickShield.png", 2, 0, 0, 2));
            items.add(new Item("cuirass", "armorCuirass.png", 2, 0, 2, 0));
            items.add(new Item("cuirass", "leatherСuirass.png", 0, 3, 1, 1));
            items.add(new Item("cuirass", "magickCuirass.png", 2, 0, 0, 2));


        } else if (StartMenu.heroType == HeroType.Mage) {
            items.add(new Item("staff", "armorStaff.png", 0, 5, 3, 0));
            items.add(new Item("staff", "damageStaff.png", 0, 7, -1, -1));
            items.add(new Item("staff", "magicStaff.png", 0, 5, 0, 3));
            items.add(new Item("ring", "armorRing.png", 2, 0, 1, 1));
            items.add(new Item("ring", "damageRing.png", 2, 2, 0, 1));
            items.add(new Item("ring", "magicRing.png", 2, 2, 0, 1));
            items.add(new Item("hat", "magicHat.png", 0, 1, 0, 1));
            items.add(new Item("hat", "damageHat.png", 0, 3, 0, 0));
            items.add(new Item("hat", "armorHat.png", 0, 1, 1, 0));

        } else if (StartMenu.heroType == HeroType.Archer) {
            items.add(new Item("bow", "armorBow.png", 4, 2, 2, 0));
            items.add(new Item("bow", "heavyBow.png", 0, 7, 0, 0));
            items.add(new Item("bow", "magicBow.png", 4, 7, 0, 2));
            items.add(new Item("amulet", "armorAmulet.png", 2, 0, 1, 0));
            items.add(new Item("amulet", "magicAmulet.png", 2, 0, 0, 1));
            items.add(new Item("amulet", "damageAmulet.png", 2, 3, 0, 0));
            items.add(new Item("glove", "armorGlove.png", 2, 0, 1, 0));
            items.add(new Item("glove", "leatherGlove.png", 2, 3, 0, 0));
            items.add(new Item("glove", "magicGlove.png", 2, 0, 0, 1));
        }
        return items;
    }

}
