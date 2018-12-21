package client.view;

import client.Client;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SignupController implements Initializable {

    private Client client;

    @FXML
    private TextField account;
    @FXML
    private PasswordField password;
    @FXML
    private PasswordField repeatPassword;

    // Hint tag
    @FXML
    private Label accountFormatTips;
    @FXML
    private Label passwordFormatTips;
    @FXML
    private Label repeatPasswordFormatTips;

    @FXML
    private AnchorPane signupPane;

    @FXML
    private void signup() throws InterruptedException, IOException {

        boolean validInfo = synchronousCheck();
        if (validInfo) {
            String ac = this.account.getText();
            String pw = this.password.getText();
            boolean success = client.getService().signUp(ac, pw);
            if (success) {
                client.gotoLogin();
            } else {
                accountFormatTips.setText("用户名重复");
            }
        }
    }

    @FXML
    private boolean synchronousCheck() {
        if (!checkAccountValid())
            return false;
        if (!checkPasswordValid())
            return false;
        if (!checkRepeatPasswordValid())
            return false;
        return true;
    }

    @FXML
    public boolean checkAccountValid() {
        String text = this.account.getText();
        if (text.isEmpty() || text == null || "".equals(text)) {
            setTipsError(accountFormatTips, "账号不能为空");
            return false;
        }
        if (!keyPressCheckValid()) {
            return false;
        }
        setTipsOk(accountFormatTips);
        return true;
    }

    @FXML
    private boolean keyPressCheckValid() {
        String text = this.account.getText();
        String regex = "[\\w]+";
        int length = text.length();
        if (!(text.matches(regex))) {
            setTipsError(accountFormatTips, "含有非法字符");
            return false;
        }
        if (!(length >= 6 && length <= 16)) {
            setTipsError(accountFormatTips, "长度为6-16个字符");
            return false;
        }
        setTipsOk(accountFormatTips);
        return true;
    }

    @FXML
    private boolean checkPasswordValid() {

        if (password.getText().isEmpty()) {
            setTipsError(passwordFormatTips, "密码不能为空");
            passwordFormatTips.setVisible(true);
            return false;
        }
        repeatPasswordFormatTips.setVisible(false);
        if (password.getText().isEmpty() || password.getText() == null || "".equals(password.getText())) {
            passwordFormatTips.setVisible(false);
            repeatPassword.setText("");
            repeatPassword.setDisable(true);
            repeatPasswordFormatTips.setVisible(false);
            return false;
        }
        if (password.getText().length() < 6) {
            setTipsError(passwordFormatTips, "密码至少6位");
            repeatPassword.setText("");
            repeatPassword.setDisable(true);
            repeatPasswordFormatTips.setVisible(false);
            return false;
        } else if (password.getText().length() > 16) {
            setTipsError(passwordFormatTips, "密码不超过16位");
            repeatPassword.setText("");
            repeatPassword.setDisable(true);
            repeatPasswordFormatTips.setVisible(false);
            return false;
        } else if (!password.getText().matches("\\w+")) {
            setTipsError(passwordFormatTips, "含有非法字符");
            repeatPassword.setText("");
            repeatPassword.setDisable(true);
            repeatPasswordFormatTips.setVisible(false);
            return false;
        }

        setTipsOk(passwordFormatTips);
        repeatPassword.setDisable(false);
        return true;
    }

    @FXML
    private boolean checkRepeatPasswordValid() {
        String passwordText = repeatPassword.getText();
        if (passwordText.isEmpty() || passwordText == null) {
            setTipsError(repeatPasswordFormatTips, "请确认密码");
            passwordFormatTips.setVisible(true);
            return false;
        }
        if (!password.getText().isEmpty() && !passwordText.equals(password.getText())) {
            setTipsError(repeatPasswordFormatTips, "两次密码不一致");
            return false;
        }

        setTipsOk(repeatPasswordFormatTips);
        return true;
    }


    @FXML
    private void setTipsOk(Label tip) {
        tip.setVisible(true);
        tip.setTextFill(Color.GREEN);
        tip.setText("✔");
    }

    @FXML
    private void setTipsError(Label tip, String msg) {
        tip.setVisible(true);
        tip.setTextFill(Color.RED);
        tip.setText(msg);
    }

    @FXML
    private void backToLogin() throws IOException {
        client.gotoLogin();
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @FXML
    public void clearAcountTip() {
        accountFormatTips.setText("");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        account.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {

            } else {
                checkAccountValid();
            }
        });
        password.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {

            } else {
                checkPasswordValid();
            }

        });
        repeatPassword.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {

            } else {
                checkRepeatPasswordValid();
            }

        });
        repeatPassword.setDisable(true);
        accountFormatTips.setVisible(false);
        passwordFormatTips.setVisible(false);
        repeatPasswordFormatTips.setVisible(false);

        this.client = Client.client;
    }
}
