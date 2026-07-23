package com.ap.marketplace.client.controller;

import com.ap.marketplace.client.SceneManager;
import com.ap.marketplace.client.Session;
import com.ap.marketplace.client.api.*;
import com.ap.marketplace.client.api.dto.*;
import com.ap.marketplace.client.util.Alerts;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdListController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> typeCombo;
    @FXML private ComboBox<CategoryDto> categoryCombo;
    @FXML private ComboBox<CityDto> cityCombo;
    @FXML private TextField minPriceField;
    @FXML private TextField maxPriceField;
    @FXML private ComboBox<String> sortCombo;
    @FXML private ListView<AdSummaryDto> adList;
    @FXML private Button adminButton;
    @FXML private Label pageLabel;

    private int page = 0;
    private int totalPages = 1;

    private static final Map<String, String> TYPE_MAP = Map.of(
            "همه انواع", "", "خودرو", "VEHICLE", "املاک", "PROPERTY", "عمومی", "GENERAL");
    private static final Map<String, String> SORT_MAP = Map.of(
            "جدیدترین", "newest", "ارزان‌ترین", "price_asc",
            "گران‌ترین", "price_desc", "بیشترین امتیاز", "rating");

    @FXML
    private void initialize() {
        adminButton.setVisible(Session.get().isAdmin());
        adminButton.setManaged(Session.get().isAdmin());

        typeCombo.setItems(FXCollections.observableArrayList(
                "همه انواع", "خودرو", "املاک", "عمومی"));
        typeCombo.getSelectionModel().selectFirst();
        sortCombo.setItems(FXCollections.observableArrayList(
                "جدیدترین", "ارزان‌ترین", "گران‌ترین", "بیشترین امتیاز"));
        sortCombo.getSelectionModel().selectFirst();

        loadLookups();
        configureCells();
        loadAds();
    }

    private void loadLookups() {
        try {
            List<CategoryDto> cats = new ArrayList<>();
            cats.add(new CategoryDto(null, "همه دسته‌ها", null));
            cats.addAll(LookupApi.categories());
            categoryCombo.setItems(FXCollections.observableArrayList(cats));
            categoryCombo.getSelectionModel().selectFirst();

            List<CityDto> cities = new ArrayList<>();
            cities.add(new CityDto(null, "همه شهرها"));
            cities.addAll(LookupApi.cities());
            cityCombo.setItems(FXCollections.observableArrayList(cities));
            cityCombo.getSelectionModel().selectFirst();
        } catch (ApiClientException e) {
            Alerts.error(e.getMessage());
        }
    }

    private void configureCells() {
        adList.setCellFactory(list -> new ListCell<>() {
            @Override protected void updateItem(AdSummaryDto ad, boolean empty) {
                super.updateItem(ad, empty);
                if (empty || ad == null) { setGraphic(null); return; }
                setGraphic(renderCard(ad));
            }
        });
        adList.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) openSelected();
        });
    }

    private Pane renderCard(AdSummaryDto ad) {
        Label title = new Label(ad.title());
        title.setStyle("-fx-font-weight:bold; -fx-font-size:15px;");
        Label type = new Label(ad.typeLabel());
        type.getStyleClass().add("badge");
        Label price = new Label(ad.priceLabel());
        price.getStyleClass().add("price");
        Label meta = new Label(ad.cityName() + " • " + ad.categoryName()
                + (ad.typeSummary() == null || ad.typeSummary().isBlank() ? "" : " • " + ad.typeSummary()));
        meta.setStyle("-fx-text-fill:#6b7280;");
        Label rating = new Label(String.format("⭐ %.1f", ad.averageRating()));

        HBox topRow = new HBox(8, title, type, new Pane(), price);
        HBox.setHgrow(topRow.getChildren().get(2), javafx.scene.layout.Priority.ALWAYS);
        HBox bottomRow = new HBox(12, meta, rating);
        VBox card = new VBox(4, topRow, bottomRow);
        card.getStyleClass().add("card");
        card.setPrefWidth(880);
        return card;
    }

    @FXML
    private void onSearch() { page = 0; loadAds(); }

    @FXML
    private void onReset() {
        searchField.clear();
        minPriceField.clear();
        maxPriceField.clear();
        typeCombo.getSelectionModel().selectFirst();
        sortCombo.getSelectionModel().selectFirst();
        categoryCombo.getSelectionModel().selectFirst();
        cityCombo.getSelectionModel().selectFirst();
        page = 0;
        loadAds();
    }

    private void loadAds() {
        try {
            String type = TYPE_MAP.get(typeCombo.getValue());
            String sort = SORT_MAP.get(sortCombo.getValue());
            CategoryDto cat = categoryCombo.getValue();
            CityDto city = cityCombo.getValue();
            Long categoryId = cat == null ? null : cat.id();
            Long cityId = city == null ? null : city.id();
            Long minP = parseLong(minPriceField.getText());
            Long maxP = parseLong(maxPriceField.getText());

            PageDto<AdSummaryDto> result = AdApi.search(searchField.getText(),
                    type == null || type.isBlank() ? null : type,
                    categoryId, cityId, minP, maxP, sort, page);

            adList.setItems(FXCollections.observableArrayList(result.content()));
            totalPages = Math.max(result.totalPages(), 1);
            pageLabel.setText("صفحه " + (page + 1) + " از " + totalPages);
            if (result.content().isEmpty()) {
                adList.setPlaceholder(new Label("آگهی‌ای یافت نشد"));
            }
        } catch (ApiClientException e) {
            Alerts.error(e.getMessage());
        }
    }

    private void openSelected() {
        AdSummaryDto selected = adList.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        Long id = selected.id();
        SceneManager.switchTo("ad-detail.fxml",
                (AdDetailController c) -> c.load(id));
    }

    @FXML private void onNextPage() { if (page + 1 < totalPages) { page++; loadAds(); } }
    @FXML private void onPrevPage() { if (page > 0) { page--; loadAds(); } }

    @FXML private void onCreate() { SceneManager.switchTo("create-ad.fxml"); }
    @FXML private void onMine() {
        SceneManager.switchTo("collection.fxml", (CollectionController c) -> c.loadMine());
    }
    @FXML private void onFavorites() {
        SceneManager.switchTo("collection.fxml", (CollectionController c) -> c.loadFavorites());
    }
    @FXML private void onChats() { SceneManager.switchTo("chat.fxml"); }
    @FXML private void onAdmin() { SceneManager.switchTo("admin.fxml"); }
    @FXML private void onLogout() {
        Session.get().logout();
        SceneManager.switchTo("login.fxml");
    }

    private static Long parseLong(String s) {
        if (s == null || s.isBlank()) return null;
        try { return Long.parseLong(s.trim().replaceAll("[,،\\s]", "")); }
        catch (NumberFormatException e) { return null; }
    }
}
