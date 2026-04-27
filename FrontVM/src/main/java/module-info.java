module com.felp.frontvm {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.json;

    opens com.felp.frontvm to javafx.fxml;
    exports com.felp.frontvm;
}