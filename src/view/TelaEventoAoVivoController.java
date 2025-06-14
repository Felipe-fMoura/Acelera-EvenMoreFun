package view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import model.Usuario;
import model.Evento;
import session.SessaoUsuario;
import service.ChatService;

import java.util.List;

public class TelaEventoAoVivoController {

    @FXML private MediaView mediaView;
    @FXML private Text lblSemVideo;
    @FXML private VBox mensagensContainer;
    @FXML private TextField campoMensagem;
    @FXML private Text nomeUsuario;

    private Evento evento;
    private Usuario usuario;

    public void setEvento(Evento evento) {
        this.evento = evento;
        this.usuario = SessaoUsuario.getInstance().getUsuario();
        nomeUsuario.setText("Você está assistindo como " + usuario.getNome());

        // Carrega mensagens anteriores do ChatService
        List<String> mensagens = ChatService.getInstancia().getMensagens(evento);
        for (String msg : mensagens) {
            adicionarMensagemNaInterface(msg);
        }

        // Exibe o vídeo se for o organizador e houver link
        if (usuario.equals(evento.getOrganizador()) && evento.getUrlVideo() != null && !evento.getUrlVideo().isEmpty()) {
            Media media = new Media(evento.getUrlVideo());
            MediaPlayer player = new MediaPlayer(media);
            mediaView.setMediaPlayer(player);
            player.play();
            lblSemVideo.setVisible(false);
        } else {
            lblSemVideo.setVisible(true);
        }
    }

    @FXML
    private void handleEnviarMensagem() {
        String msg = campoMensagem.getText().trim();
        if (!msg.isEmpty()) {
            String mensagemFormatada = usuario.getNome() + ": " + msg;

            // Adiciona na interface e na memória
            adicionarMensagemNaInterface(mensagemFormatada);
            ChatService.getInstancia().adicionarMensagem(evento, mensagemFormatada);

            campoMensagem.clear();
        }
    }

    private void adicionarMensagemNaInterface(String mensagem) {
        HBox linhaMensagem = new HBox();
        linhaMensagem.setSpacing(10);
        linhaMensagem.setAlignment(Pos.CENTER_LEFT);
        linhaMensagem.setPadding(new Insets(5));

        Text txtMensagem = new Text(mensagem);
        linhaMensagem.getChildren().add(txtMensagem);

        // Só o organizador pode excluir mensagens
        if (usuario.equals(evento.getOrganizador())) {
            Button btnExcluir = new Button("🗑");
            btnExcluir.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #888;" +
                "-fx-font-size: 10px;" +
                "-fx-padding: 2 4 2 4;" +
                "-fx-cursor: hand;"
            );
            btnExcluir.setTooltip(new Tooltip("Excluir mensagem"));
            btnExcluir.setOnAction(e -> {
                mensagensContainer.getChildren().remove(linhaMensagem);
                ChatService.getInstancia().removerMensagem(evento, mensagem);
            });
            linhaMensagem.getChildren().add(btnExcluir);
        }

        mensagensContainer.getChildren().add(linhaMensagem);
    }

    @FXML
    private void handleSairEvento() {
        mediaView.getScene().getWindow().hide();
    }

    @FXML
    private void handleLevantarMao() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Você levantou a mão! Aguarde o organizador.");
        alert.showAndWait();
    }
}
