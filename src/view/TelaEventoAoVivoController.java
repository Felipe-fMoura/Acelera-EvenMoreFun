package view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.util.Duration;

import model.Usuario;
import model.Evento;
import session.SessaoUsuario;
import service.ChatService;

import java.util.*;

public class TelaEventoAoVivoController {

    @FXML private WebView webView;
    @FXML private Text lblSemVideo;
    @FXML private VBox mensagensContainer;
    @FXML private TextField campoMensagem;
    @FXML private Text nomeUsuario;

    @FXML private HBox videoControlsPane;
    @FXML private TextField txtUrlVideo;

    private Evento evento;
    private Usuario usuario;
    private WebEngine webEngine;
    private Timeline chatRefresh;

    // Controle de quem já recebeu o aviso HAND_ACK
    private Set<String> handAckRecebidos = new HashSet<>();
    // Usuários com a próxima mensagem destacada (após ACK)
    private Set<String> aguardandoResposta = new HashSet<>();

    public void initialize() {
        webEngine = webView.getEngine();
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
        this.usuario = SessaoUsuario.getInstance().getUsuario();
        nomeUsuario.setText("Você está assistindo como " + usuario.getNome());

        boolean isOrganizador = usuario.equals(evento.getOrganizador());
        videoControlsPane.setVisible(isOrganizador);
        videoControlsPane.setManaged(isOrganizador);

        aguardandoResposta.clear();
        handAckRecebidos.clear();

        mensagensContainer.getChildren().clear();

        List<String> mensagens = ChatService.getInstancia().getMensagens(evento);
        // Atualizar estados ao carregar mensagens existentes
        for (String msg : mensagens) {
            if (msg.startsWith("[HAND_ACK] ")) {
                String nomeAck = msg.replace("[HAND_ACK] ", "");
                handAckRecebidos.add(nomeAck);
            }
            adicionarMensagemNaInterface(msg);
        }

        if (evento.getUrlVideo() != null && !evento.getUrlVideo().isEmpty()) {
            carregarVideo(evento.getUrlVideo());
        } else {
            mostrarSemVideo();
        }

        startChatAutoRefresh();
    }

    private void startChatAutoRefresh() {
        chatRefresh = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
            List<String> mensagens = ChatService.getInstancia().getMensagens(evento);
            mensagensContainer.getChildren().clear();
            for (String msg : mensagens) {
                // Atualiza handAckRecebidos dinamicamente (não remove nenhum)
                if (msg.startsWith("[HAND_ACK] ")) {
                    String nomeAck = msg.replace("[HAND_ACK] ", "");
                    handAckRecebidos.add(nomeAck);
                }
                adicionarMensagemNaInterface(msg);
            }
        }));
        chatRefresh.setCycleCount(Timeline.INDEFINITE);
        chatRefresh.play();
    }

    @FXML
    private void handleCarregarVideo() {
        String url = txtUrlVideo.getText().trim();
        if (url.isEmpty()) {
            mostrarSemVideo();
            return;
        }
        evento.setUrlVideo(url);
        carregarVideo(url);
    }

    private void carregarVideo(String url) {
        try {
            if (url.contains("youtube.com") || url.contains("youtu.be")) {
                String videoId = extrairVideoIdYouTube(url);
                if (videoId != null) {
                    String embedHtml = "<html><body style='margin:0; background:black;'>" +
                            "<iframe width='800' height='450' src='https://www.youtube.com/embed/" + videoId + "' " +
                            "frameborder='0' allowfullscreen></iframe></body></html>";
                    webEngine.loadContent(embedHtml);
                    lblSemVideo.setVisible(false);
                    return;
                }
            }
            webEngine.load(url);
            lblSemVideo.setVisible(false);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarSemVideo();
        }
    }

    private String extrairVideoIdYouTube(String url) {
        try {
            if (url.contains("youtu.be/")) {
                String videoId = url.substring(url.indexOf("youtu.be/") + 9);
                int paramIndex = videoId.indexOf("?");
                return paramIndex > 0 ? videoId.substring(0, paramIndex) : videoId;
            } else if (url.contains("youtube.com/watch?v=")) {
                String videoId = url.substring(url.indexOf("v=") + 2);
                int paramIndex = videoId.indexOf("&");
                return paramIndex > 0 ? videoId.substring(0, paramIndex) : videoId;
            }
        } catch (Exception e) {}
        return null;
    }

    private void mostrarSemVideo() {
        webEngine.loadContent("<html><body style='background:black; color:white; display:flex; justify-content:center; align-items:center; height:100%;'>" +
                "<h2>Vídeo indisponível</h2></body></html>");
        lblSemVideo.setVisible(true);
    }

    @FXML
    private void handleEnviarMensagem() {
        String msg = campoMensagem.getText().trim();
        if (!msg.isEmpty()) {
            String mensagemFormatada = usuario.getNome() + ": " + msg;
            ChatService.getInstancia().adicionarMensagem(evento, mensagemFormatada);
            campoMensagem.clear();
            refreshMensagens();
        }
    }

    private void refreshMensagens() {
        List<String> mensagens = ChatService.getInstancia().getMensagens(evento);
        mensagensContainer.getChildren().clear();
        for (String msg : mensagens) {
            adicionarMensagemNaInterface(msg);
        }
    }

    private void adicionarMensagemNaInterface(String mensagem) {
        HBox linhaMensagem = new HBox();
        linhaMensagem.setSpacing(10);
        linhaMensagem.setAlignment(Pos.CENTER_LEFT);
        linhaMensagem.setPadding(new Insets(5));

        if (mensagem.startsWith("[HAND_RAISE]")) {
            String nomeSolicitante = mensagem.replace("[HAND_RAISE] ", "");
            if (usuario.equals(evento.getOrganizador())) {
                VBox box = new VBox(5);
                Label lbl = new Label(nomeSolicitante + " levantou a mão ✋");
                lbl.setStyle("-fx-font-weight: bold; -fx-text-fill: orange;");

                Button btnOk = new Button("OK");
                btnOk.setOnAction(e -> {
                    mensagensContainer.getChildren().remove(linhaMensagem);
                    ChatService.getInstancia().removerMensagem(evento, mensagem);
                    // Enviar ACK para o usuário
                    String resposta = "[HAND_ACK] " + nomeSolicitante;
                    ChatService.getInstancia().adicionarMensagem(evento, resposta);
                    // Colocar o usuário na lista para destacar a próxima mensagem
                    aguardandoResposta.add(nomeSolicitante);
                });

                box.getChildren().addAll(lbl, btnOk);
                linhaMensagem.getChildren().add(box);
                mensagensContainer.getChildren().add(linhaMensagem);
            }
        } else if (mensagem.startsWith("[HAND_ACK]")) {
            String nome = mensagem.replace("[HAND_ACK] ", "");
            // Mostrar alerta para o usuário que recebeu o ACK apenas uma vez
            if (usuario.getNome().equals(nome) && !handAckRecebidos.contains(nome)) {
                handAckRecebidos.add(nome);
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "O palestrante foi notificado e irá atendê-lo em breve.");
                    alert.showAndWait();
                });
            }
            // Para o organizador, mostra texto estilizado
            if (usuario.equals(evento.getOrganizador())) {
                Text txtMensagem = new Text(nome + " foi atendido pelo organizador.");
                txtMensagem.setStyle("-fx-fill: gray; -fx-font-style: italic;");
                linhaMensagem.getChildren().add(txtMensagem);
                mensagensContainer.getChildren().add(linhaMensagem);
            }
        } else {
            // Mensagens normais
            String nomeRemetente = mensagem.split(":")[0].trim();
            Text txtMensagem = new Text(mensagem);

            if (usuario.equals(evento.getOrganizador()) && aguardandoResposta.contains(nomeRemetente)) {
                // Destacar apenas a primeira mensagem após o ACK
                txtMensagem.setStyle("-fx-fill: orange; -fx-font-weight: bold;");
                aguardandoResposta.remove(nomeRemetente);
            }

            linhaMensagem.getChildren().add(txtMensagem);

            if (usuario.equals(evento.getOrganizador())) {
                Button btnExcluir = new Button("🗑");
                btnExcluir.setStyle(
                        "-fx-background-color: transparent;" +
                        "-fx-text-fill: #888;" +
                        "-fx-font-size: 10px;" +
                        "-fx-padding: 2 4 2 4;" +
                        "-fx-cursor: hand;");
                btnExcluir.setTooltip(new Tooltip("Excluir mensagem"));
                btnExcluir.setOnAction(e -> {
                    mensagensContainer.getChildren().remove(linhaMensagem);
                    ChatService.getInstancia().removerMensagem(evento, mensagem);
                });
                linhaMensagem.getChildren().add(btnExcluir);
            }

            mensagensContainer.getChildren().add(linhaMensagem);
        }
    }

    @FXML
    private void handleSairEvento() {
        if (chatRefresh != null) chatRefresh.stop();
        webEngine.load(null);
        webView.getScene().getWindow().hide();
    }

    @FXML
    private void handleLevantarMao() {
        String msg = "[HAND_RAISE] " + usuario.getNome();
        ChatService.getInstancia().adicionarMensagem(evento, msg);
        refreshMensagens();
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Você levantou a mão! Aguarde o organizador.");
        alert.showAndWait();
    }
}
