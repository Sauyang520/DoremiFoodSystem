// Programmer Name: Mr.How Zhen Nan
// Program Name: Balance
// First Written: Friday, 23-June-2023
package doremi_food_system.delivery_man;

import bean.Transaction;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import utils.DataHolder;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class Balance implements Initializable {
    @FXML
    private Label currentBalance;
    @FXML
    private Label earningLabel;
    private final Transaction transaction = DataHolder.getInstance().getTransaction();




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        updateBalance();
    }

    public void updateBalance(){
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/doremifood", "root", "ILoveU5201314");

            String deliveryManID = DataHolder.getInstance().getDeliveryMan().getDeliveryManID();

            String balanceQuery = "SELECT dm_account_balance FROM delivery_man_table WHERE dm_id = ?";
            PreparedStatement balanceStatement = connection.prepareStatement(balanceQuery);
            balanceStatement.setString(1, deliveryManID);
            ResultSet balanceResultSet = balanceStatement.executeQuery();

            if (balanceResultSet.next()) {
                double currentBalances = balanceResultSet.getDouble("dm_account_balance");

                String methodQuery = "SELECT method_fee FROM dining_method_table WHERE dining_method = ?";
                PreparedStatement methodStatement = connection.prepareStatement(methodQuery);
                methodStatement.setDouble(1, transaction.getDeliveryFee());
                ResultSet methodResultSet = methodStatement.executeQuery();

                if (methodResultSet.next()) {
                    double deliveryFee = methodResultSet.getDouble("method_fee");
                    double updatedBalance = currentBalances + deliveryFee;

                    DataHolder.getInstance().getDeliveryMan().setAccountBalance(updatedBalance);

                    updateDeliveryManBalance(connection, deliveryManID, updatedBalance);

                    currentBalance.setText("RM" + String.valueOf(updatedBalance));
                    earningLabel.setText("You have earned RM" + String.valueOf(deliveryFee)+" in this order");
                }

                methodResultSet.close();
                methodStatement.close();
            }

            balanceResultSet.close();
            balanceStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateDeliveryManBalance(Connection connection, String deliveryManID, double balance) {
        try {
            String query = "UPDATE delivery_man_table SET dm_account_balance = ? WHERE dm_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setDouble(1, balance);
            statement.setString(2, deliveryManID);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void homeButton(){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/doremi_food_system/delivery_man/main-page.fxml"));
            Parent deliveryOrderPage = loader.load();

            Scene currentScene = currentBalance.getScene();
            Stage currentStage = (Stage) currentScene.getWindow();

            currentScene.setRoot(deliveryOrderPage);
            currentStage.setTitle("Delivery Order");
            Image icon = new Image(getClass().getResourceAsStream("doremi_food_system/delivery_man/location.png"));
            currentStage.getIcons().add(icon);
            currentStage.setResizable(false);
            currentStage.centerOnScreen();
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
