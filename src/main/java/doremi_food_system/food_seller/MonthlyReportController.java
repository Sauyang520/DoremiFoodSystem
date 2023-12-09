// Programmer Name: Mr.Chai Zheng Lam
// Program Name: MonthlyReportController
// First Written: Saturday, 23-June-2023
package doremi_food_system.food_seller;

import bean.FoodOrdered;
import bean.FoodSeller;
import javafx.beans.binding.When;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import utils.DataHolder;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class MonthlyReportController implements Initializable {

    @FXML
    private Label Food;

    @FXML
    private Label ID;

    @FXML
    private Label Method;

    @FXML
    private Label Month;

    @FXML
    private TableView<FoodOrdered> MonthTable;

    @FXML
    private TableColumn<FoodOrdered, String> MonthColumn;

    @FXML
    private Label Name;

    @FXML
    private Label OrderNumber;

    @FXML
    private Label Price;

    @FXML
    private Label StoreID;

    @FXML
    private Pane pane;

    private Connection connection;

    private final FoodSeller foodSeller = DataHolder.getInstance().getFoodSeller();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        StoreID.setText(foodSeller.getStoreID());
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/doremifood", "root", "ILoveU5201314");

            // Query to fetch order_date_time and extract month and year
            PreparedStatement psFetchMonthYear = connection.prepareStatement(
                    "SELECT MONTH(order_date_time) AS month, YEAR(order_date_time) AS year FROM order_table"
            );

            // Execute the query
            ResultSet rsMonthYear = psFetchMonthYear.executeQuery();

            // Fetch and populate the data into the MonthTable
            List<FoodOrdered> foodOrderedList = new ArrayList<>();
            Set<String> uniqueMonthYears = new HashSet<>();
            while (rsMonthYear.next()) {
                int monthValue = rsMonthYear.getInt("month");
                int yearValue = rsMonthYear.getInt("year");

                String monthYearFormat = String.format("%02d/%04d", monthValue, yearValue);
                if (!uniqueMonthYears.contains(monthYearFormat)) {
                    FoodOrdered foodOrdered = new FoodOrdered();
                    foodOrdered.setReportDate(monthYearFormat);
                    foodOrderedList.add(foodOrdered);
                    uniqueMonthYears.add(monthYearFormat);
                }
            }

            // Set the cell value factory for the MonthColumn to display the month and year
            MonthColumn.setCellValueFactory(new PropertyValueFactory<>("reportDate"));
            MonthTable.getItems().addAll(foodOrderedList);

            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        MonthTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Name.setText(foodSeller.getName());
            ID.setText(foodSeller.getStoreID());

            if (newValue != null) {
                String selectedMonthYear = newValue.getReportDate();

                try {
                    connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/doremifood", "root", "ILoveU5201314");

                    // Query to fetch food sales
                    PreparedStatement psFoodSales = connection.prepareStatement(
                            "SELECT SUM(od.ordered_food_amount) AS foodSales " +
                                    "FROM order_details_table od " +
                                    "INNER JOIN order_table o ON od.order_id = o.order_id " +
                                    "WHERE MONTH(o.order_date_time) = ? " +
                                    "AND YEAR(o.order_date_time) = ? " +
                                    "AND od.store_id = ?"
                    );
                    psFoodSales.setInt(1, Integer.parseInt(selectedMonthYear.substring(0, 2)));
                    psFoodSales.setInt(2, Integer.parseInt(selectedMonthYear.substring(3)));
                    psFoodSales.setString(3, foodSeller.getStoreID());

                    // Execute the query
                    ResultSet rsFoodSales = psFoodSales.executeQuery();
                    if (rsFoodSales.next()) {
                        double foodSales = rsFoodSales.getDouble("foodSales");
                        Price.setText(String.valueOf(foodSales));
                    }
                    Month.setText(selectedMonthYear);

                    PreparedStatement psBestSales = connection.prepareStatement(
                            "SELECT od.food_id, COUNT(od.order_id) AS totalOrders " +
                                    "FROM order_details_table od " +
                                    "JOIN order_table o ON od.order_id = o.order_id " +
                                    "WHERE od.store_id = ? " +
                                    "AND MONTH(o.order_date_time) = ? " +
                                    "AND YEAR(o.order_date_time) = ? " +
                                    "GROUP BY od.food_id " +
                                    "ORDER BY totalOrders DESC " +
                                    "LIMIT 1"
                    );
                    psBestSales.setString(1, foodSeller.getStoreID());
                    psBestSales.setInt(2, Integer.parseInt(selectedMonthYear.substring(0, 2)));
                    psBestSales.setInt(3, Integer.parseInt(selectedMonthYear.substring(3)));

                    // Execute the query to get the most frequently ordered food item
                    ResultSet rsBestSales = psBestSales.executeQuery();
                    if (rsBestSales.next()) {
                        String bestSalesFoodId = rsBestSales.getString("food_id");

                        // Query to fetch food name for the best sales
                        PreparedStatement psFoodName = connection.prepareStatement(
                                "SELECT food_name FROM food_table WHERE store_id = ? AND food_id = ?"
                        );
                        psFoodName.setString(1, foodSeller.getStoreID());
                        psFoodName.setString(2, bestSalesFoodId);

                        // Execute the query to fetch the food name
                        ResultSet rsFoodName = psFoodName.executeQuery();
                        if (rsFoodName.next()) {
                            String foodName = rsFoodName.getString("food_name");
                            Food.setText(foodName);
                        }
                    }

                    // Query to fetch order number
                    PreparedStatement psOrderNumber = connection.prepareStatement(
                            "SELECT COUNT(*) AS orderNumber FROM order_table " +
                                    "WHERE order_id IN (SELECT order_id FROM order_details_table WHERE store_id = ?) " +
                                    "AND MONTH(order_date_time) = ? " +
                                    "AND YEAR(order_date_time) = ?"
                    );
                    psOrderNumber.setString(1, foodSeller.getStoreID());
                    psOrderNumber.setInt(2, Integer.parseInt(selectedMonthYear.substring(0, 2)));
                    psOrderNumber.setInt(3, Integer.parseInt(selectedMonthYear.substring(3)));

                    // Execute the query
                    ResultSet rsOrderNumber = psOrderNumber.executeQuery();
                    if (rsOrderNumber.next()) {
                        int orderNumber = rsOrderNumber.getInt("orderNumber");
                        OrderNumber.setText(String.valueOf(orderNumber));
                    }

                    connection.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
