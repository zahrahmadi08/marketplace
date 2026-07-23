package com.ap.marketplace.client.util;

import javafx.scene.control.Alert;

/** نمایش خطا و موفقیت به‌صورت یکنواخت. */
public final class Alerts {

    private Alerts() { }

    public static void error(String message) {
        show(Alert.AlertType.ERROR, "خطا", message);
    }

    public static void info(String message) {
        show(Alert.AlertType.INFORMATION, "پیام", message);
    }

    private static void show(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setNodeOrientation(javafx.geometry.NodeOrientation.RIGHT_TO_LEFT);
        alert.showAndWait();
    }
}
