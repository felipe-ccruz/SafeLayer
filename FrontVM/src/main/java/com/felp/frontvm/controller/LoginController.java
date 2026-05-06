package com.felp.frontvm.controller;

import com.felp.frontvm.model.UserModel;
import com.felp.frontvm.service.UserApiService;
import com.felp.frontvm.session.Navigator;
import com.felp.frontvm.session.Session;
import com.felp.frontvm.ui.LucideIcons;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.function.Supplier;

public class LoginController {

    private static final String SELECTED_TOGGLE =
            "-fx-background-color: #ffffff; -fx-text-fill: #0f172a; -fx-cursor: hand; -fx-padding: 8 16; -fx-background-radius: 9; -fx-font-size: 12px; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 4, 0, 0, 1);";
    private static final String UNSELECTED_TOGGLE =
            "-fx-background-color: transparent; -fx-text-fill: #64748b; -fx-cursor: hand; -fx-padding: 8 16; -fx-background-radius: 9; -fx-font-size: 12px;";

    @FXML private StackPane logoBox;
    @FXML private Label lblSubtitulo;
    @FXML private Button btnModoLogin;
    @FXML private Button btnModoCadastro;
    @FXML private VBox grpNome;
    @FXML private VBox grpConfirmar;
    @FXML private TextField txtNome;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtSenha;
    @FXML private PasswordField txtConfirmar;
    @FXML private Label lblErro;
    @FXML private Button btnSubmit;

    private boolean cadastroMode = false;
    private final UserApiService userApiService = new UserApiService();

    @FXML
    public void initialize() {
        logoBox.getChildren().add(LucideIcons.monitor(24, Color.WHITE));
        aplicarModo();
    }

    @FXML
    private void modoLogin() {
        cadastroMode = false;
        aplicarModo();
    }

    @FXML
    private void modoCadastro() {
        cadastroMode = true;
        aplicarModo();
    }

    private void aplicarModo() {
        grpNome.setVisible(cadastroMode);
        grpNome.setManaged(cadastroMode);
        grpConfirmar.setVisible(cadastroMode);
        grpConfirmar.setManaged(cadastroMode);

        btnSubmit.setText(cadastroMode ? "Cadastrar" : "Entrar");
        lblSubtitulo.setText(cadastroMode ? "Crie sua conta" : "Entre na sua conta");

        btnModoLogin.setStyle(cadastroMode ? UNSELECTED_TOGGLE : SELECTED_TOGGLE);
        btnModoCadastro.setStyle(cadastroMode ? SELECTED_TOGGLE : UNSELECTED_TOGGLE);

        esconderErro();
    }

    @FXML
    private void submit() {
        String email = txtEmail.getText() == null ? "" : txtEmail.getText().trim();
        String senha = txtSenha.getText() == null ? "" : txtSenha.getText();

        if (email.isEmpty() || senha.isEmpty()) {
            mostrarErro("Preencha e-mail e senha");
            return;
        }

        if (cadastroMode) {
            String nome = txtNome.getText() == null ? "" : txtNome.getText().trim();
            String confirmar = txtConfirmar.getText() == null ? "" : txtConfirmar.getText();
            if (nome.isEmpty()) {
                mostrarErro("Informe seu nome");
                return;
            }
            if (senha.length() < 4) {
                mostrarErro("Senha precisa ter pelo menos 4 caracteres");
                return;
            }
            if (!senha.equals(confirmar)) {
                mostrarErro("As senhas não conferem");
                return;
            }
            executar(() -> userApiService.register(nome, email, senha));
        } else {
            executar(() -> userApiService.login(email, senha));
        }
    }

    private void executar(Supplier<UserModel> operacao) {
        btnSubmit.setDisable(true);
        esconderErro();

        new Thread(() -> {
            try {
                UserModel user = operacao.get();
                Platform.runLater(() -> {
                    Session.set(user);
                    Stage stage = (Stage) btnSubmit.getScene().getWindow();
                    Navigator.to(stage, "plans-view.fxml", 1100, 700, "SimpleVM — Planos");
                });
            } catch (UserApiService.ApiException e) {
                Platform.runLater(() -> {
                    btnSubmit.setDisable(false);
                    mostrarErro(e.getMessage());
                });
            }
        }, "auth").start();
    }

    private void mostrarErro(String msg) {
        lblErro.setText(msg);
        lblErro.setVisible(true);
        lblErro.setManaged(true);
    }

    private void esconderErro() {
        lblErro.setText("");
        lblErro.setVisible(false);
        lblErro.setManaged(false);
    }
}
