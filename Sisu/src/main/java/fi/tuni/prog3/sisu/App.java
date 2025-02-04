package fi.tuni.prog3.sisu;

import java.io.File;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

/**
 * JavaFX App
 */
public class App extends Application {

    /**
     * The path of accounts.txt.
     */
    public static final String FILENAME = "src/main/accounts.txt";
    private static Scene scene;
    private static Stage rootStage;

    /**
     * Start the window.
     * @param stage the main stage.
     * @throws IOException if there is any IO error.
     */
    @Override
    public void start(Stage stage) throws IOException {        
        rootStage = stage;
        scene = new Scene(loadFXML("login"), 800, 500);
        stage.setTitle("SISU");
        stage.setScene(scene);
        stage.setMaxHeight(700);
        stage.show();
    }

    /**
     * Open a dialog when the login success.
     */
    static void openDialog() {        
        final Stage dialog = new Stage();
        dialog.setTitle("Register success");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(rootStage);
        var label = new Label("Register success! Please login in!");
        var label_empty = new Label();
        var button = new Button("OK");
        button.setId("loginDialogBtn");
        button.setOnAction((ActionEvent event) -> {
            try {
                dialog.close();
                setRoot("login");
            }
            catch (IOException ex) {
            }
        });
        var vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().add(label);
        vBox.getChildren().add(label_empty);
        vBox.getChildren().add(button);
        var dialogScene = new Scene(vBox, 250, 100);
        dialog.setScene(dialogScene);
        dialog.show();
    }
    
    /**
     * Switch to other view.
     * @param fxml the fxml file name
     * @throws IOException if there is any IO error.
     */
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /**
     * The main launch.
     * @param args 
     */
    public static void main(String[] args) {
        launch();
    }
    
    /**
     * Check the accounts.txt exists or not.
     * @return True, if the file exists.
     */
    public static Boolean fileExists() {
        var file = new File(FILENAME);
        return file.exists();
    }
}