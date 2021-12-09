module com.example.email {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.email to javafx.fxml;
    exports com.example.email;
}