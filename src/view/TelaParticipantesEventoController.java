package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Evento;
import model.Usuario;
import model.UsuarioPresenca;
import service.EventoService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TelaParticipantesEventoController {

    @FXML private TableView<UsuarioPresenca> tabelaParticipantes;
    @FXML private TableColumn<UsuarioPresenca, String> colNome;
    @FXML private TableColumn<UsuarioPresenca, String> colEmail;
    @FXML private TableColumn<UsuarioPresenca, Boolean> colPresente;
    @FXML private TableColumn<UsuarioPresenca, String> colPermissao;
    @FXML private TextField txtFiltro;
    @FXML private Label lblContadorPresentes;

    private Evento evento;
    private EventoService eventoService = EventoService.getInstance();

    private ObservableList<UsuarioPresenca> listaOriginal;
    private FilteredList<UsuarioPresenca> listaFiltrada;

    public void setEvento(Evento evento) {
        this.evento = evento;

        listaOriginal = FXCollections.observableArrayList();

        // Adiciona o organizador explicitamente com permissão "organizador"
        Usuario organizador = evento.getOrganizador();
        if (organizador != null) {
            boolean presenteOrganizador = eventoService.getPresenca(evento.getId(), organizador.getId());
            listaOriginal.add(new UsuarioPresenca(organizador, presenteOrganizador, "organizador"));
        }

        // Adiciona os participantes, ignorando o organizador para evitar duplicidade
        for (Usuario u : evento.getParticipantes()) {
            if (organizador != null && u.getId() == organizador.getId()) {
                continue; // pula o organizador
            }
            boolean presente = eventoService.getPresenca(evento.getId(), u.getId());
            String permissao = eventoService.getPermissao(evento.getId(), u.getId());
            listaOriginal.add(new UsuarioPresenca(u, presente, permissao));
        }

        listaFiltrada = new FilteredList<>(listaOriginal, p -> true);
        SortedList<UsuarioPresenca> listaOrdenada = new SortedList<>(listaFiltrada);
        listaOrdenada.comparatorProperty().bind(tabelaParticipantes.comparatorProperty());

        colNome.setCellValueFactory(data -> data.getValue().nomeProperty());
        colEmail.setCellValueFactory(data -> data.getValue().emailProperty());
        colPresente.setCellValueFactory(data -> data.getValue().presenteProperty());
        colPresente.setCellFactory(CheckBoxTableCell.forTableColumn(colPresente));
        colPermissao.setCellValueFactory(data -> data.getValue().permissaoProperty());

        tabelaParticipantes.setEditable(true);
        colPresente.setEditable(true);
        tabelaParticipantes.setItems(listaOrdenada);

        // Atualiza contador de presentes sempre que alguma presença mudar
        for (UsuarioPresenca up : listaOriginal) {
            up.presenteProperty().addListener((obs, oldVal, newVal) -> atualizarContador());
        }

        atualizarContador();
    }

    @FXML
    private void salvarPresencas() {
        for (UsuarioPresenca up : listaOriginal) {
            eventoService.setPresenca(evento.getId(), up.getUsuario().getId(), up.isPresente());
        }
        Alert alerta = new Alert(Alert.AlertType.INFORMATION, "Presenças salvas com sucesso.");
        alerta.showAndWait();
        ((Stage) tabelaParticipantes.getScene().getWindow()).close();
    }

    @FXML
    private void filtrarParticipantes() {
        String filtro = txtFiltro.getText().toLowerCase();
        listaFiltrada.setPredicate(participante -> {
            if (filtro == null || filtro.isEmpty()) {
                return true;
            }
            return participante.getUsuario().getNome().toLowerCase().contains(filtro)
                    || participante.getUsuario().getEmail().toLowerCase().contains(filtro);
        });
    }

    @FXML
    private void exportarCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName("presencas.csv");
        File file = fileChooser.showSaveDialog(tabelaParticipantes.getScene().getWindow());

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("Nome;E-mail;Presente?;Permissão\n");
                for (UsuarioPresenca up : listaOriginal) {
                    writer.write(String.format("%s;%s;%s;%s\n",
                            up.getUsuario().getNome(),
                            up.getUsuario().getEmail(),
                            up.isPresente() ? "Sim" : "Não",
                            up.getPermissao()));
                }
                Alert alerta = new Alert(Alert.AlertType.INFORMATION, "CSV exportado com sucesso.");
                alerta.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
                Alert alerta = new Alert(Alert.AlertType.ERROR, "Erro ao exportar CSV.");
                alerta.showAndWait();
            }
        }
    }

    @FXML
    private void alternarTodosCheckBoxes() {
        boolean marcarTodos = listaOriginal.stream().anyMatch(p -> !p.isPresente());
        for (UsuarioPresenca up : listaOriginal) {
            up.setPresente(marcarTodos);
        }
        atualizarContador();
    }

    private void atualizarContador() {
        long total = listaOriginal.stream().filter(UsuarioPresenca::isPresente).count();
        lblContadorPresentes.setText("Presentes: " + total);
    }
}
