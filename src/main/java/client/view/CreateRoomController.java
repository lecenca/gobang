package client.view;

import client.Client;
import client.Room;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;

public class CreateRoomController implements Initializable {

    private Client client;
    private TableView<Room> roomList;

    @FXML
    private TextField roomNameField;
    @FXML
    private Button createRoomBtn;
    @FXML
    private Button backBtn;
    @FXML
    private RadioButton takeBlack;
    @FXML
    private RadioButton takeWhite;

    private Set roomId = new HashSet<>();
    private Random random = new Random();

    private static String[] defaultName = {
            "我爱下围棋",
            "大家一起来下棋",
            "棋逢对手",
            "快来挑战我",
            "孤独求败"
    };

    public void setClient(Client client) {
        this.client = client;
    }

    @FXML
    private void createRoom() throws InterruptedException, IOException {
        Room room = new Room();
        String name = roomNameField.getText();
        if (name == null || name.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("提示");
            alert.setHeaderText(null);
            alert.setContentText("房间名称不能为空");
            alert.showAndWait();
            return;
        } else {
            room.setName(name);
        }
        if(takeWhite.isSelected()){
            room.setPlayer2(client.getUserName());
        }else{
            room.setPlayer1(client.getUserName());
        }
        room.setState(Room.WAITING_1);

        boolean success = client.getService().createRoom(room);
        if(success){
            client.gotoGame(room);
        }else{
            //todo
        }
    }

    @FXML
    private void back() {
        client.backToLobby();
    }

    public void setRoomList(TableView<Room> roomList) {
        this.roomList = roomList;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        roomNameField.setText(defaultName[random.nextInt(5) % 5]);
        ToggleGroup group = new ToggleGroup();
        takeWhite.setToggleGroup(group);
        takeBlack.setToggleGroup(group);
        takeBlack.setSelected(true);
    }
}
