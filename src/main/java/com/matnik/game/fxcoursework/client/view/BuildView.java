package com.matnik.game.fxcoursework.client.view;

import com.almasb.fxgl.dsl.FXGL;
import com.matnik.game.fxcoursework.client.module.Hero;
import com.matnik.game.fxcoursework.client.module.HeroBuilder;
import com.matnik.game.fxcoursework.client.module.HeroType;
import com.matnik.game.fxcoursework.client.module.Item;
import com.matnik.game.fxcoursework.server.model.UserInfo;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGLForKtKt.texture;

public class BuildView extends Parent {

    private HBox imagePanel;
    private HBox addedImagePanel;
    public static List<Item> items;
    public static List<Item> build;
    private GridPane gridItems;
    private GridPane gridBuild;
    private HeroType heroType;
    private Text heroStatsText;
    public static Hero hero;

    public BuildView(List<Item> items, HeroType heroType) {
        this.heroType = heroType;
        var titleLabel = new Text("Create your build!");
        titleLabel.setFill(Color.GREEN);
        titleLabel.setFont(Font.font("Verdana", 30.0));
        titleLabel.setStroke(Color.BLACK);
        titleLabel.setTranslateY(650);
        titleLabel.setTranslateX(330);
        heroStatsText = new Text();
        heroStatsText.setFill(Color.GREEN);
        heroStatsText.setFont(Font.font("Verdana", 20.0));
        heroStatsText.setTranslateY(550);
        this.items = items;
        build = new ArrayList<>();
        gridItems = new GridPane();
        gridItems.setHgap(10);
        gridItems.setVgap(10);
        gridBuild = new GridPane();
        gridBuild.setHgap(10);
        gridBuild.setVgap(10);
        imagePanel = new HBox();
        imagePanel.setSpacing(10);
        addedImagePanel = new HBox();
        addedImagePanel.setTranslateY(400);
        gridBuild.setTranslateY(350);
        showGrid();
        gridItems.setTranslateY(60);
        gridBuild.setTranslateY(50);
        imagePanel.setTranslateY(50);
        var backgroundImage = new ImageView(FXGL.texture("forge.png").getImage()); // Replace with your image path
        backgroundImage.setFitWidth(FXGL.getAppWidth());
        backgroundImage.setFitHeight(FXGL.getAppHeight());

        var createButton = createButton("Create build", () -> {
            try {
                createBuild();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        createButton.setTranslateX(450);
        createButton.setTranslateY(400);
        getChildren().addAll(backgroundImage, gridItems,heroStatsText, gridBuild, imagePanel, addedImagePanel, titleLabel, createButton);
    }

    private void createBuild() throws IOException {
        hero = HeroBuilder.buildHero();
        StartMenu.out.writeObject("saveBuild");
        StartMenu.out.writeObject(new UserInfo(StartMenu.username, StartMenu.password, hero));
        getChildren().clear();
        getChildren().addAll(new MainMenuView());
    }
    private void updateHeroStats() {

        int totalHp = StartMenu.heroType.getBaseHp();
        int totalDamage = StartMenu.heroType.getBaseDamage();
        int totalArmor = StartMenu.heroType.getBaseArmor();
        int totalMagicResistance = StartMenu.heroType.getBaseMagicResistance();
        for (Item item : build){
            totalHp += item.getHp();
            totalDamage += item.getDamage();
            totalArmor += item.getArmor();
            totalMagicResistance += item.getMagicResistance();
        }

        // Отобразите суммарные статы героя в тексте
        heroStatsText.setText("Total HP: " + totalHp +
                "\nTotal Damage: " + totalDamage +
                "\nTotal Armor: " + totalArmor +
                "\nTotal Magic Resistance: " + totalMagicResistance);
    }


    private void showImage(Item item, ImageView imageView) {
        if (build.size() >= 3 || isSameTypeInBuild(item)) return;
        imagePanel.getChildren().clear();
        addedImagePanel.getChildren().add(imageView);
        build.add(item);
        items.remove(item);
        imageView.setOnMouseClicked(event -> returnImage(item, imageView)); // Set the correct event handler
        showGrid();
    }
    private boolean isSameTypeInBuild(Item item){
        for(Item buildItem : build){
            if (buildItem.getType().equals(item.getType())) return true;
        }
        return false;
    }

    private void returnImage(Item item, ImageView imageView) {
        build.remove(item);
        items.add(item);
        addedImagePanel.getChildren().remove(imageView);
        showGrid();

    }

    private void showGrid() {
        updateHeroStats();
        gridItems.getChildren().clear();
        int numRows = (int) Math.ceil((double) items.size() / 6);

        for (int y = 0; y < numRows; y++) {
            for (int x = 0; x < 6; x++) {
                int index = y * 6 + x;
                if (index < items.size()) {
                    Item item = items.get(index);
                    var imageView = texture(item.getImagePath());
                    imageView.setFitWidth(100);
                    imageView.setFitHeight(100);
                    gridItems.add(imageView, x, y);

                    // Добавляем слушателя события для показа информации при наведении мыши
                    addMouseOverListener(imageView, item);

                    imageView.setOnMouseClicked(event -> showImage(item, imageView));
                }
            }
        }
    }

    private void addMouseOverListener(ImageView imageView, Item item) {
        imageView.setOnMouseEntered(event -> {
            // Создаем сообщение о статах итема
            String tooltipText =
                    "Type: " + item.getType() + "\n" +
                            "HP: " + item.getHp() + "\n" +
                            "Damage: " + item.getDamage() + "\n" +
                            "Armor: " + item.getArmor() + "\n" +
                            "Magic Resistance: " + item.getMagicResistance();

            // Создаем Tooltip и устанавливаем текст
            Tooltip tooltip = new Tooltip(tooltipText);

            // Позиционируем Tooltip рядом с мышью
            tooltip.show(imageView, event.getScreenX() + 10, event.getScreenY() + 10);

            // Закрываем Tooltip при уходе мыши
            imageView.setOnMouseExited(exitEvent -> tooltip.hide());
        });
    }

    private Background createBackground() {
        return new Background(new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY));
    }

    private Button createButton(String text, Runnable action) {
        var button = new Button(text);
        button.setOnAction(event -> action.run());
        button.setFont(Font.font("Verdana", 24));
        button.setBackground(createBackground());
        button.setTextFill(Color.WHITE);
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #505050;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: gray;"));
        return button;
    }

}
