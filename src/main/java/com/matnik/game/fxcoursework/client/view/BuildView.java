package com.matnik.game.fxcoursework.client.view;

import com.matnik.game.fxcoursework.client.module.HeroType;
import com.matnik.game.fxcoursework.client.module.Item;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class BuildView extends Parent {

    private HBox imagePanel;
    private HBox addedImagePanel;
    private List<Item> items;
    public static List<Item> build;
    private GridPane gridItems;
    private GridPane gridBuild;
    private HeroType heroType;

    public BuildView(List<Item> items, HeroType heroType) {
        this.heroType = heroType;
        var titleLabel = new Text("Registration");
        titleLabel.setFill(Color.GREEN);
        titleLabel.setFont(Font.font("Verdana", 36.0));
        titleLabel.setStroke(Color.BLACK);
        titleLabel.setTranslateY(50);
        titleLabel.setTranslateX(300);
        this.items = items;
        this.build = new ArrayList<>();
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
        gridItems.setTranslateY(50);
        gridBuild.setTranslateY(50);
        imagePanel.setTranslateY(50);

        var createButton = createButton("Create build", () -> {
            createBuild();
        });
        getChildren().addAll(gridItems, gridBuild, imagePanel, addedImagePanel, titleLabel, createButton);
    }

    private void createBuild() {
        getChildren().clear();
        getChildren().addAll(new MainMenuView());
    }


    private void showImage(Item item, ImageView imageView) {
        if (build.size() >= 3) return;
        imagePanel.getChildren().clear();
        addedImagePanel.getChildren().add(imageView);
        build.add(item);
        items.remove(item);
        imageView.setOnMouseClicked(event -> returnImage(item, imageView)); // Set the correct event handler
        showGrid();
    }

    private void returnImage(Item item, ImageView imageView) {
        build.remove(item);
        items.add(item);
        addedImagePanel.getChildren().remove(imageView);
        showGrid();
    }

    private void showGrid() {
        gridItems.getChildren().clear();
        int numRows = (int) Math.ceil((double) items.size() / 6);

        for (int y = 0; y < numRows; y++) {
            for (int x = 0; x < 6; x++) {
                int index = y * 6 + x;
                if (index < items.size()) {
                    Item item = items.get(index);
                    var imageView = item.getImage();
                    imageView.setFitWidth(100);
                    imageView.setFitHeight(100);
                    gridItems.add(imageView, x, y);
                    imageView.setOnMouseClicked(event -> showImage(item, imageView));
                }
            }
        }
    }

    private Background createBackground() {
        return new Background(new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY));
    }

    private Button createButton(String text, Runnable action) {
        var button = new Button(text);
        button.setOnAction(event -> action.run());
        button.setFont(Font.font("Verdana", 18));
        button.setBackground(createBackground());
        button.setTextFill(Color.WHITE);
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #505050;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: gray;"));
        return button;
    }

}
