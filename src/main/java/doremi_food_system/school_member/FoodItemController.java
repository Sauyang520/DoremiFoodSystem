// Programmer Name: Mr.Sim Sau Yang
// Program Name: School Member Food Item Page Controller
// First Written: Saturday, 17-June-2023

package doremi_food_system.school_member;

import bean.Food;
import bean.FoodOrdered;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import utils.DataHolder;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Enable user to select the corresponding food item with desired quantity
 */
public class FoodItemController implements Initializable {

    @FXML
    private Label lblID;

    @FXML
    private Label lblAmount;

    @FXML
    private Label lblDescription;

    @FXML
    private Label lblName;

    @FXML
    private Label lblPrice;

    @FXML
    private Spinner<Integer> spnQuantity;

    @FXML
    private ImageView imgvwFood;

    private final Food food = DataHolder.getInstance().getFood();
    int quantity;
    double amount;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up food item information
        lblID.setText(food.getFoodSeller().getStoreID() + food.getFoodID());
        lblName.setText(food.getFoodName());
        lblPrice.setText(food.getFoodPrice() + "");
        lblDescription.setText(food.getFoodDescription());

        // Set up spinner
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100);
        valueFactory.setValue(1);
        spnQuantity.setValueFactory(valueFactory);
        lblAmount.setText(food.getFoodPrice() + "");
        quantity = spnQuantity.getValue();
        BigDecimal bdAmount = BigDecimal.valueOf(food.getFoodPrice()).multiply(BigDecimal.valueOf(quantity));
        amount = bdAmount.doubleValue();

        // Calculate the amount according to quantity
        spnQuantity.valueProperty().addListener((observableValue, integer, t1) -> {
            quantity = spnQuantity.getValue();
            BigDecimal bdAmountChange = BigDecimal.valueOf(food.getFoodPrice()).multiply(BigDecimal.valueOf(quantity));
            amount = bdAmountChange.doubleValue();
            lblAmount.setText(amount + "");
        });

        // Show the food item image only if:
        // 1. doremifood: food - Image_Path != null
        // 2. doremifood: food - Image_Path format is correct
        // 3. image is in the corresponding path
        try {
            Image foodImage = new Image(getClass().getResourceAsStream(DataHolder.getInstance().getFood().getImagePath()));
            imgvwFood.setImage(foodImage);
        } catch (Exception e) {
            Image noImage = new Image(getClass().getResourceAsStream("/doremi_food_system/school_member/imagenotavailable.png"));
            imgvwFood.setImage(noImage);
        }
    }

    /**
     * Add the food item to cart
     */
    @FXML
    void confirm(ActionEvent event) {
        FoodOrdered repeatedFood = null;
        FoodOrdered oldOrder = null;
        if (DataHolder.getInstance().getFoodOrdered() != null) {
            // Search for existing food item in cart
            for (FoodOrdered foodOrdered : DataHolder.getInstance().getFoodOrdered()) {
                if (foodOrdered.getFood().getFoodID().equals(food.getFoodID())) {
                    repeatedFood = foodOrdered;
                    oldOrder = foodOrdered;
                }
            }
        }

        if (repeatedFood != null) {
            // If the item exists, the quantity will be added to previous same item
            repeatedFood.setQuantity(repeatedFood.getQuantity() + spnQuantity.getValue());
            repeatedFood.setAmount(repeatedFood.getFood().getFoodPrice() * repeatedFood.getQuantity());
            DataHolder.getInstance().getFoodOrdered().remove(oldOrder);
            DataHolder.getInstance().getFoodOrdered().add(repeatedFood);
        } else {
            // The item is new, add to cart
            FoodOrdered foodOrdered = new FoodOrdered(food, quantity, amount, false);
            if (DataHolder.getInstance().getFoodOrdered() == null) {
                List<FoodOrdered> foodOrderedList = new ArrayList<>(List.of(foodOrdered));
                DataHolder.getInstance().setFoodOrdered(foodOrderedList);
            } else {
                DataHolder.getInstance().getFoodOrdered().add(foodOrdered);
            }
        }

        // Close the page
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
