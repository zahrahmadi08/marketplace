package com.ap.marketplace.client.controller;

import com.ap.marketplace.client.SceneManager;
import com.ap.marketplace.client.api.AdminApi;
import com.ap.marketplace.client.api.ApiClientException;
import com.ap.marketplace.client.api.dto.AdSummaryDto;
import com.ap.marketplace.client.api.dto.StatsDto;
import com.ap.marketplace.client.api.dto.UserDto;
import com.ap.marketplace.client.util.Alerts;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

public class AdminController {

    @FXML private Label statUsers, statAds, statPending, statActive;
    @FXML private ListView<AdSummaryDto> pendingList;
    @FXML private ListView<UserDto> userList;

    @FXML
    private void initialize() {
        pendingList.setCellFactory(l -> new ListCell<>() {
            @Override protected void updateItem(AdSummaryDto ad, boolean empty) {
                super.updateItem(ad, empty);
                setGraphic(empty || ad == null ? null : pendingRow(ad));
            }
        });
        userList.setCellFactory(l -> new ListCell<>() {
            @Override protected void updateItem(UserDto u, boolean empty) {
                super.updateItem(u, empty);
                setGraphic(empty || u == null ? null : userRow(u));
            }
        });
        refreshAll();
    }

    private void refreshAll() {
        loadStats();
        loadPending();
        loadUsers();
    }

    private void loadStats() {
        try {
            StatsDto s = AdminApi.stats();
            statUsers.setText("کاربران: " + s.userCount());
            statAds.setText("کل آگهی: " + s.adCount());
            statPending.setText("در انتظار: " + s.pendingCount());
            statActive.setText("فعال: " + s.activeCount());
        } catch (ApiClientException e) {
            Alerts.error(e.getMessage());
        }
    }

    private void loadPending() {
        try {
            List<AdSummaryDto> ads = AdminApi.pendingAds();
            pendingList.setItems(FXCollections.observableArrayList(ads));
            if (ads.isEmpty()) pendingList.setPlaceholder(new Label("آگهی در انتظاری نیست"));
        } catch (ApiClientException e) {
            Alerts.error(e.getMessage());
        }
    }

    private void loadUsers() {
        try {
            List<UserDto> users = AdminApi.users();
            userList.setItems(FXCollections.observableArrayList(users));
        } catch (ApiClientException e) {
            Alerts.error(e.getMessage());
        }
    }

    private Pane pendingRow(AdSummaryDto ad) {
        Label title = new Label(ad.title());
        title.setStyle("-fx-font-weight:bold;");
        Label meta = new Label(ad.typeLabel() + " • " + ad.cityName() + " • " + ad.priceLabel());
        meta.setStyle("-fx-text-fill:#6b7280;");
        VBox info = new VBox(2, title, meta);
        Pane spacer = new Pane();
        Button approve = new Button("تأیید");
        approve.getStyleClass().add("primary");
        approve.setOnAction(e -> act(() -> AdminApi.approve(ad.id()), "آگهی تأیید شد"));
        Button reject = new Button("رد");
        reject.getStyleClass().add("danger");
        reject.setOnAction(e -> act(() -> AdminApi.reject(ad.id()), "آگهی رد شد"));
        HBox row = new HBox(10, info, spacer, approve, reject);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("card");
        return row;
    }

    private Pane userRow(UserDto u) {
        boolean blocked = "BLOCKED".equals(u.status());
        boolean admin = "ADMIN".equals(u.role());
        Label name = new Label(u.fullName() + "  (@" + u.username() + ")");
        name.setStyle("-fx-font-weight:bold;");
        Label meta = new Label(u.phone() + " • نقش: " + (admin ? "ادمین" : "کاربر")
                + " • وضعیت: " + (blocked ? "مسدود" : "فعال"));
        meta.setStyle("-fx-text-fill:#6b7280;");
        VBox info = new VBox(2, name, meta);
        Pane spacer = new Pane();
        HBox row = new HBox(10, info, spacer);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("card");
        if (!admin) {
            Button toggle = new Button(blocked ? "رفع مسدودی" : "مسدودسازی");
            if (!blocked) toggle.getStyleClass().add("danger");
            toggle.setOnAction(e -> act(
                    () -> { if (blocked) AdminApi.unblock(u.id()); else AdminApi.block(u.id()); },
                    blocked ? "کاربر فعال شد" : "کاربر مسدود شد"));
            row.getChildren().add(toggle);
        }
        return row;
    }

    private void act(Runnable action, String successMessage) {
        try {
            action.run();
            Alerts.info(successMessage);
            refreshAll();
        } catch (ApiClientException e) {
            Alerts.error(e.getMessage());
        }
    }

    @FXML private void onRefresh() { refreshAll(); }
    @FXML private void onBack() { SceneManager.switchTo("ad-list.fxml"); }
}
