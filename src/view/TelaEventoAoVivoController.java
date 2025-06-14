package view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import model.Usuario;
import model.Evento;
import session.SessaoUsuario;

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
        this.usuario = SessaoUsuario.getInstance().getUsuario();  // Ajuste aqui
        nomeUsuario.setText("Você está assistindo como " + usuario.getNome());

        // Apenas exibir vídeo se for organizador e vídeo estiver disponível
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
            Text txt = new Text(usuario.getNome() + ": " + msg);
            mensagensContainer.getChildren().add(txt);
            campoMensagem.clear();
        }
    }

    @FXML
    private void handleSairEvento() {
        // fechar janela
        mediaView.getScene().getWindow().hide();
    }

    @FXML
    private void handleLevantarMao() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Você levantou a mão! Aguarde o organizador.");
        alert.showAndWait();
    }
}
