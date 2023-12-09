// Programmer Name: Mr.How Zhen Nan
// Program Name: MainController
// First Written: Tuesday, 20-June-2023


package doremi_food_system.delivery_man;

import bean.DeliveryMan;
import bean.Transaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import utils.DataHolder;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private MenuBar menuBar;
    @FXML
    private Label selectValidation;
    @FXML
    private Label deliveryBalance;

    @FXML
    private Label deliveryId;

    @FXML
    private Label deliveryName;
    @FXML
    private TableColumn<Transaction, String> OrderIDTable;

    @FXML
    private TableColumn<Transaction, String> OrderDeliveryTable;
    @FXML
    private TableView<Transaction> deliveryOrderTable;
    private final DeliveryMan deliveryMan = DataHolder.getInstance().getDeliveryMan();



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){

        deliveryId.setText(deliveryMan.getDeliveryManID());
        deliveryName.setText(deliveryMan.getName());
        deliveryBalance.setText("RM " + deliveryMan.getAccountBalance() + "");

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/doremifood", "root", "ILoveU5201314");
            String query = "SELECT ot.order_id, ot.order_delivery_address FROM order_table ot JOIN order_details_table odt ON ot.order_id = odt.order_id WHERE dining_method = 'Delivery' AND order_status = 0 AND odt.ordered_food_status = 1 AND ot.dm_id IS NULL GROUP BY ot.order_id " +
                    "HAVING COUNT(*) = (SELECT COUNT(*) FROM order_details_table WHERE order_id = ot.order_id )";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            ObservableList<Transaction> orderList = FXCollections.observableArrayList();

            while (resultSet.next()) {
                String id = resultSet.getString("order_id");
                String address = resultSet.getString("order_delivery_address");

                Transaction transaction = new Transaction(id, address);
                orderList.add(transaction);
            }

            OrderIDTable.setCellValueFactory(new PropertyValueFactory<>("orderID"));
            OrderDeliveryTable.setCellValueFactory(new PropertyValueFactory<>("deliveryAddress"));

            deliveryOrderTable.setItems(orderList);

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    };

    @FXML
    public void logout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/doremi_food_system/login-page.fxml"));
            Stage stage = (Stage) menuBar.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Doremi Food");
            stage.centerOnScreen();
            stage.show();
            DataHolder.deleteInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void selectDelivery(){
        Transaction selectedDelivery = deliveryOrderTable.getSelectionModel().getSelectedItem();
        if (selectedDelivery!=null){
            DataHolder.getInstance().setTransaction(selectedDelivery);

            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/doremi_food_system/delivery_man/delivery-detail.fxml"));
                Parent deliveryDetailPage = loader.load();

                Scene currentScene = deliveryOrderTable.getScene();
                Stage currentStage = (Stage) currentScene.getWindow();

                currentScene.setRoot(deliveryDetailPage);
                currentStage.setTitle("Delivery Detail");
                Image icon = new Image(getClass().getResourceAsStream("/doremi_food_system/delivery_man/location.png"));
                currentStage.getIcons().add(icon);
                currentStage.setResizable(false);
                currentStage.centerOnScreen();
                currentStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            selectValidation.setText("Please select an order to deliver");
        }
    }
}
