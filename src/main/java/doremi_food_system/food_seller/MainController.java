// Programmer Name: Mr.Chai Zheng Lam
// Program Name: Main Controller
// First Written: Saturday, 17-June-2023

package doremi_food_system.food_seller;

import bean.Food;
import bean.FoodOrdered;
import bean.FoodSeller;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Callback;
import utils.DataHolder;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private Pane pane;

    @FXML
    private Label Storename;
    @FXML
    private TableColumn<FoodOrdered, Double> amount;

    @FXML
    private TableColumn<FoodOrdered, String> foodname;

    @FXML
    private TableColumn<FoodOrdered, String> orderID;


    @FXML
    private TableColumn<FoodOrdered, Integer> quantity;

    private Connection connection;

    private String StoreID;

    private final FoodSeller foodSeller = DataHolder.getInstance().getFoodSeller();
    @FXML
    private TableView<FoodOrdered> OrderTable = new TableView<>();



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Storename.setText(foodSeller.getName());

        orderID.setCellValueFactory(new PropertyValueFactory<>("orderID"));
        foodname.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<FoodOrdered, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<FoodOrdered, String> param) {
                FoodOrdered foodOrdered = param.getValue();
                Food food = foodOrdered.getFood();
                String foodName = food.getFoodName();
                return new SimpleStringProperty(foodName);
            }
        });
        quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        amount.setCellValueFactory(new PropertyValueFactory<>("amount"));

        StoreID = foodSeller.getStoreID();
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/doremifood", "root", "ILoveU5201314");
            // 拿东西的query
            PreparedStatement psFoodStatus;
            // query拿到的东西
            ResultSet rsFoodStatus;
            psFoodStatus = connection.prepareStatement("SELECT od.order_id, od.food_id, od.ordered_food_quantity, od.ordered_food_amount, f.food_name " +
                    "FROM order_details_table od INNER JOIN food_table f ON od.food_id = f.food_id WHERE od.ordered_food_status = ? AND od.store_id = ?");
            psFoodStatus.setInt(1, 0);
            psFoodStatus.setString(2, StoreID);
            rsFoodStatus = psFoodStatus.executeQuery();

            List<FoodOrdered> foodOrderedList = new ArrayList<>();

            while (rsFoodStatus.next()) {
                String orderID = rsFoodStatus.getString("order_id");
                String foodID = rsFoodStatus.getString("food_id");
                int quantity = rsFoodStatus.getInt("ordered_food_quantity");
                double amount = rsFoodStatus.getDouble("ordered_food_amount");
                String foodName = rsFoodStatus.getString("food_name");

                Food food = new Food(foodID, foodName);
                FoodOrdered foodOrdered = new FoodOrdered(orderID, food, quantity, amount, false);
                foodOrderedList.add(foodOrdered);
            }
            OrderTable.getItems().addAll(foodOrderedList);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void View() {
        FoodOrdered selectedFoodOrdered = OrderTable.getSelectionModel().getSelectedItem();
        if (selectedFoodOrdered != null) {
            openViewController(selectedFoodOrdered);
        }
    }

    @FXML
    public void openViewController(FoodOrdered foodOrdered){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewOrder-page.fxml"));
            Parent root = loader.load();
            ViewController viewController = loader.getController();
            viewController.setData(foodOrdered.getOrderID());
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("View Order");
            Image icon = new Image(getClass().getResourceAsStream("/doremi_food_system/picture/icon.png"));
            stage.getIcons().add(icon);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // According order_id, store_id, food_id and change order_food_status
    @FXML
    public void Complete(){
        FoodOrdered selectedFoodOrdered = OrderTable.getSelectionModel().getSelectedItem();
        if (selectedFoodOrdered != null) {
            String orderID = selectedFoodOrdered.getOrderID();
            String foodID = selectedFoodOrdered.getFood().getFoodID();
            String storeID = foodSeller.getStoreID();

            try {
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/doremifood", "root", "ILoveU5201314");

                // Update the order_food_status in the database
                PreparedStatement psUpdateStatus = connection.prepareStatement("UPDATE order_details_table SET ordered_food_status = ? WHERE order_id = ? AND store_id = ? AND food_id = ?");
                psUpdateStatus.setInt(1, 1);
                psUpdateStatus.setString(2, orderID);
                psUpdateStatus.setString(3, storeID);
                psUpdateStatus.setString(4, foodID);
                psUpdateStatus.executeUpdate();

                // Optional: You can also update the UI or perform any other necessary actions after completing the order.

                connection.close();
                loadMainPage();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void loadContent(String fxmlfile, Pane pane){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlfile));
            Pane content = loader.load();
            pane.getChildren().setAll(content);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void changeScene(String fxml) throws IOException {
        Stage currentStage = (Stage) pane.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource(fxml));
        Scene scene = new Scene(root);
        currentStage.setScene(scene);
        currentStage.centerOnScreen();
    }

    @FXML
    void loadMainPage() throws IOException {
        changeScene("main-page.fxml");
    }
    @FXML
    public void loadManageFoodMenu(){loadContent("ManageFoodMenu-page.fxml",pane);}

    @FXML
    public void loadMonthlyReport(){loadContent("MonthlyReport-page.fxml",pane);}

    @FXML
    public void Logout() throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("LogOut");
        alert.setHeaderText("You are about to LogOut the system!");
        alert.setContentText("Click OK to logout program.");
        if (alert.showAndWait().get() == ButtonType.OK) {
            changeScene("/doremi_food_system/login-page.fxml");
        }
    }
    @FXML
    public void Refresh() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/doremifood", "root", "ILoveU5201314");

            // Check whether the order_food_status in the same order_id are all 1
            PreparedStatement psCheckStatus = connection.prepareStatement("SELECT order_id FROM order_details_table WHERE ordered_food_status = ? GROUP BY order_id HAVING COUNT(*) = SUM(ordered_food_status)");
            psCheckStatus.setInt(1, 1);
            ResultSet rsCheckStatus = psCheckStatus.executeQuery();

            List<String> completedOrders = new ArrayList<>();

            while (rsCheckStatus.next()) {
                String orderID = rsCheckStatus.getString("order_id");
                completedOrders.add(orderID);
            }

            // Change the order_status from 0 to 1 for non-delivery completed orders
            PreparedStatement psUpdateStatus = connection.prepareStatement("UPDATE order_table SET order_status = ? WHERE order_id = ? AND dining_method != ?");
            psUpdateStatus.setInt(1, 1);
            psUpdateStatus.setString(3, "Delivery");

            for (String orderID : completedOrders) {
                psUpdateStatus.setString(2, orderID);
                psUpdateStatus.executeUpdate();
            }

            // Optional: You can perform any other necessary actions after refreshing the orders.

            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
