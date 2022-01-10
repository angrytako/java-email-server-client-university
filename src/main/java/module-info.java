module com.example.email {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.email to javafx.fxml;
    exports com.example.email;
    exports com.example.email.Main;
    opens com.example.email.Main to javafx.fxml;
    exports com.example.email.Controller;
    opens com.example.email.Controller to javafx.fxml;
    exports com.example.email.Client;
    opens com.example.email.Client to javafx.fxml;
    exports com.example.email.Server;
    opens com.example.email.Server to javafx.fxml;
}