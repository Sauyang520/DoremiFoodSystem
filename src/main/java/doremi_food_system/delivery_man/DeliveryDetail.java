// Programmer Name: Mr.How Zhen Nan
// Program Name: DeliveryDetail
// First Written: Wednesday, 21-June-2023

package doremi_food_system.delivery_man;

import bean.DeliveryMan;
import bean.FoodOrdered;
import bean.FoodSeller;
import bean.Transaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import utils.DataHolder;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.List;
import java.util.ResourceBundle;

public class DeliveryDetail implements Initializable {
    @FXML
    private Label deliveryAddress;

    @FXML
    private TableView<FoodOrdered> deliveryDetailTable;

    @FXML
    private Label deliveryOrderId;

    @FXML
    private TableColumn<FoodOrdered, String> foodIDColumn;

    @FXML
    private TableColumn<FoodOrdered, String> foodNameColumn;

    @FXML
    private TableColumn<FoodOrdered, String> storeColumn;
    private final Transaction transaction = DataHolder.getInstance().getTransaction();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        deliveryOrderId.setText(transaction.getOrderID());
        deliveryAddress.setText(transaction.getDeliveryAddress());


        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/doremifood", "root", "ILoveU5201314");
            String query = "SELECT odt.food_ID, ft.food_name, odt.store_id FROM order_details_table odt JOIN order_table ot ON odt.order_id = ot.order_id JOIN food_table ft ON odt.food_id = ft.food_id WHERE ot.order_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, transaction.getOrderID());
            ResultSet resultSet = statement.executeQuery();

            ObservableList<FoodOrdered> detailList = FXCollections.observableArrayList();

            while (resultSet.next()) {
                String foodID = resultSet.getString("food_id");
                String foodName = resultSet.getString("food_name");
                String storeID = resultSet.getString("store_id");

                FoodOrdered foodOrdered = new FoodOrdered(foodID, foodName, storeID);
                detailList.add(foodOrdered);
            }

            foodIDColumn.setCellValueFactory(new PropertyValueFactory<>("foodID"));
            foodNameColumn.setCellValueFactory(new PropertyValueFactory<>("foodName"));
            storeColumn.setCellValueFactory(new PropertyValueFactory<>("storeID"));

            deliveryDetailTable.setItems(detailList);

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void backButton(){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/doremi_food_system/delivery_man/main-page.fxml"));
            Parent deliveryDetailPage = loader.load();

            Scene currentScene = deliveryDetailTable.getScene();
            Stage currentStage = (Stage) currentScene.getWindow();

            currentScene.setRoot(deliveryDetailPage);
            currentStage.setTitle("Doremi Food");
            Image icon = new Image(getClass().getResourceAsStream("/doremi_food_system/delivery_man/location.png"));
            currentStage.getIcons().add(icon);
            currentStage.setResizable(false);
            currentStage.centerOnScreen();
            currentStage.show();
    } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    public void confirmDelivery(){
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/doremi_food_system/delivery_man/delivery-order.fxml"));
                Parent deliveryDetailPage = loader.load();

                Scene currentScene = deliveryDetailTable.getScene();
                Stage currentStage = (Stage) currentScene.getWindow();

                currentScene.setRoot(deliveryDetailPage);
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

