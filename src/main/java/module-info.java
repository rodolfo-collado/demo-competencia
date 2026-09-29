module ni.edu.uam.demo {
    requires javafx.controls;
    requires javafx.fxml;

    opens ni.edu.uam.demo to javafx.fxml;
    opens ni.edu.uam.demo.controller to javafx.fxml;
    opens ni.edu.uam.demo.model to javafx.base;

    exports ni.edu.uam.demo;
    exports ni.edu.uam.demo.model;
    exports ni.edu.uam.demo.service;
    exports ni.edu.uam.demo.controller;
}