package com.matnik.game.fxcoursework.client.controller;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.matnik.game.fxcoursework.client.module.Item;
import com.matnik.game.fxcoursework.client.view.BuildView;
import com.matnik.game.fxcoursework.client.view.StartMenu;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameScene;

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

        List<Item> items = new ArrayList<Item>();
        for (int i = 0; i < 18; i++) {
            items.add(new Item());
        }
        root.getChildren().add(new BuildView(items, StartMenu.heroType));
        getGameScene().addUINode(root);

    }

    @Override
    protected void initInput() {

    }

    public static void main(String[] args) {
        launch(args);
    }

}
