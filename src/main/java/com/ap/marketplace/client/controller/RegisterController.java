package com.ap.marketplace.client.controller;

import com.ap.marketplace.client.SceneManager;
import com.ap.marketplace.client.Session;
import com.ap.marketplace.client.api.ApiClientException;
import com.ap.marketplace.client.api.AuthApi;
import com.ap.marketplace.client.api.dto.AuthResponse;
import com.ap.marketplace.client.util.Alerts;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private TextField fullNameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML
    private void onRegister() {
        String username = text(usernameField);
        String fullName = text(fullNameField);
        String phone = text(phoneField);
        String email = text(emailField);
        String password = passwordField.getText() == null ? "" : passwordField.getText();

        if (username.isEmpty() || fullName.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            Alerts.error("همه فیلدهای ضروری را پر کنید");
            return;
        }
        try {
            AuthResponse res = AuthApi.register(username, password, fullName,
                    email.isEmpty() ? null : email, phone);
            Session.get().login(res.token(), res.user());
            Alerts.info("ثبت‌نام موفق بود. خوش آمدید!");
            SceneManager.switchTo("ad-list.fxml");
        } catch (ApiClientException e) {
            Alerts.error(e.getMessage());
        }
    }

    @FXML
    private void onGoLogin() {
        SceneManager.switchTo("login.fxml");
    }

    private static String text(TextField f) {
        return f.getText() == null ? "" : f.getText().trim();
    }
}
