module com.example.email {
    requires javafx.controls;
    requires javafx.fxml;

    
    exports com.example.email;
    opens com.example.email to javafx.fxml;
}

