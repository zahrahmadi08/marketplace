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

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    private void onLogin() {
        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();
        if (username.isEmpty() || password.isEmpty()) {
            Alerts.error("نام کاربری و رمز عبور را وارد کنید");
            return;
        }
        try {
            AuthResponse res = AuthApi.login(username, password);
            Session.get().login(res.token(), res.user());
            SceneManager.switchTo("ad-list.fxml");
        } catch (ApiClientException e) {
            Alerts.error(e.getMessage());
        }
    }

    @FXML
    private void onGoRegister() {
        SceneManager.switchTo("register.fxml");
    }
}
