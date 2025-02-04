package fi.tuni.prog3.sisu;

import fi.tuni.prog3.sisu.Degrees.Major;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;

/**
 * This class is used to control main.fxml elements.
 * @author shuang.fan@tuni.fi and some parts added heidi.seppi@tuni.fi
 */
public class MainController {    
    @FXML private Label user;
    @FXML private TextField oldPassword;
    @FXML private PasswordField newPassword1;
    @FXML private PasswordField newPassword2;
    @FXML private ImageView oldPassword_view;
    @FXML private ImageView newPassword1_view;
    @FXML private ImageView newPassword2_view;
    @FXML private Label oldPsMessage;
    @FXML private Label newPs1Message;
    @FXML private Label newPs2Message;
    @FXML private Label message;
    @FXML private Label changeSuccess;
    @FXML private ScrollPane leftView;
    @FXML private ScrollPane rightView;
    @FXML private TabPane tabPane;
    @FXML private Tab tab_info;
    @FXML private Tab tab_structure;
    @FXML private Tab tab_password;
    @FXML private Tab tab_details;
    @FXML private Button btn_openStructure;
    @FXML private Label info_id;
    @FXML private Label info_name;
    @FXML private Label info_startYear;
    @FXML private Label info_endYear;
    @FXML private Label info_degree;
    @FXML private Label info_major;
    @FXML private Label info_message;
    @FXML private Button btn_editDetails;
    @FXML private Button btn_saveDetails;
    @FXML private Label details_id;
    @FXML private Label details_name_message;
    @FXML private Label details_startYear_message;
    @FXML private Label details_endYear_message;
    @FXML private Label details_degree_message;
    @FXML private Label details_major_message;
    @FXML private TextField details_name;
    @FXML private TextField details_startYear;
    @FXML private TextField details_endYear;
    @FXML private ComboBox details_degree;
    @FXML private ComboBox details_major;
    @FXML private Label details_message;
    @FXML private Button btn_saveStudy;
    @FXML private Label save_message;
    
    private static final String OLDPSERROR = "The old password is incorrect! Please check your input!";
    private static final String PS1ERROR = "Password is invalid! Please check the rule!";
    private static final String PS2ERROR = "The confirmed password is not same as the password!";
    private static final String PSRULE = "Password can contain \"0-9a-zA-Z\", \"_\", \"-\", \"!\".\n"
            + "Min length: 6; max length: 20.\n"
            + "New password cannot be same as the old one!";
    private static final String SUCCESS = "New password has been successfully saved!";
    // icons from https://icons8.com/
    private Image correctIcon = new Image(getClass().getResourceAsStream("/images/icons8-check-mark-48.png"));
    private Image incorrectIcon = new Image(getClass().getResourceAsStream("/images/icons8-cross-mark-48.png"));
    private static final Integer minYear = 2000;
    private static final Integer maxYear = 2050;
    
    private Degrees degreeData = new Degrees();
    private ArrayList<String> degreeList = new ArrayList<>();
    private String selectedDegree = "";
    private String selectedMajor = "";
    
    /**
     * Initialize the default view.
     * @throws Exception
     */
    public void initialize() throws Exception {                
        user.setText(LoginController.USER);
        this.oldPsMessage.setText(OLDPSERROR);
        this.newPs1Message.setText(PS1ERROR);
        this.newPs2Message.setText(PS2ERROR);
        this.message.setText(PSRULE);
        this.changeSuccess.setText(SUCCESS);
        this.oldPsMessage.setVisible(false);
        this.newPs1Message.setVisible(false);
        this.newPs2Message.setVisible(false);
        this.message.setVisible(false);
        this.changeSuccess.setVisible(false);
        this.info_message.setVisible(false);
        this.save_message.setText("Don't forget to save after you checked all.");
        
        this.tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.SELECTED_TAB);
        this.tabPane.getTabs().remove(tab_structure);
        this.tabPane.getTabs().remove(tab_password);
        this.tabPane.getTabs().remove(tab_details);
        
        initializeTabInfo();
        initializeTabDetails();
    }

    @FXML
    private void switchToLogin() throws IOException {
        App.setRoot("login");
    }
        
    @FXML
    private void saveStudy() throws IOException {
        var courses = new ArrayList<String>();
        var checkboxes = StudyStructureTab._checkboxesMap;
        for (var key : checkboxes.keySet()) {
            for (var checkbox : checkboxes.get(key)) {
                if (checkbox.isSelected()) {
                    courses.add(checkbox.getText());
                }
            }
        }
        var id = LoginController.ID;
        
        // make sure the save path exists
        var path = "src/main/students";
        Files.createDirectories(Paths.get(path));
        
        // save file
        var filename = String.format("%s/%s.txt", path, id);
        try (var file = new BufferedWriter(new FileWriter(filename))) {
            var isFirst = true;
            for (var course : courses) {
                if (isFirst) {
                    file.write(course);
                    isFirst = false;
                }
                else {
                    file.write(String.format("\n%s", course));
                }
            }
            this.save_message.setText("Saved successfully!");
            file.close();
        }
        catch (IOException e) {
            this.save_message.setText("Save failed.");
        }
    }
    
    @FXML
    private void openTabStructure() {
        this.tabPane.getTabs().add(tab_structure);
        this.tabPane.getSelectionModel().select(tab_structure);
    }
    
    @FXML
    private void openTabPassword() {
        this.tabPane.getTabs().add(tab_password);
        this.tabPane.getSelectionModel().select(tab_password);
    }
    
    @FXML
    private void changePassword() throws IOException {
        var oldPs = oldPassword.getText();
        var newPs1 = newPassword1.getText();
        var newPs2 = newPassword2.getText();
        
        // Check the old password is correct or not.
        if (!AccountsData.exists(oldPs)) {
            this.oldPsMessage.setVisible(true);
            this.oldPassword_view.setImage(incorrectIcon);
        }
        else {
            this.oldPassword_view.setImage(correctIcon);
        }
        
        // Check the new password is legal or not.
        if (!AccountsData.isValidStr(newPs1.toUpperCase(), AccountsData.VALIDPASSWORD)
                || newPs1.length()<6 || newPs1.length()>20 || oldPs.equals(newPs1)) {
            this.newPs1Message.setVisible(true);
            this.message.setVisible(true);
            this.newPassword1_view.setImage(incorrectIcon);
        }
        else {
            this.newPassword1_view.setImage(correctIcon);
        }
        
        // Check the confirmed password is legal or not.
        if (!newPs2.equals(newPs1)) {
            this.newPs2Message.setVisible(true);
            this.newPassword2_view.setImage(incorrectIcon);
        }
        else {
            this.newPassword2_view.setImage(correctIcon);
        }
        
        // Update the accounts.txt.
        if (!this.message.isVisible() && !this.newPs1Message.isVisible() 
                && !this.newPs2Message.isVisible() && !this.oldPsMessage.isVisible()) {
            AccountsData.updatePassword(newPs2);
            this.changeSuccess.setVisible(true);
        }        
    }
    
    @FXML
    private void input_oldPs(KeyEvent key) {
        this.changeSuccess.setVisible(false);
        this.oldPsMessage.setVisible(false);
        this.oldPassword_view.setImage(null);
    }
    
    @FXML
    private void input_newPs1(KeyEvent key) {
        this.changeSuccess.setVisible(false);
        this.message.setVisible(false);
        this.newPs1Message.setVisible(false);
        this.newPassword1_view.setImage(null);
    }
    
    @FXML
    private void input_newPs2(KeyEvent key) {
        this.changeSuccess.setVisible(false);
        this.newPs2Message.setVisible(false);
        this.newPassword2_view.setImage(null);
    }
    
    @FXML
    private void openEditDetails() throws IOException {
        this.tabPane.getTabs().add(tab_details);
        this.tabPane.getSelectionModel().select(tab_details);
    }
    
    @FXML
    private void saveDetails() throws IOException {
        var canSave = true;
        var id = this.details_id.getText();
        var name = this.details_name.getText();
        var startYear = this.details_startYear.getText();
        var endYear = this.details_endYear.getText();
        var degree_obj = this.details_degree.getValue();
        var major_obj = this.details_major.getValue();
        String degree;
        if (degree_obj == null) {
            // User hasn't selected any other degree.
            degree = selectedDegree;
        }
        else {
           degree = degree_obj.toString();
        }
        String major;
        if (this.details_major.getItems().isEmpty() || major_obj == null) {
            major = selectedMajor;
        }
        else {
            major = major_obj.toString();
        }

        
        if (name.isBlank() || name.equals("null")) {
            this.details_name_message.setVisible(true);
            canSave = false;
        }
        
        if (startYear.isBlank() || startYear.equals("null") 
                || !isValidYear(startYear)) {
            this.details_startYear_message.setVisible(true);
            canSave = false;
        }
        
        if (endYear.isBlank() || endYear.equals("null")
                || !isValidYear(endYear)) {
            this.details_endYear_message.setVisible(true);
            canSave = false;
        }
        
        if (degree.isBlank() || degree.equals("null")
            || !degreeList.contains(degree)) {
            this.details_degree_message.setVisible(true);
            canSave = false;
        }
        
        if (major.isBlank() || major.equals("null")) {
            this.details_major_message.setVisible(true);
            canSave = false;
        }
        
        // TODO: 
        // User's major selection information needs to be added to student file 
        // and fetched from student file!
        if (canSave) {
            //save details
            var students = StudentsData.readFromFile();
            for (var student : students) {
                if (student.getId().equals(id)) {
                    student.setName(name);
                    student.setStartYear(startYear);
                    student.setEndYear(endYear);
                    student.setDegree(degree);
                    student.setMajor(major);
                }
            }
            StudentsData.writeToFile(students);
            
            // Update the student info.
            if (degree.equals(selectedDegree) == false || major.equals(selectedMajor) == false) {
                degreeData.createStudyStructure(degree, major);
                this.selectedDegree = degree;
                this.selectedMajor = major;
                
                // Updates studyStructureTab after user selected different degree.
                StudyStructureTab.initialize(this.leftView, this.rightView, 
                        save_message,selectedDegree, 
                    degreeData.getDegreeStructureList());
            } 
            
            this.initializeTabInfo();
            this.details_message.setVisible(true);
        }
    }
    
    @FXML
    private void input_details_name(KeyEvent key) {
        this.details_name_message.setVisible(false);
        this.details_message.setVisible(false);
    }
    
    @FXML
    private void input_details_startYear(KeyEvent key) {
        this.details_startYear_message.setVisible(false);
        this.details_message.setVisible(false);
    }
    
    @FXML
    private void input_details_endYear(KeyEvent key) {
        this.details_endYear_message.setVisible(false);
        this.details_message.setVisible(false);
    }
    
    @FXML
    private void change_degree() {
        this.details_degree_message.setVisible(false);
        this.details_message.setVisible(false);
    }
    
    @FXML
    private void change_major() {
        this.details_major_message.setVisible(false);
    }
    
    /**
    * Changes major ComboBox according to selected degree from degree ComboBox. .
    */
    private void update_major_comboBox() {
        if (this.details_degree.getValue() != null) {
            String selectedDegree = this.details_degree.getValue().toString();
            if (degreeList.contains(selectedDegree)) {
                this.selectedDegree = selectedDegree;
                this.details_major.getItems().clear();
                this.details_major.setPromptText("");
                ArrayList<Major> majorList = degreeData.getMajorList(selectedDegree);
                if (majorList.isEmpty() == false) {
                    for (Major majorObj : majorList) {
                        this.details_major.getItems().add(majorObj.getName());
                    }
                }
            }
        }
    }
    
    // Initialize the info-tab.
    private void initializeTabInfo() throws IOException {
        var students = StudentsData.readFromFile();
        if (students == null) {
            return;
        }
        
        var id = LoginController.ID;
        String name = "";
        String startYear = "";
        String endYear = "";
        String degree = "";
        String major = "";
        
        // Student's major selection data (string) needs to be fetched from
        // Student file.
        for (var student : students) {
            if (student.getId().equals(id)) {
                this.info_id.setText(id);
                name = student.getName();
                startYear = student.getStartYear();
                endYear = student.getEndYear();
                degree = student.getDegree();
                major = student.getMajor();
                this.info_name.setText(name);
                this.info_startYear.setText(startYear);
                this.info_endYear.setText(endYear);
                this.info_degree.setText(degree);
                this.info_major.setText(major);
                }
            }
        
        if (name.isBlank() || name.equals("null")
                || startYear.isBlank() || startYear.equals("null")
                || endYear.isBlank() || endYear.equals("null")
                || degree.isBlank() || degree.equals("null")
                || major.isBlank() || major.equals("null")) {
            this.btn_openStructure.setDisable(true);
            this.info_message.setVisible(true);
        }
        else {
            this.btn_openStructure.setDisable(false);
            this.info_message.setVisible(false);
        }
    }
    
    // Initialize the details-tab.
    private void initializeTabDetails() throws IOException {
        var id = LoginController.ID;
        this.details_id.setText(id);
        this.details_name_message.setVisible(false);
        this.details_startYear_message.setVisible(false);
        this.details_endYear_message.setVisible(false);
        this.details_degree_message.setVisible(false);
        this.details_major_message.setVisible(false);
        this.details_message.setVisible(false);
        this.details_major.setEditable(false);
        
        // Program fetches information about the degrees.
        degreeData.fetchDegreesFromApi();
        degreeList = degreeData.getDegreeNames(); 
        for (String degreeName : degreeList) {
            this.details_degree.getItems().add(degreeName);
        }
        
        // Listens if user changes degree and updates major combobox according to that.
        this.details_degree.valueProperty().addListener(new ChangeListener<String>() {
            @Override 
            public void changed(ObservableValue ov, String oldValue, String newValue) {
                update_major_comboBox();
            }    
        });
        
        var students = StudentsData.readFromFile();
        for (var student : students) {
            if (student.getId().equals(id)) {
                var name = student.getName();
                var startYear = student.getStartYear();
                var endYear = student.getEndYear();
                var degree = student.getDegree();
                var major = student.getMajor();
                
                if (!name.isBlank() && !name.equals("null")) {
                    this.details_name.setText(name);
                }
                if (!startYear.isBlank() && !startYear.equals("null")) {
                    this.details_startYear.setText(startYear);
                }
                if (!endYear.isBlank() && !endYear.equals("null")) {
                    this.details_endYear.setText(endYear);
                }
               
                if (!degree.isBlank() && !degree.equals("null")) {
                // Updates majors if user had already selected some degree before.
                    this.details_degree.setPromptText(degree);
                    this.selectedDegree = degree;
                    
                    // Get the major info if the degree is selected.
                    if (!major.isBlank() && !major.equals("null")) {
                        this.details_major.getItems().clear();
                        this.details_major.setPromptText(major);
                        this.selectedMajor = major;
                        ArrayList<Major> majorList = degreeData.getMajorList(selectedDegree);
                        if (majorList != null) {
                            for (Degrees.Major majorObj : majorList) {
                                this.details_major.getItems().add(majorObj.getName());
                            }
                        }
                        degreeData.createStudyStructure(selectedDegree, selectedMajor);
                        
                        // Initialize the study structure-tab.
                        StudyStructureTab.initialize(this.leftView, 
                                this.rightView,
                                this.save_message,
                                selectedDegree,
                                degreeData.getDegreeStructureList());
                    }
                }
            }
        }
    }
    
    // Check the study year is valid.
    private Boolean isValidYear(String input) {
        if (input.isBlank() || input.equals("null")) {
            return false;
        }
        try {
            Integer year = Integer.parseInt(input);
            return (year <= maxYear && year >= minYear);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}