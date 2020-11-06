package com.sziffer.series;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {

    private Button connection;
    private TextField username;
    private TextField password;
    private DatabaseManager databaseManager;

    private Stage primaryStage;
    private ConnectionScreen connectionScreen;


    @Override
    public void start(Stage primaryStage) throws Exception {

        this.primaryStage = primaryStage;
        primaryStage.setResizable(false);
        StackPane stackPane = new StackPane();
        VBox group = new VBox(20);
        group.setAlignment(Pos.CENTER);
        final MediaPlayer bgVideo = new MediaPlayer(
                new Media(getClass().getResource("bg.mp4").toString())
        );
        MediaView mediaView = new MediaView(bgVideo);
        bgVideo.setMute(true);
        bgVideo.setCycleCount(MediaPlayer.INDEFINITE);
        bgVideo.play();

        stackPane.getChildren().addAll(mediaView,group);

        databaseManager = new DatabaseManager(this);
        connectionScreen = new ConnectionScreen(databaseManager);

        connection = new Button("Connect");
        connection.setMaxWidth(200);

        connection.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                handleConnectionClick();

            }
        });

        username = new TextField();
        password = new PasswordField();
        username.setPromptText("Username");
        password.setPromptText("Password");

        username.setMaxWidth(200);
        password.setMaxWidth(200);

        Label label = new Label("Please sign-in to the Oracle Server");
        label.setFont(Font.font(25));
        label.setTextFill(Color.WHITE);
        group.getChildren().addAll(label, username, password, connection);
        Scene scene = new Scene(stackPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(windowEvent -> {
            databaseManager.closeConnection();
            System.exit(0);
        });

    }

    public void connected() {
        primaryStage.setScene(new Scene(connectionScreen, 800, 600));
    }

    void setSeries(ObservableList<SeriesItem> series) {
        Platform.runLater(() -> connectionScreen.fetchDataToList(series));
    }

    void seriesRemoved(SeriesItem seriesItem) {
        Platform.runLater(() -> connectionScreen.itemRemoved(seriesItem));
    }

    private void handleConnectionClick() {
        if (!username.getText().isEmpty() && !password.getText().isEmpty()) {
            databaseManager.connectToOracleDb(username.getText(), password.getText());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
