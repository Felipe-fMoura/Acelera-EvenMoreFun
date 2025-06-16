package view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import model.Evento;
import model.Usuario;
import service.EventoService;
import java.time.LocalDateTime;
import java.util.function.UnaryOperator;

public class TelaCriarEventoController {
    @FXML private TextField txtTitulo;
    @FXML private TextArea txtDescricao;
    @FXML private DatePicker dateData;
    @FXML private TextField txtHora;
    @FXML private TextField txtLocal;
    @FXML private TextField txtImagem;
    @FXML private ComboBox<String> cbCategoria;
    @FXML private CheckBox checkPrivado;
    @FXML private TextField txtPalestrante;
    
    private Usuario usuarioLogado;
    private EventoService eventoService = EventoService.getInstance();

    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
    }

    @FXML
    private void initialize() {
        cbCategoria.getItems().addAll("Festas", "Esportes", "Educação", "Negócios", "Outros");

        // TextFormatter para aceitar apenas números e até 4 dígitos
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getControlNewText();
            if (text.matches("\\d{0,4}")) {  // aceita 0 a 4 dígitos
                return change;
            }
            return null;
        };
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        txtHora.setTextFormatter(textFormatter);

        // Listener para formatar com ":" enquanto digita
        txtHora.textProperty().addListener((obs, oldText, newText) -> {
            // Remove ":" para não atrapalhar a formatação
            String digits = newText.replaceAll(":", "");

            if (digits.length() > 4) {
                digits = digits.substring(0, 4);
            }

            String formatted = digits;
            if (digits.length() >= 3) {
                formatted = digits.substring(0, digits.length() - 2) + ":" + digits.substring(digits.length() - 2);
            }

            if (!newText.equals(formatted)) {
                txtHora.setText(formatted);
                txtHora.positionCaret(formatted.length());
            }
        });
    }

    @FXML
    private void handleCriarEvento() {
        try {
            String horaTexto = txtHora.getText().trim();

            // Ajusta o formato da hora, exemplo: "1000" vira "10:00"
            if (horaTexto.matches("\\d{3,4}")) {
                int len = horaTexto.length();
                String horaFormatada = horaTexto.substring(0, len - 2) + ":" + horaTexto.substring(len - 2);
                horaTexto = horaFormatada;
            }

            java.time.LocalTime hora = java.time.LocalTime.parse(horaTexto);
            LocalDateTime dataHora = LocalDateTime.of(dateData.getValue(), hora);

            Evento evento = new Evento(
                txtTitulo.getText(),
                txtDescricao.getText(),
                dataHora,
                txtLocal.getText(),
                usuarioLogado,
                txtPalestrante.getText()
            );

            evento.setOrganizador(usuarioLogado);
            evento.setCategoria(cbCategoria.getValue());
            evento.setPrivado(checkPrivado.isSelected());
            evento.setImagem(txtImagem.getText());

            eventoService.criarEvento(evento);
            usuarioLogado.organizarEvento(evento);
            
         // Adiciona o organizador como participante com permissão "organizador"
            eventoService.adicionarParticipanteComPermissao(evento.getId(), usuarioLogado.getId(), "organizador");


            txtTitulo.getScene().getWindow().hide();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Hora inválida");
            alert.setContentText("Por favor, digite a hora no formato HHmm (ex: 1000) ou HH:mm (ex: 10:00).");
            alert.showAndWait();
        }
    }
}
