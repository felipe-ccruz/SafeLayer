package com.felp.frontvm;

import com.felp.frontvm.session.Navigator;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        Navigator.to(stage, "login-view.fxml", 900, 640, "SimpleVM — Login");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
