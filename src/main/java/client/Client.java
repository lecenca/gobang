package client;

import client.gameService.GameService;
import client.view.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class Client extends Application {

    public static Client client;

    private Stage loginStage;
    private Stage lobbyStage;
    private Stage createRoomStage;
    private Stage gameStage;
    private Stage signUpStage;

    private LoginController loginController;
    private LobbyController lobbyController;
    private GameController gameController;
    private ChessBoard chessBoard;
    private ChatBox chatBox;

    private GameService service;

    private int state;

    private String userName;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Client.client = this;

        service = new GameService(this);
        service.init();
        gotoLogin();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void gotoLogin() throws IOException {
        if(state==SIGN_UP)
            signUpStage.close();
        if(state==LOBBY)
            lobbyStage.close();
        state = LOGIN;

        loginStage = new Stage();
        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/Login.fxml"));
        Parent root = loginLoader.load();
        loginStage.setScene(new Scene(root));
        loginController = loginLoader.getController();

        loginController.setClient(this);
        loginController.resetAccount();
        loginController.resetPassword();
        loginStage.show();
    }

    public void gotoLobby() {
        state = LOBBY;
        loginController = null;
        loginStage.close();
        try {
            lobbyStage = new Stage();
            lobbyStage.setTitle("MicroOnlineGo - 大厅");
            lobbyStage.setResizable(false);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Lobby.fxml"));
            Parent root = loader.load();
            lobbyStage.setScene(new Scene(root));
            lobbyController = loader.getController();
            lobbyController.setClient(this);
            lobbyStage.show();

            Room[] waitingRoomList = service.requireWaitingRoomList();
            lobbyController.showRoomList(waitingRoomList);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void gotoCreateRoom() {
        state = CREATE_ROOM;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CreateRoom.fxml"));
            Parent root = loader.load();
            createRoomStage = new Stage();
            createRoomStage.setTitle("MicroOnlineGo - 创建房间");
            createRoomStage.setResizable(false);
            createRoomStage.setScene(new Scene(root));
            CreateRoomController createRoomController = loader.getController();
            createRoomController.setClient(this);
            createRoomStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void backToLobby() {
        if (state == CREATE_ROOM) {
            createRoomStage.close();
        } else if (state == GAME) {
            gameStage.close();
            gotoLobby();
        }
    }

    public void gotoGame(Room room) throws IOException {
        if (state == LOBBY) {
            lobbyStage.close();
        } else if (state == CREATE_ROOM) {
            createRoomStage.close();
            lobbyStage.close();
        }
        state = GAME;

        gameStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Game.fxml"));
        Parent root = loader.load();
        gameController = loader.getController();
        gameController.updateRoom(room);
        gameStage.setScene(new Scene(root));
        gameStage.setTitle("MicroOnlineGo - 房间 " + room.getName());
        gameStage.show();

    }

    public void gotoSignup() throws IOException {
        state = SIGN_UP;
        loginStage.close();
        signUpStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Signup.fxml"));
        Parent root = loader.load();
        signUpStage.setScene(new Scene(root));
        signUpStage.show();
    }

    public GameService getService() {
        return service;
    }

    public LoginController getLoginController() {
        return loginController;
    }

    public LobbyController getLobbyController() {
        return lobbyController;
    }

    public GameController getGameController() {
        return gameController;
    }

    public ChessBoard getChessBoard() {
        return chessBoard;
    }

    public void setChessBoard(ChessBoard chessBoard) {
        this.chessBoard = chessBoard;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ChatBox getChatBox() {
        return chatBox;
    }

    public void setChatBox(ChatBox chatBox) {
        this.chatBox = chatBox;
    }

    private static final int LOGIN = 1;
    private static final int LOBBY = 2;
    private static final int CREATE_ROOM = 3;
    private static final int GAME = 4;
    private static final int SIGN_UP = 5;
}
