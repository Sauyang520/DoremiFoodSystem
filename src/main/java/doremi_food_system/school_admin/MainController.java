// Programmer Name: Mr.Sim Sau Yang
// Program Name: School Admin Main Page Controller
// First Written: Saturday, 17-June-2023

package doremi_food_system.school_admin;

import bean.DeliveryMan;
import bean.FoodSeller;
import bean.SchoolMember;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private MenuBar mnbAdmin;

    @FXML
    private Label lblNotice;

    @FXML
    private Label lblBalance;

    @FXML
    private ChoiceBox<String> cbUser;

    @FXML
    private RadioButton rdbtnAll;

    @FXML
    private RadioButton rdbtnID;

    @FXML
    private RadioButton rdbtnName;

    @FXML
    private TableColumn tbclmnBalance;

    @FXML
    private TableColumn tbclmnID;

    @FXML
    private TableColumn tbclmnName;

    @FXML
    private TableColumn tbclmnPassword;

    @FXML
    private TableView tbvwUser;

    @FXML
    private TextField txtfID;

    @FXML
    private TextField txtfName;

    @FXML
    private TextField txtfPassword;

    @FXML
    private TextField txtfSearch;

    private Connection connection;

    private static final String SCHOOL_MEMBER = "School Member";
    private static final String FOOD_SELLER = "Food Seller";
    private static final String DELIVERY_MAN = "Delivery Man";
    private final String[] user = {SCHOOL_MEMBER, FOOD_SELLER, DELIVERY_MAN};
    private String id, password, name;
    private double balance;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Add the three user to choice box
        cbUser.getItems().addAll(user);
        check();

        lblBalance.setText("0.0");

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/doremifood", "root", "ILoveU5201314");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Show user data according to selected type of user
        cbUser.setOnAction(event -> {
            clearField();
            try {
                showData();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        // Paste the user information selected from table to text field
        tbvwUser.getSelectionModel().getSelectedItems().addListener((ListChangeListener) change -> {
            lblNotice.setText("");
            switch (cbUser.getValue()) {
                case SCHOOL_MEMBER -> {
                    SchoolMember selectedSM = (SchoolMember) tbvwUser.getSelectionModel().getSelectedItem();
                    if (selectedSM != null) {
                        txtfID.setText(selectedSM.getTpNumber());
                        txtfPassword.setText(selectedSM.getPassword());
                        txtfName.setText(selectedSM.getName());
                        lblBalance.setText(selectedSM.getAccountBalance() + "");
                    }
                }
                case FOOD_SELLER -> {
                    FoodSeller selectedFS = (FoodSeller) tbvwUser.getSelectionModel().getSelectedItem();
                    if (selectedFS != null) {
                        txtfID.setText(selectedFS.getStoreID());
                        txtfPassword.setText(selectedFS.getPassword());
                        txtfName.setText(selectedFS.getName());
                        lblBalance.setText(selectedFS.getAccountBalance() + "");
                    }
                }
                case DELIVERY_MAN -> {
                    DeliveryMan selectedDM = (DeliveryMan) tbvwUser.getSelectionModel().getSelectedItem();
                    if (selectedDM != null) {
                        txtfID.setText(selectedDM.getDeliveryManID());
                        txtfPassword.setText(selectedDM.getPassword());
                        txtfName.setText(selectedDM.getName());
                        lblBalance.setText(selectedDM.getAccountBalance() + "");
                    }
                }
            }
        });

        // Search using user ID or name
        txtfSearch.textProperty().addListener((observableValue, s, t1) -> {
            tbvwUser.getItems().clear();
            ObservableList users = tbvwUser.getItems();
            String input = txtfSearch.getText();

            if (!txtfSearch.getText().isEmpty()) {
                if (cbUser.getValue() != null) {
                    String sql;
                    switch (cbUser.getValue()) {
                        case SCHOOL_MEMBER -> {
                            if (rdbtnID.isSelected()) {
                                // Select doremifood: school_member_table , sm_id contains input
                                sql = "SELECT * FROM school_member_table WHERE LOWER(sm_id) LIKE LOWER(?)";
                                collectData(sql, input, users);
                            } else if (rdbtnName.isSelected()) {
                                // Select doremifood: school_member_table , sm_name contains input
                                sql = "SELECT * FROM school_member_table WHERE LOWER(sm_name) LIKE LOWER(?)";
                                collectData(sql, input, users);
                            }
                        }
                        case FOOD_SELLER -> {
                            if (rdbtnID.isSelected()) {
                                // Select doremifood: store_table , store_id contains input
                                sql = "SELECT * FROM store_table WHERE LOWER(store_id) LIKE LOWER(?)";
                                collectData(sql, input, users);
                            } else if (rdbtnName.isSelected()) {
                                // Select doremifood: store_table , store_name contains input
                                sql = "SELECT * FROM store_table WHERE LOWER(store_name) LIKE LOWER(?)";
                                collectData(sql, input, users);
                            }
                        }
                        case DELIVERY_MAN -> {
                            if (rdbtnID.isSelected()) {
                                // Select doremifood: delivery_man_table , dm_id contains input
                                sql = "SELECT * FROM delivery_man_table WHERE LOWER(dm_id) LIKE LOWER(?)";
                                collectData(sql, input, users);
                            } else if (rdbtnName.isSelected()) {
                                // Select doremifood: delivery_man_table , dm_name contains input
                                sql = "SELECT * FROM delivery_man_table WHERE LOWER(dm_name) LIKE LOWER(?)";
                                collectData(sql, input, users);
                            }
                        }
                        default -> tbvwUser.getItems().clear();
                    }
                }
            }
        });
    }

    /**
     * Get the data which ID or Name contains input
     *
     * @param sql   query statement
     * @param input user input to search for
     * @param users observablelist to show data in table
     */
    public void collectData(String sql, String input, ObservableList users) {
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, "%" + input.toLowerCase() + "%");
            ResultSet rs = ps.executeQuery();
            getData(rs, users);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 1. Collect the data from result set
     * 2. Add user object into observable list by calling define(users) method
     * 3. Show data on table
     *
     * @param rs    reseut set of query
     * @param users observable list
     * @throws SQLException exception when error on accessing database
     */
    public void getData(ResultSet rs, ObservableList users) throws SQLException {
        while (rs.next()) {
            id = rs.getString(1);
            password = rs.getString(2);
            name = rs.getString(3);
            balance = rs.getDouble(4);
            defineUser(users);
        }
        tbvwUser.setItems(users);
    }

    /**
     * Create the user object according to choice box
     *
     * @param users observable list for table showing
     */
    public void defineUser(ObservableList users) {
        switch (cbUser.getValue()) {
            case SCHOOL_MEMBER -> users.add(new SchoolMember(id, password, name, balance));
            case FOOD_SELLER -> users.add(new FoodSeller(id, password, name, balance));
            case DELIVERY_MAN -> users.add(new DeliveryMan(id, password, name, balance));
            default -> {
            }
        }
    }

    /**
     * 1. Set up the query
     * 2. Get the data and show on table by calling showSelect() method
     *
     * @throws SQLException exception when error on accessing database
     */
    private void showData() throws SQLException {
        tbvwUser.getItems().clear();
        ObservableList users = tbvwUser.getItems();
        String input = txtfSearch.getText();
        String sqlAll;
        String sqlID;
        String sqlName;
        switch (cbUser.getValue()) {
            case SCHOOL_MEMBER -> {
                setColumn("tpNumber", "name", "password", "accountBalance");

                // Select doremifood: school_member_table
                sqlAll = "SELECT * FROM school_member_table";
                // Select doremifood: school_member_table , sm_id contains input
                sqlID = "SELECT * FROM school_member_table WHERE LOWER(sm_id) LIKE LOWER(?)";
                // Select doremifood: school_member_table , sm_name contains input
                sqlName = "SELECT * FROM school_member_table WHERE LOWER(sm_name) LIKE LOWER(?) ";
                showSelect(sqlAll, sqlID, sqlName, users, input);
            }
            case FOOD_SELLER -> {
                setColumn("storeID", "name", "password", "accountBalance");

                // Select doremifood: store_table
                sqlAll = "SELECT * FROM store_table";
                // Select doremifood: store_table , store_id contains input
                sqlID = "SELECT * FROM store_table WHERE LOWER(store_id) LIKE LOWER(?)";
                // Select doremifood: store_table , store_name contains input
                sqlName = "SELECT * FROM store_table WHERE LOWER(store_name) LIKE LOWER(?)";
                showSelect(sqlAll, sqlID, sqlName, users, input);
            }
            case DELIVERY_MAN -> {
                setColumn("deliveryManID", "name", "password", "accountBalance");

                // Select doremifood: delivery_man_table
                sqlAll = "SELECT * FROM delivery_man_table";
                // Select doremifood: delivery_man_table , dm_id contains input
                sqlID = "SELECT * FROM delivery_man_table WHERE LOWER(dm_id) LIKE LOWER(?)";
                // Select doremifood: delivery_man_table , dm_name contains input
                sqlName = "SELECT * FROM delivery_man_table WHERE LOWER(dm_name) LIKE LOWER(?)";
                showSelect(sqlAll, sqlID, sqlName, users, input);
            }
            default -> {
            }
        }
    }

    /**
     * Set up table column (All the params are the user's object attribute)
     *
     * @param id       user id
     * @param name     username
     * @param password user password
     * @param balance  user balance
     */
    public void setColumn(String id, String name, String password, String balance) {
        tbclmnID.setCellValueFactory(new PropertyValueFactory<>(id));
        tbclmnName.setCellValueFactory(new PropertyValueFactory<>(name));
        tbclmnPassword.setCellValueFactory(new PropertyValueFactory<>(password));
        tbclmnBalance.setCellValueFactory(new PropertyValueFactory<>(balance));
    }

    /**
     * @param sqlAll  query (all)
     * @param sqlID   query (ID)
     * @param sqlName query (Name)
     * @param users   observable list for table showing
     * @param input   user input as query criteria
     * @throws SQLException exception when error on accessing database
     */
    public void showSelect(String sqlAll, String sqlID, String sqlName, ObservableList users, String input) throws SQLException {
        PreparedStatement ps;
        ResultSet rs;
        // Run the corresponding query according to criteria selected on radio button
        if (rdbtnAll.isSelected()) {
            ps = connection.prepareStatement(sqlAll);
            rs = ps.executeQuery();
            getData(rs, users);
        } else if (rdbtnID.isSelected()) {
            if (!txtfSearch.getText().isEmpty()) {
                ps = connection.prepareStatement(sqlID);
                ps.setString(1, "%" + input.toLowerCase() + "%");
                rs = ps.executeQuery();
                getData(rs, users);
            }
        } else if (rdbtnName.isSelected()) {
            if (!txtfSearch.getText().isEmpty()) {
                ps = connection.prepareStatement(sqlName);
                ps.setString(1, "%" + input.toLowerCase() + "%");
                rs = ps.executeQuery();
                getData(rs, users);
            }
        } else {
            tbvwUser.getItems().clear();
        }
    }

    /**
     * According to radio button selected:
     * 1. Set the disability of text field
     * 2. Set the data on table
     */
    @FXML
    void check() {
        if (rdbtnAll.isSelected()) {
            clearField();
            txtfSearch.setDisable(true);
            txtfSearch.clear();
            if (cbUser.getValue() != null) {
                try {
                    showData();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else if (rdbtnID.isSelected() || rdbtnName.isSelected()) {
            clearField();
            tbvwUser.getItems().clear();
            txtfSearch.setDisable(false);
            txtfSearch.clear();
        }
    }

    /**
     * Clear the text field
     */
    public void clearField() {
        txtfID.clear();
        txtfName.clear();
        txtfPassword.clear();
        lblBalance.setText("0.0");
    }

    /**
     * Add RM 10 to balance per clicked
     */
    @FXML
    public void addBalance() {
        try {
            double balance = Double.parseDouble(lblBalance.getText());
            BigDecimal bdBalance = BigDecimal.valueOf(balance).add(BigDecimal.valueOf(10));
            lblBalance.setText(bdBalance.doubleValue() + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Subtract RM 10 to balance per clicked
     */
    @FXML
    public void subTractBalance() {
        try {
            double balance = Double.parseDouble(lblBalance.getText());
            if (balance >= 100) {
                BigDecimal bdBalance = BigDecimal.valueOf(balance).subtract(BigDecimal.valueOf(100));
                lblBalance.setText(bdBalance.doubleValue() + "");
            } else {
                lblBalance.setText("0.0");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Add new user to doremifood
     */
    @FXML
    public void add() {
        // Validate all text field are filled
        if (!(txtfID.getText().isEmpty() || txtfName.getText().isEmpty() || txtfPassword.getText().isEmpty())) {
            // Validate user ID length is less than 8
            if (txtfID.getText().length() <= 8) {
                // Validate user type is selected
                if (cbUser.getValue() != null) {
                    String sqlSearch;
                    String sqlAdd;
                    switch (cbUser.getValue()) {
                        case SCHOOL_MEMBER -> {
                            // Select doremifood: school_member_table , input match sm_id
                            sqlSearch = "SELECT sm_id FROM school_member_table WHERE sm_id = ?";
                            // Insert into doremifood: school_member_table
                            sqlAdd = "INSERT INTO school_member_table VALUES (?, ?, ?, ?)";
                            insert(sqlSearch, sqlAdd);

                        }
                        case FOOD_SELLER -> {
                            // Select doremifood: store_id , input match store_id
                            sqlSearch = "SELECT store_id FROM store_table WHERE store_id = ?";
                            // Insert into doremifood: store_id
                            sqlAdd = "INSERT INTO store_table VALUES (?, ?, ?, ?)";
                            insert(sqlSearch, sqlAdd);
                        }
                        case DELIVERY_MAN -> {
                            // Select doremifood: delivery_man_table , input match dm_id
                            sqlSearch = "SELECT dm_id FROM delivery_man_table WHERE dm_id = ?";
                            // Insert into doremifood: delivery_man_table
                            sqlAdd = "INSERT INTO delivery_man_table VALUES (?, ?, ?, ?)";
                            insert(sqlSearch, sqlAdd);
                        }
                    }
                }else {
                    lblNotice.setText("Please select user type.");
                }
            } else {
                lblNotice.setText("The ID should not be more than 8 characters.");
            }
        } else {
            lblNotice.setText("Please complete the information");
        }
    }

    /**
     * Insert the new user into doremifood
     *
     * @param sqlSearch query to search existing account
     * @param sqlAdd    query to add user
     */
    public void insert(String sqlSearch, String sqlAdd) {
        try {
            PreparedStatement psSearch = connection.prepareStatement(sqlSearch);
            psSearch.setString(1, txtfID.getText());
            ResultSet rsSearch = psSearch.executeQuery();

            if (rsSearch.isBeforeFirst()) {
                // Existing account found
                lblNotice.setText("The account already exists");
            } else {
                double balance = Double.parseDouble(lblBalance.getText());
                PreparedStatement psAdd = connection.prepareStatement(sqlAdd);
                psAdd.setString(1, txtfID.getText());
                psAdd.setString(2, txtfPassword.getText());
                psAdd.setString(3, txtfName.getText());
                psAdd.setDouble(4, balance);
                psAdd.executeUpdate();
                lblNotice.setText("Account successfully added.");

                if (cbUser.getValue().equals(SCHOOL_MEMBER)) {
                    LocalDateTime localDateTime = LocalDateTime.now();
                    Timestamp timestamp = Timestamp.valueOf(localDateTime);
                    if (balance > 0) {
                        PreparedStatement psInsertHistory;
                        try {
                            // Insert into doremifood: sm_balance_history_table if there is amount added
                            psInsertHistory = connection.prepareStatement("INSERT INTO sm_balance_history_table " +
                                    "(sm_id, transaction_date_time, balance_amount_changed, latest_balance, `action`) " +
                                    "VALUES (?, ?, ?, ?, ?)");
                            psInsertHistory.setString(1, txtfID.getText());
                            psInsertHistory.setTimestamp(2, timestamp);
                            psInsertHistory.setDouble(3, balance);
                            psInsertHistory.setDouble(4, balance);
                            psInsertHistory.setString(5, "Top Up");
                            psInsertHistory.executeUpdate();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (cbUser.getValue().equals(FOOD_SELLER)) {
                    File file = new File("src/main/resources/doremi_food_system/store_image/" + txtfID.getText() + "/");
                    if (!(file.exists())) {
                        boolean makeFile = file.mkdirs();
                        if (makeFile) {
                            System.out.println("Successfully create directory for new food seller: " +
                                    "src/main/resources/doremi_food_system/store_image/" + txtfID.getText());
                        } else {
                            System.out.println("src/main/resources/doremi_food_system/store_image/" + txtfID.getText()
                                    + " cannot be created.");
                        }
                    }
                }

                clearField();
                showData();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Modify selected user
     */
    @FXML
    public void modify() {
        // Validate all text field are filled
        if (!(txtfID.getText().isEmpty() || txtfName.getText().isEmpty() || txtfPassword.getText().isEmpty())) {
            // Validate user ID length is less than 8
            if (txtfID.getText().length() <= 8) {
                // Validate user type is selected
                if (cbUser.getValue() != null) {
                    String sqlSearch;
                    String sqlModify;
                    PreparedStatement psModify;
                    switch (cbUser.getValue()) {
                        case SCHOOL_MEMBER -> {
                            SchoolMember schoolMember = (SchoolMember) tbvwUser.getSelectionModel().getSelectedItem();
                            if (schoolMember != null) {
                                // Select doremifood: school_member_table , input match sm_id
                                sqlSearch = "SELECT sm_id FROM school_member_table WHERE sm_id = ?";

                                // Update doremifood: school_member_table , using sm_id
                                sqlModify = "UPDATE school_member_table " +
                                        "SET `sm_id` = ?, " +
                                        "`sm_password` = ?, " +
                                        "`sm_name` = ?, " +
                                        "`sm_account_balance` = ? " +
                                        "WHERE `sm_id` = ?";
                                try {
                                    psModify = connection.prepareStatement(sqlModify);
                                    modifyUser(sqlSearch, psModify, schoolMember, null, null);
                                    clearField();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                lblNotice.setText("Please select an account to modify");
                            }
                        }
                        case FOOD_SELLER -> {
                            FoodSeller foodSeller = (FoodSeller) tbvwUser.getSelectionModel().getSelectedItem();
                            if (foodSeller != null) {
                                // Select doremifood: store_table , input match store_id
                                sqlSearch = "SELECT store_id FROM store_table WHERE store_id = ?";

                                // Update doremifood: store_table , using store_id
                                sqlModify = "UPDATE store_table " +
                                        "SET store_id = ?, " +
                                        "store_password = ?, " +
                                        "store_name = ?, " +
                                        "store_account_balance = ? " +
                                        "WHERE store_id = ?";
                                try {
                                    psModify = connection.prepareStatement(sqlModify);
                                    modifyUser(sqlSearch, psModify, null, foodSeller, null);
                                    clearField();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                lblNotice.setText("Please select an account to modify");
                            }
                        }
                        case DELIVERY_MAN -> {
                            DeliveryMan deliveryMan = (DeliveryMan) tbvwUser.getSelectionModel().getSelectedItem();
                            if (deliveryMan != null) {
                                // Select doremifood: delivery_man_table , input match dm_id
                                sqlSearch = "SELECT dm_id FROM delivery_man_table WHERE dm_id = ?";

                                // Update doremifood: delivery_man_table , using dm_id
                                sqlModify = "UPDATE delivery_man_table " +
                                        "SET dm_id = ?, " +
                                        "dm_password = ?, " +
                                        "dm_name = ?, " +
                                        "dm_account_balance = ? " +
                                        "WHERE dm_id = ?";
                                try {
                                    psModify = connection.prepareStatement(sqlModify);
                                    modifyUser(sqlSearch, psModify, null, null, deliveryMan);
                                    clearField();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                lblNotice.setText("Please select an account to modify");
                            }
                        }
                    }
                } else {
                    lblNotice.setText("Please select an account to modify.");
                }
            } else {
                lblNotice.setText("The ID should not be more than 8 characters.");
            }
        } else {
            lblNotice.setText("Please complete the information");
        }
    }

    /**
     * Prepare to modify the user account
     *
     * @param sqlSearch    query to search exist account
     * @param psModify     query to modify
     * @param schoolMember school member is selected
     * @param foodSeller   food seller is selected
     * @param deliveryMan  deliveryman is selected
     * @throws SQLException exception when error on accessing database
     */
    public void modifyUser(String sqlSearch, PreparedStatement psModify, SchoolMember schoolMember, FoodSeller foodSeller, DeliveryMan deliveryMan) throws SQLException {
        PreparedStatement psSearch = connection.prepareStatement(sqlSearch);
        psSearch.setString(1, txtfID.getText());
        ResultSet rsSearch = psSearch.executeQuery();

        if (rsSearch.isBeforeFirst()) {
            rsSearch.next();
            String id;
            switch (cbUser.getValue()) {
                case SCHOOL_MEMBER -> {
                    id = rsSearch.getString("sm_id");
                    if (schoolMember.getTpNumber().equals(id)) {
                        startModify(psModify, schoolMember, foodSeller, deliveryMan);
                    } else {
                        // Existing account found
                        lblNotice.setText("The account ID already exists!");
                    }
                }
                case FOOD_SELLER -> {
                    id = rsSearch.getString("store_id");
                    if (foodSeller.getStoreID().equals(id)) {
                        startModify(psModify, schoolMember, foodSeller, deliveryMan);
                    } else {
                        // Existing account found
                        lblNotice.setText("The account ID already exists!");
                    }
                }
                case DELIVERY_MAN -> {
                    id = rsSearch.getString("dm_id");
                    if (deliveryMan.getDeliveryManID().equals(id)) {
                        startModify(psModify, schoolMember, foodSeller, deliveryMan);
                    } else {
                        // Existing account found
                        lblNotice.setText("The account ID already exists!");
                    }
                }
            }
        } else {
            startModify(psModify, schoolMember, foodSeller, deliveryMan);
        }
    }

    /**
     * Start modify the user account
     *
     * @param psModify     query to modify
     * @param schoolMember school member is selected
     * @param foodSeller   food seller is selected
     * @param deliveryMan  deliveryman is selected
     * @throws SQLException exception when error on accessing database
     */
    public void startModify(PreparedStatement psModify, SchoolMember schoolMember, FoodSeller foodSeller, DeliveryMan deliveryMan) throws SQLException {
        double balance = Double.parseDouble(lblBalance.getText());
        psModify.setString(1, txtfID.getText());
        psModify.setString(2, txtfPassword.getText());
        psModify.setString(3, txtfName.getText());
        psModify.setDouble(4, balance);
        switch (cbUser.getValue()) {
            case SCHOOL_MEMBER -> psModify.setString(5, schoolMember.getTpNumber());
            case FOOD_SELLER -> psModify.setString(5, foodSeller.getStoreID());
            case DELIVERY_MAN -> psModify.setString(5, deliveryMan.getDeliveryManID());
        }
        psModify.executeUpdate();

        if (cbUser.getValue().equals(SCHOOL_MEMBER)) {
            LocalDateTime localDateTime = LocalDateTime.now();
            Timestamp timestamp = Timestamp.valueOf(localDateTime);
            double changeAmount = BigDecimal.valueOf(balance).subtract(BigDecimal.valueOf(schoolMember.getAccountBalance())).doubleValue();

            // Insert into doremifood: sm_balance_history_table , update the change of balance
            PreparedStatement psInsertHistory = connection.prepareStatement("INSERT INTO sm_balance_history_table " +
                    "(sm_id, transaction_date_time, balance_amount_changed, latest_balance, `action`) " +
                    "VALUES (?, ?, ?, ?, ?)");
            psInsertHistory.setString(1, txtfID.getText());
            psInsertHistory.setTimestamp(2, timestamp);
            psInsertHistory.setDouble(3, changeAmount);
            psInsertHistory.setDouble(4, balance);

            if (changeAmount > 0) {
                psInsertHistory.setString(5, "Top Up");
                psInsertHistory.executeUpdate();
            } else if (changeAmount < 0) {
                psInsertHistory.setString(5, "Withdraw");
                psInsertHistory.executeUpdate();
            }

        }

        if (cbUser.getValue().equals(FOOD_SELLER)) {
            String oldDirectoryName = "src/main/resources/doremi_food_system/store_image/" + foodSeller.getStoreID() + "/";
            File oldDirectory = new File(oldDirectoryName);
            String newDirectoryName = "src/main/resources/doremi_food_system/store_image/" + txtfID.getText() + "/";
            File newDirectory = new File(newDirectoryName);
            boolean rename = oldDirectory.renameTo(newDirectory);
            if (rename) {
                System.out.println("Successfully rename directory for food seller: " + newDirectoryName);
            } else {
                System.out.println(oldDirectory + " cannot be renamed.");
            }
        }

        showData();
        lblNotice.setText("Account successfully modified.");
    }

    /**
     * Enter view report page
     */
    @FXML
    public void report() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/doremi_food_system/school_admin/report-page.fxml"));
            Stage stage = (Stage) mnbAdmin.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Report");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Logout to login page
     */
    @FXML
    public void logout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/doremi_food_system/login-page.fxml"));
            Stage stage = (Stage) mnbAdmin.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Doremi Food");
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}