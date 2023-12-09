// Programmer Name: Mr.Sim Sau Yang
// Program Name: Balance History Page Controller
// First Written: Saturday, 23-June-2023

package doremi_food_system.school_member;

import bean.BalanceHistory;
import bean.SchoolMember;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import utils.DataHolder;

import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class BalanceHistoryController implements Initializable {

    @FXML
    private Label lblBalance;

    @FXML
    private TableColumn<BalanceHistory, String> tbclmAction;

    @FXML
    private TableColumn<BalanceHistory, Double> tbclmBalance;

    @FXML
    private TableColumn<BalanceHistory, Double> tbclmChange;

    @FXML
    private TableColumn<BalanceHistory, LocalDateTime> tbclmDate;

    @FXML
    private TableView<BalanceHistory> tbvwHistory;

    private SchoolMember schoolMember = DataHolder.getInstance().getSchoolMember();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Set up column
        tbclmDate.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        tbclmChange.setCellValueFactory(new PropertyValueFactory<>("changeAmount"));
        tbclmBalance.setCellValueFactory(new PropertyValueFactory<>("newBalance"));
        tbclmAction.setCellValueFactory(new PropertyValueFactory<>("action"));


        lblBalance.setText("RM " + schoolMember.getAccountBalance()+"");

        ObservableList<BalanceHistory> balanceHistory = tbvwHistory.getItems();

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/doremifood", "root", "ILoveU5201314");

            // Select doremifood: sm_balance_history_table
            PreparedStatement psGetHistory = connection.prepareStatement("SELECT * FROM sm_balance_history_table " +
                    "WHERE sm_id = ? " +
                    "ORDER BY transaction_number DESC ");

            psGetHistory.setString(1, schoolMember.getTpNumber());
            ResultSet rsHistory = psGetHistory.executeQuery();

            while (rsHistory.next()){
                int transactionNumber = rsHistory.getInt("transaction_number");
                LocalDateTime dateTime = rsHistory.getTimestamp("transaction_date_time").toLocalDateTime();
                double changeAmount = rsHistory.getDouble("balance_amount_changed");
                double newBalance = rsHistory.getDouble("latest_balance");
                String action = rsHistory.getString("action");

                balanceHistory.add(new BalanceHistory(schoolMember.getTpNumber(), transactionNumber, dateTime, changeAmount, newBalance, action));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        tbvwHistory.setItems(balanceHistory);
    }

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
