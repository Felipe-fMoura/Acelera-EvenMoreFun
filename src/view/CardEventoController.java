package view;

import java.awt.Desktop;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Evento;
import model.Usuario;
import otp.EmailSender;
import otp.QRCodeGenerator;
import otp.IPUtil;
import service.EventoService;
import service.UsuarioService;
import session.SessaoUsuario;
import javafx.geometry.Bounds;

public class CardEventoController {
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
    @FXML private Button btnCompartilhar;
    @FXML private Button btnLista;
    @FXML private Button btnEditar;

    private Evento evento;
    private Usuario usuarioLogado;
    private EventoService eventoService = EventoService.getInstance();

    public void setEvento(Evento evento, Usuario usuarioLogado) {
        this.evento = evento;
        this.usuarioLogado = usuarioLogado != null ? usuarioLogado : SessaoUsuario.getInstance().getUsuario();

        txtTituloEvento.setText(evento.getTitulo());
        txtDescricaoEvento.setText(evento.getDescricao());
        txtNomeOrganizador.setText(evento.getOrganizador().getNome());
        txtDataEvento.setText(evento.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        lblParticipantes.setText(evento.getParticipantes().size() + " participantes");
        lblLocal.setText(evento.getLocal());
        lblPalestrante.setText(evento.getPalestrante());

        if (evento.getImagem() != null && !evento.getImagem().isEmpty()) {
            try {
                imgEvento.setImage(new Image(evento.getImagem()));
            } catch (Exception e) {
                // fallback para imagem padrão se ocorrer erro
                InputStream defaultImgStream = getClass().getResourceAsStream("/images/default-event.jpg");
                if (defaultImgStream != null) {
                    imgEvento.setImage(new Image(defaultImgStream));
                }
            }
        }

        boolean isOrganizador = evento.getOrganizador() != null && usuarioLogado != null &&
                                evento.getOrganizador().getId() == usuarioLogado.getId();

        btnLista.setVisible(isOrganizador);
        btnLista.setManaged(isOrganizador);

        btnEditar.setVisible(isOrganizador);
        btnEditar.setManaged(isOrganizador);
        
     // Oculta o botão "Entrar" se o evento for presencial
        if (btnEntrar != null && evento.getTipo() != null) {
            boolean isOnline = !evento.getTipo().equalsIgnoreCase("Presencial");
            btnEntrar.setVisible(isOnline);
            btnEntrar.setManaged(isOnline); // também remove o espaço do layout
        }

        atualizarEstadoParticipacao();
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
                    	String conteudoQR = "http://" + ip + ":8080/presenca?eventoId=" + evento.getId() + "&usuarioId=" + usuarioLogado.getId();
                    	
                        byte[] qrCodeBytes = QRCodeGenerator.generateQRCode(conteudoQR, 200);
                        EmailSender.sendEmailWithAttachment(
                                email,
                                "Confirmação de Participação",
                                "Olá, sua participação no evento '" + evento.getTitulo() + "' foi confirmada.\n" +
                                "Apresente o QR Code em anexo na entrada do evento.",
                                qrCodeBytes,
                                "qrcode.png"
                        );
                        mostrarAlerta("QR Code enviado com sucesso para: " + email);
                    } catch (Exception e) {
                        e.printStackTrace();
                        mostrarAlerta("Erro ao enviar QR Code por e-mail.");
                    }
                });
            }

            atualizarEstadoParticipacao();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro ao atualizar participação.");
        }
    }
    @FXML
    private Button btnCompartilhar1;

    @FXML
    private void handleCompartilhar(ActionEvent event) {
        ContextMenu menu = new ContextMenu();

        MenuItem facebook = new MenuItem("", carregarIcone("/resources/face.png"));
        facebook.setOnAction(e -> compartilharFacebook());

        MenuItem whatsapp = new MenuItem("", carregarIcone("/resources/zap.png"));
        whatsapp.setOnAction(e -> compartilharWhatsApp());

        MenuItem twitter = new MenuItem("", carregarIcone("/resources/x.png"));
        twitter.setOnAction(e -> compartilharTwitter());

        menu.getItems().addAll(facebook, whatsapp, twitter);
        menu.show(btnCompartilhar, Side.BOTTOM, 0, 0);
        
        Bounds boundsInScreen = btnCompartilhar1.localToScreen(btnCompartilhar1.getBoundsInLocal());
        menu.show(btnCompartilhar1, boundsInScreen.getMinX(), boundsInScreen.getMaxY());
    }

    private void compartilharFacebook() {
        String url = "https://www.facebook.com/sharer/sharer.php?u=" + urlEncode(gerarLinkEvento());
        abrirLinkNoNavegador(url);
    }

    private void compartilharWhatsApp() {
        String texto = "Confira este evento incrível: ";
        String url = "https://wa.me/?text=" + urlEncode(texto + gerarLinkEvento());
        abrirLinkNoNavegador(url);
    }

    private void compartilharTwitter() {
        try {
            String texto = "Confira este evento incrível: ";
            String textoEncode = URLEncoder.encode(texto + gerarLinkEvento(), StandardCharsets.UTF_8.toString());
            String url = "https://twitter.com/intent/tweet?text=" + textoEncode;
            abrirLinkNoNavegador(url);
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
            btnParticipar.setStyle(isParticipante ?
                "-fx-background-color: #e74c3c; -fx-text-fill: white;" :
                "-fx-background-color: #2ecc71; -fx-text-fill: white;");
        }
        lblParticipantes.setText(evento.getParticipantes().size() + " participantes");
    }

    private String gerarLinkEvento() {
        return "https://eventmorefun.com/eventos/" + evento.getId() +
               "?nome=" + evento.getTitulo().replace(" ", "+");
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
        if (evento.getOrganizador() == null || usuarioLogado == null ||
            evento.getOrganizador().getId() != usuarioLogado.getId()) {
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
            stage.show();

            stage.setOnHiding(e -> {
                if (telaMenuController != null) {
                    telaMenuController.carregarEventos();
                }
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
        boolean isOrganizador = evento.getOrganizador() != null &&
                                evento.getOrganizador().getId() == usuarioLogado.getId();

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
            stage.show();
            
         // Marca a presença automaticamente
            eventoService.marcarPresenca(evento.getId(), usuarioLogado.getId());
            
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro ao abrir a sala do evento.");
        }
    }
}
