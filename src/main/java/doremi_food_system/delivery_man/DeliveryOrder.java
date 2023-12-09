// Programmer Name: Mr.How Zhen Nan
// Program Name: DeliveryOrder
// First Written: Thursday, 22-June-2023

package doremi_food_system.delivery_man;

import bean.FoodOrdered;
import bean.FoodSeller;
import bean.Transaction;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import utils.DataHolder;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DeliveryOrder implements Initializable {
    @FXML
    private Button deliveredButton;
    @FXML
    private Label deliveryOrderAddress;

    @FXML
    private Label deliveryOrderID;

    @FXML
    private Label deliveryOrderManID;
    private final Transaction transaction = DataHolder.getInstance().getTransaction();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        deliveryOrderID.setText(transaction.getOrderID());
        deliveryOrderAddress.setText(transaction.getDeliveryAddress());
        String deliveryManID = DataHolder.getInstance().getDeliveryMan().getDeliveryManID();
        deliveryOrderManID.setText(deliveryManID);

        updateDeliveryManID(deliveryManID);
        }

    private void updateDeliveryManID(String deliveryManID) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/doremifood", "root", "ILoveU5201314");
            String query = "UPDATE order_table SET dm_id = ? WHERE order_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, deliveryManID);
            statement.setString(2, transaction.getOrderID());
            statement.executeUpdate();

            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateStatusButton(){
        deliveredStatus(1);
    }

    private void deliveredStatus(int status){
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/doremifood", "root", "ILoveU5201314");
            String query = "UPDATE order_table SET order_status = ? WHERE order_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, status);
            statement.setString(2, transaction.getOrderID());
            statement.executeUpdate();

            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/doremi_food_system/delivery_man/balance.fxml"));
            Parent deliveryOrderPage = loader.load();

            Scene currentScene = deliveryOrderID.getScene();
            Stage currentStage = (Stage) currentScene.getWindow();

            currentScene.setRoot(deliveryOrderPage);
            currentStage.setTitle("Delivery Order");
            Image icon = new Image(getClass().getResourceAsStream("/doremi_food_system/delivery_man/location.png"));
            currentStage.getIcons().add(icon);
            currentStage.setResizable(false);
            currentStage.centerOnScreen();
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}




