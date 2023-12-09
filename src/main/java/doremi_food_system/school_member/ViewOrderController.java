// Programmer Name: Mr.Sim Sau Yang
// Program Name: School Member View Order Page Controller
// First Written: Saturday, 17-June-2023

package doremi_food_system.school_member;

import bean.*;
import javafx.beans.Observable;
import javafx.collections.ListChangeListener;
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

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Enable the user to view the order and its status
 */
public class ViewOrderController implements Initializable {

    @FXML
    private DatePicker dtpckrStart;

    @FXML
    private DatePicker dtpckrEnd;

    @FXML
    private RadioButton rdbtnAll;

    @FXML
    private RadioButton rdbtnDate;

    @FXML
    private RadioButton rdbtnID;

    @FXML
    private TableColumn<Transaction, LocalDateTime> tbclmnDateTime;

    @FXML
    private TableColumn<Transaction, String> tbclmnID;

    @FXML
    private TableColumn<Transaction, String> tbclmnMethod;

    @FXML
    private TableColumn<Transaction, Boolean> tbclmnStatus;

    @FXML
    private TableColumn<Transaction, Double> tbclmnTotal;

    @FXML
    private TableView<Transaction> tbvwTransaction;

    @FXML
    private TextField txtfID;

    private Connection connection;
    private final SchoolMember schoolMember = DataHolder.getInstance().getSchoolMember();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up table
        tbclmnID.setCellValueFactory(new PropertyValueFactory<>("orderID"));
        tbclmnMethod.setCellValueFactory(new PropertyValueFactory<>("method"));
        tbclmnTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        tbclmnDateTime.setCellValueFactory(new PropertyValueFactory<>("receiptGeneratedTime"));
        tbclmnStatus.setCellValueFactory(new PropertyValueFactory<>("orderStatus"));

        setRadioButton(true, true, true);

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/doremifood", "root", "ILoveU5201314");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        dtpckrEnd.valueProperty().addListener(this::searchID);

        // Search using ID
        txtfID.textProperty().addListener((observableValue, s, t1) -> {
            String input = txtfID.getText();
            tbvwTransaction.getItems().clear();
            ObservableList<Transaction> transactions = tbvwTransaction.getItems();
            if (!input.isEmpty()) {
                try {
                    // Select doremifood: order_table , order_id contains input
                    PreparedStatement psID = connection.prepareStatement("SELECT * FROM order_table o " +
                            "JOIN dining_method_table dm USING (`dining_method`) " +
                            "WHERE o.sm_id = ? AND o.order_id LIKE ? " +
                            "ORDER BY o.order_date_time DESC ");
                    psID.setString(1, schoolMember.getTpNumber());
                    psID.setString(2, "%" + input + "%");
                    ResultSet rsID = psID.executeQuery();

                    if (rsID.isBeforeFirst()) {
                        // Show the entered Order ID
                        while (rsID.next()) {
                            collectData(rsID, transactions);
                        }
                        tbvwTransaction.setItems(transactions);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        // Show the receipt by selecting the transaction from the table
        tbvwTransaction.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<>() {
            @Override
            public void onChanged(Change<? extends Transaction> change) {
                Transaction transaction = tbvwTransaction.getSelectionModel().getSelectedItem();
                if (transaction != null) {
                    try {
                        // Select doremifood: order_details_table , selected transaction match to order_id
                        PreparedStatement psTransaction = connection.prepareStatement("SELECT s.store_id, s.store_name, " +
                                "f.food_id, f.food_name, f.food_price, od.ordered_food_quantity, od.ordered_food_amount, od.ordered_food_status " +
                                "FROM order_details_table od " +
                                "JOIN store_table s ON s.store_id = od.store_id " +
                                "JOIN food_table f ON f.food_id = od.food_id " +
                                "WHERE od.order_id = ?;");
                        psTransaction.setString(1, transaction.getOrderID());

                        ResultSet rsFoodOrdered = psTransaction.executeQuery();
                        List<FoodOrdered> foodOrderedList = new ArrayList<>();
                        while (rsFoodOrdered.next()) {
                            String storeID = rsFoodOrdered.getString("store_id");
                            String storeName = rsFoodOrdered.getString("store_name");
                            String foodID = rsFoodOrdered.getString("food_id");
                            String foodName = rsFoodOrdered.getString("food_name");
                            double price = rsFoodOrdered.getDouble("food_price");
                            int quantity = rsFoodOrdered.getInt("ordered_food_quantity");
                            double amount = rsFoodOrdered.getDouble("ordered_food_amount");
                            boolean status = rsFoodOrdered.getBoolean("ordered_food_status");

                            foodOrderedList.add(new FoodOrdered(new Food(new FoodSeller(storeID, null, storeName, 0), foodID, foodName, price, null, null)
                                    , quantity, amount, status));
                        }
                        transaction.setFoodOrdered(foodOrderedList);
                        DataHolder.getInstance().setTransaction(transaction);

                        Image icon = new Image(getClass().getResourceAsStream("/doremi_food_system/picture/icon.png"));
                        // Open food status page
                        FXMLLoader foodLoader = new FXMLLoader();
                        foodLoader.setLocation(getClass().getResource("/doremi_food_system/school_member/foodstatus-page.fxml"));
                        Stage foodStage = new Stage();
                        foodStage.setScene(new Scene(foodLoader.load()));
                        foodStage.getIcons().add(icon);
                        foodStage.setTitle("Food Status");
                        foodStage.setResizable(false);
                        foodStage.centerOnScreen();
                        foodStage.show();

                        // Open receipt page
                        FXMLLoader receiptLoader = new FXMLLoader();
                        receiptLoader.setLocation(getClass().getResource("/doremi_food_system/school_member/receipt-page.fxml"));
                        Stage receiptStage = new Stage();
                        receiptStage.setX(50);
                        receiptStage.setY(50);
                        receiptStage.setScene(new Scene(receiptLoader.load()));
                        receiptStage.getIcons().add(icon);
                        receiptStage.setTitle("Receipt");
                        receiptStage.setResizable(false);
                        receiptStage.show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * Search the order by date
     */
    private void searchID(Observable observable) {
        tbvwTransaction.getItems().clear();

        LocalDate startDate = dtpckrStart.getValue();
        LocalDate endDate = dtpckrEnd.getValue();

        if (startDate.isBefore(endDate)) {
            // Get start date and end date
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
            Timestamp startTimeStamp = Timestamp.valueOf(startDateTime);
            Timestamp endTimeStamp = Timestamp.valueOf(endDateTime);

            ObservableList<Transaction> transactions = tbvwTransaction.getItems();
            // Select doremifood: order_table , order(s) between both dates is shown
            try {
                PreparedStatement psDate = connection.prepareStatement("SELECT * FROM order_table o " +
                        "JOIN dining_method_table dm USING (`dining_method`) " +
                        "WHERE o.sm_id = ? AND o.order_date_time >= ? AND o.order_date_time <= ? " +
                        "ORDER BY o.order_date_time DESC ");
                psDate.setString(1, schoolMember.getTpNumber());
                psDate.setTimestamp(2, startTimeStamp);
                psDate.setTimestamp(3, endTimeStamp);
                ResultSet rsDate = psDate.executeQuery();

                if (rsDate.isBeforeFirst()) {
                    while (rsDate.next()) {
                        collectData(rsDate, transactions);
                    }
                    tbvwTransaction.setItems(transactions);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Set the disability of attribute
     *
     * @param start start date picker
     * @param end   end date picker
     * @param txtf  text field for searching
     */
    public void setRadioButton(boolean start, boolean end, boolean txtf) {
        dtpckrStart.setDisable(start);
        dtpckrEnd.setDisable(end);
        txtfID.setDisable(txtf);
    }

    /**
     * Set the disability of attribute according to the radio button chosen
     */
    @FXML
    public void check() {
        tbvwTransaction.getItems().clear();
        if (rdbtnDate.isSelected()) {
            setRadioButton(false, false, true);
            txtfID.clear();
        } else if (rdbtnID.isSelected()) {
            setRadioButton(true, true, false);
        } else if (rdbtnAll.isSelected()) {
            setRadioButton(true, true, true);
            txtfID.clear();

            // Search doremifood: order_table
            try {
                PreparedStatement psAllTransaction = connection.prepareStatement("SELECT * FROM order_table o " +
                        "JOIN dining_method_table dm USING (`dining_method`) WHERE o.sm_id = ? ORDER BY o.order_date_time DESC");
                psAllTransaction.setString(1, schoolMember.getTpNumber());
                ResultSet rsAllTransaction = psAllTransaction.executeQuery();

                // show all order(s)
                ObservableList<Transaction> transactions = tbvwTransaction.getItems();
                if (rsAllTransaction.isBeforeFirst()) {
                    while (rsAllTransaction.next()) {
                        collectData(rsAllTransaction, transactions);
                    }
                    tbvwTransaction.setItems(transactions);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Retrieve the data from result set and add into an observable list
     *
     * @param rs           result set to store the data collected
     * @param transactions observable list for table showing
     * @throws SQLException exception when error on accessing database
     */
    public void collectData(ResultSet rs, ObservableList<Transaction> transactions) throws SQLException {
        String orderID = rs.getString("order_id");
        String tpNumber = rs.getString("sm_id");
        String method = rs.getString("dining_method");
        String deliveryAddress = rs.getString("order_delivery_address");
        double total = rs.getDouble("order_total");
        Timestamp timestamp = rs.getTimestamp("order_date_time");
        LocalDateTime receiptGeneratedTime = timestamp.toLocalDateTime();
        double deliveryFee = rs.getDouble("method_fee");
        boolean status = rs.getBoolean("order_status");

        transactions.add(new Transaction(orderID, tpNumber, total, receiptGeneratedTime, method
                , null, deliveryFee, deliveryAddress, null, status));
    }

    /**
     * Back to user main page
     */
    @FXML
    public void goBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/doremi_food_system/school_member/main-page.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("Doremi Food");
            stage.show();
            DataHolder.deleteInstance();
            DataHolder.getInstance().setSchoolMember(schoolMember);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
