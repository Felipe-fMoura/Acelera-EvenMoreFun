package view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Evento;
import model.Usuario;
import service.EventoService;
import java.time.LocalDateTime;

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
    }

    @FXML
    private void handleCriarEvento() {
        LocalDateTime dataHora = LocalDateTime.of(
            dateData.getValue(),
            java.time.LocalTime.parse(txtHora.getText())
        );
        
        Evento evento = new Evento(
            txtTitulo.getText(),
            txtDescricao.getText(),
            dataHora,
            txtLocal.getText(),       
            usuarioLogado,
            txtPalestrante.getText()
        );
        
        evento.setCategoria(cbCategoria.getValue());
        evento.setPrivado(checkPrivado.isSelected());
        evento.setImagem(txtImagem.getText());
        
        eventoService.criarEvento(evento);
        usuarioLogado.organizarEvento(evento);
        
        // Fechar a janela
        txtTitulo.getScene().getWindow().hide();
    }
}