package fi.tuni.prog3.sisu;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;

/**
 * This class is used to control signIn.fxml elements.
 * @author shuang.fan@tuni.fi
 */
public class SignInController {
    @FXML private Button back;
    @FXML private Button signIn;
    @FXML private TextField studentNum;
    @FXML private TextField newUser;
    @FXML private PasswordField password1;
    @FXML private PasswordField password2;
    @FXML private TextArea messages;
    @FXML private ImageView studentNum_image;
    @FXML private ImageView newUser_image;
    @FXML private ImageView password1_image;
    @FXML private ImageView password2_image;
    // icons from https://icons8.com/
    private Image correctIcon = new Image(getClass().getResourceAsStream("/images/icons8-check-mark-48.png"));
    private Image incorrectIcon = new Image(getClass().getResourceAsStream("/images/icons8-cross-mark-48.png"));
    private static final String SNERROR = "This student number has already registered or the format is illegal.";
    private static final String UNERROR = "User name is invalid! Please check the rule!";
    private static final String PS1ERROR = "Password is invalid! Please check the rule!";
    private static final String PS2ERROR = "The confirmed password is not same as the password!";
    private static final String USEREXISTS = "The user name exists! Please back to login in or choose another user name!";
    
    /**
     * Initialize the default view.
     */
    public void initialize() {
        messages.setEditable(false);
        messages.setStyle("-fx-text-fill: red; -fx-control-inner-background: empty;");
        messages.setText("");
    }
    
    @FXML
    private void back() throws IOException {
        App.setRoot("login");
    }
    
    @FXML
    private void signIn() throws IOException {
        var num = studentNum.getText().toUpperCase();
        var user = newUser.getText();
        var password_1 = password1.getText();
        var password_2 = password2.getText();
        
        // Check the Student Number is unique and valid.
        if (!AccountsData.isUnique(num) 
                || !AccountsData.isValidStr(num, AccountsData.VALIDID)) {
            this.numError();
            return;
        }
        else {
            this.studentNum_image.setImage(correctIcon);
        }
        
        // Check the user name.
        if (user.length()>15 || user.length()<4) {
            this.userError();
        }
        else if (!AccountsData.isValidStr(user.toUpperCase(), AccountsData.VALIDUSER)) {
            this.userError();
        }
        else {
            this.newUser_image.setImage(this.correctIcon);
        }
        
        // Check the password.
        if (password_2.length()>20 || password_2.length()<6) {
            this.password1Error();
        }
        else if (!AccountsData.isValidStr(password_2.toUpperCase(), AccountsData.VALIDPASSWORD)) {
            this.password1Error();
        }
        else {
            this.password1_image.setImage(this.correctIcon);
        }
        
        // Check the comfirmed password.
        if (!password_1.equals(password_2)) {
            this.password2Error();
        }
        else {
            this.password2_image.setImage(this.correctIcon);
        }
        
        if (this.messages.getText().equals("")) {
            AccountsData.addNewUser(user, password_2, num);
            if (AccountsData.ErrorMessage.equals("user")) {
                App.openDialog();
            }
            else {
                this.messages.setText(USEREXISTS);
            }
        }
    }

    @FXML
    private void inputName(KeyEvent key) {
        this.newUser_image.setImage(null);
        this.messages.setText("");
    }
    
    @FXML
    private void inputPS1(KeyEvent key) {
        this.password1_image.setImage(null);
        this.messages.setText("");
    }
    
    @FXML
    private void inputPS2(KeyEvent key) {
        this.password2_image.setImage(null);
        this.messages.setText("");
    }
    
    private void numError() {
        this.studentNum_image.setImage(this.incorrectIcon);
        this.messages.setText(SNERROR);
    }
    
    private void userError() {
        this.newUser_image.setImage(this.incorrectIcon);
        this.messages.setText(UNERROR);
    }
    
    private void password1Error() {
        this.password1_image.setImage(this.incorrectIcon);
        if (this.messages.toString().equals("")) {
            this.messages.setText(PS1ERROR);
        }
        else {
            this.messages.appendText("\n" + PS1ERROR);
        }        
    }
    
    private void password2Error() {
        this.password2_image.setImage(this.incorrectIcon);
        if (this.messages.toString().equals("")) {
            this.messages.setText(PS2ERROR);
        }
        else {
            this.messages.appendText("\n" + PS2ERROR);
        }
    }   
}
