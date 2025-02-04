package fi.tuni.prog3.sisu;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
 * This class is used to control login.fxml elements.
 * @author shuang.fan@tuni.fi
 */
public class LoginController {
    @FXML private Button loginBtn_cancel;
    @FXML private TextField inputName;
    @FXML private PasswordField inputPassword;
    @FXML private Label message;
    private static final String USERERROR = "The account doesn't exist! \nPlease check your user name or create a new account!";
    private static final String PSERROR = "The password is not correct! \nPlease check your password!";
    static String USER;
    static String ID;
    
    /**
     * Initialize the login view.
     * @throws Exception if there is any error
     */
    public void initialize() throws Exception{
        this.message.setText("");
    }

    @FXML
    private void cancel() {
        Stage stage = (Stage) loginBtn_cancel.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void login() throws IOException {
        if (!App.fileExists()) {
            this.message.setText(USERERROR);
            return;
        }
        
        var user = inputName.getText();
        var password = inputPassword.getText();
        var id = AccountsData.checkUser(user, password);
        
        switch (AccountsData.ErrorMessage) {
            case "":
                USER = user;
                ID = id;
                this.message.setText("Logging, please wait...");
                App.setRoot("main");
                break;
            case "user":
                this.message.setText(USERERROR);
                break;
            case "password":
                this.message.setText(PSERROR);
                break;
            default:
                this.message.setText(USERERROR);
        }
    }
    
    @FXML
    private void signIn() throws IOException {
        App.setRoot("signIn");
    }
    
    @FXML
    private void input(KeyEvent key) {
        this.message.setText("");
    }
}
