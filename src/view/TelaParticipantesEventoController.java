package view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Usuario;
import model.Evento;
import model.UsuarioPresenca;
import service.EventoService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.CheckBoxTableCell;

public class TelaParticipantesEventoController {

    @FXML private TableView<UsuarioPresenca> tabelaParticipantes;
    @FXML private TableColumn<UsuarioPresenca, String> colNome;
    @FXML private TableColumn<UsuarioPresenca, String> colEmail;
    @FXML private TableColumn<UsuarioPresenca, Boolean> colPresente;

    private Evento evento;
    private EventoService eventoService = EventoService.getInstance();

    public void setEvento(Evento evento) {
        this.evento = evento;

        ObservableList<UsuarioPresenca> lista = FXCollections.observableArrayList();
        for (Usuario u : evento.getParticipantes()) {
            // Recuperar o estado real da presença no evento para já mostrar na tabela
            boolean presente = eventoService.getPresenca(evento.getId(), u.getId());
            lista.add(new UsuarioPresenca(u, presente));
        }

        colNome.setCellValueFactory(data -> data.getValue().nomeProperty());
        colEmail.setCellValueFactory(data -> data.getValue().emailProperty());

        // Esta coluna de checkbox deve usar property boolean para funcionar
        colPresente.setCellValueFactory(data -> data.getValue().presenteProperty());

        // Permite o checkbox funcionar na tabela
        colPresente.setCellFactory(CheckBoxTableCell.forTableColumn(colPresente));

        tabelaParticipantes.setEditable(true);
        colPresente.setEditable(true);

        tabelaParticipantes.setItems(lista);
    }

    @FXML
    private void salvarPresencas() {
        for (UsuarioPresenca up : tabelaParticipantes.getItems()) {
            eventoService.setPresenca(evento.getId(), up.getUsuario().getId(), up.isPresente());
        }
        Alert alerta = new Alert(Alert.AlertType.INFORMATION, "Presenças salvas com sucesso.");
        alerta.showAndWait();
        ((Stage) tabelaParticipantes.getScene().getWindow()).close();
    }
}
