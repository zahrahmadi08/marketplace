package com.ap.marketplace.client.controller;

import com.ap.marketplace.client.SceneManager;
import com.ap.marketplace.client.Session;
import com.ap.marketplace.client.api.*;
import com.ap.marketplace.client.api.dto.*;
import com.ap.marketplace.client.util.Alerts;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class AdDetailController {

    @FXML private Label titleLabel, priceLabel, metaLabel, typeSummaryLabel,
            descriptionLabel, sellerLabel, typeBadge, statusBadge;
    @FXML private Button favoriteButton, chatButton, soldButton, deleteButton;
    @FXML private ComboBox<Integer> scoreCombo;
    @FXML private TextField commentField;
    @FXML private ListView<RatingDto> ratingList;

    private Long adId;
    private AdDetailDto ad;
    private boolean isFavorite = false;

    @FXML
    private void initialize() {
        scoreCombo.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5));
        ratingList.setCellFactory(l -> new ListCell<>() {
            @Override protected void updateItem(RatingDto r, boolean empty) {
                super.updateItem(r, empty);
                if (empty || r == null) { setText(null); return; }
                String comment = r.comment() == null || r.comment().isBlank() ? "" : " — " + r.comment();
                setText("⭐".repeat(r.score()) + "  " + r.raterName() + comment);
            }
        });
    }

    /** ورودی از صفحه لیست: id آگهی. */
    public void load(Long id) {
        this.adId = id;
        try {
            ad = AdApi.detail(id);
            render();
            loadRatings();
            detectFavorite();
        } catch (ApiClientException e) {
            Alerts.error(e.getMessage());
        }
    }

    private void render() {
        titleLabel.setText(ad.title());
        typeBadge.setText(ad.typeLabel());
        statusBadge.setText(statusLabel(ad.status()));
        priceLabel.setText(ad.priceLabel());
        metaLabel.setText(ad.cityName() + " • " + ad.categoryName());
        typeSummaryLabel.setText(ad.typeSummary());
        descriptionLabel.setText(ad.description());
        sellerLabel.setText(ad.owner().fullName() + " — " + ad.owner().phone());

        boolean owner = Session.get().user() != null
                && ad.owner().id().equals(Session.get().user().id());
        // عملیات مالک فقط برای مالک؛ چت/علاقه/امتیاز فقط برای غیرمالک.
        soldButton.setVisible(owner); soldButton.setManaged(owner);
        deleteButton.setVisible(owner); deleteButton.setManaged(owner);
        chatButton.setVisible(!owner); chatButton.setManaged(!owner);
        favoriteButton.setVisible(!owner); favoriteButton.setManaged(!owner);
    }

    private void loadRatings() {
        try {
            List<RatingDto> ratings = RatingApi.list(adId);
            ratingList.setItems(FXCollections.observableArrayList(ratings));
            if (ratings.isEmpty()) ratingList.setPlaceholder(new Label("هنوز امتیازی ثبت نشده"));
        } catch (ApiClientException e) {
            Alerts.error(e.getMessage());
        }
    }

    private void detectFavorite() {
        try {
            isFavorite = FavoriteApi.list().stream().anyMatch(a -> a.id().equals(adId));
            favoriteButton.setText(isFavorite ? "حذف از علاقه‌مندی" : "افزودن به علاقه‌مندی");
        } catch (ApiClientException ignored) { }
    }

    @FXML
    private void onToggleFavorite() {
        try {
            if (isFavorite) FavoriteApi.remove(adId);
            else FavoriteApi.add(adId);
            isFavorite = !isFavorite;
            favoriteButton.setText(isFavorite ? "حذف از علاقه‌مندی" : "افزودن به علاقه‌مندی");
        } catch (ApiClientException e) {
            Alerts.error(e.getMessage());
        }
    }

    @FXML
    private void onChat() {
        try {
            ConversationDto conv = ChatApi.start(adId);
            SceneManager.switchTo("chat.fxml", (ChatController c) -> c.openConversation(conv.id()));
        } catch (ApiClientException e) {
            Alerts.error(e.getMessage());
        }
    }

    @FXML
    private void onMarkSold() {
        try {
            AdApi.markSold(adId);
            Alerts.info("آگهی فروخته‌شده شد");
            load(adId);
        } catch (ApiClientException e) {
            Alerts.error(e.getMessage());
        }
    }

    @FXML
    private void onDelete() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "آیا از حذف این آگهی مطمئن‌اید؟",
                ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.getDialogPane().setNodeOrientation(javafx.geometry.NodeOrientation.RIGHT_TO_LEFT);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                try {
                    AdApi.delete(adId);
                    Alerts.info("آگهی حذف شد");
                    SceneManager.switchTo("ad-list.fxml");
                } catch (ApiClientException e) {
                    Alerts.error(e.getMessage());
                }
            }
        });
    }

    @FXML
    private void onRate() {
        Integer score = scoreCombo.getValue();
        if (score == null) { Alerts.error("امتیاز را انتخاب کنید"); return; }
        try {
            RatingApi.rate(adId, score, commentField.getText());
            commentField.clear();
            scoreCombo.getSelectionModel().clearSelection();
            Alerts.info("امتیاز ثبت شد");
            loadRatings();
        } catch (ApiClientException e) {
            Alerts.error(e.getMessage());
        }
    }

    @FXML private void onBack() { SceneManager.switchTo("ad-list.fxml"); }

    private static String statusLabel(String status) {
        return switch (status) {
            case "PENDING" -> "در انتظار بررسی";
            case "ACTIVE" -> "فعال";
            case "REJECTED" -> "ردشده";
            case "SOLD" -> "فروخته‌شده";
            case "DELETED" -> "حذف‌شده";
            default -> status;
        };
    }
}
