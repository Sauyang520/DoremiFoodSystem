// Programmer Name: Mr.Sim Sau Yang
// Program Name: School Member Cart Page Controller
// First Written: Saturday, 17-June-2023

package doremi_food_system.school_member;

import bean.Food;
import bean.FoodOrdered;
import javafx.beans.Observable;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import utils.DataHolder;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Enable user to view selected items, and modify or delete the item
 */
public class CartController implements Initializable {

    @FXML
    private Label lblTotal;

    @FXML
    private Button btnNext;

    @FXML
    private Spinner<Integer> spnQuantity;

    @FXML
    private TableColumn<FoodOrdered, Double> tbclmnAmount;

    @FXML
    private TableColumn<FoodOrdered, String> tbclmnFood;

    @FXML
    private TableColumn<FoodOrdered, String> tbclmnName;

    @FXML
    private TableColumn<FoodOrdered, Double> tbclmnPrice;

    @FXML
    private TableColumn<FoodOrdered, Integer> tbclmnQuantity;

    @FXML
    private TableColumn<FoodOrdered, String> tbclmnStore;

    @FXML
    private TableView<FoodOrdered> tbvwFoodOrdered = new TableView<>();

    private final SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100);
    private final ObservableList<FoodOrdered> foodOrdered = tbvwFoodOrdered.getItems();
    private final List<FoodOrdered> foodOrderedList = DataHolder.getInstance().getFoodOrdered();
    private FoodOrdered selectedFood;
    private double total = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (!(foodOrderedList == null)) {
            if (!(foodOrderedList.size() > 0))
                btnNext.setDisable(true);
        } else {
            btnNext.setDisable(true);
        }

        // Set up table column
        tbclmnStore.setCellValueFactory(cellData -> {
            FoodOrdered foodOrdered = cellData.getValue();
            Food food = foodOrdered.getFood();
            String storeID = food.getFoodSeller().getStoreID();
            return new SimpleStringProperty(storeID);
        });
        tbclmnFood.setCellValueFactory(cellData -> {
            FoodOrdered foodOrdered = cellData.getValue();
            Food food = foodOrdered.getFood();
            String foodID = food.getFoodID();
            return new SimpleStringProperty(foodID);
        });
        tbclmnName.setCellValueFactory(cellData -> {
            FoodOrdered foodOrdered = cellData.getValue();
            Food food = foodOrdered.getFood();
            String foodName = food.getFoodName();
            return new SimpleStringProperty(foodName);
        });
        tbclmnPrice.setCellValueFactory(cellData -> {
            FoodOrdered foodOrdered = cellData.getValue();
            Food food = foodOrdered.getFood();
            double price = food.getFoodPrice();
            return new SimpleDoubleProperty(price).asObject();
        });
        tbclmnQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        tbclmnAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));


        if (foodOrderedList != null) {
            foodOrdered.addAll(foodOrderedList);
            tbvwFoodOrdered.setItems(foodOrdered);

            // Calculate the subtotal
            BigDecimal bdTotal = BigDecimal.valueOf(total);
            for (FoodOrdered ordered : foodOrderedList) {
                bdTotal = bdTotal.add(BigDecimal.valueOf(ordered.getAmount()));
            }
            total = bdTotal.doubleValue();
            lblTotal.setText(total + "");
        }


        tbvwFoodOrdered.getSelectionModel().getSelectedItems().addListener(this::selectItem);

        // Modify the quantity of food item
        spnQuantity.valueProperty().addListener((observableValue, integer, t1) -> {
            if (selectedFood != null) {
                selectedFood.setQuantity(spnQuantity.getValue());
                BigDecimal amount = BigDecimal.valueOf(selectedFood.getFood().getFoodPrice())
                        .multiply(BigDecimal.valueOf(selectedFood.getQuantity()));
                selectedFood.setAmount(amount.doubleValue());
                tbvwFoodOrdered.refresh();

                // Making changes to the total when the quantity of food item selected is changed
                total = 0;
                BigDecimal bdTotal = BigDecimal.valueOf(total);
                if (foodOrderedList != null) {
                    for (FoodOrdered ordered : foodOrderedList) {
                        bdTotal = bdTotal.add(BigDecimal.valueOf(ordered.getAmount()));
                    }
                    lblTotal.setText(bdTotal + "");
                }
            }
        });
    }

    /**
     * Identify the selected food item from the table
     */
    public void selectItem(Observable observable) {
        // Paste the selected food item quantity to spinner
        selectedFood = tbvwFoodOrdered.getSelectionModel().getSelectedItem();
        if (selectedFood != null) {
            valueFactory.setValue(selectedFood.getQuantity());
            spnQuantity.setValueFactory(valueFactory);
        }
    }

    /**
     * Delete the selected food item
     */
    @FXML
    public void delete() {
        selectedFood = tbvwFoodOrdered.getSelectionModel().getSelectedItem();
        if (selectedFood != null || foodOrderedList != null) {
            DataHolder.getInstance().getFoodOrdered().remove(selectedFood);

            foodOrdered.remove(selectedFood);
            tbvwFoodOrdered.refresh();
        }
        if (foodOrderedList != null) {
            if (!(foodOrderedList.size() > 0)) {
                btnNext.setDisable(true);
                lblTotal.setText("0");
            }
        }
    }

    /**
     * Go to select method page
     */
    @FXML
    public void next(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/doremi_food_system/school_member/method-page.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("Method");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Back to the select food item main page
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
