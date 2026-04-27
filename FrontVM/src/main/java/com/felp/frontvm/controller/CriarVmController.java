package com.felp.frontvm.controller;

import com.felp.frontvm.model.VmModel;
import com.felp.frontvm.service.VmApiService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class CriarVmController {

    private static final String ESTILO_BASE =
            "-fx-background-color: #16213e;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 12;" +
            "-fx-cursor: hand;";

    private static final String BORDA_NORMAL =
            "-fx-border-color: #0f3460; -fx-border-width: 1; -fx-border-radius: 8;";

    private static final String BORDA_SELECIONADA =
            "-fx-border-color: #e94560; -fx-border-width: 2; -fx-border-radius: 8;";

    @FXML private TextField txtNome;
    @FXML private RadioButton rbWindows10;
    @FXML private RadioButton rbWindows11;
    @FXML private HBox cardWeak;
    @FXML private HBox cardMedium;
    @FXML private HBox cardStrong;
    @FXML private Button btnCriar;
    @FXML private Button btnCancelar;

    private String perfilSelecionado = "WEAK";
    private final VmApiService vmApiService = new VmApiService();

    @FXML
    public void initialize() {
        cardWeak.setOnMouseClicked(e -> selecionarPerfil("WEAK", cardWeak));
        cardMedium.setOnMouseClicked(e -> selecionarPerfil("MEDIUM", cardMedium));
        cardStrong.setOnMouseClicked(e -> selecionarPerfil("STRONG", cardStrong));

        selecionarPerfil("WEAK", cardWeak);
    }

    private void selecionarPerfil(String perfil, HBox cardSelecionado) {
        perfilSelecionado = perfil;
        cardWeak.setStyle(ESTILO_BASE + BORDA_NORMAL);
        cardMedium.setStyle(ESTILO_BASE + BORDA_NORMAL);
        cardStrong.setStyle(ESTILO_BASE + BORDA_NORMAL);
        cardSelecionado.setStyle(ESTILO_BASE + BORDA_SELECIONADA);
    }

    @FXML
    private void handleCriar() {
        String nome = txtNome.getText() == null ? "" : txtNome.getText().trim();
        if (nome.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atenção", "Nome obrigatório");
            return;
        }

        String osType = rbWindows10.isSelected() ? "WINDOWS_10" : "WINDOWS_11";

        btnCriar.setDisable(true);
        btnCancelar.setDisable(true);

        new Thread(() -> {
            VmModel created = vmApiService.create(nome, osType, perfilSelecionado);
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
