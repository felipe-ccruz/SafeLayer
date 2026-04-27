package com.felp.frontvm;

import com.felp.frontvm.model.VmModel;
import com.felp.frontvm.service.VmApiService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class MainController {

    @FXML private FlowPane flowPaneVms;
    @FXML private Button btnNovaVm;
    @FXML private Label lblStatus;

    private final VmApiService vmApiService = new VmApiService();

    @FXML
    public void initialize() {
        btnNovaVm.setOnAction(e -> abrirTelaCriacao());
        carregarVms();
    }

    private void carregarVms() {
        lblStatus.setText("Carregando...");

        Task<List<VmModel>> task = new Task<>() {
            @Override
            protected List<VmModel> call() {
                return vmApiService.findAll();
            }
        };

        task.setOnSucceeded(ev -> {
            List<VmModel> vms = task.getValue();
            flowPaneVms.getChildren().clear();
            for (VmModel vm : vms) {
                flowPaneVms.getChildren().add(criarCardVm(vm));
            }
            lblStatus.setText(vms.size() + " VMs carregadas");
        });

        task.setOnFailed(ev -> lblStatus.setText("Erro ao carregar VMs"));

        new Thread(task, "carregarVms").start();
    }

    private VBox criarCardVm(VmModel vm) {
        String corStatus = corDoStatus(vm.getStatus());

        VBox card = new VBox(8);
        card.setPrefSize(200, 220);
        card.setMinSize(200, 220);
        card.setMaxSize(200, 220);
        card.setStyle(
                "-fx-background-color: #16213e;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #0f3460;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 12;" +
                "-fx-padding: 15;"
        );

        Label lblNome = new Label(vm.getName());
        lblNome.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px; -fx-font-weight: bold;");

        Region spacerHeader = new Region();
        HBox.setHgrow(spacerHeader, Priority.ALWAYS);

        Circle statusDot = new Circle(6, Color.web(corStatus));

        HBox header = new HBox(8, lblNome, spacerHeader, statusDot);

        Label lblStatusVm = new Label(vm.getStatusLabel());
        lblStatusVm.setStyle("-fx-text-fill: " + corStatus + "; -fx-font-size: 11px; -fx-font-weight: bold;");

        Separator sep = new Separator();

        Label lblOs = new Label(vm.getOsLabel());
        lblOs.setStyle("-fx-text-fill: #a0a0b0; -fx-font-size: 11px;");

        Label lblHardware = new Label(
                "💾 " + vm.getProfileLabel() +
                " | 🖥 " + vm.getCpuCores() + " CPU" +
                " | 🧠 " + vm.getRamGb() + " GB RAM"
        );
        lblHardware.setStyle("-fx-text-fill: #a0a0b0; -fx-font-size: 10px;");

        Label lblData = new Label("📅 " + vm.getCreatedAt());
        lblData.setStyle("-fx-text-fill: #a0a0b0; -fx-font-size: 10px;");

        Region spacerBottom = new Region();
        VBox.setVgrow(spacerBottom, Priority.ALWAYS);

        HBox botoes = new HBox(6);
        String status = vm.getStatus();

        if ("STOPPED".equals(status) || "PAUSED".equals(status)) {
            Button btnLigar = botaoAcao("▶ Ligar", "#00c853", 10);
            btnLigar.setOnAction(e -> executarAcao(() -> vmApiService.start(vm.getId())));
            botoes.getChildren().add(btnLigar);
        }
        if ("RUNNING".equals(status)) {
            Button btnPausar = botaoAcao("⏸ Pausar", "#ffa726", 10);
            btnPausar.setOnAction(e -> executarAcao(() -> vmApiService.pause(vm.getId())));
            botoes.getChildren().add(btnPausar);
        }
        if ("RUNNING".equals(status) || "PAUSED".equals(status)) {
            Button btnParar = botaoAcao("⏹ Parar", "#0f3460", 10);
            btnParar.setOnAction(e -> executarAcao(() -> vmApiService.stop(vm.getId())));
            botoes.getChildren().add(btnParar);
        }

        Button btnDeletar = botaoAcao("🗑", "#e94560", 12);
        btnDeletar.setOnAction(e -> confirmarEDeletar(vm));
        botoes.getChildren().add(btnDeletar);

        card.getChildren().addAll(header, lblStatusVm, sep, lblOs, lblHardware, lblData, spacerBottom, botoes);
        return card;
    }

    private Button botaoAcao(String texto, String cor, int fontSize) {
        Button btn = new Button(texto);
        btn.setStyle(
                "-fx-background-color: " + cor + ";" +
                "-fx-text-fill: #ffffff;" +
                "-fx-font-size: " + fontSize + "px;" +
                "-fx-background-radius: 6;" +
                "-fx-padding: 6 10 6 10;" +
                "-fx-cursor: hand;"
        );
        return btn;
    }

    private void confirmarEDeletar(VmModel vm) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar exclusão");
        alert.setHeaderText(null);
        alert.setContentText("Deseja realmente deletar a VM " + vm.getName() + "?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            executarAcao(() -> vmApiService.delete(vm.getId()));
        }
    }

    private void executarAcao(Supplier<?> acao) {
        new Thread(() -> {
            acao.get();
            Platform.runLater(this::carregarVms);
        }, "acaoVm").start();
    }

    private String corDoStatus(String status) {
        return switch (status) {
            case "RUNNING" -> "#00c853";
            case "STOPPED" -> "#ff1744";
            case "PAUSED" -> "#ffa726";
            default -> "#a0a0b0";
        };
    }

    public void abrirTelaCriacao() {
        try {
            FXMLLoader loader = new FXMLLoader(MainController.class.getResource("criar-vm-view.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Nova VM");
            stage.setScene(new Scene(root, 420, 480));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            carregarVms();
        } catch (Exception e) {
            System.err.println("Erro ao abrir tela de criação: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
