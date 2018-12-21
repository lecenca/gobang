package client.view;

import client.Client;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class ChatBox implements Initializable {
    @FXML
    private ListView<String> chatBox;

    private Client client;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chatBox.setItems(FXCollections.<String>observableArrayList());
        client = Client.client;
        client.setChatBox(this);
    }

    public void clearMessage() {
        chatBox.getItems().clear();
    }

    public void sendMessage(String message) {
        chatBox.getItems().add(message);
        if (chatBox.getItems().size() > 20) {
            chatBox.getItems().remove(0);
        }
        chatBox.scrollTo(chatBox.getItems().size() - 1);
    }

    public void setItems(ObservableList<String> chatMessage) {
        chatBox.setItems(chatMessage);
    }

    public void clear() {
        chatBox.getItems().clear();
    }

    public ListView<String> getChatBox() {
        return chatBox;
    }

}
