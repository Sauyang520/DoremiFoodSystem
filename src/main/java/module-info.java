module doremi_food_system {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens bean to javafx.base;
    opens utils to java.base;

    opens doremi_food_system to javafx.fxml;
    exports doremi_food_system;

    opens doremi_food_system.school_member to javafx.fxml;
    exports doremi_food_system.school_member;

    opens doremi_food_system.food_seller to javafx.fxml;
    exports doremi_food_system.food_seller;

    opens doremi_food_system.delivery_man to javafx.fxml;
    exports doremi_food_system.delivery_man;

    opens doremi_food_system.school_admin to javafx.fxml;
    exports doremi_food_system.school_admin;
}