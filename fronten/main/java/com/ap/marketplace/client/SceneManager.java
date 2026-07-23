package com.ap.marketplace.client;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.Consumer;

/** مدیریت پنجره و جابه‌جایی بین صفحه‌ها. FXMLها از resources/fxml بارگذاری می‌شوند. */
public final class SceneManager {

    private static Stage stage;

    private SceneManager() { }

    public static void init(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("سامانه آگهی دست دوم");
        stage.setWidth(1000);
        stage.setHeight(680);
    }

    public static Stage stage() { return stage; }

    /** بارگذاری یک صفحه ساده. */
    public static void switchTo(String fxml) {
        switchTo(fxml, null);
    }

    /**
     * بارگذاری صفحه و اجرای یک آماده‌سازی روی کنترلر (مثلاً دادن id آگهی).
     * @param initializer تابعی که کنترلر بارگذاری‌شده را می‌گیرد.
     */
    public static <C> void switchTo(String fxml, Consumer<C> initializer) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneManager.class.getResource("/fxml/" + fxml));
            Parent root = loader.load();
            if (initializer != null) {
                C controller = loader.getController();
                initializer.accept(controller);
            }
            Scene scene = stage.getScene();
            if (scene == null) {
                scene = new Scene(root);
                var css = SceneManager.class.getResource("/css/app.css");
                if (css != null) scene.getStylesheets().add(css.toExternalForm());
                stage.setScene(scene);
            } else {
                scene.setRoot(root);
            }
            stage.getScene().setNodeOrientation(javafx.geometry.NodeOrientation.RIGHT_TO_LEFT);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("بارگذاری صفحه ناموفق بود: " + fxml, e);
        }
    }
}
