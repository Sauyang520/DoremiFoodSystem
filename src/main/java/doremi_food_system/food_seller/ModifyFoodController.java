// Programmer Name: Mr.Chai Zheng Lam
// Program Name: ModifyFoodController
// First Written: Saturday, 23-June-2023
package doremi_food_system.food_seller;

import bean.FoodSeller;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utils.DataHolder;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class ModifyFoodController implements Initializable {

    @FXML
    private Button CancelButton;

    @FXML
    private TextArea FoodDescription;

    @FXML
    private TextField FoodID;

    @FXML
    private TextField FoodName;

    @FXML
    private TextField Price;

    @FXML
    private TextField Path;
    @FXML
    private Label WarningLabel;

    @FXML
    private Button ModifyButton;
    private Connection connection;

    private final FoodSeller foodSeller = DataHolder.getInstance().getFoodSeller();

    private String oldFoodID;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setFoodData(String foodID, String foodName, double foodPrice, String foodDescription, String path) {
        FoodID.setText(foodID);
        FoodName.setText(foodName);
        Price.setText(Double.toString(foodPrice));
        FoodDescription.setText(foodDescription);
        Path.setText(path);
        oldFoodID = foodID;
    }

    @FXML
    public void Modify() {
        String foodID = FoodID.getText();
        String foodName = FoodName.getText();
        String foodPrice = Price.getText();
        String foodDescription = FoodDescription.getText();
        String path = Path.getText();

        // Validate FoodID
        if (foodID.isEmpty() || !foodID.matches("[A-Za-z0-9]+")) {
            WarningLabel.setText("Food ID is required and should be alphanumeric");
            return;
        }

        // Validate FoodName
        if (foodName.isEmpty()) {
            WarningLabel.setText("Food Name is required");
            return;
        }

        // Validate Price
        double price;
        try {
            price = Double.parseDouble(foodPrice);
        } catch (NumberFormatException e) {
            WarningLabel.setText("Price must be a valid number");
            return;
        }
        if (foodPrice.isEmpty() || price < 0) {
            WarningLabel.setText("Price is required and should be a positive number");
            return;
        }

        // Validate Path
        if (path == null || path.isEmpty()) {
            WarningLabel.setText("Path is required");
            return;
        }

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/doremifood", "root", "ILoveU5201314");

            // Check if the food name already exists in the table (excluding the current record)
            PreparedStatement psCheckFoodName = connection.prepareStatement(
                    "SELECT food_id FROM food_table WHERE store_id = ? AND food_name = ? AND food_id != ?"
            );
            psCheckFoodName.setString(1, foodSeller.getStoreID());
            psCheckFoodName.setString(2, foodName);
            psCheckFoodName.setString(3, foodID);

            ResultSet rsFoodName = psCheckFoodName.executeQuery();

            if (rsFoodName.next()) {
                WarningLabel.setText("Food with the same name already exists.");
                return;
            }

            // Check if the food ID already exists in the table (excluding the current record)
            PreparedStatement psCheckFoodID = connection.prepareStatement(
                    "SELECT food_id FROM food_table WHERE store_id = ? AND food_id = ? AND food_id != ?"
            );
            psCheckFoodID.setString(1, foodSeller.getStoreID());
            psCheckFoodID.setString(2, foodID);
            psCheckFoodID.setString(3, foodID);

            ResultSet rsFoodID = psCheckFoodID.executeQuery();

            if (rsFoodID.next()) {
                WarningLabel.setText("Food with the same ID already exists.");
                return;
            }

            // Update the food data in the table
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE food_table SET food_id = ?, food_name = ?, food_price = ?, food_description = ?, food_image_path = ? WHERE food_id = ?"
            );
            statement.setString(1, foodID);
            statement.setString(2, foodName);
            statement.setString(3, foodPrice);
            statement.setString(4, foodDescription);
            statement.setString(5, path);
            statement.setString(6, oldFoodID);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                // Update successful
                System.out.println("Food data updated successfully!");
                closeForm();
            } else {
                // Update failed
                System.out.println("Failed to update food data!");
            }

            psCheckFoodID.close();
            psCheckFoodName.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void closeForm(){
        Stage stage = (Stage) FoodID.getScene().getWindow();
        stage.close();
    }

}
