module com.felp.frontvm {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.json;
    requires java.net.http;

    opens com.felp.frontvm to javafx.fxml;
    opens com.felp.frontvm.controller to javafx.fxml;
    exports com.felp.frontvm;
}