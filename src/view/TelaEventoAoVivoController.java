package view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import model.Usuario;
import model.Evento;
import session.SessaoUsuario;
import service.ChatService;

import java.util.List;

public class TelaEventoAoVivoController {

    @FXML private WebView webView;              // Substituído MediaView por WebView
    @FXML private Text lblSemVideo;
    @FXML private VBox mensagensContainer;
    @FXML private TextField campoMensagem;
    @FXML private Text nomeUsuario;

    @FXML private HBox videoControlsPane;
    @FXML private TextField txtUrlVideo;

    private Evento evento;
    private Usuario usuario;
    private WebEngine webEngine;

    public void initialize() {
        webEngine = webView.getEngine();
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
        this.usuario = SessaoUsuario.getInstance().getUsuario();
        nomeUsuario.setText("Você está assistindo como " + usuario.getNome());

        // Mostrar painel de URL só para o organizador
        boolean isOrganizador = usuario.equals(evento.getOrganizador());
        videoControlsPane.setVisible(isOrganizador);
        videoControlsPane.setManaged(isOrganizador);

        // Se for organizador, carrega URL do evento no campo
        if (isOrganizador && evento.getUrlVideo() != null) {
            txtUrlVideo.setText(evento.getUrlVideo());
        }

        // Carrega mensagens anteriores do ChatService
        List<String> mensagens = ChatService.getInstancia().getMensagens(evento);
        for (String msg : mensagens) {
            adicionarMensagemNaInterface(msg);
        }

        // Exibe o vídeo se houver URL válida
        if (evento.getUrlVideo() != null && !evento.getUrlVideo().isEmpty()) {
            carregarVideo(evento.getUrlVideo());
        } else {
            mostrarSemVideo();
        }
    }

    @FXML
    private void handleCarregarVideo() {
        String url = txtUrlVideo.getText().trim();
        if (url.isEmpty()) {
            mostrarSemVideo();
            return;
        }
        // Atualiza o evento com a URL nova
        evento.setUrlVideo(url);
        carregarVideo(url);
    }

    private void carregarVideo(String url) {
        try {
            // Se a url for YouTube, faz embed correto para webview (iframe)
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
            // Caso não seja YouTube, tenta carregar a URL diretamente
            webEngine.load(url);
            lblSemVideo.setVisible(false);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarSemVideo();
        }
    }

    private String extrairVideoIdYouTube(String url) {
        // Pega o id do vídeo do youtube do link completo
        String videoId = null;
        try {
            if (url.contains("youtu.be/")) {
                int index = url.indexOf("youtu.be/") + 9;
                videoId = url.substring(index);
                int paramIndex = videoId.indexOf("?");
                if (paramIndex > 0) videoId = videoId.substring(0, paramIndex);
            } else if (url.contains("youtube.com/watch?v=")) {
                int index = url.indexOf("v=") + 2;
                videoId = url.substring(index);
                int paramIndex = videoId.indexOf("&");
                if (paramIndex > 0) videoId = videoId.substring(0, paramIndex);
            }
        } catch (Exception e) {
            // falha ao extrair id, retorna null
        }
        return videoId;
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
        // Limpar webview para liberar recursos
        webEngine.load(null);
        webView.getScene().getWindow().hide();
    }

    @FXML
    private void handleLevantarMao() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Você levantou a mão! Aguarde o organizador.");
        alert.showAndWait();
    }
}
