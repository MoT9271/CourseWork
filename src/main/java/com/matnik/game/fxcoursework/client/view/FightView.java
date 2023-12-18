package com.matnik.game.fxcoursework.client.view;

import com.almasb.fxgl.dsl.FXGL;
import com.matnik.game.fxcoursework.client.module.Hero;
import com.matnik.game.fxcoursework.client.module.HeroType;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
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
    private Hero enemyHero;
    private Button attackButton;
    private Text playerArmorLabel;
    private Text playerMagicResistanceLabel;
    private Text enemyArmorLabel;
    private Text enemyMagicResistanceLabel;


    ImageView heroImageView;
    ImageView enemyHeroImageView;

    public FightView() throws IOException, ClassNotFoundException {
        StartMenu.out.writeObject(BuildView.hero);
        enemyHero = (Hero) StartMenu.in.readObject();
        isPlayerTurn = StartMenu.in.readObject().toString().equals("First");
        playerMaxHp = BuildView.hero.getHp();
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



        // Use an HBox for horizontal arrangement of buttons
        var buttonBox = new HBox(20, attackButton);

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
        playerHPLabel = createLabel("HP: " + BuildView.hero.getHp(), 50, 120);
        playerAttackLabel = createLabel("Attack: " + BuildView.hero.getDamage(), 150, 120);

        enemyHPLabel = createLabel("HP: " + enemyHero.getHp(), 600, 120);
        enemyAttackLabel = createLabel("Attack: " + enemyHero.getDamage(), 700, 120);
        playerArmorLabel = createLabel("Armor: " + BuildView.hero.getArmor(), 50, 150);
        playerMagicResistanceLabel = createLabel("Magic Resistance: " + BuildView.hero.getMagicResistance(), 50, 180);

        enemyArmorLabel = createLabel("Armor: " + enemyHero.getArmor(), 600, 150);
        enemyMagicResistanceLabel = createLabel("Magic Resistance: " + enemyHero.getMagicResistance(), 600, 180);

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
                playerArmorLabel,
                playerMagicResistanceLabel,
                enemyHPLabel,
                enemyAttackLabel,
                enemyArmorLabel,
                enemyMagicResistanceLabel
        );

    }

    private void handleEnemyAttack() {
        attackButton.setDisable(true);
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
                    BuildView.hero = (Hero) StartMenu.in.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

                Platform.runLater(() -> {
                    try {
                        int damageDealt = enemyHero.getDamage();
                        if (enemyHero.getHeroType() == HeroType.Mage) {
                            damageDealt -= BuildView.hero.getMagicResistance();
                        } else {
                            damageDealt -= BuildView.hero.getArmor();
                        }
                        if (damageDealt < 0) {damageDealt = 0;}
                        getNotificationService().pushNotification("You get " + damageDealt + " damage");
                        if (BuildView.hero.getHp() <= 0) {
                            handlePlayerDefeated();
                            return;
                        }
                        updatePlayerUI();
                        attackButton.setDisable(false);
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


        int damageDealt = BuildView.hero.getDamage();
        if (BuildView.hero.getHeroType() == HeroType.Mage) {
            damageDealt -= enemyHero.getMagicResistance();
        } else {
            damageDealt -= enemyHero.getArmor();
        }
        if (damageDealt >= 0)
            enemyHero.setHp(enemyHero.getHp() - damageDealt);

        // Update UI
        updateEnemyUI();

        // Check if the enemy is defeated
        if (enemyHero.getHp() <= 0) {
            handleEnemyDefeated();
            return;
        }
        StartMenu.out.writeObject(enemyHero);

        isPlayerTurn = false;


        handleEnemyAttack();

    }


    private void updatePlayerUI() {
        // Update player's HP, attack, armor, and magic resistance labels
        playerHPLabel.setText("HP: " + BuildView.hero.getHp());
        playerAttackLabel.setText("Attack: " + BuildView.hero.getDamage());
        playerArmorLabel.setText("Armor: " + BuildView.hero.getArmor());
        playerMagicResistanceLabel.setText("Magic Resistance: " + BuildView.hero.getMagicResistance());
        playerHPBar.setProgress((double) BuildView.hero.getHp() / playerMaxHp);
    }

    private void updateEnemyUI() {
        // Update enemy's HP, attack, armor, and magic resistance labels
        enemyHPLabel.setText("HP: " + enemyHero.getHp());
        enemyAttackLabel.setText("Attack: " + enemyHero.getDamage());
        enemyArmorLabel.setText("Armor: " + enemyHero.getArmor());
        enemyMagicResistanceLabel.setText("Magic Resistance: " + enemyHero.getMagicResistance());
        enemyHPBar.setProgress((double) enemyHero.getHp() / enemyMaxHp);
    }


    private void handleEnemyDefeated() throws IOException {
        StartMenu.out.writeObject(enemyHero);
        showMessage("You won");
        getChildren().clear();
        getChildren().addAll(new MainMenuView());
    }

    private void handlePlayerDefeated() {
        showMessage("You lose");
        getChildren().clear();
        getChildren().addAll(new MainMenuView());
    }

    private void handleBlock() {
        // Implement the logic for blocking
    }

    private Background createBackground() {
        return new Background(new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY));
    }
}
