module com.example.email {
    requires javafx.controls;
    requires javafx.fxml;

    
    exports com.example.email;
    opens com.example.email to javafx.fxml;
    exports com.example.email.client;
    opens com.example.email.client to javafx.fxml;
    exports com.example.email.server;
    opens com.example.email.server to javafx.fxml;
}

