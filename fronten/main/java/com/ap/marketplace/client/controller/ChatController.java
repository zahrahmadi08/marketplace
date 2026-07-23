package com.ap.marketplace.client.controller;

import com.ap.marketplace.client.SceneManager;
import com.ap.marketplace.client.Session;
import com.ap.marketplace.client.api.ApiClientException;
import com.ap.marketplace.client.api.ChatApi;
import com.ap.marketplace.client.api.dto.ConversationDto;
import com.ap.marketplace.client.api.dto.MessageDto;
import com.ap.marketplace.client.util.Alerts;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.List;

public class ChatController {

    @FXML private ListView<ConversationDto> convList;
    @FXML private ListView<MessageDto> messageList;
    @FXML private TextField messageField;

    private Long currentConversationId;

    @FXML
    private void initialize() {
        convList.setCellFactory(l -> new ListCell<>() {
            @Override protected void updateItem(ConversationDto c, boolean empty) {
                super.updateItem(c, empty);
                if (empty || c == null) { setText(null); return; }
                String other = otherPartyName(c);
                String unread = c.unreadCount() > 0 ? "  (" + c.unreadCount() + ")" : "";
                String last = c.lastMessage() == null ? "" : "\n" + trim(c.lastMessage());
                setText(c.adTitle() + " — " + other + unread + last);
            }
        });
        messageList.setCellFactory(l -> new ListCell<>() {
            @Override protected void updateItem(MessageDto m, boolean empty) {
                super.updateItem(m, empty);
                if (empty || m == null) { setGraphic(null); return; }
                boolean mine = Session.get().user() != null
                        && m.senderId().equals(Session.get().user().id());
                Label bubble = new Label(m.text());
                bubble.setWrapText(true);
                bubble.setMaxWidth(360);
                bubble.setStyle(mine
                        ? "-fx-background-color:#dbeafe; -fx-padding:8; -fx-background-radius:10;"
                        : "-fx-background-color:#eef2f5; -fx-padding:8; -fx-background-radius:10;");
                setGraphic(bubble);
                setAlignment(mine ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
            }
        });
        convList.getSelectionModel().selectedItemProperty().addListener((o, a, b) -> {
            if (b != null) openConversation(b.id());
        });
        loadConversations();
    }

    private void loadConversations() {
        try {
            List<ConversationDto> convs = ChatApi.conversations();
            convList.setItems(FXCollections.observableArrayList(convs));
            if (convs.isEmpty()) convList.setPlaceholder(new Label("گفت‌وگویی وجود ندارد"));
        } catch (ApiClientException e) {
            Alerts.error(e.getMessage());
        }
    }

    /** ورودی از صفحه جزئیات: باز کردن مستقیم یک گفت‌وگو. */
    public void openConversation(Long conversationId) {
        this.currentConversationId = conversationId;
        try {
            List<MessageDto> messages = ChatApi.messages(conversationId);
            messageList.setItems(FXCollections.observableArrayList(messages));
            if (messages.isEmpty()) messageList.setPlaceholder(new Label("پیامی نیست؛ اولین پیام را بفرستید"));
            messageList.scrollTo(messages.size() - 1);
        } catch (ApiClientException e) {
            Alerts.error(e.getMessage());
        }
    }

    @FXML
    private void onSend() {
        if (currentConversationId == null) { Alerts.error("ابتدا یک گفت‌وگو را انتخاب کنید"); return; }
        String text = messageField.getText();
        if (text == null || text.isBlank()) return;
        try {
            ChatApi.send(currentConversationId, text.trim());
            messageField.clear();
            openConversation(currentConversationId);
        } catch (ApiClientException e) {
            Alerts.error(e.getMessage());
        }
    }

    @FXML private void onRefresh() {
        loadConversations();
        if (currentConversationId != null) openConversation(currentConversationId);
    }

    @FXML private void onBack() { SceneManager.switchTo("ad-list.fxml"); }

    private String otherPartyName(ConversationDto c) {
        Long myId = Session.get().user() == null ? null : Session.get().user().id();
        return c.buyerId().equals(myId) ? c.sellerName() : c.buyerName();
    }

    private static String trim(String s) {
        return s.length() > 40 ? s.substring(0, 40) + "…" : s;
    }
}
