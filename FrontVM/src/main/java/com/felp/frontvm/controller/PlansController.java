package com.felp.frontvm.controller;

import com.felp.frontvm.model.UserModel;
import com.felp.frontvm.service.UserApiService;
import com.felp.frontvm.session.Navigator;
import com.felp.frontvm.session.Session;
import com.felp.frontvm.ui.LucideIcons;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class PlansController {

    @FXML private StackPane logoBox;
    @FXML private Label lblSaudacao;
    @FXML private Label lblPlanoAtual;
    @FXML private HBox containerPlanos;

    private final UserApiService userApiService = new UserApiService();

    private record PlanInfo(String code, String label, String price, String priceLabel,
                            String headerColor, List<String> features) {
    }

    private static final List<PlanInfo> PLANS = List.of(
            new PlanInfo("BASIC", "Basic", "Grátis", "Grátis",
                    "linear-gradient(to bottom right, #94a3b8, #475569)",
                    List.of("Até 1 VM", "Apenas perfil Fraco", "Suporte básico")),
            new PlanInfo("STANDARD", "Standard", "R$ 19,90", "R$ 19,90/mês",
                    "linear-gradient(to bottom right, #38bdf8, #2563eb)",
                    List.of("Até 3 VMs", "Perfis Fraco e Médio", "Suporte por e-mail")),
            new PlanInfo("PREMIUM", "Premium", "R$ 49,90", "R$ 49,90/mês",
                    "linear-gradient(to bottom right, #818cf8, #9333ea)",
                    List.of("VMs ilimitadas", "Todos os perfis", "Suporte prioritário"))
    );

    @FXML
    public void initialize() {
        logoBox.getChildren().add(LucideIcons.monitor(20, Color.WHITE));

        UserModel user = Session.get();
        if (user == null) {
            Platform.runLater(() -> {
                Stage stage = (Stage) logoBox.getScene().getWindow();
                Navigator.to(stage, "login-view.fxml", 900, 640, "SimpleVM — Login");
            });
            return;
        }

        atualizarHeader();
        renderizarPlanos();
    }

    private void atualizarHeader() {
        UserModel user = Session.get();
        lblSaudacao.setText("Olá, " + user.getName());
        lblPlanoAtual.setText("Plano atual: " + user.getPlanLabel());
    }

    private void renderizarPlanos() {
        containerPlanos.getChildren().clear();
        String currentPlan = Session.get().getPlan();
        for (PlanInfo plan : PLANS) {
            containerPlanos.getChildren().add(criarCard(plan, plan.code.equals(currentPlan)));
        }
    }

    private VBox criarCard(PlanInfo plan, boolean atual) {
        VBox card = new VBox();
        card.setPrefSize(280, 460);
        card.setMinWidth(280);
        card.setMaxWidth(280);

        String borderColor = atual ? "#0f172a" : "#e2e8f0";
        int borderWidth = atual ? 2 : 1;
        card.setStyle(
                "-fx-background-color: #ffffff;" +
                "-fx-background-radius: 16;" +
                "-fx-border-color: " + borderColor + ";" +
                "-fx-border-width: " + borderWidth + ";" +
                "-fx-border-radius: 16;"
        );

        StackPane header = new StackPane();
        header.setPrefHeight(110);
        header.setMinHeight(110);
        header.setStyle(
                "-fx-background-color: " + plan.headerColor + ";" +
                "-fx-background-radius: 15 15 0 0;"
        );

        Label planTitle = new Label(plan.label);
        planTitle.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;");
        header.getChildren().add(planTitle);

        if (atual) {
            Label badge = new Label("Plano atual");
            badge.setStyle(
                    "-fx-background-color: rgba(255,255,255,0.95);" +
                    "-fx-text-fill: #0f172a;" +
                    "-fx-font-size: 10px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 4 10;" +
                    "-fx-background-radius: 999;"
            );
            StackPane.setAlignment(badge, Pos.TOP_RIGHT);
            StackPane.setMargin(badge, new Insets(12, 12, 0, 0));
            header.getChildren().add(badge);
        }

        VBox body = new VBox(14);
        body.setPadding(new Insets(20));

        Label price = new Label(plan.price);
        price.setStyle("-fx-text-fill: #0f172a; -fx-font-size: 26px; -fx-font-weight: bold;");

        Label priceSub = new Label(plan.code.equals("BASIC") ? "para começar" : "por mês");
        priceSub.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");

        VBox precoBox = new VBox(2, price, priceSub);

        VBox features = new VBox(10);
        for (String feat : plan.features) {
            HBox row = new HBox(8);
            row.setAlignment(Pos.CENTER_LEFT);
            row.getChildren().add(LucideIcons.check(14, Color.web("#10b981")));
            Label l = new Label(feat);
            l.setStyle("-fx-text-fill: #334155; -fx-font-size: 12px;");
            row.getChildren().add(l);
            features.getChildren().add(row);
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btn = new Button();
        btn.setMaxWidth(Double.MAX_VALUE);
        if (atual) {
            btn.setText("Ir para minhas VMs");
            btn.setStyle(
                    "-fx-background-color: #0f172a;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 13px;" +
                    "-fx-padding: 12;" +
                    "-fx-background-radius: 12;" +
                    "-fx-cursor: hand;"
            );
            btn.setOnAction(e -> irParaVms());
        } else if ("BASIC".equals(plan.code)) {
            btn.setText("Voltar ao Basic");
            btn.setStyle(
                    "-fx-background-color: #f1f5f9;" +
                    "-fx-text-fill: #0f172a;" +
                    "-fx-font-size: 13px;" +
                    "-fx-padding: 12;" +
                    "-fx-background-radius: 12;" +
                    "-fx-cursor: hand;"
            );
            btn.setOnAction(e -> confirmarMudanca(plan));
        } else {
            btn.setText("Comprar — " + plan.priceLabel);
            btn.setStyle(
                    "-fx-background-color: #0f172a;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 13px;" +
                    "-fx-padding: 12;" +
                    "-fx-background-radius: 12;" +
                    "-fx-cursor: hand;"
            );
            btn.setOnAction(e -> confirmarMudanca(plan));
        }

        body.getChildren().addAll(precoBox, features, spacer, btn);
        card.getChildren().addAll(header, body);
        return card;
    }

    private void confirmarMudanca(PlanInfo plan) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar plano");
        alert.setHeaderText(null);
        if ("BASIC".equals(plan.code)) {
            alert.setContentText("Deseja voltar ao plano Basic? VMs além do limite continuarão criadas, mas você não poderá criar novas até remover.");
        } else {
            alert.setContentText("Confirmar contratação do plano " + plan.label + " por " + plan.priceLabel + "?");
        }
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) return;

        new Thread(() -> {
            try {
                UserModel updated = userApiService.changePlan(Session.getUserId(), plan.code);
                Platform.runLater(() -> {
                    Session.set(updated);
                    atualizarHeader();
                    renderizarPlanos();
                    mostrarInfo("Plano alterado para " + updated.getPlanLabel());
                });
            } catch (UserApiService.ApiException e) {
                Platform.runLater(() -> mostrarErro(e.getMessage()));
            }
        }, "changePlan").start();
    }

    @FXML
    private void irParaVms() {
        Stage stage = (Stage) logoBox.getScene().getWindow();
        Navigator.to(stage, "main-view.fxml", 1200, 720, "SimpleVM — VM Manager");
    }

    @FXML
    private void sair() {
        Session.clear();
        Stage stage = (Stage) logoBox.getScene().getWindow();
        Navigator.to(stage, "login-view.fxml", 900, 640, "SimpleVM — Login");
    }

    private void mostrarInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Sucesso");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void mostrarErro(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Erro");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
