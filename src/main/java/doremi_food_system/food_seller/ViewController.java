// Programmer Name: Mr.Chai Zheng Lam
// Program Name: ViewController
// First Written: Saturday, 23-June-2023
package doremi_food_system.food_seller;

import bean.FoodOrdered;
import bean.FoodSeller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import utils.DataHolder;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class ViewController implements Initializable {

    @FXML
    private Label Amount;

    @FXML
    private Label Amount1;

    @FXML
    private Label Amount11;

    @FXML
    private Label Amount111;

    @FXML
    private Button CloseButton;

    @FXML
    private Label Food;

    @FXML
    private Label Food1;

    @FXML
    private Label Food11;

    @FXML
    private Label Food111;

    @FXML
    private Label OrderID;

    @FXML
    private Label OrderID1;

    @FXML
    private Label OrderID11;

    @FXML
    private Label OrderID111;

    @FXML
    private Label Price;

    @FXML
    private Label Price1;

    @FXML
    private Label Price11;

    @FXML
    private Label Price111;

    @FXML
    private Label Quantity;

    @FXML
    private Label Quantity1;

    @FXML
    private Label Quantity11;

    @FXML
    private Label Quantity111;

    @FXML
    private Label method;
    @FXML
    private Label method1;
    @FXML
    private Label method11;
    @FXML
    private Label method111;

    private Connection connection;

    private final FoodSeller foodSeller = DataHolder.getInstance().getFoodSeller();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setData(String orderID) {
        String StoreID = foodSeller.getStoreID();
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/doremifood", "root", "ILoveU5201314");
            PreparedStatement ps = connection.prepareStatement("SELECT od.order_id, od.ordered_food_quantity, od.ordered_food_amount, od.food_price, f.food_name, ot.dining_method " +
                    "FROM order_details_table od " +
                    "INNER JOIN food_table f ON od.food_id = f.food_id " +
                    "INNER JOIN order_table ot ON od.order_id = ot.order_id " +
                    "WHERE od.order_id = ? AND od.store_id = ?");

            ps.setString(1, orderID);
            ps.setString(2, StoreID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                OrderID.setText(rs.getString("order_id"));
                Quantity.setText(Integer.toString(rs.getInt("ordered_food_quantity")));
                Price.setText(Double.toString(rs.getDouble("food_price")));
                Amount.setText(Double.toString(rs.getDouble("ordered_food_amount")));
                Food.setText(rs.getString("food_name"));
                method.setText(rs.getString("dining_method"));
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void Close(ActionEvent event) {
        Stage stage = (Stage) Food.getScene().getWindow();
        stage.close();
    }

}
