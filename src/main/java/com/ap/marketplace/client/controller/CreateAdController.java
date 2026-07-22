package com.ap.marketplace.client.controller;

import com.ap.marketplace.client.SceneManager;
import com.ap.marketplace.client.api.AdApi;
import com.ap.marketplace.client.api.ApiClientException;
import com.ap.marketplace.client.api.LookupApi;
import com.ap.marketplace.client.api.dto.AdDetailDto;
import com.ap.marketplace.client.api.dto.CategoryDto;
import com.ap.marketplace.client.api.dto.CityDto;
import com.ap.marketplace.client.util.Alerts;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.*;

public class CreateAdController {

    @FXML private ComboBox<String> typeCombo;
    @FXML private TextField titleField, priceField;
    @FXML private TextArea descriptionArea, imagesArea;
    @FXML private ComboBox<CategoryDto> categoryCombo;
    @FXML private ComboBox<CityDto> cityCombo;
    @FXML private VBox vehicleBox, propertyBox, generalBox;
    @FXML private TextField brandField, modelField, yearField, mileageField;
    @FXML private TextField areaField, roomsField, addressField, conditionField;
    @FXML private CheckBox forRentCheck;

    private static final Map<String, String> TYPE_MAP = new LinkedHashMap<>();
    static {
        TYPE_MAP.put("خودرو", "VEHICLE");
        TYPE_MAP.put("املاک", "PROPERTY");
        TYPE_MAP.put("عمومی", "GENERAL");
    }

    @FXML
    private void initialize() {
        typeCombo.setItems(FXCollections.observableArrayList(TYPE_MAP.keySet()));
        typeCombo.getSelectionModel().selectFirst();
        typeCombo.valueProperty().addListener((o, a, b) -> refreshTypeBoxes());
        try {
            categoryCombo.setItems(FXCollections.observableArrayList(LookupApi.categories()));
            cityCombo.setItems(FXCollections.observableArrayList(LookupApi.cities()));
        } catch (ApiClientException e) {
            Alerts.error(e.getMessage());
        }
        refreshTypeBoxes();
    }

    private void refreshTypeBoxes() {
        String type = TYPE_MAP.get(typeCombo.getValue());
        toggle(vehicleBox, "VEHICLE".equals(type));
        toggle(propertyBox, "PROPERTY".equals(type));
        toggle(generalBox, "GENERAL".equals(type));
    }

    private void toggle(VBox box, boolean visible) {
        box.setVisible(visible);
        box.setManaged(visible);
    }

    @FXML
    private void onSubmit() {
        String title = titleField.getText() == null ? "" : titleField.getText().trim();
        String description = descriptionArea.getText() == null ? "" : descriptionArea.getText().trim();
        Long price = parseLong(priceField.getText());
        CategoryDto category = categoryCombo.getValue();
        CityDto city = cityCombo.getValue();
        String type = TYPE_MAP.get(typeCombo.getValue());

        if (title.isEmpty() || description.isEmpty() || price == null || category == null || city == null) {
            Alerts.error("عنوان، توضیحات، قیمت، دسته و شهر را کامل کنید");
            return;
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("type", type);
        body.put("title", title);
        body.put("description", description);
        body.put("price", price);
        body.put("categoryId", category.id());
        body.put("cityId", city.id());
        body.put("imageUrls", imageUrls());

        switch (type) {
            case "VEHICLE" -> body.put("vehicle", map(
                    "brand", nz(brandField), "model", nz(modelField),
                    "productionYear", parseInt(yearField.getText()),
                    "mileageKm", parseInt(mileageField.getText())));
            case "PROPERTY" -> body.put("property", map(
                    "areaSqm", parseInt(areaField.getText()),
                    "rooms", parseInt(roomsField.getText()),
                    "address", nz(addressField),
                    "forRent", forRentCheck.isSelected()));
            case "GENERAL" -> body.put("general", map("itemCondition", nz(conditionField)));
        }

        try {
            AdDetailDto created = AdApi.create(body);
            Alerts.info("آگهی «" + created.title() + "» ثبت شد و در انتظار تأیید است.");
            SceneManager.switchTo("ad-list.fxml");
        } catch (ApiClientException e) {
            Alerts.error(e.getMessage());
        }
    }

    private List<String> imageUrls() {
        if (imagesArea.getText() == null || imagesArea.getText().isBlank()) return List.of();
        List<String> urls = new ArrayList<>();
        for (String line : imagesArea.getText().split("\\R")) {
            if (!line.isBlank()) urls.add(line.trim());
        }
        return urls;
    }

    private static Map<String, Object> map(Object... kv) {
        Map<String, Object> m = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) m.put((String) kv[i], kv[i + 1]);
        return m;
    }

    private static String nz(TextField f) {
        String s = f.getText();
        return s == null || s.isBlank() ? null : s.trim();
    }

    private static Long parseLong(String s) {
        if (s == null || s.isBlank()) return null;
        try { return Long.parseLong(s.trim().replaceAll("[,،\\s]", "")); }
        catch (NumberFormatException e) { return null; }
    }

    private static Integer parseInt(String s) {
        if (s == null || s.isBlank()) return null;
        try { return Integer.parseInt(s.trim().replaceAll("[,،\\s]", "")); }
        catch (NumberFormatException e) { return null; }
    }

    @FXML private void onBack() { SceneManager.switchTo("ad-list.fxml"); }
}
