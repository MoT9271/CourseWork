package com.matnik.game.fxcoursework.client.view;

import com.almasb.fxgl.dsl.FXGL;
import com.matnik.game.fxcoursework.client.module.Hero;
import com.matnik.game.fxcoursework.client.module.HeroBuilder;
import com.matnik.game.fxcoursework.client.module.HeroType;
import com.matnik.game.fxcoursework.server.model.Response;
import com.matnik.game.fxcoursework.server.model.UserInfo;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.IOException;

import javafx.scene.control.Label;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

public class FightView extends Parent {

    private ProgressBar playerHPBar;
    private ProgressBar enemyHPBar;


    private int enemyMaxHp;
    private int playerMaxHp;

    private Text playerHPLabel;
    private Text playerAttackLabel;

    private Text enemyHPLabel;
    private Text enemyAttackLabel;
    private boolean isPlayerTurn;
    private Hero hero;
    private Hero enemyHero;
    private Button attackButton;
    private Button blockButton;

    ImageView heroImageView;
    ImageView enemyHeroImageView;

    public FightView() throws IOException, ClassNotFoundException {
        hero = HeroBuilder.buildHero();
        StartMenu.out.writeObject(hero);
        enemyHero = (Hero) StartMenu.in.readObject();
        isPlayerTurn = StartMenu.in.readObject().toString().equals("First");
        playerMaxHp = hero.getHp();
        enemyMaxHp = enemyHero.getHp();
        var titleLabel = new Text("Fight");
        titleLabel.setFill(Color.YELLOW);
        titleLabel.setFont(Font.font("Verdana", 36.0));
        titleLabel.setStroke(Color.BLACK);
        titleLabel.setTranslateY(50);
        titleLabel.setTranslateX(350);
        heroImageView = switch (StartMenu.heroType) {
            case Archer -> new ImageView(texture("archer.png").getImage());
            case Knight -> new ImageView(texture("knight.png").getImage());
            case Mage -> new ImageView(texture("mage.png").getImage());
        };
        enemyHeroImageView = switch (enemyHero.getHeroType()) {
            case Archer -> new ImageView(texture("enemyArcher.png").getImage());
            case Knight -> new ImageView(texture("enemyKnight.png").getImage());
            case Mage -> new ImageView(texture("enemyMage.png").getImage());
        };


        // Add ImageView for hero
        heroImageView.setFitWidth(350);
        heroImageView.setFitHeight(500);
        heroImageView.setTranslateY(225);
        enemyHeroImageView.setFitWidth(350);
        enemyHeroImageView.setFitHeight(500);
        enemyHeroImageView.setTranslateY(225);
        enemyHeroImageView.setTranslateX(450);
        var backgroundImage = new ImageView(texture("arena.png").getImage()); // Replace with your image path
        backgroundImage.setFitWidth(FXGL.getAppWidth());
        backgroundImage.setFitHeight(FXGL.getAppHeight());

        // Add buttons for Attack and Block
        attackButton = createButton("Attack", () -> {
            try {
                handleAttack();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        blockButton = createButton("Block", this::handleBlock);

        // Use an HBox for horizontal arrangement of buttons
        var buttonBox = new HBox(20, attackButton, blockButton);

        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);
        buttonBox.setTranslateY(650);
        playerHPBar = new ProgressBar(1.0);
        playerHPBar.setPrefWidth(350);
        playerHPBar.setTranslateY(200);
        playerHPBar.setStyle("-fx-accent: green;");

        enemyHPBar = new ProgressBar(1.0);
        enemyHPBar.setPrefWidth(350);
        enemyHPBar.setTranslateX(450);
        enemyHPBar.setTranslateY(200);
        enemyHPBar.setStyle("-fx-accent: red;");
        playerHPLabel = createLabel("HP: " + hero.getHp(), 50, 170);
        playerAttackLabel = createLabel("Attack: " + hero.getDamage(), 150, 170);

        enemyHPLabel = createLabel("HP: " + enemyHero.getHp(), 600, 170);
        enemyAttackLabel = createLabel("Attack: " + enemyHero.getDamage(), 700, 170);

        if (!isPlayerTurn) {
            handleEnemyAttack();
        }

        getChildren().addAll(
                backgroundImage,
                heroImageView,
                enemyHeroImageView,
                titleLabel,
                buttonBox,
                playerHPBar,
                enemyHPBar,
                playerHPLabel,
                playerAttackLabel,
                enemyHPLabel,
                enemyAttackLabel
        );
    }

    private void handleEnemyAttack() {
        attackButton.setDisable(true);
        blockButton.setDisable(true);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws InterruptedException {
                Platform.runLater(() -> {
                    try {
                        getNotificationService().pushNotification("Waiting for enemy move");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

                try {
                    // Небольшая задержка перед чтением объекта
                    Thread.sleep(500);
                    hero = (Hero) StartMenu.in.readObject();
                } catch (IOException | ClassNotFoundException | InterruptedException e) {
                    throw new RuntimeException(e);
                }

                Platform.runLater(() -> {
                    try {
                        getNotificationService().pushNotification("You get " + enemyHero.getDamage() + " damage");
                        updatePlayerUI();
                        attackButton.setDisable(false);
                        blockButton.setDisable(false);
                        isPlayerTurn = true;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

                return null;
            }
        };

        Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return task;
            }
        };

        service.start();
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


    private Text createLabel(String text, double translateX, double translateY) {
        Text label = new Text(text);
        label.setFont(Font.font("Verdana", 20));
        label.setFill(Color.WHITE);
        label.setTranslateX(translateX);
        label.setTranslateY(translateY);
        return label;
    }


    private void handleAttack() throws IOException, InterruptedException {


        // Player's turn to attack
        int damageDealt = hero.getDamage(); // Replace with your logic

        // Update enemy's HP
        enemyHero.setHp(enemyHero.getHp() - damageDealt);

        // Update UI
        updateEnemyUI();

        // Check if the enemy is defeated
        if (enemyHero.getHp() <= 0) {
            handleEnemyDefeated();
        }
        StartMenu.out.writeObject(enemyHero);

        isPlayerTurn = false;


        handleEnemyAttack();

    }

    public static void applyShakeEffect(Node node, Duration duration, double intensity) {
        TranslateTransition tt = new TranslateTransition(duration, node);
        tt.setByX(intensity);
        tt.setInterpolator(Interpolator.DISCRETE);
        tt.setAutoReverse(true);
        tt.setCycleCount(30);
        tt.play();
    }

    private void updatePlayerUI() {
        // Update player's HP and attack labels
        playerHPLabel.setText("HP: " + hero.getHp());
        playerAttackLabel.setText("Attack: " + hero.getDamage());
        playerHPBar.setProgress((double) hero.getHp() / playerMaxHp);
        applyShakeEffect(heroImageView, Duration.seconds(2), 10);
    }

    private void updateEnemyUI() {
        // Update enemy's HP and attack labels
        enemyHPLabel.setText("HP: " + enemyHero.getHp());
        enemyAttackLabel.setText("Attack: " + enemyHero.getDamage());
        enemyHPBar.setProgress((double) enemyHero.getHp() / enemyMaxHp);
        applyShakeEffect(enemyHeroImageView, Duration.seconds(2), 10);
    }

    private void handleEnemyDefeated() {
        // Implement logic when the enemy is defeated
    }

    private void handlePlayerDefeated() {
        // Implement logic when the player is defeated
    }

    private void handleBlock() {
        // Implement the logic for blocking
    }

    private Background createBackground() {
        return new Background(new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY));
    }
}
