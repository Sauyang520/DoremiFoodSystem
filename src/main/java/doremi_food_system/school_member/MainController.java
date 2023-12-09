// Programmer Name: Mr.Sim Sau Yang
// Program Name: School Member Main Page Controller
// First Written: Saturday, 17-June-2023

package doremi_food_system.school_member;

import bean.Food;
import bean.FoodSeller;
import bean.SchoolMember;

import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import utils.DataHolder;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * The main page of the school member to select the food
 */
public class MainController implements Initializable {

    @FXML
    private MenuBar mnbMain;

    @FXML
    private Label lblID;

    @FXML
    private Label lblName;

    @FXML
    private Label lblBalance;

    @FXML
    private ChoiceBox<String> cbxStoreName;

    @FXML
    private TableColumn<Food, String> tbclmnID;

    @FXML
    private TableColumn<Food, String> tbclmnName;

    @FXML
    private TableColumn<Food, Double> tbclmnPrice;

    @FXML
    private TableView<Food> tbvwFood;

    private Connection connection;
    private final SchoolMember schoolMember = DataHolder.getInstance().getSchoolMember();
    private final List<String> storeNames = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Set up table column
        tbclmnID.setCellValueFactory(new PropertyValueFactory<>("foodID"));
        tbclmnName.setCellValueFactory(new PropertyValueFactory<>("foodName"));
        tbclmnPrice.setCellValueFactory(new PropertyValueFactory<>("foodPrice"));

        lblID.setText(schoolMember.getTpNumber());
        lblName.setText(schoolMember.getName());
        lblBalance.setText("RM " + schoolMember.getAccountBalance() + "");

        // Search for existing food store
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/doremifood", "root", "ILoveU5201314");
            PreparedStatement psStoreName;
            ResultSet rsStoreName;
            // Select doremifood: store_table , store_name
            psStoreName = connection.prepareStatement("SELECT store_name FROM store_table WHERE store_name NOT LIKE '%N/A%'");
            rsStoreName = psStoreName.executeQuery();

            if (rsStoreName.isBeforeFirst()) {
                while (rsStoreName.next()) {
                    storeNames.add(rsStoreName.getString("store_name"));
                }
                // Show the store name to choice box
                cbxStoreName.getItems().addAll(storeNames);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        cbxStoreName.setOnAction(this::showFood);

        tbvwFood.getSelectionModel().getSelectedItems().addListener(this::selectFood);
    }

    /**
     * Enable the user to enter select food item page to select the food
     */
    public void selectFood(Observable observable) {
        // Get the food selected from table
        Food selectedFood = tbvwFood.getSelectionModel().getSelectedItem();
        if (selectedFood != null) {
            DataHolder.getInstance().setFood(selectedFood);
            // Go to food item page
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/doremi_food_system/school_member/fooditem-page.fxml"));
                Stage stage = new Stage();
                stage.setScene(new Scene(loader.load()));
                stage.setTitle("Item");
                Image icon = new Image(getClass().getResourceAsStream("/doremi_food_system/picture/icon.png"));
                stage.getIcons().add(icon);
                stage.setResizable(false);
                stage.centerOnScreen();
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Show available food items of the food store selected on choice box
     */
    public void showFood(ActionEvent event) {
        tbvwFood.getItems().clear();
        ObservableList<Food> foods = tbvwFood.getItems();
        String storeName = cbxStoreName.getValue();

        // Collect the food items
        try {
            // Select doremifood: store_table JOIN food_table , get the food details USING store name
            PreparedStatement psFood = connection.prepareStatement("SELECT s.store_id, s.`store_name`" +
                    ", f.food_id, f.food_name, f.food_price, f.food_description, f.food_image_path " +
                    "FROM store_table s " +
                    "JOIN food_table f " +
                    "USING (store_id) " +
                    "WHERE s.`store_name` = ? AND f.food_name NOT LIKE '%N/A%'");
            psFood.setString(1, storeName);
            ResultSet rsFood = psFood.executeQuery();

            if (rsFood.isBeforeFirst()) {
                while (rsFood.next()) {
                    foods.add(new Food(
                            new FoodSeller(rsFood.getString("store_id"), null, rsFood.getString("store_name"), 0)
                            , rsFood.getString("food_id"), rsFood.getString("food_name")
                            , rsFood.getDouble("food_price"), rsFood.getString("food_description")
                            , rsFood.getString("food_image_path")));
                }
            }
            tbvwFood.setItems(foods);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Enable user to enter cart page to view the selected food items
     */
    @FXML
    public void viewCart(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/doremi_food_system/school_member/cart-page.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Cart");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Go to view order page
     */
    @FXML
    public void viewOrder() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/doremi_food_system/school_member/vieworder-page.fxml"));
            Stage stage = (Stage) mnbMain.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Order");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Go to view balance history page
     */
    @FXML
    public void viewBalance() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/doremi_food_system/school_member/balancehistory-page.fxml"));
            Stage stage = (Stage) mnbMain.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Balance");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Logout to Login Page
     */
    @FXML
    public void logout() {
        try {
            DataHolder.deleteInstance();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/doremi_food_system/login-page.fxml"));
            Stage stage = (Stage) mnbMain.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Doremi Food");
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
