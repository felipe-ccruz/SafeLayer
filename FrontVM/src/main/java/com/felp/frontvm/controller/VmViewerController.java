package com.felp.frontvm.controller;

import com.felp.frontvm.model.VmModel;
import com.felp.frontvm.service.VmApiService;
import com.felp.frontvm.ui.LucideIcons;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class VmViewerController {

    private static final String WIN10_WALLPAPER =
            "linear-gradient(to bottom right, #0ea5e9, #2563eb 50%, #4338ca)";
    private static final String WIN11_WALLPAPER =
            "linear-gradient(to bottom right, #6366f1, #9333ea 50%, #db2777)";

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
            boolean isWin11 = "WINDOWS_11".equals(vm.getOsType());
            String wallpaper = isWin11 ? WIN11_WALLPAPER : WIN10_WALLPAPER;
            screen.setStyle(
                    "-fx-background-color: " + wallpaper + ";" +
                    "-fx-background-radius: 16;"
            );

            VBox content = new VBox(8);
            content.setAlignment(Pos.CENTER);

            Label icon = new Label("◉");
            icon.setStyle("-fx-text-fill: rgba(255,255,255,0.9); -fx-font-size: 48px;");

            Label osName = new Label(vm.getOsLabel());
            osName.setStyle("-fx-text-fill: rgba(255,255,255,0.95); -fx-font-size: 18px;");

            Label sub = new Label("máquina virtual em execução");
            sub.setStyle("-fx-text-fill: rgba(255,255,255,0.65); -fx-font-size: 13px;");

            content.getChildren().addAll(icon, osName, sub);
            screen.getChildren().add(content);
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
