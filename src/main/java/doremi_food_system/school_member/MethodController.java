// Programmer Name: Mr.Sim Sau Yang
// Program Name: School Member Method Page Controller
// First Written: Saturday, 17-June-2023

package doremi_food_system.school_member;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import utils.DataHolder;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

/**
 * Enable the user to select the pick-up method
 * 1. Pick Up
 * 2. Dine In
 * 3. Delivery
 */
public class MethodController implements Initializable {

    @FXML
    private Text txtDeliveryFee;

    @FXML
    private Label lblNotice;
    @FXML
    private TextField txtFDeliveryAddress;

    private double deliveryFee;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/doremifood", "root", "ILoveU5201314");
            // Select doremifood: dining_method_table , method_fee
            PreparedStatement psGetDeliveryFee = connection.prepareStatement("SELECT method_fee FROM dining_method_table WHERE `dining_method` = 'Delivery' ");
            ResultSet rsDeliveryFee = psGetDeliveryFee.executeQuery();
            rsDeliveryFee.next();
            deliveryFee = rsDeliveryFee.getDouble("method_fee");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        txtDeliveryFee.setText(deliveryFee + "");
    }

    /**
     * Select pick up method
     */
    @FXML
    public void pickUp(ActionEvent event) {
        DataHolder.getInstance().setFoodMethod("Pick Up");
        DataHolder.getInstance().setDeliveryFee(0);
        goCheckOut(event);
    }

    /**
     * Select dine in method
     */
    @FXML
    public void dineIn(ActionEvent event) {
        DataHolder.getInstance().setFoodMethod("Dine In");
        DataHolder.getInstance().setDeliveryFee(0);
        goCheckOut(event);
    }

    /**
     * Select delivery method
     */
    @FXML
    public void delivery(ActionEvent event) {
        if (!txtFDeliveryAddress.getText().isEmpty()) {
            DataHolder.getInstance().setDeliveryAddress(txtFDeliveryAddress.getText());
            DataHolder.getInstance().setFoodMethod("Delivery");
            DataHolder.getInstance().setDeliveryFee(deliveryFee);
            goCheckOut(event);
        } else {
            lblNotice.setText("Please enter delivery address.");
        }
    }

    /**
     * Go to check out page
     */
    public void goCheckOut(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/doremi_food_system/school_member/checkout-page.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Check Out");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Back to the cart
     */
    @FXML
    public void goBack(ActionEvent event) {
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
}
