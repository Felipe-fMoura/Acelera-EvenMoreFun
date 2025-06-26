package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;

import model.Usuario;
import model.Badge;

import java.io.File;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;

public class PerfilUsuarioController {
    @FXML private Label lblNomeUsuario;
    @FXML private ImageView imgFotoPerfil;
    @FXML private FlowPane painelBadges;
    @FXML private Label lblUsername;
    @FXML private Label lblEmail;
    @FXML private Label lblNascimento;
    @FXML private Label lblCriado;
    @FXML private Label lblTituloBadges;
    @FXML private Button btnEnviarPedido;  // Botão para enviar pedido

    private Usuario usuarioVisualizado; // usuário do perfil aberto
    private Usuario usuarioLogado;      // usuário logado no sistema

    private static final String ICONE_PADRAO_PATH_1 = "/profile/badge.png";
    private static final String ICONE_PADRAO_PATH_2 = "/resources/profile/badge.png";

    /**
     * Configura o perfil com o usuário visualizado e o usuário logado
     */
    public void setUsuarios(Usuario usuarioVisualizado, Usuario usuarioLogado) {
        this.usuarioVisualizado = usuarioVisualizado;
        this.usuarioLogado = usuarioLogado;
        carregarDados();
        atualizarBotaoPedido();
    }

   private void carregarDados() {
    lblNomeUsuario.setText("📛 " + usuarioVisualizado.getNomeCompleto());
    lblUsername.setText("🧑‍💻 @" + usuarioVisualizado.getUsername());
    lblEmail.setText("📧 " + usuarioVisualizado.getEmail());

    try {
        String caminho = usuarioVisualizado.getCaminhoFotoPerfil();

        if (caminho != null && !caminho.trim().isEmpty()) {
            if (caminho.startsWith("file:/")) {
                imgFotoPerfil.setImage(new Image(caminho));
            } else {
                File imagemArquivo = new File(caminho);
                if (imagemArquivo.exists()) {
                    imgFotoPerfil.setImage(new Image(imagemArquivo.toURI().toString()));
                } else {
                    carregarImagemPadrao();
                }
            }
        } else {
            carregarImagemPadrao();
        }
    } catch (Exception e) {
        System.out.println("Erro ao carregar imagem de perfil: " + e.getMessage());
        carregarImagemPadrao();
    }


    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    if (usuarioVisualizado.getDataNascimento() != null) {
        lblNascimento.setText("🎂 " + usuarioVisualizado.getDataNascimento().format(formatter));
    } else {
        lblNascimento.setText("🎂 Não informado");
    }

    if (usuarioVisualizado.getDataCriacao() != null) {
        lblCriado.setText("📅 Conta criada em " + usuarioVisualizado.getDataCriacao().toLocalDate().format(formatter));
    } else {
        lblCriado.setText("📅 Conta criada em —");
    }

    int totalBadges = usuarioVisualizado.getBadges().size();
    lblTituloBadges.setText("🏅 Badges conquistadas (" + totalBadges + "):");

    painelBadges.getChildren().clear();
    for (Badge badge : usuarioVisualizado.getBadges()) {
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

    /**
     * Atualiza o estado do botão de envio de pedido de amizade conforme status da relação
     */
    private void atualizarBotaoPedido() {
        if (usuarioVisualizado.equals(usuarioLogado)) {
            btnEnviarPedido.setDisable(true);
            btnEnviarPedido.setText("Este é você");
        } else if (usuarioLogado.getAmigos().contains(usuarioVisualizado)) {
            btnEnviarPedido.setDisable(true);
            btnEnviarPedido.setText("Já é seu amigo");
        } else if (usuarioLogado.getPedidosEnviados().contains(usuarioVisualizado)) {
            btnEnviarPedido.setDisable(true);
            btnEnviarPedido.setText("Pedido enviado");
        } else if (usuarioLogado.getPedidosRecebidos().contains(usuarioVisualizado)) {
            btnEnviarPedido.setDisable(true);
            btnEnviarPedido.setText("Pedido recebido");
        } else {
            btnEnviarPedido.setDisable(false);
            btnEnviarPedido.setText("Enviar Pedido de Amizade");
        }
        btnEnviarPedido.setVisible(true);
    }

    @FXML
    private void handleEnviarPedido() {
        if (usuarioVisualizado == null || usuarioLogado == null) {
            return;
        }

        usuarioLogado.enviarPedido(usuarioVisualizado);
        atualizarBotaoPedido();

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Pedido de amizade");
        alerta.setHeaderText(null);
        alerta.setContentText("Pedido de amizade enviado para @" + usuarioVisualizado.getUsername() + "!");
        alerta.showAndWait();
    }
    
    private void carregarImagemPadrao() {
        InputStream is = getClass().getResourceAsStream("/resources/profile/iconPadraoUser.png");
        if (is != null) {
            imgFotoPerfil.setImage(new Image(is));
        } else {
            System.out.println("Imagem padrão não encontrada.");
            
        }
    }

}

