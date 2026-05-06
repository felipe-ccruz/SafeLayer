package com.felp.frontvm.session;

import com.felp.frontvm.MainApp;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class Navigator {

    private Navigator() {
    }

    public static void to(Stage stage, String fxml, double width, double height, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(fxml));
            Parent root = loader.load();
            Scene scene = new Scene(root, width, height);
            stage.setTitle(title);
            stage.setScene(scene);
            stage.sizeToScene();
            stage.show();
        } catch (Exception e) {
            System.err.println("Erro ao navegar para " + fxml + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
