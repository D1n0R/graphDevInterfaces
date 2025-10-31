module com.petstore {
    requires javafx.controls;
    requires javafx.fxml;
    requires rest.assured;
    requires java.sql;
    requires rest.assured.common;
    requires com.fasterxml.jackson.databind;


    opens com.petstore.ui to javafx.fxml;
    exports com.petstore.ui;

    //opens com.petstore.models to com.fasterxml.jackson.databind;
    opens com.petstore.models;
}