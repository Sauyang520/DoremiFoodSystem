// Programmer Name: Mr.Sim Sau Yang
// Program Name: Food Status Page Controller
// First Written: Saturday, 18-June-2023

package doremi_food_system.school_member;

import bean.Food;
import bean.FoodOrdered;
import bean.Transaction;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import utils.DataHolder;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Show the food status
 */
public class FoodStatusController implements Initializable {

    @FXML
    private TableColumn<FoodOrdered, Double> tbclmnAmount;

    @FXML
    private TableColumn<FoodOrdered, String> tbclmnFood;

    @FXML
    private TableColumn<FoodOrdered, Double> tbclmnPrice;

    @FXML
    private TableColumn<FoodOrdered, Integer> tbclmnQuantity;

    @FXML
    private TableColumn<FoodOrdered, Boolean> tbclmnStatus;

    @FXML
    private TableColumn<FoodOrdered, String> tbclmnStore;

    @FXML
    private TableView<FoodOrdered> tbvwFoodOrdered;

    private final Transaction transaction = DataHolder.getInstance().getTransaction();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up table column
        tbclmnStore.setCellValueFactory(cellData -> {
            FoodOrdered foodOrdered = cellData.getValue();
            Food food = foodOrdered.getFood();
            String storeName = food.getFoodSeller().getName();
            return new SimpleStringProperty(storeName);
        });
        tbclmnFood.setCellValueFactory(cellData -> {
            FoodOrdered foodOrdered = cellData.getValue();
            Food food = foodOrdered.getFood();
            String foodName = food.getFoodName();
            return new SimpleStringProperty(foodName);
        });
        tbclmnPrice.setCellValueFactory(cellData -> {
            FoodOrdered foodOrdered = cellData.getValue();
            Food food = foodOrdered.getFood();
            double price = food.getFoodPrice();
            return new SimpleObjectProperty<>(price);
        });
        tbclmnQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        tbclmnAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        tbclmnStatus.setCellValueFactory(new PropertyValueFactory<>("foodStatus"));

        ObservableList<FoodOrdered> observableList = tbvwFoodOrdered.getItems();
        observableList.addAll(transaction.getFoodOrdered());
        tbvwFoodOrdered.setItems(observableList);

    }
}
