/*
 * Controller responsável pela exibição e interações com cards de eventos.
 * 
 * Métodos/Fluxos principais:
 * 
 * - setEvento(Evento, Usuario)
 *   Configura todos os componentes visuais do card e lógica de exibição condicional.
 *   Usa ArrayList para participantes e HashMap para presenças (via EventoService).
 * 
 * - handleParticipar()
 *   Gerencia participação no evento com EventoService, incluindo geração de QRCode
 *   (QRCodeGenerator) e envio de e-mail (EmailSender).
 * 
 * - handleCompartilhar(ActionEvent)
 *   Implementa compartilhamento em redes sociais com ContextMenu e MenuItems customizados.
 *   Registra ações via NotificacaoService.
 * 
 * - handleEntrar(ActionEvent)
 *   Controla acesso à sala do evento online, com validações de permissão e presença.
 * 
 * - handleCurtir(ActionEvent)
 *   Gerencia curtidas usando HashSet para evitar duplicações e atualiza interface.
 * 
 * - recarregarComentarios(), carregarComentarios()
 *   Exibe comentários usando ArrayList e padrão ComentarioComUsuario para associação
 *   texto-usuário. Implementa scroll automático para novos comentários.
 * 
 * - handleEnviarComentario()
 *   Adiciona novos comentários ao evento via EventoService e atualiza a UI.
 * 
 * Estruturas de dados principais:
 * 
 * - ArrayList<ComentarioComUsuario> comentarios
 *   Armazena comentários exibidos no card.
 * 
 * - HashMap (implícito via EventoService)
 *   Gerencia presenças e curtidas de usuários.
 * 
 * Componentes de UI:
 * 
 * - ImageView (imgEvento, imgPerfilOrganizador)
 *   Exibe imagens com tratamento de fallback para recursos padrão.
 * 
 * - ContextMenu
 *   Menu dinâmico para compartilhamento em redes sociais.
 * 
 * - ScrollPane + VBox
 *   Container scrollável para exibição de comentários.
 * 
 * Serviços integrados:
 * 
 * - EventoService: Gerencia dados do evento
 * - NotificacaoService: Registra ações do usuário
 * - UsuarioService: Validações de cadastro
 * - EmailSender + QRCodeGenerator: Funcionalidade de QR Code
 * 
 * Padrões utilizados:
 * 
 * - Observer: Atualização automática da UI quando dados mudam
 * - Builder: Para construção de objetos complexos (notificações)
 */

package controllers;

import java.awt.Desktop;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Comentario;
import model.Evento;
import model.Notificacao;
import model.Usuario;
import otp.EmailSender;
import otp.IPUtil;
import otp.QRCodeGenerator;
import service.EventoService;
import service.NotificacaoService;
import service.UsuarioService;
import session.SessaoUsuario;

public class CardEventoController {

	@FXML private VBox paneComentarios;
	@FXML private VBox vboxComentarios;
	@FXML private ScrollPane scrollComentarios;
	@FXML private TextField txtNovoComentario;
	@FXML private Button btnEnviarComentario;
	@FXML private Text txtTituloEvento;
	@FXML private Text txtDescricaoEvento;
	@FXML private Text txtNomeOrganizador;
	@FXML private Text txtDataEvento;
	@FXML private Label lblParticipantes;
	@FXML private Label lblLocal;
	@FXML private ImageView imgEvento;
    @FXML private Label lblPalestrante;
	@FXML private Button btnEntrar;
	@FXML private Button btnParticipar;
	@FXML private Button btnLista;
	@FXML private Button btnEditar;
	@FXML private Button btnGaleria;
	@FXML private Button btnNotificacao;
	@FXML private Button btnCurtir;
	@FXML private ImageView imgPerfilOrganizador;
	@FXML private Text txtUserNameOrganizador;

	private Evento evento;
	private Usuario usuarioLogado;
	private EventoService eventoService = EventoService.getInstance();
	private boolean jaCurtiu = false;
	UsuarioService usuarioService = UsuarioService.getInstance();

	public void setEvento(Evento evento, Usuario usuarioLogado) {
		this.evento = evento;
		this.usuarioLogado = SessaoUsuario.getInstance().getUsuario();
		inicializarCurtida();

		Usuario organizador = evento.getOrganizador();
		if (organizador != null) {
			Image imagemOrganizador;

			try {
				String urlFoto = organizador.getCaminhoFotoPerfil();

				if (urlFoto != null && !urlFoto.trim().isEmpty()) {
					String caminhoFinal;
					if (urlFoto.startsWith("http") || urlFoto.startsWith("file:")) {
						caminhoFinal = urlFoto;
					} else {
						caminhoFinal = "file:///" + urlFoto.replace("\\", "/");
					}

					imagemOrganizador = new Image(caminhoFinal);

					// Verifica se houve erro ao carregar
					if (imagemOrganizador.isError()) {
						throw new IOException("Erro ao carregar imagem personalizada.");
					}
				} else {
					throw new IOException("Caminho da imagem está vazio ou nulo.");
				}
			} catch (Exception e) {
				// Carrega imagem padrão caso ocorra erro ou não haja imagem definida
				InputStream defaultImgStream = getClass()
						.getResourceAsStream("/resources/profile/iconFotoPerfilDefault.png");
				imagemOrganizador = new Image(defaultImgStream);
			}

			imgPerfilOrganizador.setImage(imagemOrganizador);

			// Define nome de usuário (ou nome comum como fallback)
			txtNomeOrganizador
					.setText(organizador.getUsername() != null ? organizador.getUsername() : organizador.getNome());
		}

		txtTituloEvento.setText(evento.getTitulo());
		txtDescricaoEvento.setText(evento.getDescricao());
		txtDataEvento.setText(evento.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
		lblParticipantes.setText(evento.getParticipantes().size() + " participantes");
		lblLocal.setText(evento.getLocal());
		lblPalestrante.setText(evento.getPalestrante());

		if (evento.getImagem() != null && !evento.getImagem().isEmpty()) {
			try {
				imgEvento.setImage(new Image(evento.getImagem()));
			} catch (Exception e) {
				InputStream defaultImgStream = getClass().getResourceAsStream("/resources/default-event.jpg");
				if (defaultImgStream != null) {
					imgEvento.setImage(new Image(defaultImgStream));
				}
			}
		}

		boolean isOrganizador = evento.getOrganizador() != null && usuarioLogado != null
				&& evento.getOrganizador().getId() == usuarioLogado.getId();

		btnLista.setVisible(isOrganizador);
		btnLista.setManaged(isOrganizador);

		btnEditar.setVisible(isOrganizador);
		btnEditar.setManaged(isOrganizador);

		btnNotificacao.setVisible(isOrganizador);
		btnNotificacao.setManaged(isOrganizador);

		if (btnEntrar != null && evento.getTipo() != null) {
			boolean isOnline = !evento.getTipo().equalsIgnoreCase("Presencial");
			btnEntrar.setVisible(isOnline);
			btnEntrar.setManaged(isOnline);
		}

		if (telaMenuController != null) {
			String caminhoFoto = evento.getOrganizador().getCaminhoFotoPerfil();
			telaMenuController.atualizarFotoPerfilOrganizador(caminhoFoto);
		}

		atualizarEstadoParticipacao();
		recarregarComentarios();
	}

	@FXML
	private void handleParticipar() {
		usuarioLogado = SessaoUsuario.getInstance().getUsuario();
		if (usuarioLogado == null) {
			mostrarAlerta("Você precisa estar logado para participar de um evento.");
			return;
		}

		try {
			if (eventoService.isParticipante(evento.getId(), usuarioLogado.getId())) {
				eventoService.removerParticipante(evento.getId(), usuarioLogado.getId());
			} else {
				eventoService.adicionarParticipante(evento.getId(), usuarioLogado.getId());

				TextInputDialog dialog = new TextInputDialog(usuarioLogado.getEmail());
				dialog.setTitle("Confirmação de Participação");
				dialog.setHeaderText("Confirmação de E-mail");
				dialog.setContentText("Digite seu e-mail para receber o QR Code:");

				dialog.showAndWait().ifPresent(email -> {
					try {
						String ip = IPUtil.getLocalIPv4();
						String conteudoQR = "http://" + ip + ":8080/presenca?eventoId=" + evento.getId() + "&usuarioId="
								+ usuarioLogado.getId();

						byte[] qrCodeBytes = QRCodeGenerator.generateQRCode(conteudoQR, 200);
						EmailSender.sendEmailWithAttachment(email, "Confirmação de Participação",
								"Olá, sua participação no evento '" + evento.getTitulo() + "' foi confirmada.\n"
										+ "Apresente o QR Code em anexo na entrada do evento.",
								qrCodeBytes, "qrcode.png");
						mostrarAlerta("QR Code enviado com sucesso para: " + email);
					} catch (Exception e) {
						e.printStackTrace();
						mostrarAlerta("Erro ao enviar QR Code por e-mail.");
					}
				});
			}

			atualizarEstadoParticipacao();
			int userId = SessaoUsuario.getUsuarioLogado().getId();

			Notificacao notificacao = new Notificacao("Você está participando do evento '" + evento.getTitulo() + "'",
					LocalDateTime.now(), false, Notificacao.Tipo.HISTORICO, "Sistema");

			NotificacaoService.getInstance().registrarNotificacao(userId, notificacao);

		} catch (Exception e) {
			e.printStackTrace();
			mostrarAlerta("Erro ao atualizar participação.");
		}
	}

	@FXML
	private Button btnCompartilhar1;

	@FXML
	private void handleCompartilhar(ActionEvent event) {

		Notificacao notificacao = new Notificacao("Você compartilhou o evento '" + evento.getTitulo() + "'",
				LocalDateTime.now(), false, Notificacao.Tipo.HISTORICO, "Sistema");
		NotificacaoService.getInstance().registrarNotificacao(usuarioLogado.getId(), notificacao);

		ContextMenu menu = new ContextMenu();

		MenuItem facebook = new MenuItem("", carregarIcone("/resources/logo/face.png"));
		facebook.setOnAction(e -> compartilharFacebook());

		MenuItem whatsapp = new MenuItem("", carregarIcone("/resources/logo/zap.png"));
		whatsapp.setOnAction(e -> compartilharWhatsApp());

		MenuItem twitter = new MenuItem("", carregarIcone("/resources/logo/x.png"));
		twitter.setOnAction(e -> compartilharTwitter());

		menu.getItems().addAll(facebook, whatsapp, twitter);
		menu.show(btnCompartilhar1, Side.BOTTOM, 0, 0);

		Bounds boundsInScreen = btnCompartilhar1.localToScreen(btnCompartilhar1.getBoundsInLocal());
		menu.show(btnCompartilhar1, boundsInScreen.getMinX(), boundsInScreen.getMaxY());

	}

	private void compartilharFacebook() {
		String url = "https://www.facebook.com/sharer/sharer.php?u=" + urlEncode(gerarLinkEvento());
		abrirLinkNoNavegador(url);

		int userId = SessaoUsuario.getUsuarioLogado().getId();
		Notificacao notificacao = new Notificacao(
				"Você compartilhou o evento '" + evento.getTitulo() + "'" + " no Facebook", LocalDateTime.now(), false,
				Notificacao.Tipo.HISTORICO, "Sistema");

		NotificacaoService.getInstance().registrarNotificacao(userId, notificacao);

	}

	private void compartilharWhatsApp() {
		String texto = "Confira este evento incrível: ";
		String url = "https://wa.me/?text=" + urlEncode(texto + gerarLinkEvento());
		abrirLinkNoNavegador(url);

		int userId = SessaoUsuario.getUsuarioLogado().getId();

		Notificacao notificacao = new Notificacao(
				"Você compartilhou o evento '" + evento.getTitulo() + "'" + " no Whatsapp", LocalDateTime.now(), false,
				Notificacao.Tipo.HISTORICO, "Sistema");

		NotificacaoService.getInstance().registrarNotificacao(userId, notificacao);
	}

	private void compartilharTwitter() {
		try {
			String texto = "Confira este evento incrível: ";
			String textoEncode = URLEncoder.encode(texto + gerarLinkEvento(), StandardCharsets.UTF_8.toString());
			String url = "https://twitter.com/intent/tweet?text=" + textoEncode;
			abrirLinkNoNavegador(url);
			int userId = SessaoUsuario.getUsuarioLogado().getId();
			Notificacao notificacao = new Notificacao(
					"Você compartilhou o evento '" + evento.getTitulo() + "'" + " no Twitter", LocalDateTime.now(),
					false, Notificacao.Tipo.HISTORICO, "Sistema");

			NotificacaoService.getInstance().registrarNotificacao(userId, notificacao);

		} catch (Exception e) {
			e.printStackTrace();
			mostrarAlerta("Erro ao preparar link para Twitter.");
		}
	}

	private String urlEncode(String texto) {
		try {
			return URLEncoder.encode(texto, StandardCharsets.UTF_8.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return texto;
		}
	}

	private ImageView carregarIcone(String caminhoRelativo) {
		try {
			Image img = new Image(getClass().getResourceAsStream(caminhoRelativo));
			ImageView imgView = new ImageView(img);
			imgView.setFitWidth(16);
			imgView.setFitHeight(16);
			return imgView;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private void abrirLinkNoNavegador(String url) {
		try {
			Desktop.getDesktop().browse(new URI(url));
		} catch (Exception e) {
			e.printStackTrace();
			mostrarAlerta("Erro ao abrir o navegador para compartilhar.");
		}
	}

	private void atualizarEstadoParticipacao() {
		usuarioLogado = SessaoUsuario.getInstance().getUsuario();
		if (usuarioLogado != null) {
			boolean isParticipante = eventoService.isParticipante(evento.getId(), usuarioLogado.getId());
			btnParticipar.setText(isParticipante ? "Cancelar" : "Participar");

		}
		lblParticipantes.setText(evento.getParticipantes().size() + " participantes");
	}

	private String gerarLinkEvento() {
		return "https://eventmorefun.com/eventos/" + evento.getId() + "?nome=" + evento.getTitulo().replace(" ", "+");
	}

	private void mostrarAlerta(String mensagem) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Aviso");
		alert.setHeaderText(null);
		alert.setContentText(mensagem);
		alert.showAndWait();
	}

	private TelaMenuController telaMenuController;

	public void setTelaMenuController(TelaMenuController controller) {
		this.telaMenuController = controller;
	}

	@FXML
	private void editarEvento(ActionEvent event) {
		usuarioLogado = SessaoUsuario.getInstance().getUsuario();
		if (evento.getOrganizador() == null || usuarioLogado == null
				|| evento.getOrganizador().getId() != usuarioLogado.getId()) {
			mostrarAlerta("Você não tem permissão para editar este evento.");
			return;
		}

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaEditarEvento.fxml"));
			Parent root = loader.load();

			TelaEditarEventoController controller = loader.getController();
			controller.setUsuarioLogado(usuarioLogado);
			controller.setEvento(evento);

			Stage stage = new Stage();
			stage.setScene(new Scene(root));
			stage.setTitle("Editar Evento");
			stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/logo/LOGOROXA.png")));
			stage.show();

			stage.setOnHiding(e -> {
				if (telaMenuController != null) {
					telaMenuController.carregarEventos();
				}

				int userId = SessaoUsuario.getUsuarioLogado().getId();
				Notificacao notificacao = new Notificacao("Você editou o evento '" + evento.getTitulo() + "'",
						LocalDateTime.now(), false, Notificacao.Tipo.HISTORICO, "Sistema");

				NotificacaoService.getInstance().registrarNotificacao(userId, notificacao);

			});

		} catch (IOException e) {
			e.printStackTrace();
			mostrarAlerta("Erro ao abrir tela de edição de evento");
		}
	}

	@FXML
	private void handleLista(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaParticipantesEvento.fxml"));
			Parent root = loader.load();

			TelaParticipantesEventoController controller = loader.getController();
			controller.setEvento(this.evento);

			Stage stage = new Stage();
			stage.setTitle("Lista de Participantes");
			stage.setScene(new Scene(root));
			stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/logo/LOGOROXA.png")));
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
			mostrarAlerta("Erro ao abrir lista de participantes.");
		}
	}

	@FXML
	private void handleEntrar(ActionEvent event) {
		usuarioLogado = SessaoUsuario.getInstance().getUsuario();

		if (usuarioLogado == null) {
			mostrarAlerta("Você precisa estar logado para entrar na sala do evento.");
			return;
		}

		UsuarioService usuarioService = UsuarioService.getInstance();
		if (!usuarioService.isCadastroCompleto(usuarioLogado)) {
			mostrarAlerta("Seu cadastro está incompleto. Atualize seus dados antes de entrar no evento.");
			return;

		}
		boolean isParticipante = eventoService.isParticipante(evento.getId(), usuarioLogado.getId());
		boolean isOrganizador = evento.getOrganizador() != null
				&& evento.getOrganizador().getId() == usuarioLogado.getId();

		if (!isParticipante && !isOrganizador) {
			mostrarAlerta("Apenas participantes inscritos ou organizadores podem entrar na sala do evento.");
			return;
		}

		if (!isOrganizador && !evento.isAcessoLiberado()) {
			mostrarAlerta("A sala do evento ainda está trancada. Aguarde o organizador liberar o acesso.");
			return;
		}

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaEventoAoVivo.fxml"));
			Parent root = loader.load();

			TelaEventoAoVivoController controller = loader.getController();
			controller.setEvento(this.evento);

			Stage stage = new Stage();
			stage.setTitle("Sala do Evento");
			stage.setScene(new Scene(root));
			stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/logo/LOGOROXA.png")));
			stage.show();

			// Marca a presença automaticamente
			eventoService.marcarPresenca(evento.getId(), usuarioLogado.getId());
			int userId = SessaoUsuario.getUsuarioLogado().getId();

			Notificacao notificacao = new Notificacao("Você entrou ao-vivo no evento '" + evento.getTitulo() + "'",
					LocalDateTime.now(), false, Notificacao.Tipo.HISTORICO, "Sistema");

			NotificacaoService.getInstance().registrarNotificacao(userId, notificacao);

		} catch (IOException e) {
			e.printStackTrace();
			mostrarAlerta("Erro ao abrir a sala do evento.");
		}
	}

	@FXML
	private void handleGaleria(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaGaleria.fxml"));
			Parent root = loader.load();

			TelaGaleriaController controller = loader.getController();
			controller.setEvento(this.evento, SessaoUsuario.getInstance().getUsuario());

			Stage stage = new Stage();
			stage.setTitle("Galeria do Evento");
			stage.setScene(new Scene(root));
			stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/logo/LOGOROXA.png")));
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
			mostrarAlerta("Erro ao abrir galeria de fotos.");
		}
	}

	@FXML
	private void handleNotificacao(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaEnviarNotificacao.fxml"));
			Parent root = loader.load();

			TelaEnviarNotificacaoController controller = loader.getController();
			controller.setDados(evento, usuarioLogado);

			Stage stage = new Stage();
			stage.setTitle("Enviar Notificação");
			stage.setScene(new Scene(root));
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void handleCurtir(ActionEvent event) {
		this.usuarioLogado = SessaoUsuario.getInstance().getUsuario();

		if (usuarioLogado == null) {
			mostrarAlerta("Você precisa estar logado para curtir um evento.");
			return;
		}

		boolean sucesso;
		if (jaCurtiu) {
			// Tenta remover a curtida
			sucesso = evento.descurtirEvento(usuarioLogado);
			if (sucesso) {
				jaCurtiu = false;
				Notificacao notificacao = new Notificacao("Você descurtiu o evento '" + evento.getTitulo() + "'",
						LocalDateTime.now(), false, Notificacao.Tipo.HISTORICO, "Sistema");
				NotificacaoService.getInstance().registrarNotificacao(usuarioLogado.getId(), notificacao);

			}
		} else {
			// Tenta curtir o evento
			sucesso = evento.curtirEvento(usuarioLogado);
			if (sucesso) {

				Notificacao notificacao = new Notificacao("Você curtiu o evento '" + evento.getTitulo() + "'",
						LocalDateTime.now(), false, Notificacao.Tipo.HISTORICO, "Sistema");
				NotificacaoService.getInstance().registrarNotificacao(usuarioLogado.getId(), notificacao);

				jaCurtiu = true;
			}
		}

		if (sucesso) {
			atualizarBotaoCurtir();
		} else {
			mostrarAlerta("Operação não permitida.");
		}
	}

	private void atualizarBotaoCurtir() {
		String textoBotao = jaCurtiu ? "Descurtir" : "Curtir";
		btnCurtir.setText(textoBotao + " (" + evento.getCurtidas() + ")");
	}

	// Método para inicializar o estado de curtida ao carregar o evento
	private void inicializarCurtida() {
		if (usuarioLogado != null) {
			jaCurtiu = evento.getUsuariosQueCurtiram().contains(usuarioLogado.getId());
		} else {
			jaCurtiu = false;
		}
		atualizarBotaoCurtir();
	}

	// Sessão de comentarios por evento
	private List<ComentarioComUsuario> comentarios = new ArrayList<>();

	@FXML
	private void carregarComentarios() {
		vboxComentarios.getChildren().clear();

		for (ComentarioComUsuario comentario : comentarios) {
			// Container principal do comentário
			HBox comentarioContainer = new HBox(10);
			comentarioContainer.setStyle("-fx-background-color: #e1e1e1; -fx-padding: 8; -fx-background-radius: 5;");

			// Foto do usuário
			ImageView fotoUsuario = new ImageView();
			fotoUsuario.setFitHeight(40);
			fotoUsuario.setFitWidth(40);
			fotoUsuario.setPreserveRatio(true);

			try {
				String caminhoFoto = comentario.fotoUsuario;
				if (caminhoFoto != null && !caminhoFoto.isEmpty()) {
					if (caminhoFoto.startsWith("http") || caminhoFoto.startsWith("file:")) {
						fotoUsuario.setImage(new Image(caminhoFoto));
					} else {
						fotoUsuario.setImage(new Image("file:" + caminhoFoto));
					}
				} else {
					// Foto padrão se não tiver
					InputStream defaultImgStream = getClass()
							.getResourceAsStream("/resources/profile/iconFotoPerfilDefault.png");
					if (defaultImgStream != null) {
						fotoUsuario.setImage(new Image(defaultImgStream));
					}
				}
				
				// Adiciona clique na foto para abrir perfil
				fotoUsuario.setOnMouseClicked(event -> abrirPerfilUsuario(comentario.nomeUsuario));
				fotoUsuario.setStyle("-fx-cursor: hand;");
				
			} catch (Exception e) {
				e.printStackTrace();
			}

			// Container do texto do comentário
			VBox textoContainer = new VBox(5);

			// Nome do usuário
			Label nomeLabel = new Label(comentario.nomeUsuario);
			nomeLabel.setOnMouseClicked(event -> abrirPerfilUsuario(comentario.nomeUsuario));
			nomeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #3a6cd1; -fx-cursor: hand;");


			// Texto do comentário
			Label comentarioLabel = new Label(comentario.texto);
			comentarioLabel.setWrapText(true);
			comentarioLabel.setStyle("-fx-font-size: 14px;");

			textoContainer.getChildren().addAll(nomeLabel, comentarioLabel);
			comentarioContainer.getChildren().addAll(fotoUsuario, textoContainer);

			vboxComentarios.getChildren().add(comentarioContainer);
		}

		// Scroll para o fim para ver o comentário mais recente
		scrollComentarios.layout();
		scrollComentarios.setVvalue(1.0);
	}

	@FXML
	private void handleEnviarComentario() {
		String texto = txtNovoComentario.getText().trim();
		if (!texto.isEmpty()) {
			Usuario usuario = SessaoUsuario.getInstance().getUsuario();
			if (usuario != null) {
				Comentario novo = new Comentario(texto, usuario.getId());
				eventoService.adicionarComentarioAoEvento(evento.getId(), novo);

				// Atualiza a lista de comentários na interface corretamente
				recarregarComentarios();
				// Limpa o campo
				txtNovoComentario.clear();

				// Registrar notificação
				Notificacao notificacao = new Notificacao("Você comentou no evento '" + evento.getTitulo() + "'",
						LocalDateTime.now(), false, Notificacao.Tipo.HISTORICO, "Sistema");
				NotificacaoService.getInstance().registrarNotificacao(usuario.getId(), notificacao);
			} else {
				mostrarAlerta("Você precisa estar logado para comentar.");
			}
		} else {
			Alert alert = new Alert(Alert.AlertType.WARNING, "O comentário não pode ser vazio.", ButtonType.OK);
			alert.showAndWait();
		}
	}

	private static class ComentarioComUsuario {
		String texto;
		String nomeUsuario;
		String fotoUsuario;

		public ComentarioComUsuario(String texto, Usuario usuario) {
			this.texto = texto;
			this.nomeUsuario = usuario.getUsername() != null ? usuario.getUsername() : usuario.getNome();
			this.fotoUsuario = usuario.getCaminhoFotoPerfil();
		}
	}

	public void recarregarComentarios() {
		// Limpa os comentários atuais
		comentarios.clear();

		List<Comentario> comentariosDoBanco = eventoService.getComentariosDoEvento(evento.getId());
		for (Comentario c : comentariosDoBanco) {
			Usuario autor = usuarioService.buscarPorId(c.getUsuarioId());
			comentarios.add(new ComentarioComUsuario(c.getTexto(), autor));
		}

		// Atualiza a interface
		carregarComentarios();
	}
	
	private void abrirPerfilUsuario(String nomeUsuario) {
	    Usuario usuario = usuarioService.buscarPorUsername(nomeUsuario);
	    if (usuario == null) {
	        mostrarAlerta("Usuário não encontrado.");
	        return;
	    }

	    try {
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PerfilUsuario.fxml"));
	        Parent root = loader.load();

	        PerfilUsuarioController controller = loader.getController();
	        controller.setUsuario(usuario);

	        Stage stage = new Stage();
	        stage.setTitle("Perfil de " + usuario.getNome());
	        stage.setScene(new Scene(root));
	        stage.setResizable(false);
	        stage.show();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}



}
