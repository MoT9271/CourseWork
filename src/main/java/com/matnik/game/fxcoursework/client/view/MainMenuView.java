package com.matnik.game.fxcoursework.client.view;

import com.almasb.fxgl.dsl.FXGL;
import com.matnik.game.fxcoursework.client.controller.App;
import com.matnik.game.fxcoursework.client.module.HeroType;
import com.matnik.game.fxcoursework.server.model.Response;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.IOException;

import static com.almasb.fxgl.dsl.FXGL.showMessage;
import static com.almasb.fxgl.dsl.FXGL.texture;

public class MainMenuView extends Parent {
    static HeroType enemyType;

    public MainMenuView() {
        var titleLabel = new Text("Main Menu");
        titleLabel.setFill(Color.YELLOW);
        titleLabel.setFont(Font.font("Verdana", 36.0));
        titleLabel.setStroke(Color.BLACK);
        titleLabel.setTranslateY(50);
        titleLabel.setTranslateX(250);

        var createMenuButton = createButton("Start Fight", () -> {
            try {
                startFight();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        var createBuildButton = createButton("Create Build", () -> showBuildView());
        var exitButton = createButton("Exit", () -> exitGame());

        // Use a VBox for vertical arrangement of buttons
        var buttonBox = new VBox(20, createMenuButton, createBuildButton, exitButton);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);
        buttonBox.setTranslateY(350);
        buttonBox.setTranslateX(400);

        // Set background image using texture method
        var backgroundImage = new ImageView(texture("portal.jpg").getImage()); // Replace with your image path
        backgroundImage.setFitWidth(FXGL.getAppWidth());
        backgroundImage.setFitHeight(FXGL.getAppHeight());
        getChildren().addAll(backgroundImage, titleLabel, buttonBox);
    }

    private Button createButton(String text, Runnable action) {
        var button = new Button(text);
        button.setOnAction(event -> action.run());
        button.setFont(Font.font("Verdana", 30));
        button.setBackground(createBackground());
        button.setTextFill(Color.WHITE);
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #505050;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: gray;"));
        return button;
    }

    private void startFight() throws IOException, ClassNotFoundException {
        // Обращение к статическим переменным из StartMenu
        StartMenu.out.writeObject("findOpponent");
        Response response = (Response) StartMenu.in.readObject();
        String message = response.message();
        // Проверка ответа сервера и дальнейшие действия
        // Открывается FightView
        if ("NoPlayersFound".equals(message)) {
            showMessage("No players available for fight\n You are in queue");

            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    try {
                        Response response1 = (Response) StartMenu.in.readObject();

                        Platform.runLater(() -> {
                            getChildren().clear();
                            try {
                                getChildren().addAll(new FightView());
                            } catch (IOException | ClassNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        });

                    } catch (IOException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }

                    return null;
                }
            };

            new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return task;
                }
            }.start();




        }
        else if ("OpponentFound".equals(message)) {
            getChildren().clear();
            getChildren().addAll(new FightView());
        }
    }

    private void showBuildView() {
        getChildren().clear();
        getChildren().addAll(new BuildView(BuildView.items, StartMenu.heroType));
    }

    private void exitGame() {
        FXGL.getGameController().exit();
    }

    private Background createBackground() {
        return new Background(new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY));
    }
}
