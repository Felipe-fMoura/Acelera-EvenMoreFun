package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Evento;
import model.Usuario;
import service.EventoService;

import java.io.IOException;
import java.time.LocalDateTime;

public class TelaEditarEventoController {
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
    private Evento eventoSelecionado;
    private Evento evento;

    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
    }
    
    public void setEvento(int eventoId) {
        this.eventoSelecionado = eventoService.buscarEventoPorId(eventoId);
        if (eventoSelecionado != null) {
            preencherCamposComEvento(eventoSelecionado);
        }
    }
    
    private void preencherCamposComEvento(Evento evento) {
        txtTitulo.setText(evento.getTitulo());
        txtDescricao.setText(evento.getDescricao());
        dateData.setValue(evento.getData().toLocalDate());
        txtHora.setText(evento.getData().toLocalTime().toString());
        txtLocal.setText(evento.getLocal());
        txtImagem.setText(evento.getImagem());
        cbCategoria.setValue(evento.getCategoria());
        checkPrivado.setSelected(evento.isPrivado());
        txtPalestrante.setText(evento.getPalestrante());
    }
    

    @FXML
    private void initialize() {
        cbCategoria.getItems().addAll("Festas", "Esportes", "Educação", "Negócios", "Outros");
    }

    
    public void carregarEvento(Evento evento) {
        this.evento = evento;

        // Preencher os campos com os dados do evento
        txtTitulo.setText(evento.getTitulo());
        txtDescricao.setText(evento.getDescricao());
        dateData.setValue(evento.getData().toLocalDate());
        txtHora.setText(evento.getData().toLocalTime().toString());
        txtLocal.setText(evento.getLocal());
        txtImagem.setText(evento.getImagem());
        cbCategoria.setValue(evento.getCategoria());
        checkPrivado.setSelected(evento.isPrivado());
        txtPalestrante.setText(evento.getPalestrante());
    }
    
    
    @FXML
    private void handleEditarEvento() {
        if (evento == null) {
           // mostrarAlerta("Erro interno: evento não carregado.");
            return;
        }

        LocalDateTime dataHora = LocalDateTime.of(
            dateData.getValue(),
            java.time.LocalTime.parse(txtHora.getText())
        );

        evento.setTitulo(txtTitulo.getText());
        evento.setDescricao(txtDescricao.getText());
        evento.setData(dataHora);
        evento.setLocal(txtLocal.getText());
        evento.setImagem(txtImagem.getText());
        evento.setCategoria(cbCategoria.getValue());
        evento.setPrivado(checkPrivado.isSelected());
        evento.setPalestrante(txtPalestrante.getText());

        eventoService.atualizarEvento(evento); // Se tiver esse método

        txtTitulo.getScene().getWindow().hide(); // Fecha a janela
        
    }
    
   
    
    
    
}