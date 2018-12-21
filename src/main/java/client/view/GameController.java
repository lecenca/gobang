package client.view;

import client.Client;
import client.Room;
import client.Stone;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import util.MessageGenerate;


import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class GameController implements Initializable {

    private Room room;

    private boolean roomOwner; //true为player1，false为player2
    private boolean begin;
    private int turn;

    // Pane
    @FXML
    private AnchorPane gamePane;
    @FXML
    private HBox playerPane;
    @FXML
    private AnchorPane scorePane;

    // Player information label
    @FXML
    private Label player1Name;
    @FXML
    private Label player2Name;

    // Control button
    @FXML
    private ToggleButton ready;
    @FXML
    private Button surrender;
    @FXML
    private ToggleButton axis;

    // Chat windows
    @FXML
    private ChatBox chatBoxController;
    @FXML
    private ListView<String> chatBox;
    @FXML
    private TextField inputField;
    @FXML
    private Button send;


    @FXML
    private Label gameResultShow;
    @FXML
    private Label[] axisLable = new Label[38];

    private Client client;

    public GameController() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initAxis();
        hideAxis();
        gameResultShow.setVisible(false);

        Image image = new Image("/image/bg004.jpg", 1160, 700, false, true);
        BackgroundSize backgroundSize = new BackgroundSize(1161, 700, true, true, true, false);
        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        Background background = new Background(backgroundImage);
        gamePane.setBackground(background);

        Image image2 = new Image("/image/bg014.jpg", 420, 200, false, true);
        BackgroundSize backgroundSize2 = new BackgroundSize(390, 200, true, true, true, false);
        BackgroundImage backgroundImage2 = new BackgroundImage(image2, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize2);
        Background background2 = new Background(backgroundImage2);
        playerPane.setBackground(background2);

        Image image3 = new Image("/image/bg013.jpg", 470, 125, false, true);
        BackgroundSize backgroundSize3 = new BackgroundSize(484, 123, true, true, true, false);
        BackgroundImage backgroundImage3 = new BackgroundImage(image3, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize3);
        Background background3 = new Background(backgroundImage3);
        scorePane.setBackground(background3);
        inputField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                String text = inputField.getText();
                if (text == null || "".equals(text) || text.length() == 0) {
                    send.setDisable(true);
                } else {
                    send.setDisable(false);
                    if (event.getCode() == KeyCode.ENTER) {
                        try {
                            chat();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        client = Client.client;
    }

    private void initAxis() {
        for (int i = 0; i < 38; ++i) {
            axisLable[i] = new Label();
            gamePane.getChildren().add(axisLable[i]);
        }
        for (int i = 1; i <= 19; ++i) {
            axisLable[i - 1].setText(String.valueOf(i));
            axisLable[i - 1].setTextFill(Color.color(0.9, 0.1, 0.1));
            axisLable[i - 1].setLayoutX(25 + i * 30);
            axisLable[i - 1].setLayoutY(612.0);
            axisLable[i - 1].setPrefSize(15, 15);
            axisLable[i - 1].setAlignment(Pos.BASELINE_CENTER);
        }
        for (int i = 0; i < 19; ++i) {
            axisLable[i + 19].setText(String.valueOf((char) ('A' + i)));
            axisLable[i + 19].setTextFill(Color.color(0.9, 0.1, 0.1));
            axisLable[i + 19].setLayoutX(38);
            axisLable[i + 19].setLayoutY(592 - i * 30);
            axisLable[i + 19].setPrefSize(15, 15);
            axisLable[i + 19].setAlignment(Pos.BASELINE_CENTER);
        }
    }

    private void hideAxis() {
        for (Label label : axisLable) {
            label.setVisible(false);
        }
    }

    private void showAxis() {
        for (Label label : axisLable) {
            label.setVisible(true);
        }
    }

    public void initRoom(Room room) {
    }

    @FXML
    private void ready() throws InterruptedException {
        gameResultShow.setVisible(false);
        if (ready.isSelected()) {
            client.getService().ready(true);
            room.setState(room.getState()+1);
            if(room.getState()==Room.GAMMING){
                gameStart();
            }else{
                ready.setText("取消准备");
            }
        } else {
            room.setState(room.getState()-1);
            client.getService().ready(false);
            ready.setText("准备");
        }
    }

    @FXML
    public void gameStart() {
        begin = true;
        turn = Stone.Black;
        ready.setText("游戏中");
        ready.setDisable(true);
        surrender.setDisable(false);
    }

    @FXML
    public void gameOver(boolean win) {
        begin = false;
        gameResultShow.setVisible(true);
        if(win){
            gameResultShow.setText("您胜出了");
        }else{
            gameResultShow.setText("您输了");
        }
        room.setState(Room.GAME_OVER);
    }

    @FXML
    private void surrender() throws InterruptedException {
        client.getService().out(room.getName());
        client.backToLobby();
    }

    public void setAnotherReady(boolean ready) {
        if(ready)
            room.setState(room.getState()+1);
        else
            room.setState(room.getState()-1);
        if(room.getState()==Room.GAMMING)
            gameStart();
    }

    public boolean isBegin() {
        return begin;
    }

    public boolean isRoomOwner() {
        return roomOwner;
    }

    public int getTurn() {
        return turn;
    }

    public void reverseTurn() {
        turn = -turn;
    }

    @FXML
    public void displayAxis() {
        if (axis.isSelected()) {
            showAxis();
        } else {
            hideAxis();
        }
    }

    @FXML
    private void chat() throws InterruptedException {
        if(room.getPlayer1()==null||room.getPlayer2()==null){
            inputField.setText("");
            return;
        }
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        String time = String.format(" (" + format.format(date) + "):");
        chatBoxController.sendMessage(client.getUserName() + time + inputField.getText());
        client.getService().chat(client.getUserName() + time + inputField.getText());
        inputField.clear();
        send.setDisable(true);
    }

    public ChatBox getChatBoxController() {
        return chatBoxController;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public void updateRoom(Room room) {
        this.room = room;
        if (room.getPlayer1() != null) {
            player1Name.setText(room.getPlayer1());
            if (room.getPlayer1().equals(client.getUserName())) {
                roomOwner = true;
                client.getChessBoard().setColor(Stone.Black);
            }
        } else {
            player1Name.setText("待加入");
        }
        if (room.getPlayer2() != null) {
            player2Name.setText(room.getPlayer2());
            if (room.getPlayer2().equals(client.getUserName())) {
                roomOwner = false;
                client.getChessBoard().setColor(Stone.White);
            }
        } else {
            player2Name.setText("待加入");
        }
    }

    public void newPlayer(String playerName) {
        if (room.getPlayer1() == null){
            room.setPlayer1(playerName);
            player1Name.setText(playerName);
        }
        if (room.getPlayer2() == null){
            room.setPlayer2(playerName);
            player2Name.setText(playerName);
        }
    }

    public void opponentOut() {
        if(room.getState()==Room.GAMMING){
            room.setState(Room.GAME_OVER);
            room.out(room.getAnotherPlayer(client.getUserName()));
            gameResultShow.setVisible(true);
            gameResultShow.setText("对手逃走了，你胜出了");
        }else{
            if(roomOwner){
                room.setPlayer2(null);
                player2Name.setText("等待加入");
            }else{
                room.setPlayer1(null);
                player1Name.setText("等待加入");
            }
            if(!ready.isSelected()&&room.getState()==Room.WAITING_2)
                room.setState(Room.WAITING_1);
        }
    }
}
