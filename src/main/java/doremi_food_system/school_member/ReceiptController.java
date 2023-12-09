// Programmer Name: Mr.Sim Sau Yang
// Program Name: School Member Receipt Page Controller
// First Written: Saturday, 17-June-2023

package doremi_food_system.school_member;

import bean.FoodOrdered;
import bean.Transaction;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PrinterJob;
import javafx.scene.text.Text;
import utils.DataHolder;

import java.math.BigDecimal;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Show the receipt and enable user to print
 */
public class ReceiptController implements Initializable {

    @FXML
    private Text txtReceipt;

    private final Transaction transaction = DataHolder.getInstance().getTransaction();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up receipt details
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String receiptGenerateDateTime = transaction.getReceiptGeneratedTime().format(dateTimeFormatter);

        StringBuilder sb = new StringBuilder();
        sb.append("****************************************************************\n")
                .append("###                                Doremi Food                                ###\n")
                .append("****************************************************************\n")
                .append("Order ID\t\t: ").append(transaction.getOrderID()).append("\n")
                .append("TP Number\t: ").append(transaction.getTpNumber()).append("\n")
                .append("Method\t\t: ").append(transaction.getMethod()).append("\n")
                .append("Date Time\t: ").append(receiptGenerateDateTime).append("\n")
                .append("________________________________________________________________\n")
                .append("ID\t\t\tName\t\tPrice\t\tQuantity\tAmount\n");

        double subtotal = 0.0;
        BigDecimal bdSubtotal = BigDecimal.valueOf(subtotal);
        for (FoodOrdered foodOrdered : transaction.getFoodOrdered()) {
            bdSubtotal = bdSubtotal.add(BigDecimal.valueOf(foodOrdered.getAmount()));
            String id = foodOrdered.getFood().getFoodSeller().getStoreID() + foodOrdered.getFood().getFoodID();
            String name = foodOrdered.getFood().getFoodName();
            String price = foodOrdered.getFood().getFoodPrice() + "";
            sb.append(id).append("\t").append(name).append("\t").append(price).append("\t\t")
                    .append(foodOrdered.getQuantity()).append("\t\t").append(foodOrdered.getAmount()).append("\n");
        }
        subtotal = bdSubtotal.doubleValue();
        sb.append("________________________________________________________________\n")
                .append("SubTotal\t\t: ").append(subtotal).append("\n")
                .append("Delivery Fee\t: ").append(transaction.getDeliveryFee()).append("\n")
                .append("________________________________________________________________\n\n")
                .append("Total (RM)\t: ").append(transaction.getTotal()).append("\n\n")
                .append("****************************************************************\n\n")
                .append("             Thank you for using Doremi Food System             \n\n");

        txtReceipt.setText(sb.toString());
    }

    /**
     * Print the receipt
     */
    @FXML
    public void print() {
        PrinterJob printerJob = PrinterJob.createPrinterJob();
        if (printerJob != null) {
            boolean printed = printerJob.printPage(txtReceipt);
            if (printed) {
                printerJob.endJob();
            }
        }
    }
}
