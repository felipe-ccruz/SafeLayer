package com.felp.frontvm.controller;

import com.felp.frontvm.model.VmModel;
import com.felp.frontvm.service.VmApiService;
import com.felp.frontvm.ui.LucideIcons;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class CriarVmController {

    private static final String BORDER_NORMAL = "#e2e8f0";
    private static final String BORDER_SELECTED = "#0f172a";

    private static final String OS_CARD_BASE =
            "-fx-background-color: #ffffff;" +
            "-fx-background-radius: 14;" +
            "-fx-padding: 12;" +
            "-fx-cursor: hand;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 14;";

    private static final String PROFILE_CARD_BASE =
            "-fx-background-color: #ffffff;" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 10 14 10 14;" +
            "-fx-cursor: hand;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 12;";

    @FXML private TextField txtNome;
    @FXML private VBox cardOsUbuntu;
    @FXML private VBox cardOsWin11;
    @FXML private StackPane previewUbuntu;
    @FXML private StackPane previewWin11;
    @FXML private HBox cardWeak;
    @FXML private HBox cardMedium;
    @FXML private HBox cardStrong;
    @FXML private Button btnCriar;
    @FXML private Button btnCancelar;
    @FXML private Button btnFechar;

    private String osSelecionado = "UBUNTU";
    private String perfilSelecionado = "WEAK";
    private final VmApiService vmApiService = new VmApiService();

    @FXML
    public void initialize() {
        btnFechar.setGraphic(LucideIcons.x(18, Color.web("#94a3b8")));

        cardOsUbuntu.setOnMouseClicked(e -> selecionarOs("UBUNTU"));
        cardOsWin11.setOnMouseClicked(e -> selecionarOs("WINDOWS_11"));

        cardWeak.setOnMouseClicked(e -> selecionarPerfil("WEAK"));
        cardMedium.setOnMouseClicked(e -> selecionarPerfil("MEDIUM"));
        cardStrong.setOnMouseClicked(e -> selecionarPerfil("STRONG"));

        selecionarOs("UBUNTU");
        selecionarPerfil("WEAK");
    }

    private void selecionarOs(String os) {
        osSelecionado = os;
        boolean ubuntu = "UBUNTU".equals(os);

        cardOsUbuntu.setStyle(OS_CARD_BASE + "-fx-border-color: " + (ubuntu ? BORDER_SELECTED : BORDER_NORMAL) + ";");
        cardOsWin11.setStyle(OS_CARD_BASE + "-fx-border-color: " + (!ubuntu ? BORDER_SELECTED : BORDER_NORMAL) + ";");

        atualizarCheckMark(previewUbuntu, ubuntu);
        atualizarCheckMark(previewWin11, !ubuntu);
    }

    private void atualizarCheckMark(StackPane preview, boolean show) {
        preview.getChildren().removeIf(node -> "check-mark".equals(node.getId()));
        if (!show) return;

        StackPane mark = new StackPane(LucideIcons.check(12, Color.WHITE));
        mark.setId("check-mark");
        mark.setPrefSize(22, 22);
        mark.setMinSize(22, 22);
        mark.setMaxSize(22, 22);
        mark.setStyle("-fx-background-color: #0f172a; -fx-background-radius: 999;");
        StackPane.setAlignment(mark, Pos.TOP_RIGHT);
        StackPane.setMargin(mark, new javafx.geometry.Insets(6, 6, 0, 0));
        preview.getChildren().add(mark);
    }

    private void selecionarPerfil(String perfil) {
        perfilSelecionado = perfil;
        cardWeak.setStyle(PROFILE_CARD_BASE + "-fx-border-color: " + ("WEAK".equals(perfil) ? BORDER_SELECTED : BORDER_NORMAL) + ";");
        cardMedium.setStyle(PROFILE_CARD_BASE + "-fx-border-color: " + ("MEDIUM".equals(perfil) ? BORDER_SELECTED : BORDER_NORMAL) + ";");
        cardStrong.setStyle(PROFILE_CARD_BASE + "-fx-border-color: " + ("STRONG".equals(perfil) ? BORDER_SELECTED : BORDER_NORMAL) + ";");
    }

    @FXML
    private void handleCriar() {
        String nome = txtNome.getText() == null ? "" : txtNome.getText().trim();
        if (nome.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atenção", "Nome obrigatório");
            return;
        }

        btnCriar.setDisable(true);
        btnCancelar.setDisable(true);

        new Thread(() -> {
            VmModel created = vmApiService.create(nome, osSelecionado, perfilSelecionado);
            Platform.runLater(() -> {
                if (created != null) {
                    fecharJanela();
                } else {
                    btnCriar.setDisable(false);
                    btnCancelar.setDisable(false);
                    mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao criar VM");
                }
            });
        }, "criarVm").start();
    }

    @FXML
    private void handleCancelar() {
        fecharJanela();
    }

    private void fecharJanela() {
        ((Stage) btnCriar.getScene().getWindow()).close();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
