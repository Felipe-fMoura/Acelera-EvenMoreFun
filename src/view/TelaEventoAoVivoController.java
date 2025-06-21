package view;

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import model.Evento;
import model.Notificacao;
import model.Usuario;
import service.ChatService;
import service.ChatService.MensagemChat;
import service.NotificacaoService;
import session.SessaoUsuario;

public class TelaEventoAoVivoController {

	@FXML
	private WebView webView;
	@FXML
	private Text lblSemVideo;
	@FXML
	private VBox mensagensContainer;
	@FXML
	private TextField campoMensagem;
	@FXML
	private Text nomeUsuario;

	@FXML
	private HBox videoControlsPane;
	@FXML
	private TextField txtUrlVideo;

	@FXML
	private HBox acessoControlsPane;
	@FXML
	private Button btnToggleAcesso;

	private Evento evento;
	private Usuario usuario;
	private WebEngine webEngine;
	private Timeline chatRefresh;
	Usuario usuarioLogado = SessaoUsuario.getInstance().getUsuario();

	private Set<String> handAckRecebidos = new HashSet<>();
	private Map<String, Boolean> aguardandoRespostaMap = new HashMap<>();

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
		acessoControlsPane.setVisible(isOrganizador);
		acessoControlsPane.setManaged(isOrganizador);

		atualizarBotaoAcesso();

		aguardandoRespostaMap.clear();
		handAckRecebidos.clear();

		mensagensContainer.getChildren().clear();

		List<MensagemChat> mensagens = ChatService.getInstancia().getMensagens(evento);
		for (MensagemChat msg : mensagens) {
			String texto = msg.getTexto();
			if (texto.startsWith("[HAND_ACK] ")) {
				String nomeAck = texto.replace("[HAND_ACK] ", "");
				handAckRecebidos.add(nomeAck);
			}
			adicionarMensagemNaInterface(msg);

			Notificacao notificacao = new Notificacao("Você comentou '" + msg + "no evento" + evento.getTitulo() + "'",
					LocalDateTime.now(), false, Notificacao.Tipo.HISTORICO, "Sistema");
			NotificacaoService.getInstance().registrarNotificacao(usuarioLogado.getId(), notificacao);

		}

		if (evento.getUrlVideo() != null && !evento.getUrlVideo().isEmpty()) {
			carregarVideo(evento.getUrlVideo());
		} else {
			mostrarSemVideo();
		}

		startChatAutoRefresh();
	}

	@FXML
	private void handleToggleAcesso() {
		evento.setAcessoLiberado(!evento.isAcessoLiberado());
		atualizarBotaoAcesso();
	}

	private void atualizarBotaoAcesso() {
		if (evento.isAcessoLiberado()) {
			btnToggleAcesso.setText("🔓 Acesso Liberado");
			btnToggleAcesso.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
		} else {
			btnToggleAcesso.setText("🔒 Acesso Trancado");
			btnToggleAcesso.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white;");
		}
	}

	private void startChatAutoRefresh() {
		chatRefresh = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
			List<MensagemChat> mensagens = ChatService.getInstancia().getMensagens(evento);
			mensagensContainer.getChildren().clear();
			for (MensagemChat msg : mensagens) {
				String texto = msg.getTexto();
				if (texto.startsWith("[HAND_ACK] ")) {
					String nomeAck = texto.replace("[HAND_ACK] ", "");
					handAckRecebidos.add(nomeAck);
				}
				adicionarMensagemNaInterface(msg);
			}
		}));
		chatRefresh.setCycleCount(Animation.INDEFINITE);
		chatRefresh.play();
	}

	private void mostrarSemVideo() {
		webEngine.loadContent(
				"<html><body style='background:black; color:white; display:flex; justify-content:center; align-items:center; height:100%;'>"
						+ "<h2>Vídeo indisponível</h2></body></html>");
		lblSemVideo.setVisible(true);
	}

	@FXML
	private void handleCarregarVideo() {
		String url = txtUrlVideo.getText().trim();

		if (url.isEmpty()) {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Selecione um vídeo MP4");
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivos de vídeo MP4", "*.mp4"));

			File arquivo = fileChooser.showOpenDialog(webView.getScene().getWindow());
			if (arquivo != null) {
				String caminho = arquivo.toURI().toString();
				carregarVideo(caminho);
				evento.setUrlVideo(caminho);
			} else {
				mostrarSemVideo();
			}

		} else {
			evento.setUrlVideo(url);
			carregarVideo(url);
		}
	}

	private void carregarVideo(String url) {
		try {
			if (url.contains("youtube.com") || url.contains("youtu.be")) {
				String videoId = extrairVideoIdYouTube(url);
				if (videoId != null) {
					String embedHtml = """
							<html><body style='margin:0; background:black;'>
							<iframe width='100%%' height='100%%' src='https://www.youtube.com/embed/%s'
							frameborder='0' allowfullscreen></iframe></body></html>
							""".formatted(videoId);
					webEngine.loadContent(embedHtml, "text/html");
					lblSemVideo.setVisible(false);
					return;
				}
			} else if (url.startsWith("file:/") && url.endsWith(".mp4")) {
				String html = """
						<html>
						<body style="margin:0; background:black;">
						  <video width="100%%" height="100%%" controls autoplay>
						    <source src="%s" type="video/mp4">
						    Seu navegador não suporta o elemento de vídeo.
						  </video>
						</body>
						</html>
						""".formatted(url);
				webEngine.loadContent(html, "text/html");
				lblSemVideo.setVisible(false);
				return;
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@FXML
	private void handleEnviarMensagem() {
		String msg = campoMensagem.getText().trim();
		if (!msg.isEmpty()) {
			ChatService.getInstancia().adicionarMensagem(evento, usuario.getId(), usuario.getNome() + ": " + msg);
			campoMensagem.clear();
			refreshMensagens();
		}
	}

	private void refreshMensagens() {
		List<MensagemChat> mensagens = ChatService.getInstancia().getMensagens(evento);
		mensagensContainer.getChildren().clear();
		for (MensagemChat msg : mensagens) {
			adicionarMensagemNaInterface(msg);
		}
	}

	private void adicionarMensagemNaInterface(MensagemChat mensagem) {
		String texto = mensagem.getTexto();
		HBox linhaMensagem = new HBox();
		linhaMensagem.setSpacing(10);
		linhaMensagem.setAlignment(Pos.CENTER_LEFT);
		linhaMensagem.setPadding(new Insets(5));

		if (texto.startsWith("[HAND_RAISE] ")) {
			String nomeSolicitante = texto.replace("[HAND_RAISE] ", "");
			if (usuario.equals(evento.getOrganizador())) {
				VBox box = new VBox(5);
				Label lbl = new Label(nomeSolicitante + " levantou a mão ✋");
				lbl.setStyle("-fx-font-weight: bold; -fx-text-fill: orange;");

				Button btnOk = new Button("OK");
				btnOk.setOnAction(e -> {
					mensagensContainer.getChildren().remove(linhaMensagem);
					ChatService.getInstancia().removerMensagem(evento, mensagem);
					String resposta = "[HAND_ACK] " + nomeSolicitante;
					ChatService.getInstancia().adicionarMensagem(evento, 0, resposta);
					aguardandoRespostaMap.put(nomeSolicitante, Boolean.FALSE);
				});

				box.getChildren().addAll(lbl, btnOk);
				linhaMensagem.getChildren().add(box);
				mensagensContainer.getChildren().add(linhaMensagem);
			} else {
				Text txt = new Text(nomeSolicitante + " levantou a mão ✋");
				txt.setStyle("-fx-font-style: italic; -fx-fill: gray;");
				linhaMensagem.getChildren().add(txt);
				mensagensContainer.getChildren().add(linhaMensagem);
			}
		} else if (texto.startsWith("[HAND_ACK] ")) {
			String nome = texto.replace("[HAND_ACK] ", "");
			if (usuario.getNome().equals(nome) && !handAckRecebidos.contains(nome)) {
				handAckRecebidos.add(nome);
				Platform.runLater(() -> {
					Alert alert = new Alert(Alert.AlertType.INFORMATION,
							"O palestrante foi notificado e irá atendê-lo em breve.");
					alert.showAndWait();
				});
			}
			if (usuario.equals(evento.getOrganizador())) {
				Text txtMensagem = new Text(nome + " foi atendido pelo organizador.");
				txtMensagem.setStyle("-fx-fill: gray; -fx-font-style: italic;");
				linhaMensagem.getChildren().add(txtMensagem);
				mensagensContainer.getChildren().add(linhaMensagem);
			}
		} else {
			String nomeRemetente = texto.split(":")[0].trim();
			Text txtMensagem = new Text(texto);

			if (usuario.equals(evento.getOrganizador()) && aguardandoRespostaMap.containsKey(nomeRemetente)) {
				Boolean jaDestacado = aguardandoRespostaMap.get(nomeRemetente);
				if (jaDestacado == Boolean.FALSE) {
					txtMensagem.setStyle("-fx-fill: orange; -fx-font-weight: bold;");
					aguardandoRespostaMap.put(nomeRemetente, Boolean.TRUE);
				} else if (jaDestacado == Boolean.TRUE) {
					aguardandoRespostaMap.remove(nomeRemetente);
				}
			}

			linhaMensagem.getChildren().add(txtMensagem);

			if (usuario.equals(evento.getOrganizador())) {
				Button btnExcluir = new Button("🗑");
				btnExcluir.setStyle(
						"-fx-background-color: transparent; -fx-text-fill: #888; -fx-font-size: 10px; -fx-padding: 2 4; -fx-cursor: hand;");
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
		if (chatRefresh != null) {
			chatRefresh.stop();
		}
		webEngine.load(null);
		webView.getScene().getWindow().hide();
	}

	@FXML
	private void handleLevantarMao() {
		String msg = "[HAND_RAISE] " + usuario.getNome();
		ChatService.getInstancia().adicionarMensagem(evento, usuario.getId(), msg);
		refreshMensagens();
		Alert alert = new Alert(Alert.AlertType.INFORMATION, "Você levantou a mão! Aguarde o organizador.");
		alert.showAndWait();
	}
}
