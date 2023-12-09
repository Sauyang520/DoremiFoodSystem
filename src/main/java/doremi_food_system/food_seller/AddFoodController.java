// Programmer Name: Mr.Chai Zheng Lam
// Program Name: AddFoodController
// First Written: Saturday, 23-June-2023
package doremi_food_system.food_seller;

import bean.FoodSeller;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utils.DataHolder;
import javafx.scene.control.Label;

import java.sql.*;

public class AddFoodController {
    @FXML
    private TextArea NewFoodDescription;

    @FXML
    private TextField NewFoodID;

    @FXML
    private TextField NewFoodName;

    @FXML
    private TextField NewFoodPrice;

    @FXML
    private Button AddButton;

    @FXML
    private Button CancelButton;
    @FXML
    private final FoodSeller foodSeller = DataHolder.getInstance().getFoodSeller();

    private Connection connection;

    @FXML
    private Label WarningLabel;
    @FXML
    private TextField NewPath;

    @FXML
    public void Add() {
        if (!NewFoodID.getText().isEmpty() && !NewFoodName.getText().isEmpty()
                && !NewFoodPrice.getText().isEmpty() && !NewFoodDescription.getText().isEmpty()) {
            String storeID = foodSeller.getStoreID();
            String foodID = NewFoodID.getText();
            String foodName = NewFoodName.getText();
            String foodPrice = NewFoodPrice.getText();
            String foodDescription = NewFoodDescription.getText();
            String image = NewPath.getText();

            try {
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/doremifood", "root", "ILoveU5201314");

                // Check if the food name already exists in the table
                PreparedStatement psCheckFoodName = connection.prepareStatement(
                        "SELECT food_id FROM food_table WHERE store_id = ? AND food_name = ?"
                );
                psCheckFoodName.setString(1, storeID);
                psCheckFoodName.setString(2, foodName);

                ResultSet rsFoodName = psCheckFoodName.executeQuery();

                if (rsFoodName.next()) {
                    WarningLabel.setText("Food with the same name already exists.");
                } else {
                    // Check if the food ID already exists in the table
                    PreparedStatement psCheckFoodID = connection.prepareStatement(
                            "SELECT food_id FROM food_table WHERE store_id = ? AND food_id = ?"
                    );
                    psCheckFoodID.setString(1, storeID);
                    psCheckFoodID.setString(2, foodID);

                    ResultSet rsFoodID = psCheckFoodID.executeQuery();

                    if (rsFoodID.next()) {
                        WarningLabel.setText("Food with the same ID already exists.");
                    } else {
                        // Validate Price
                        double price;
                        try {
                            price = Double.parseDouble(foodPrice);
                        } catch (NumberFormatException e) {
                            WarningLabel.setText("Price must be a valid number.");
                            return;
                        }

                        // Insert the new food into the table
                        PreparedStatement psInsert = connection.prepareStatement(
                                "INSERT INTO food_table (store_id, food_id, food_name, food_price, food_description, food_image_path) " +
                                        "VALUES (?, ?, ?, ?, ?, ?)"
                        );
                        psInsert.setString(1, storeID);
                        psInsert.setString(2, foodID);
                        psInsert.setString(3, foodName);
                        psInsert.setString(4, foodPrice);
                        psInsert.setString(5, foodDescription);
                        psInsert.setString(6, image);

                        int rowsAffected = psInsert.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("Food added successfully.");
                            closeForm();
                        } else {
                            System.out.println("Failed to add food.");
                        }

                        psInsert.close();
                    }

                    psCheckFoodID.close();
                }

                psCheckFoodName.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            WarningLabel.setText("Please fill in all columns.");
        }
    }

    @FXML
    public void closeForm(){
        Stage stage = (Stage) NewFoodID.getScene().getWindow();
        stage.close();
    }
}
