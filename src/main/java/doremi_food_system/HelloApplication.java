// Programmer Name: Mr.Sim Sau Yang
// Program Name: Main Class
// First Written: Saturday, 17-June-2023

package doremi_food_system;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * To start the program, enter login page
 */
public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/doremi_food_system/login-page.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.setTitle("Doremi Food");
        Image icon = new Image(getClass().getResourceAsStream("/doremi_food_system/picture/icon.png"));
        stage.getIcons().add(icon);
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.show();

        stage.setOnCloseRequest(event -> {
            event.consume();
            logout(stage);
        });
    }

    public static void main(String[] args) {
        launch();
    }

    public void logout(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Close");
        alert.setHeaderText("You are about to close the system!");
        alert.setContentText("Click OK to end program.");
        alert.getDialogPane();
        if (alert.showAndWait().get() == ButtonType.OK) {
            stage.close();
        }
    }
}