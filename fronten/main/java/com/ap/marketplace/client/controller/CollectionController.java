package com.ap.marketplace.client.controller;

import com.ap.marketplace.client.SceneManager;
import com.ap.marketplace.client.api.AdApi;
import com.ap.marketplace.client.api.ApiClientException;
import com.ap.marketplace.client.api.FavoriteApi;
import com.ap.marketplace.client.api.dto.AdDetailDto;
import com.ap.marketplace.client.api.dto.AdSummaryDto;
import com.ap.marketplace.client.util.Alerts;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Supplier;

/** صفحه لیست مشترک برای «آگهی‌های من» و «علاقه‌مندی‌ها». */
public class CollectionController {

    @FXML private Label titleLabel;
    @FXML private ListView<AdSummaryDto> adList;

    @FXML
    private void initialize() {
        adList.setCellFactory(l -> new ListCell<>() {
            @Override protected void updateItem(AdSummaryDto ad, boolean empty) {
                super.updateItem(ad, empty);
                setGraphic(empty || ad == null ? null : card(ad));
            }
        });
        adList.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) open();
        });
    }

    public void loadMine() {
        titleLabel.setText("آگهی‌های من");
        fill(AdApi::mine);
    }

    public void loadFavorites() {
        titleLabel.setText("علاقه‌مندی‌های من");
        fill(FavoriteApi::list);
    }

    private void fill(Supplier<List<AdSummaryDto>> source) {
        try {
            List<AdSummaryDto> items = source.get();
            adList.setItems(FXCollections.observableArrayList(items));
            if (items.isEmpty()) adList.setPlaceholder(new Label("موردی وجود ندارد"));
        } catch (ApiClientException e) {
            Alerts.error(e.getMessage());
        }
    }

    private Pane card(AdSummaryDto ad) {
        Label title = new Label(ad.title());
        title.setStyle("-fx-font-weight:bold; -fx-font-size:15px;");
        Label status = new Label(statusLabel(ad.status()));
        status.getStyleClass().add("badge");
        Label price = new Label(ad.priceLabel());
        price.getStyleClass().add("price");
        Label meta = new Label(ad.cityName() + " • " + ad.categoryName());
        meta.setStyle("-fx-text-fill:#6b7280;");
        Pane spacer = new Pane();
        HBox top = new HBox(8, title, status, spacer, price);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        VBox box = new VBox(4, top, meta);
        box.getStyleClass().add("card");
        box.setPrefWidth(880);
        return box;
    }

    private void open() {
        AdSummaryDto sel = adList.getSelectionModel().getSelectedItem();
        if (sel != null) SceneManager.switchTo("ad-detail.fxml", (AdDetailController c) -> c.load(sel.id()));
    }

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

    @FXML private void onBack() { SceneManager.switchTo("ad-list.fxml"); }
}
