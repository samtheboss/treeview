module com.smartapps.relationship {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.smartapps.relationship to javafx.fxml;
    exports com.smartapps.relationship;
}