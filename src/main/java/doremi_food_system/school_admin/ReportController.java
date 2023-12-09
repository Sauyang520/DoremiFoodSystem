package doremi_food_system.school_admin;

import bean.Transaction;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.ResourceBundle;

public class ReportController implements Initializable {

    @FXML
    private ChoiceBox<String> cbxMonth;

    @FXML
    private CheckBox ckbxDelivery;

    @FXML
    private CheckBox ckbxDineIn;

    @FXML
    private CheckBox ckbxPickUp;

    @FXML
    private TableColumn<Transaction, LocalDateTime> tbclmnDateTime;

    @FXML
    private TableColumn<Transaction, String> tbclmnMethod;

    @FXML
    private TableColumn<Transaction, String> tbclmnOrder;

    @FXML
    private TableColumn<Transaction, String> tbclmnSM;

    @FXML
    private TableColumn<Transaction, Double> tbclmnTotal;

    @FXML
    private TableView<Transaction> tbvwReport;

    @FXML
    private Text txtTotalOrder;

    @FXML
    private Text txtTotalSales;

    @FXML
    private Text txtWarning;

    private Connection connection;
    private String[] month = {"January", "February", "March", "April", "May", "June", "July", "August", "September"
            , "October", "November", "December"};

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tbclmnOrder.setCellValueFactory(new PropertyValueFactory<>("orderID"));
        tbclmnSM.setCellValueFactory(new PropertyValueFactory<>("tpNumber"));
        tbclmnMethod.setCellValueFactory(new PropertyValueFactory<>("method"));
        tbclmnDateTime.setCellValueFactory(new PropertyValueFactory<>("receiptGeneratedTime"));
        tbclmnTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/doremifood", "root", "ILoveU5201314");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        cbxMonth.getItems().addAll(month);

        cbxMonth.setOnAction(event -> search());

    }

    @FXML
    public void search() {
        if (cbxMonth.getValue() == null) {
            txtWarning.setText("Please select a month.");
        } else {
            txtWarning.setText("");
            tbvwReport.getItems().clear();
            ObservableList<Transaction> order = tbvwReport.getItems();

            String month = "order_date_time BETWEEN '2023-01-01' AND '2024-01-01'";
            switch (cbxMonth.getValue()) {
                case "January":
                    month = "order_date_time BETWEEN '2023-01-01' AND '2023-02-01'";
                    break;
                case "February":
                    month = "order_date_time BETWEEN '2023-02-01' AND '2023-03-01'";
                    break;
                case "March":
                    month = "order_date_time BETWEEN '2023-03-01' AND '2023-04-01'";
                    break;
                case "April":
                    month = "order_date_time BETWEEN '2023-04-01' AND '2023-05-01'";
                    break;
                case "May":
                    month = "order_date_time BETWEEN '2023-05-01' AND '2023-06-01'";
                    break;
                case "June":
                    month = "order_date_time BETWEEN '2023-06-01' AND '2023-07-01'";
                    break;
                case "July":
                    month = "order_date_time BETWEEN '2023-07-01' AND '2023-08-01'";
                    break;
                case "August":
                    month = "order_date_time BETWEEN '2023-08-01' AND '2023-09-01'";
                    break;
                case "September":
                    month = "order_date_time BETWEEN '2023-09-01' AND '2023-10-01'";
                    break;
                case "October":
                    month = "order_date_time BETWEEN '2023-10-01' AND '2023-011-01'";
                    break;
                case "November":
                    month = "order_date_time BETWEEN '2023-11-01' AND '2023-012-01'";
                    break;
                case "December":
                    month = "order_date_time BETWEEN '2023-12-01' AND '2024-01-01'";
                    break;
            }

            try {
                if (ckbxDineIn.isSelected()) {
                    PreparedStatement psDineIn = connection.prepareStatement("SELECT * FROM order_table " +
                            "WHERE dining_method = 'Dine In' AND " + month);
                    ResultSet rsDineIn = psDineIn.executeQuery();
                    while (rsDineIn.next()) {
                        String orderID = rsDineIn.getString("order_id");
                        String smID = rsDineIn.getString("sm_id");
                        double total = rsDineIn.getDouble("order_total");
                        String method = rsDineIn.getString("dining_method");
                        LocalDateTime dateTime = rsDineIn.getTimestamp("order_date_time").toLocalDateTime();

                        order.add(new Transaction(orderID, smID, total, method, dateTime));
                    }
                }

                if (ckbxPickUp.isSelected()) {
                    PreparedStatement psPickUp = connection.prepareStatement("SELECT * FROM order_table " +
                            "WHERE dining_method = 'Pick Up' AND " + month);
                    ResultSet rsPickUp = psPickUp.executeQuery();
                    while (rsPickUp.next()) {
                        String orderID = rsPickUp.getString("order_id");
                        String smID = rsPickUp.getString("sm_id");
                        double total = rsPickUp.getDouble("order_total");
                        String method = rsPickUp.getString("dining_method");
                        LocalDateTime dateTime = rsPickUp.getTimestamp("order_date_time").toLocalDateTime();

                        order.add(new Transaction(orderID, smID, total, method, dateTime));
                    }
                }

                if (ckbxDelivery.isSelected()) {
                    PreparedStatement psDelivery = connection.prepareStatement("SELECT * FROM order_table " +
                            "WHERE dining_method = 'Delivery' AND " + month);
                    ResultSet rsDelivery = psDelivery.executeQuery();
                    while (rsDelivery.next()) {
                        String orderID = rsDelivery.getString("order_id");
                        String smID = rsDelivery.getString("sm_id");
                        double total = rsDelivery.getDouble("order_total");
                        String method = rsDelivery.getString("dining_method");
                        LocalDateTime dateTime = rsDelivery.getTimestamp("order_date_time").toLocalDateTime();

                        order.add(new Transaction(orderID, smID, total, method, dateTime));
                    }
                }

                double total = 0;
                int countOrder = 0;
                for (Transaction transaction : order) {
                    total = BigDecimal.valueOf(total).add(BigDecimal.valueOf(transaction.getTotal())).doubleValue();
                    countOrder += 1;
                }

                txtTotalSales.setText(total+"");
                txtTotalOrder.setText(countOrder+"");
                order.sort(new Comparator<Transaction>() {
                    @Override
                    public int compare(Transaction o1, Transaction o2) {
                        return o1.getReceiptGeneratedTime().compareTo(o2.getReceiptGeneratedTime());
                    }
                });
                tbvwReport.setItems(order);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void goBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/doremi_food_system/school_admin/main-page.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("Doremi Food");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
