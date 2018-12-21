package client.view;

import client.Client;
import client.gameService.GameService;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    private Client client;

    @FXML
    private Button offlineMode;
    @FXML
    private TextField account;
    @FXML
    private PasswordField password;
    @FXML
    private Label emptyAccountTips;
    @FXML
    private Label emptyPasswordTips;
    @FXML
    private Label invaildMessageTips;
    @FXML
    private AnchorPane loginPane;

    public LoginController() {

    }


    public void initialize(URL location, ResourceBundle resources) {
        emptyAccountTips.setVisible(false);
        emptyPasswordTips.setVisible(false);
        invaildMessageTips.setVisible(false);
    }

    @FXML
    private void login() throws InterruptedException {
        GameService service = client.getService();
        String nameStr = account.getText();
        String passwordStr = password.getText();
        client.setUserName(nameStr);
        boolean success = service.login(nameStr,passwordStr);
        if(success){
            client.gotoLobby();
        }else{
            client.getLoginController().logInFail();
        }
    }

    @FXML
    private void signup() throws IOException {
        client.gotoSignup();
    }

    public void logInFail(){
        invaildMessageTips.setText("登录失败");
    }

    @FXML
    private void setTipsError(Label tip, String msg) {
        tip.setVisible(true);
        tip.setTextFill(Color.RED);
        tip.setText(msg);
    }

    @FXML
    public void resetAccount() {
        account.setText("");
    }

    @FXML
    public void resetPassword() {
        password.setText("");
    }

    @FXML
    public void clearTip() {
        setTipsError(invaildMessageTips, "");
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
