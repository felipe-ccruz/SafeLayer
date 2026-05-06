package com.felp.frontvm.controller;

import com.felp.frontvm.model.VmModel;
import com.felp.frontvm.service.VmApiService;
import com.felp.frontvm.ui.LucideIcons;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class VmViewerController {

    private static final String UBUNTU_IMAGE = "/com/felp/frontvm/images/ubuntu.jpeg";
    private static final String WIN11_IMAGE = "/com/felp/frontvm/images/windows-11.jpg";

    private static final double SCREEN_WIDTH = 900;
    private static final double SCREEN_HEIGHT = 540;

    @FXML private Circle statusDot;
    @FXML private Label lblNome;
    @FXML private Label lblOs;
    @FXML private Button btnPower;
    @FXML private Button btnFechar;
    @FXML private StackPane screen;

    private VmModel vm;
    private final VmApiService vmApiService = new VmApiService();

    public void setVm(VmModel vm) {
        this.vm = vm;
        atualizarUi();
    }

    @FXML
    public void initialize() {
        btnFechar.setGraphic(LucideIcons.x(20, Color.web("#94a3b8")));
    }

    private void atualizarUi() {
        boolean running = "RUNNING".equals(vm.getStatus());
        boolean paused = "PAUSED".equals(vm.getStatus());

        lblNome.setText(vm.getName());
        lblOs.setText("· " + vm.getOsLabel());

        statusDot.setFill(Color.web(running ? "#10b981" : (paused ? "#f59e0b" : "#64748b")));

        atualizarBotaoPower(running);
        atualizarTela(running, paused);
    }

    private void atualizarBotaoPower(boolean running) {
        if (running) {
            btnPower.setText("Parar");
            btnPower.setGraphic(LucideIcons.power(14, Color.web("#fca5a5")));
            btnPower.setStyle(
                    "-fx-background-color: rgba(239,68,68,0.15);" +
                    "-fx-text-fill: #fca5a5;" +
                    "-fx-font-size: 13px;" +
                    "-fx-padding: 6 12 6 12;" +
                    "-fx-background-radius: 8;" +
                    "-fx-cursor: hand;"
            );
        } else {
            btnPower.setText("Iniciar");
            btnPower.setGraphic(LucideIcons.power(14, Color.web("#6ee7b7")));
            btnPower.setStyle(
                    "-fx-background-color: rgba(16,185,129,0.15);" +
                    "-fx-text-fill: #6ee7b7;" +
                    "-fx-font-size: 13px;" +
                    "-fx-padding: 6 12 6 12;" +
                    "-fx-background-radius: 8;" +
                    "-fx-cursor: hand;"
            );
        }
    }

    private void atualizarTela(boolean running, boolean paused) {
        screen.getChildren().clear();

        if (running) {
            screen.setStyle("-fx-background-color: #0f172a; -fx-background-radius: 16;");

            ImageView wallpaper = carregarWallpaper();
            if (wallpaper != null) {
                StackPane.setAlignment(wallpaper, Pos.CENTER);
                screen.getChildren().add(wallpaper);
            } else {
                Label fallback = new Label(vm.getOsLabel());
                fallback.setStyle("-fx-text-fill: rgba(255,255,255,0.85); -fx-font-size: 18px;");
                screen.getChildren().add(fallback);
            }
        } else if (paused) {
            screen.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 16;");
            Label l = new Label("máquina pausada");
            l.setStyle("-fx-text-fill: #f59e0b; -fx-font-size: 14px;");
            screen.getChildren().add(l);
        } else {
            screen.setStyle("-fx-background-color: #0f172a; -fx-background-radius: 16;");
            Label l = new Label("máquina desligada");
            l.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px;");
            screen.getChildren().add(l);
        }
    }

    private ImageView carregarWallpaper() {
        String path = "WINDOWS_11".equals(vm.getOsType()) ? WIN11_IMAGE : UBUNTU_IMAGE;
        var stream = getClass().getResourceAsStream(path);
        if (stream == null) return null;

        ImageView view = new ImageView(new Image(stream));
        view.setPreserveRatio(true);
        view.setFitWidth(SCREEN_WIDTH);
        view.setFitHeight(SCREEN_HEIGHT);
        view.setSmooth(true);
        return view;
    }

    @FXML
    private void handlePower() {
        boolean running = "RUNNING".equals(vm.getStatus());
        btnPower.setDisable(true);

        new Thread(() -> {
            VmModel updated = running
                    ? vmApiService.stop(vm.getId())
                    : vmApiService.start(vm.getId());

            Platform.runLater(() -> {
                btnPower.setDisable(false);
                if (updated != null) {
                    vm = updated;
                    atualizarUi();
                }
            });
        }, "viewerPower").start();
    }

    @FXML
    private void handleFechar() {
        ((Stage) btnFechar.getScene().getWindow()).close();
    }
}
