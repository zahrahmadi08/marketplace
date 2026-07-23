package com.ap.marketplace.client;

import javafx.application.Application;
import javafx.stage.Stage;

public class MarketplaceClientApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        SceneManager.init(primaryStage);
        SceneManager.switchTo("login.fxml");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
