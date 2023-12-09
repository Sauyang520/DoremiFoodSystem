// Programmer Name: Mr.Chai Zheng Lam
// Program Name: ManageFoodMenuController
// First Written: Saturday, 23-June-2023
package doremi_food_system.food_seller;

import bean.Food;
import bean.FoodSeller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import utils.DataHolder;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ManageFoodMenuController implements Initializable {
    @FXML
    private Button ModifyButton;
    @FXML
    private Button AddButton;
    @FXML
    private TableView<Food> FoodMenuTable;

    @FXML
    private TableColumn<Food, String> description;

    @FXML
    private TableColumn<Food, String> foodID;

    @FXML
    private TableColumn<Food, String> foodName;

    @FXML
    private TableColumn<Food, Double> price;

    String StoreID;

    private Connection connection;
    private final FoodSeller foodSeller = DataHolder.getInstance().getFoodSeller();

    private final Food food = DataHolder.getInstance().getFood();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // set up table
        foodID.setCellValueFactory(new PropertyValueFactory<>("foodID"));
        foodName.setCellValueFactory(new PropertyValueFactory<>("foodName"));
        price.setCellValueFactory(new PropertyValueFactory<>("foodPrice"));
        description.setCellValueFactory(new PropertyValueFactory<>("foodDescription"));


        StoreID = foodSeller.getStoreID();
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/doremifood", "root", "ILoveU5201314");
            // 拿东西的query
            PreparedStatement psFood;
            // query拿到的东西
            ResultSet rsFood;
            // 改一下 如果不要 加N/A 名字
            psFood = connection.prepareStatement("SELECT * FROM food_table WHERE store_id = ?");
            psFood.setString(1, StoreID);
            rsFood = psFood.executeQuery();
            List<Food> foodList = new ArrayList<>();
            while (rsFood.next()) {
                String foodID = rsFood.getString("food_id");
                String foodName = rsFood.getString("food_name");
                double foodPrice = rsFood.getDouble("food_price");
                String foodDescription = rsFood.getString("food_description");
                String path = rsFood.getString("food_image_path");

                Food food = new Food(foodID, foodName, foodPrice, foodDescription, path);
                foodList.add(food);
            }

            FoodMenuTable.getItems().addAll(foodList);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @FXML
    public void AddForm(ActionEvent event) throws IOException {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddFood-page.fxml"));
            Parent root = loader.load();

            Stage windowStage = new Stage();
            windowStage.setScene(new Scene(root));
            windowStage.setTitle("Add Form");
            Image icon = new Image(getClass().getResourceAsStream("/doremi_food_system/picture/icon.png"));
            windowStage.getIcons().add(icon);
            windowStage.setResizable(false);
            windowStage.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    public void ModifyForm(ActionEvent event) throws IOException{
        try {
            Food selectedFood = FoodMenuTable.getSelectionModel().getSelectedItem();
            if (selectedFood == null) {
                // No food item is selected
                return;
            }

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ModifyFood-page.fxml"));
                Parent root = loader.load();

                // Get the controller instance
                ModifyFoodController modifyController = loader.getController();

                // Set the selected food data into the ModifyFoodController fields
                modifyController.setFoodData(selectedFood.getFoodID(), selectedFood.getFoodName(), selectedFood.getFoodPrice(), selectedFood.getFoodDescription(), selectedFood.getImagePath());

                Stage windowStage = new Stage();
                windowStage.setScene(new Scene(root));
                windowStage.setTitle("Modify Form");
                Image icon = new Image(getClass().getResourceAsStream("/doremi_food_system/picture/icon.png"));
                windowStage.getIcons().add(icon);
                windowStage.setResizable(false);
                windowStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
