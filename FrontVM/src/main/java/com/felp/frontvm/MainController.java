package com.felp.frontvm;

import com.felp.frontvm.controller.VmViewerController;
import com.felp.frontvm.model.VmModel;
import com.felp.frontvm.service.VmApiService;
import com.felp.frontvm.ui.LucideIcons;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class MainController {

    private static final String TEXT_900 = "#0f172a";
    private static final String TEXT_700 = "#334155";
    private static final String TEXT_500 = "#64748b";
    private static final String TEXT_400 = "#94a3b8";
    private static final String BORDER = "#e2e8f0";
    private static final String SLATE_100 = "#f1f5f9";
    private static final String AMBER = "#f59e0b";

    private static final String WIN10_GRADIENT =
            "linear-gradient(to bottom right, #38bdf8, #2563eb)";
    private static final String WIN11_GRADIENT =
            "linear-gradient(to bottom right, #818cf8, #9333ea)";

    @FXML private FlowPane flowPaneVms;
    @FXML private Button btnNovaVm;
    @FXML private Button btnNovaVmEmpty;
    @FXML private Label lblStatus;
    @FXML private Label lblContador;
    @FXML private VBox emptyState;
    @FXML private StackPane logoBox;
    @FXML private StackPane emptyIconBox;

    private final VmApiService vmApiService = new VmApiService();

    @FXML
    public void initialize() {
        logoBox.getChildren().add(LucideIcons.monitor(20, Color.WHITE));
        emptyIconBox.getChildren().add(LucideIcons.monitor(36, Color.web(TEXT_400)));

        btnNovaVm.setGraphic(LucideIcons.plus(14, Color.WHITE));
        btnNovaVmEmpty.setGraphic(LucideIcons.plus(14, Color.WHITE));

        btnNovaVm.setOnAction(e -> abrirTelaCriacao());
        btnNovaVmEmpty.setOnAction(e -> abrirTelaCriacao());

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
            atualizarContador(vms.size());
            boolean vazio = vms.isEmpty();
            emptyState.setVisible(vazio);
            emptyState.setManaged(vazio);
            flowPaneVms.setVisible(!vazio);
            flowPaneVms.setManaged(!vazio);
            lblStatus.setText(vms.size() + " VMs carregadas");
        });

        task.setOnFailed(ev -> lblStatus.setText("Erro ao carregar VMs"));

        new Thread(task, "carregarVms").start();
    }

    private void atualizarContador(int total) {
        lblContador.setText(total + (total == 1 ? " máquina" : " máquinas"));
    }

    private VBox criarCardVm(VmModel vm) {
        boolean isWin11 = "WINDOWS_11".equals(vm.getOsType());
        String gradient = isWin11 ? WIN11_GRADIENT : WIN10_GRADIENT;
        String corStatus = corDoStatus(vm.getStatus());

        VBox card = new VBox();
        card.setPrefSize(280, 320);
        card.setMaxWidth(280);
        card.setStyle(
                "-fx-background-color: #ffffff;" +
                "-fx-background-radius: 16;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 16;"
        );

        StackPane header = new StackPane();
        header.setPrefHeight(160);
        header.setMinHeight(160);
        header.setStyle(
                "-fx-background-color: " + gradient + ";" +
                "-fx-background-radius: 16 16 0 0;"
        );
        header.setCursor(Cursor.HAND);
        header.setOnMouseClicked(e -> abrirVmViewer(vm));

        Node monitorIcon = LucideIcons.monitor(60, Color.web("white", 0.95));

        HBox badge = new HBox(6);
        badge.setAlignment(Pos.CENTER);
        badge.setStyle(
                "-fx-background-color: rgba(0,0,0,0.25);" +
                "-fx-background-radius: 999;" +
                "-fx-padding: 4 10 4 10;"
        );
        Circle dot = new Circle(3, Color.web(corStatus));
        Label badgeLabel = new Label(vm.getStatusLabel());
        badgeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11px;");
        badge.getChildren().addAll(dot, badgeLabel);
        StackPane.setAlignment(badge, Pos.TOP_RIGHT);
        StackPane.setMargin(badge, new Insets(12, 12, 0, 0));

        if ("RUNNING".equals(vm.getStatus())) {
            FadeTransition pulse = new FadeTransition(Duration.seconds(1), dot);
            pulse.setFromValue(1.0);
            pulse.setToValue(0.3);
            pulse.setAutoReverse(true);
            pulse.setCycleCount(Animation.INDEFINITE);
            pulse.play();
        }

        header.getChildren().addAll(monitorIcon, badge);

        VBox body = new VBox(10);
        body.setPadding(new Insets(16));

        Label lblName = new Label(vm.getName());
        lblName.setStyle("-fx-text-fill: " + TEXT_900 + "; -fx-font-size: 15px; -fx-font-weight: bold;");

        Label lblOs = new Label(vm.getOsLabel());
        lblOs.setStyle("-fx-text-fill: " + TEXT_500 + "; -fx-font-size: 12px;");

        Label lblSpec = new Label(
                vm.getProfileLabel() + " · " +
                vm.getCpuCores() + " CPU · " +
                vm.getRamGb() + " GB"
        );
        lblSpec.setStyle("-fx-text-fill: " + TEXT_400 + "; -fx-font-size: 11px;");

        VBox info = new VBox(2, lblName, lblOs);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        HBox botoes = construirBotoes(vm);

        body.getChildren().addAll(info, lblSpec, spacer, botoes);

        card.getChildren().addAll(header, body);
        return card;
    }

    private HBox construirBotoes(VmModel vm) {
        HBox botoes = new HBox(8);
        botoes.setAlignment(Pos.CENTER_LEFT);

        String status = vm.getStatus();

        if (!"RUNNING".equals(status)) {
            Button btnIniciar = botaoPrimario("Iniciar", LucideIcons.play(12, Color.WHITE));
            btnIniciar.setOnAction(e -> executarAcao(() -> vmApiService.start(vm.getId())));
            HBox.setHgrow(btnIniciar, Priority.ALWAYS);
            btnIniciar.setMaxWidth(Double.MAX_VALUE);
            botoes.getChildren().add(btnIniciar);
        }

        if ("RUNNING".equals(status)) {
            Button btnPausar = botaoSecundario("Pausar", LucideIcons.pause(12, Color.WHITE), AMBER, "white");
            btnPausar.setOnAction(e -> executarAcao(() -> vmApiService.pause(vm.getId())));
            HBox.setHgrow(btnPausar, Priority.ALWAYS);
            btnPausar.setMaxWidth(Double.MAX_VALUE);
            botoes.getChildren().add(btnPausar);

            Button btnParar = botaoSecundario("Parar", LucideIcons.square(12, Color.web(TEXT_700)), SLATE_100, TEXT_700);
            btnParar.setOnAction(e -> executarAcao(() -> vmApiService.stop(vm.getId())));
            HBox.setHgrow(btnParar, Priority.ALWAYS);
            btnParar.setMaxWidth(Double.MAX_VALUE);
            botoes.getChildren().add(btnParar);
        }

        if ("PAUSED".equals(status)) {
            Button btnParar = botaoSecundario("Parar", LucideIcons.square(12, Color.web(TEXT_700)), SLATE_100, TEXT_700);
            btnParar.setOnAction(e -> executarAcao(() -> vmApiService.stop(vm.getId())));
            HBox.setHgrow(btnParar, Priority.ALWAYS);
            btnParar.setMaxWidth(Double.MAX_VALUE);
            botoes.getChildren().add(btnParar);
        }

        Button btnDelete = new Button();
        btnDelete.setGraphic(LucideIcons.trash(16, Color.web(TEXT_400)));
        btnDelete.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 8;"
        );
        btnDelete.setOnAction(e -> confirmarEDeletar(vm));
        botoes.getChildren().add(btnDelete);

        return botoes;
    }

    private Button botaoPrimario(String texto, Node icone) {
        Button btn = new Button(texto);
        btn.setGraphic(icone);
        btn.setGraphicTextGap(6);
        btn.setStyle(
                "-fx-background-color: " + TEXT_900 + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-padding: 8 12 8 12;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;"
        );
        return btn;
    }

    private Button botaoSecundario(String texto, Node icone, String bg, String fg) {
        Button btn = new Button(texto);
        btn.setGraphic(icone);
        btn.setGraphicTextGap(6);
        btn.setStyle(
                "-fx-background-color: " + bg + ";" +
                "-fx-text-fill: " + fg + ";" +
                "-fx-font-size: 13px;" +
                "-fx-padding: 8 12 8 12;" +
                "-fx-background-radius: 10;" +
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
            case "RUNNING" -> "#10b981";
            case "STOPPED" -> "#94a3b8";
            case "PAUSED" -> "#f59e0b";
            default -> "#94a3b8";
        };
    }

    public void abrirTelaCriacao() {
        try {
            FXMLLoader loader = new FXMLLoader(MainController.class.getResource("criar-vm-view.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Nova VM");
            stage.setScene(new Scene(root, 460, 620));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            carregarVms();
        } catch (Exception e) {
            System.err.println("Erro ao abrir tela de criação: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void abrirVmViewer(VmModel vm) {
        try {
            FXMLLoader loader = new FXMLLoader(MainController.class.getResource("vm-viewer-view.fxml"));
            Parent root = loader.load();

            VmViewerController ctrl = loader.getController();
            ctrl.setVm(vm);

            Stage stage = new Stage();
            stage.setTitle(vm.getName() + " — VM Manager");
            stage.setScene(new Scene(root, 1100, 700));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            carregarVms();
        } catch (Exception e) {
            System.err.println("Erro ao abrir viewer: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
