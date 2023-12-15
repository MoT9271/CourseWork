package com.matnik.game.fxcoursework.client.view;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.matnik.game.fxcoursework.client.module.HeroType;
import com.matnik.game.fxcoursework.server.model.Response;
import com.matnik.game.fxcoursework.server.model.UserInfo;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static com.almasb.fxgl.dsl.FXGL.showMessage;
import static com.almasb.fxgl.dsl.FXGL.texture;

public class StartMenu extends FXGLMenu {

    private VBox menuBox;
    public static HeroType heroType;
    public static Socket socket;
    public static ObjectOutputStream out;
    public static ObjectInputStream in;

    public StartMenu() {
        super(MenuType.MAIN_MENU);
        // Initialize socket and streams
        try {
            socket = new Socket("127.0.0.1", 5555);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        var imageTexture = texture("menu-bg.png");
        var image = new ImageView(imageTexture.getImage());
        image.setFitHeight(750);
        image.setFitWidth(800);
        var title = new Text("Main Menu");
        title.setFill(Color.WHITE);
        title.setFont(Font.font("Verdana", 36.0));
        title.setStroke(Color.BLACK);

        var brickTexture = FXGL.getAssetLoader().loadTexture("menu-bg.png");
        brickTexture.setTranslateX(50);
        brickTexture.setTranslateY(50);

        FXGL.getGameScene().addUINode(brickTexture);
        var ipAddress = new TextField("127.0.0.1");
        ipAddress.setStyle("-fx-background-color: transparent; -fx-text-inner-color: white;");
        ipAddress.setFont(Font.font("Verdana", 18.0));

        var loginButton = createButton("Login", () -> showLoginForm());
        var registerButton = createButton("Register", () -> showRegistrationForm());
        var exitButton = createButton("Exit", () -> showMessage("Exit"));
        var sceneWidth = getAppWidth();
        var sceneHeight = getAppHeight();

        menuBox = new VBox(20, title, ipAddress, loginButton, registerButton, exitButton);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setTranslateX((sceneWidth - menuBox.getBoundsInParent().getWidth()) / 2);
        menuBox.setTranslateY(sceneHeight - menuBox.getBoundsInParent().getHeight() * 10);

        getContentRoot().getChildren().addAll(image, menuBox);
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

    private void showRegistrationForm() {
        menuBox.getChildren().clear();

        // Set up the registration form
        var titleLabel = new Text("Registration");
        titleLabel.setFill(Color.WHITE);
        titleLabel.setFont(Font.font("Verdana", 36.0));
        titleLabel.setStroke(Color.BLACK);

        var usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        usernameField.setStyle("-fx-background-color: transparent; -fx-text-inner-color: white;");
        usernameField.setFont(Font.font("Verdana", 18.0));

        var passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setStyle("-fx-background-color: transparent; -fx-text-inner-color: white;");
        passwordField.setFont(Font.font("Verdana", 18.0));

        var createButton = createButton("Create", () -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            // Validate username and password
            if (!username.isEmpty() && !password.isEmpty()) {
                // Send registration information to the server
                sendMessage("registration", new UserInfo(username, password));


                try {
                    // Instead of reading a String, read a Response object
                    Response response = (Response) in.readObject();

                    // Now you can access the fields of the Response object
                    boolean success = response.success();
                    String message = response.message();

                    // Process the response as needed
                    showMessage(message);
                    fireNewGame();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                // Show error message if username or password is empty
                showMessage("Please enter username and password");
            }
        });

        var backButton = createButton("Back", () -> {
            getContentRoot().getChildren().clear();
            getContentRoot().getChildren().add(new StackPane(menuBox));
        });

        var registrationBox = new VBox(20, titleLabel, usernameField, passwordField, createButton, backButton);
        registrationBox.setAlignment(Pos.CENTER);
        registrationBox.setBackground(createBackground());
        registrationBox.setStyle("-fx-background-color: #808080; -fx-text-fill: white;");

        // Create a black background
        var blackBackground = new Rectangle(getAppWidth(), getAppHeight());
        blackBackground.setFill(Color.BLACK);

        // Set up the panel with three images
        var imageBox = new VBox(20);
        imageBox.setAlignment(Pos.CENTER);

        // Load three images and create ImageViews
        var archerFace = new ImageView(texture("archerFace.png").getImage());
        var knightFace = new ImageView(texture("knightFace.png").getImage());
        var mageFace = new ImageView(texture("mageFace.png").getImage());

        // Set the size for the images
        archerFace.setFitHeight(70);
        archerFace.setFitWidth(60);
        knightFace.setFitHeight(70);
        knightFace.setFitWidth(60);
        mageFace.setFitHeight(70);
        mageFace.setFitWidth(60);

        var largeImage = new ImageView();

        archerFace.setOnMouseEntered(e -> showLargeImage(largeImage, "archer.png", HeroType.Archer));
        knightFace.setOnMouseEntered(e -> showLargeImage(largeImage, "knight.png", HeroType.Knight));
        mageFace.setOnMouseEntered(e -> showLargeImage(largeImage, "mage.png", HeroType.Mage));

        // Static descriptions for each image
        var archerDescription = new Text("Archer - Master of ranged attacks");
        archerDescription.setFill(Color.WHITE);
        archerDescription.setFont(Font.font("Verdana", 14.0));

        var knightDescription = new Text("Knight - The mighty melee warrior");
        knightDescription.setFill(Color.WHITE);
        knightDescription.setFont(Font.font("Verdana", 14.0));

        var mageDescription = new Text("Mage - Harnesses the power of magic");
        mageDescription.setFill(Color.WHITE);
        mageDescription.setFont(Font.font("Verdana", 14.0));

        imageBox.getChildren().addAll(archerFace, archerDescription, knightFace, knightDescription, mageFace, mageDescription);

        // Move the imageBox down
        imageBox.setTranslateY(300);

        // Add the registration form, black background, and imageBox to the content root
        getContentRoot().getChildren().addAll(blackBackground, registrationBox, imageBox, largeImage);
    }

    private void showLoginForm() {
        menuBox.getChildren().clear();

        // Set up the login form
        var titleLabel = new Text("Login");
        titleLabel.setFill(Color.WHITE);
        titleLabel.setFont(Font.font("Verdana", 36.0));
        titleLabel.setStroke(Color.BLACK);

        var usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setStyle("-fx-background-color: transparent; -fx-text-inner-color: white;");
        usernameField.setFont(Font.font("Verdana", 18.0));

        var passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle("-fx-background-color: transparent; -fx-text-inner-color: white;");
        passwordField.setFont(Font.font("Verdana", 18.0));

        var loginButton = createButton("Login", () -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            // Validate username and password
            if (!username.isEmpty() && !password.isEmpty()) {
                // Send login information to the server
                sendMessage("login", new UserInfo(username, password));

                try {
                    // Instead of reading a String, read a Response object
                    Response response = (Response) in.readObject();

                    // Now you can access the fields of the Response object
                    boolean success = response.success();
                    String message = response.message();

                    // Process the response as needed
                    showMessage(message);
                    fireNewGame();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                // Show error message if username or password is empty
                showMessage("Please enter username and password");
            }
        });

        var loginBox = new VBox(20, titleLabel, usernameField, passwordField, loginButton);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setBackground(createBackground());
        loginBox.setStyle("-fx-background-color: #808080; -fx-text-fill: white;");

        // Add the login form to the content root
        getContentRoot().getChildren().addAll(loginBox);
    }

    // Helper method to show only the larger image
    public static void sendMessage(String operation, UserInfo userInfo) {
        try {
            // Send the operation type to the server
            out.writeObject(operation);

            // Send the user information to the server
            out.writeObject(userInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void showLargeImage(ImageView largeImage, String imagePath, HeroType selectedType) {
        heroType = selectedType;
        largeImage.setImage(texture(imagePath).getImage());
        largeImage.setTranslateX(menuBox.getBoundsInParent().getWidth() + 350);
        largeImage.setFitHeight(600);
        largeImage.setFitWidth(400);
    }
}
