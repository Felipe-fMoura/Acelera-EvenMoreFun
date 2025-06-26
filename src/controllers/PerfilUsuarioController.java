package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;

import model.Usuario;
import model.Badge;

import java.io.InputStream;
import java.time.format.DateTimeFormatter;

public class PerfilUsuarioController {
    @FXML private Label lblNomeUsuario;
    @FXML private FlowPane painelBadges;
    @FXML private Label lblUsername;
    @FXML private Label lblEmail;
    @FXML private Label lblNascimento;
    @FXML private Label lblCriado;
    @FXML private Label lblTituloBadges;

    private static final String ICONE_PADRAO_PATH_1 = "/profile/badge.png";
    private static final String ICONE_PADRAO_PATH_2 = "/resources/profile/badge.png";

    public void setUsuario(Usuario usuario) {
        lblNomeUsuario.setText("📛 " + usuario.getNome());
        lblUsername.setText("🧑‍💻 @" + usuario.getUsername());
        lblEmail.setText("📧 " + usuario.getEmail());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        if (usuario.getDataNascimento() != null) {
            lblNascimento.setText("🎂 " + usuario.getDataNascimento().format(formatter));
        } else {
            lblNascimento.setText("🎂 Não informado");
        }

        if (usuario.getDataCriacao() != null) {
            lblCriado.setText("📅 Conta criada em " + usuario.getDataCriacao().toLocalDate().format(formatter));
        } else {
            lblCriado.setText("📅 Conta criada em —");
        }

        // Contador de badges
        int totalBadges = usuario.getBadges().size();
        lblTituloBadges.setText("🏅 Badges conquistadas (" + totalBadges + "):");

        // Preenche badges
        painelBadges.getChildren().clear();
        for (Badge badge : usuario.getBadges()) {
            VBox box = new VBox();
            box.setAlignment(Pos.CENTER);

            ImageView icone = criarImageViewIcone(badge.getIconePath());
            icone.setFitWidth(40);
            icone.setFitHeight(40);

            Label nome = new Label(badge.getNome());
            nome.setStyle("-fx-font-size: 10;");

            Tooltip tooltip = new Tooltip(badge.getDescricao());
            Tooltip.install(icone, tooltip);

            box.getChildren().addAll(icone, nome);
            painelBadges.getChildren().add(box);
        }
    }

    private ImageView criarImageViewIcone(String caminhoIcone) {
        try {
            if (caminhoIcone != null && !caminhoIcone.trim().isEmpty()) {
                if (caminhoIcone.startsWith("file:/")) {
                    return new ImageView(new Image(caminhoIcone));
                }

                InputStream iconStream = getClass().getResourceAsStream(caminhoIcone);
                if (iconStream != null) {
                    return new ImageView(new Image(iconStream));
                }
            }

            InputStream fallback = getClass().getResourceAsStream(ICONE_PADRAO_PATH_1);
            if (fallback == null) fallback = getClass().getResourceAsStream(ICONE_PADRAO_PATH_2);

            return (fallback != null) ? new ImageView(new Image(fallback)) : new ImageView();

        } catch (Exception e) {
            System.out.println("Erro ao carregar badge: " + e.getMessage());
            return new ImageView();
        }
    }
}
