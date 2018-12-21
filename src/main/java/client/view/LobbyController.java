package client.view;

import client.Client;
import client.Room;
//import client.User;
import client.gameService.EnterRoomResult;
import client.gameService.GameService;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LobbyController implements Initializable {

    private Client client;

    @FXML
    private Button createRoomBtn;
    @FXML
    private TableView<Room> roomList;
    @FXML
    private TableColumn<Room, String> roomNameCol;
    @FXML
    private TableColumn<Room, String> player1Col;
    @FXML
    private TableColumn<Room, String> player2Col;

    private ObservableList<Room> roomData = FXCollections.observableArrayList();

    @FXML
    private void logout() throws IOException, InterruptedException {
        client.getService().logout();
        client.gotoLogin();
    }

    @FXML
    private void gotoCreateRoom() {
        client.gotoCreateRoom();
    }

    @FXML
    private void clickRoom(MouseEvent mouseEvent) throws InterruptedException, IOException {
        if (mouseEvent.getClickCount() == 2) {
            Room room = roomList.getSelectionModel().getSelectedItem();
            GameService service = client.getService();
            EnterRoomResult result = service.enterRoom(room);
            room.enter(client.getUserName());
            if(result.success){
                room.setState(result.roomState);
                client.gotoGame(room);
            }else{
                //todo
            }
        }
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAssociation();
    }

    public void setAssociation() {
        roomList.setItems(roomData);
        roomNameCol.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        player1Col.setCellValueFactory(cellData -> cellData.getValue().getPlayer1Property());
        player2Col.setCellValueFactory(cellData -> cellData.getValue().getPlayer2Property());
    }

    public void addRoom(Room room) {
        this.roomList.getItems().add(room);
    }

    public ObservableList<Room> getRoomData() {
        return roomData;
    }

    public void showRoomList(Room[] waitingRoomList) {
        for(Room room: waitingRoomList){
            this.roomList.getItems().add(room);
        }
    }

    public void removeRoom(String roomName) {
        for(Room room: roomData){
            if(room.getName().equals(roomName)){
                roomData.remove(room);
                return;
            }
        }
    }

}
