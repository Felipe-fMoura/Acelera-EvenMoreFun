package view;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Evento;
import model.Usuario;
import service.EventoService;
import session.SessaoUsuario;
import otp.QRCodeGenerator;
import otp.EmailSender;

public class CardEventoController {
    // Elementos existentes
    @FXML private Text txtTituloEvento;
    @FXML private Text txtDescricaoEvento;
    @FXML private Text txtNomeOrganizador;
    @FXML private Text txtDataEvento;
    @FXML private Label lblParticipantes;
    @FXML private Label lblLocal;
    @FXML private ImageView imgEvento;
    @FXML private Label lblPalestrante;

    // Botões
    @FXML private Button btnParticipar;
    @FXML private Button btnCompartilhar;

    private Evento evento;
    Usuario usuarioLogado = SessaoUsuario.getInstance().getUsuario();
    private EventoService eventoService = EventoService.getInstance();

    public void setEvento(Evento evento, Usuario usuarioLogado) {
        this.evento = evento;
        this.usuarioLogado = usuarioLogado;

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
                imgEvento.setImage(new Image(getClass().getResourceAsStream("/images/default-event.jpg")));
            }
        }

        atualizarEstadoParticipacao();
    }

    @FXML
    private void handleParticipar() {
        if (usuarioLogado == null) return;

        try {
            if (eventoService.isParticipante(evento.getId(), usuarioLogado.getId())) {
                eventoService.removerParticipante(evento.getId(), usuarioLogado.getId());
            } else {
                eventoService.adicionarParticipante(evento.getId(), usuarioLogado.getId());

                // ✅ Abrir popup para confirmar o e-mail
                TextInputDialog dialog = new TextInputDialog(usuarioLogado.getEmail());
                dialog.setTitle("Confirmação de Participação");
                dialog.setHeaderText("Confirmação de E-mail");
                dialog.setContentText("Digite seu e-mail para receber o QR Code:");

                dialog.showAndWait().ifPresent(email -> {
                    try {
                        String conteudoQR = "Presença confirmada no evento: " + evento.getTitulo();
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
    private void handleCompartilhar() {
        try {
            String link = gerarLinkEvento();
            javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
            javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
            content.putString(link);
            clipboard.setContent(content);

            btnCompartilhar.setText("Copiado!");
            new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        javafx.application.Platform.runLater(() -> btnCompartilhar.setText("Compartilhar"));
                    }
                },
                2000
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void atualizarEstadoParticipacao() {
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

    // Conexão com tela principal
    private TelaMenuController telaMenuController;
    public void setTelaMenuController(TelaMenuController controller) {
        this.telaMenuController = controller;
    }

    @FXML
    private void editarEvento(ActionEvent event) {
        if (evento.getOrganizador().getId() != usuarioLogado.getId()) {
            mostrarAlerta("Você não tem permissão para editar este evento.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaEditarEvento.fxml"));
            Parent root = loader.load();

            TelaEditarEventoController controller = loader.getController();
            controller.setUsuarioLogado(usuarioLogado);
            controller.carregarEvento(evento);

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
}
