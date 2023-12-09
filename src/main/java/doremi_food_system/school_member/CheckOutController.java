// Programmer Name: Mr.Sim Sau Yang
// Program Name: School Member Check Out Page Controller
// First Written: Saturday, 17-June-2023

package doremi_food_system.school_member;

import bean.Food;
import bean.FoodOrdered;
import bean.SchoolMember;
import bean.Transaction;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import utils.DataHolder;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Show the order details for user to check out
 */
public class CheckOutController implements Initializable {

    @FXML
    private Label lblBalance;

    @FXML
    private Label lblDeliveryFee;

    @FXML
    private Label lblSubTotal;

    @FXML
    private Label lblTotal;

    @FXML
    private Label lblUserID;

    @FXML
    private Label lblUserName;

    @FXML
    private Label lblMethod;

    @FXML
    private TableColumn<FoodOrdered, Double> tbclmnAmount;

    @FXML
    private TableColumn<FoodOrdered, String> tbclmnFood;

    @FXML
    private TableColumn<FoodOrdered, String> tbclmnName;

    @FXML
    private TableColumn<FoodOrdered, Double> tbclmnPrice;

    @FXML
    private TableColumn<FoodOrdered, Integer> tbclmnQuantity;

    @FXML
    private TableColumn<FoodOrdered, String> tbclmnStore;

    @FXML
    private TableView<FoodOrdered> tbvwOrder = new TableView<>();

    @FXML
    private Button btnPlaceOrder;

    private final List<FoodOrdered> foodOrderedList = DataHolder.getInstance().getFoodOrdered();
    private final ObservableList<FoodOrdered> foodOrdered = tbvwOrder.getItems();
    private final SchoolMember schoolMember = DataHolder.getInstance().getSchoolMember();
    private double subTotal = 0.0;
    private final double deliveryFee = DataHolder.getInstance().getDeliveryFee();
    private double total = 0.0;
    private double newBalance = schoolMember.getAccountBalance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up table
        tbclmnStore.setCellValueFactory(cellData -> {
            FoodOrdered foodOrdered = cellData.getValue();
            Food food = foodOrdered.getFood();
            String storeID = food.getFoodSeller().getStoreID();
            return new SimpleStringProperty(storeID);
        });
        tbclmnFood.setCellValueFactory(cellData -> {
            FoodOrdered foodOrdered = cellData.getValue();
            Food food = foodOrdered.getFood();
            String foodID = food.getFoodID();
            return new SimpleStringProperty(foodID);
        });
        tbclmnName.setCellValueFactory(cellData -> {
            FoodOrdered foodOrdered = cellData.getValue();
            Food food = foodOrdered.getFood();
            String foodName = food.getFoodName();
            return new SimpleStringProperty(foodName);
        });
        tbclmnPrice.setCellValueFactory(cellData -> {
            FoodOrdered foodOrdered = cellData.getValue();
            Food food = foodOrdered.getFood();
            double price = food.getFoodPrice();
            return new SimpleDoubleProperty(price).asObject();
        });
        tbclmnQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        tbclmnAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));

        // Set up order detail
        foodOrdered.addAll(foodOrderedList);
        tbvwOrder.setItems(foodOrdered);

        lblUserID.setText(schoolMember.getTpNumber());
        lblUserName.setText(schoolMember.getName());
        lblMethod.setText(DataHolder.getInstance().getFoodMethod());


        BigDecimal bdSubTotal = BigDecimal.valueOf(subTotal);
        for (FoodOrdered ordered : foodOrderedList) {
            BigDecimal bdAmount = BigDecimal.valueOf(ordered.getAmount());
            bdSubTotal = bdSubTotal.add(bdAmount);
        }
        subTotal = bdSubTotal.doubleValue();

        BigDecimal bdDeliveryFee = BigDecimal.valueOf(deliveryFee);

        BigDecimal bdTotal = BigDecimal.valueOf(total);
        bdTotal = bdTotal.add(bdDeliveryFee).add(bdSubTotal);
        total = bdTotal.doubleValue();

        lblSubTotal.setText(subTotal + "");
        lblDeliveryFee.setText(deliveryFee + "");
        lblTotal.setText(total + "");

        BigDecimal bdNewBalance = BigDecimal.valueOf(newBalance);
        bdNewBalance = bdNewBalance.subtract(bdTotal);
        newBalance = bdNewBalance.doubleValue();
        if (schoolMember.getAccountBalance() >= total) {
            lblBalance.setText("Current value: " + schoolMember.getAccountBalance() + "\nBalance after order: " + newBalance);
        } else {
            lblBalance.setText("You do not have enough balance!" + "\nCurrent Balance: " + schoolMember.getAccountBalance());
            btnPlaceOrder.setDisable(true);
        }
    }

    /**
     * Update database and place order
     */
    @FXML
    public void placeOrder(ActionEvent event) {
        Transaction transaction;
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/doremifood", "root", "ILoveU5201314");

            // Get the last Order ID
            // Select doremifood : order_table , only order_id is needed
            PreparedStatement psGetID = connection.prepareStatement("SELECT order_id FROM order_table " +
                    "ORDER BY order_date_time DESC LIMIT 1");
            ResultSet rsLastID = psGetID.executeQuery();
            String orderID;
            // Set the new Order ID
            if (rsLastID.isBeforeFirst()) {
                rsLastID.next();
                orderID = "Z" + (Integer.parseInt(rsLastID.getString("order_id").substring(1)) + 1) + "";
            } else {
                // No data in transaction table, start with Z1
                orderID = "Z1";
            }

            String method = DataHolder.getInstance().getFoodMethod();
            String deliveryAddress = DataHolder.getInstance().getDeliveryAddress();
            LocalDateTime receiptGeneratedDateTime = LocalDateTime.now();
            // Create a new transaction object
            transaction = new Transaction(orderID, schoolMember.getTpNumber(), total, receiptGeneratedDateTime
                    , method, null, deliveryFee, deliveryAddress, foodOrderedList, false);

            // Convert LocalDateTime to Timestamp to store into database
            Timestamp timestamp = Timestamp.valueOf(transaction.getReceiptGeneratedTime());
            PreparedStatement psInsert;

            switch (method) {
                case "Dine In", "Pick Up" -> {
                    // Insert into doremifood: order_table , without delivery address
                    psInsert = connection.prepareStatement("INSERT INTO order_table " +
                            "(order_id, sm_id, order_total, order_date_time, `dining_method`, order_status) " +
                            "VALUES (?, ?, ?, ?, ?, ?)");
                    psInsert.setString(1, transaction.getOrderID());
                    psInsert.setString(2, transaction.getTpNumber());
                    psInsert.setDouble(3, transaction.getTotal());
                    psInsert.setTimestamp(4, timestamp);
                    psInsert.setString(5, transaction.getMethod());
                    psInsert.setBoolean(6, transaction.isOrderStatus());
                    psInsert.executeUpdate();
                }
                case "Delivery" -> {
                    // Insert into doremifood: order_table , with delivery address
                    psInsert = connection.prepareStatement("INSERT INTO order_table " +
                            "(order_id, sm_id, order_total, order_date_time, `dining_method`, order_delivery_address, order_status) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)");
                    psInsert.setString(1, transaction.getOrderID());
                    psInsert.setString(2, transaction.getTpNumber());
                    psInsert.setDouble(3, transaction.getTotal());
                    psInsert.setTimestamp(4, timestamp);
                    psInsert.setString(5, transaction.getMethod());
                    psInsert.setString(6, transaction.getDeliveryAddress());
                    psInsert.setBoolean(7, false);
                    psInsert.executeUpdate();
                }
                default -> {
                }
            }

            // Insert into doremifood: order_details_table
            PreparedStatement psInsertFood = connection.prepareStatement("INSERT INTO order_details_table VALUES (?, ?, ?, ?, ?, ?, ?)");
            for (FoodOrdered ordered : transaction.getFoodOrdered()) {
                psInsertFood.setString(1, transaction.getOrderID());
                psInsertFood.setString(2, ordered.getFood().getFoodSeller().getStoreID());
                psInsertFood.setString(3, ordered.getFood().getFoodID());
                psInsertFood.setDouble(4, ordered.getFood().getFoodPrice());
                psInsertFood.setInt(5, ordered.getQuantity());
                psInsertFood.setDouble(6, ordered.getAmount());
                psInsertFood.setBoolean(7, false);
                psInsertFood.executeUpdate();
            }

            // Update doremifood: school_member_table , new balance after payment
            schoolMember.setAccountBalance(newBalance);
            PreparedStatement psModifySM = connection.prepareStatement("UPDATE school_member_table " +
                    "SET sm_account_balance = ? " +
                    "WHERE sm_id = ?");
            psModifySM.setDouble(1, schoolMember.getAccountBalance());
            psModifySM.setString(2, schoolMember.getTpNumber());
            psModifySM.executeUpdate();

            // Insert into doremifood: sm_balance_history_table
            PreparedStatement psInsertHistory = connection.prepareStatement("INSERT INTO sm_balance_history_table " +
                    "(sm_id, transaction_date_time, balance_amount_changed, latest_balance, `action`) " +
                    "VALUES (?, ?, ?, ?, ?)");
            psInsertHistory.setString(1, transaction.getTpNumber());
            psInsertHistory.setTimestamp(2, timestamp);
            psInsertHistory.setDouble(3, transaction.getTotal()*-1);
            psInsertHistory.setDouble(4, schoolMember.getAccountBalance());
            psInsertHistory.setString(5, transaction.getOrderID());
            psInsertHistory.executeUpdate();

            for (FoodOrdered ordered : foodOrderedList) {
                // Select doremifood: store_table , find the store corresponding to food ordered
                PreparedStatement psGetStore = connection.prepareStatement("SELECT store_id, store_account_balance FROM store_table " +
                        "WHERE store_id = ?");
                psGetStore.setString(1, ordered.getFood().getFoodSeller().getStoreID());
                ResultSet rsStore = psGetStore.executeQuery();
                rsStore.next();
                String storeID = rsStore.getString("store_id");
                double balance = rsStore.getDouble("store_account_balance");
                BigDecimal newBalance = BigDecimal.valueOf(balance);
                newBalance = newBalance.add(BigDecimal.valueOf(ordered.getAmount()));

                // Update doremifood: store_table , new balance after receive order
                PreparedStatement psModifyFS = connection.prepareStatement("UPDATE store_table " +
                        "SET store_account_balance = ? " +
                        "WHERE store_id = ?");
                psModifyFS.setDouble(1, newBalance.doubleValue());
                psModifyFS.setString(2, storeID);
                psModifyFS.executeUpdate();
            }

            DataHolder.getInstance().setTransaction(transaction);

            // Open new receipt page
            FXMLLoader receiptLoader = new FXMLLoader();
            receiptLoader.setLocation(getClass().getResource("/doremi_food_system/school_member/receipt-page.fxml"));
            Stage receiptStage = new Stage();
            receiptStage.setScene(new Scene(receiptLoader.load()));
            Image icon = new Image(getClass().getResourceAsStream("/doremi_food_system/picture/icon.png"));
            receiptStage.getIcons().add(icon);
            receiptStage.setTitle("Receipt");
            receiptStage.setResizable(false);
            receiptStage.centerOnScreen();
            receiptStage.show();

            // Return to main page
            // Clear the booking data
            DataHolder.deleteInstance();
            DataHolder.getInstance().setSchoolMember(schoolMember);

            FXMLLoader mainLoader = new FXMLLoader();
            mainLoader.setLocation(getClass().getResource("/doremi_food_system/school_member/main-page.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(mainLoader.load());
            stage.setScene(scene);
            stage.setTitle("Doremi Food");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Back to school member cart page
     */
    @FXML
    void goBack(ActionEvent event) {
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
        DataHolder.getInstance().setDeliveryFee(0);
    }
}
