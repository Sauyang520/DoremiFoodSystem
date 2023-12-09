// Programmer Name: Mr.Sim Sau Yang
// Program Name: Login Page Controller
// First Written: Saturday, 17-June-2023

package doremi_food_system;

import bean.DeliveryMan;
import bean.FoodSeller;
import bean.SchoolAdmin;
import bean.SchoolMember;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utils.DataHolder;

import java.sql.*;

/**
 * Login page enable the four types of users:
 * 1. School member
 * 2. Food Seller
 * 3. Delivery Man
 * 4. School Administrator
 * to enter their username and password to enter corresponding page
 */
public class LoginController {

    @FXML
    private Label welcomeText;

    @FXML
    private PasswordField pwFPassword;

    @FXML
    private TextField txtFUsername;

    private Connection connection;

    /**
     * Prepare the data retrieved from database for login
     *
     * @param sql      query
     * @param username input username
     * @param password input password
     * @return ResultSet the data retrieved
     * @throws SQLException exception when error on accessing database
     */
    public ResultSet getResult(String sql, String username, String password) throws SQLException {
        PreparedStatement psSearchUser = connection.prepareStatement(sql);
        psSearchUser.setString(1, username);
        psSearchUser.setString(2, password);
        return psSearchUser.executeQuery();
    }

    /**
     * Validate the input to proceed to the next stage
     */
    @FXML
    protected void login(ActionEvent event) {
        String username = txtFUsername.getText();
        String password = pwFPassword.getText();

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/doremifood", "root", "ILoveU5201314");
            // Select doremifood: school_member_info , input match id and password
            String searchSchoolMember = "SELECT * FROM school_member_table WHERE BINARY sm_id = ? AND BINARY sm_password = ?";
            ResultSet resultSet = getResult(searchSchoolMember, username, password);

            if (resultSet.isBeforeFirst()) {
                // school member account founded
                resultSet.next();
                SchoolMember schoolMember = new SchoolMember(resultSet.getString("sm_id")
                        , resultSet.getString("sm_password"), resultSet.getString("sm_name")
                        , resultSet.getDouble("sm_account_balance"));

                // Enter school member main page
                DataHolder.getInstance().setSchoolMember(schoolMember);
                goToNextPage("/doremi_food_system/school_member/main-page.fxml", event);

            } else {
                // school member account not found, search for food seller
                // Select doremifood: food_seller_info , input match id and password
                String searchFoodSeller = "SELECT * FROM store_table WHERE BINARY store_id = ? AND BINARY store_password = ?";
                resultSet = getResult(searchFoodSeller, username, password);

                if (resultSet.isBeforeFirst()) {
                    // food seller account founded
                    resultSet.next();
                    FoodSeller foodSeller = new FoodSeller(resultSet.getString("store_id")
                            , resultSet.getString("store_password"), resultSet.getString("store_name")
                            , resultSet.getDouble("store_account_balance"));

                    // Enter food seller main page
                    DataHolder.getInstance().setFoodSeller(foodSeller);
                    goToNextPage("/doremi_food_system/food_seller/main-page.fxml", event);

                } else {
                    // food seller account not found, search for delivery man
                    // Select doremifood: delivery_man_info , input match id and password
                    String searchDeliveryMan = "SELECT * FROM delivery_man_table WHERE BINARY dm_id = ? AND BINARY dm_password = ?";
                    resultSet = getResult(searchDeliveryMan, username, password);

                    if (resultSet.isBeforeFirst()) {
                        // delivery man account founded
                        resultSet.next();
                        DeliveryMan deliveryMan = new DeliveryMan(resultSet.getString("dm_id")
                                , resultSet.getString("dm_password"), resultSet.getString("dm_name")
                                , resultSet.getDouble("dm_account_balance"));

                        // Enter delivery man main page
                        DataHolder.getInstance().setDeliveryMan(deliveryMan);
                        goToNextPage("/doremi_food_system/delivery_man/main-page.fxml", event);

                    } else {
                        // delivery man account not found, search for school admin
                        // Select doremifood: school_admin_info , input match id and password
                        String searchAdmin = "SELECT * FROM school_administrator_table WHERE BINARY am_id = ? AND BINARY am_password = ?";
                        resultSet = getResult(searchAdmin, username, password);

                        if (resultSet.isBeforeFirst()) {
                            // school admin account not founded
                            resultSet.next();
                            SchoolAdmin schoolAdmin = new SchoolAdmin(resultSet.getString("am_id")
                                    , resultSet.getString("am_password"), resultSet.getString("am_name"));

                            // Enter school admin main page
                            DataHolder.getInstance().setSchoolAdmin(schoolAdmin);
                            goToNextPage("/doremi_food_system/school_admin/main-page.fxml", event);

                        } else {
                            // Incorrect input
                            welcomeText.setText("Please enter correct information.");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Proceed to the corresponding page
     *
     * @param fxmlFile  the fxml file name
     * @param event     used to get the current stage
     */
    public void goToNextPage(String fxmlFile, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(fxmlFile));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.setTitle("Doremi Food");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}