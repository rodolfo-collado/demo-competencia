module ni.edu.uam.demo {
    requires javafx.controls;
    requires javafx.fxml;


    opens ni.edu.uam.demo to javafx.fxml;
    exports ni.edu.uam.demo;
}