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

public class PerfilUsuarioController {
    @FXML private Label lblNomeUsuario;
    @FXML private FlowPane painelBadges;

    private static final String ICONE_PADRAO_PATH_1 = "/profile/badge.png";
    private static final String ICONE_PADRAO_PATH_2 = "/resources/profile/badge.png";

    public void setUsuario(Usuario usuario) {
        lblNomeUsuario.setText(usuario.getNome());

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
                // Carrega imagem externa (file:/...)
                if (caminhoIcone.startsWith("file:/")) {
                    return new ImageView(new Image(caminhoIcone));
                }

                // Carrega imagem interna do projeto
                InputStream iconStream = getClass().getResourceAsStream(caminhoIcone);
                if (iconStream != null) {
                    return new ImageView(new Image(iconStream));
                }
            }

            // Fallback: badge padrão
            InputStream fallback = getClass().getResourceAsStream(ICONE_PADRAO_PATH_1);
            if (fallback == null) fallback = getClass().getResourceAsStream(ICONE_PADRAO_PATH_2);

            return (fallback != null) ? new ImageView(new Image(fallback)) : new ImageView();

        } catch (Exception e) {
            System.out.println("Erro ao carregar badge: " + e.getMessage());
            return new ImageView();
        }
    }


}
