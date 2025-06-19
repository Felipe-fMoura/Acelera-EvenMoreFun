package view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Evento;
import model.Notificacao;
import model.Usuario;
import otp.EmailSender;
import service.NotificacaoService;

import java.time.LocalDateTime;

public class TelaEnviarNotificacaoController {

    @FXML private TextArea txtMensagem;
    @FXML private CheckBox chkEmail;
    @FXML private Button btnFechar;
    @FXML private Button btnEnviar;

    private Evento evento;
    private Usuario organizador;

    public void setDados(Evento evento, Usuario organizador) {
        this.evento = evento;
        this.organizador = organizador;
    }

    @FXML
    private void handleFechar() {
        Stage stage = (Stage) btnFechar.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleEnviar() {
        String mensagem = txtMensagem.getText().trim();
        boolean enviarPorEmail = chkEmail.isSelected();

        if (mensagem.isEmpty()) {
            mostrarAlerta("A mensagem não pode estar vazia.");
            return;
        }

        for (Usuario participante : evento.getParticipantes()) {
            Notificacao notificacao = new Notificacao(
                mensagem,
                LocalDateTime.now(),
                enviarPorEmail,
                Notificacao.Tipo.ALERTA,
                organizador.getNome()
            );
            NotificacaoService.getInstance().registrarNotificacao(participante.getId(), notificacao);

            if (enviarPorEmail) {
                try {
                    EmailSender.sendEmail(
                        participante.getEmail(),
                        "Notificação sobre o evento: " + evento.getTitulo(),
                        mensagem
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                    mostrarAlerta("Erro ao enviar e-mail para: " + participante.getEmail());
                }
            }
        }

        mostrarAlerta("Notificação enviada com sucesso.");
        handleFechar();
    }

    private void mostrarAlerta(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Aviso");
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}
