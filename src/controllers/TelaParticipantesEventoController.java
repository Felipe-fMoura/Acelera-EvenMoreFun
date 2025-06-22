/*
 * setEvento(Evento)
 * - Inicializa a lista de participantes do evento, incluindo o organizador.
 * - Obtém status de presença e permissões de cada usuário via EventoService.
 * - Prepara lista observável com propriedades para uso na tabela.
 * - Configura colunas da tabela com propriedades dos participantes.
 * - Configura listener para atualização do contador de presentes ao alterar presença.
 * - Atualiza ranking de participação no chat.
 * 
 * atualizarRanking()
 * - Calcula ranking dos participantes baseado na quantidade de mensagens no chat via ChatService.
 * - Ordena ranking decrescente.
 * - Atualiza ListView com posições e nomes.
 *
 * salvarPresencas()
 * - Persiste no EventoService o estado atualizado de presença para cada participante.
 * - Exibe alerta de confirmação.
 * - Fecha a janela atual.
 *
 * filtrarParticipantes()
 * - Filtra a lista exibida na tabela pelo nome ou e-mail conforme texto no campo de filtro.
 *
 * exportarCSV()
 * - Abre diálogo para salvar arquivo CSV.
 * - Exporta dados dos participantes e ranking em formato CSV, tratando adequadamente campos com caracteres especiais.
 * - Exibe alertas de sucesso ou erro.
 *
 * alternarTodosCheckBoxes()
 * - Marca ou desmarca todos os participantes como presentes baseado no estado atual (se algum não está marcado, marca todos).
 * - Atualiza contador de presentes.
 *
 * atualizarContador()
 * - Atualiza label que mostra quantidade de participantes marcados como presentes.
 *
 * Técnicas e estruturas utilizadas:
 * - Uso extensivo de propriedades observáveis do JavaFX (`ObservableList`, `FilteredList`, `SortedList`) para tabela dinâmica.
 * - Listener para atualizações reativas na UI.
 * - Ordenação customizada para ranking.
 * - Manipulação segura de CSV para evitar quebras de linha e campos especiais.
 * - Interação com serviços singleton (`EventoService`, `ChatService`) para lógica de negócio.
 * - UI com `TableView` configurada para edição de checkbox, `TextField` para filtro, `ListView` para ranking.
 * - Diálogos para salvar arquivos e exibição de alertas.
 */

package controllers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Evento;
import model.Usuario;
import model.UsuarioPresenca;
import service.ChatService;
import service.EventoService;

public class TelaParticipantesEventoController {

	@FXML private TableView<UsuarioPresenca> tabelaParticipantes;
	@FXML private TableColumn<UsuarioPresenca, String> colNome;
	@FXML private TableColumn<UsuarioPresenca, String> colEmail;
	@FXML private TableColumn<UsuarioPresenca, Boolean> colPresente;
	@FXML private TableColumn<UsuarioPresenca, String> colPermissao;
	@FXML private TableColumn<UsuarioPresenca, String> colTelefone;
	@FXML private TableColumn<UsuarioPresenca, String> colCpf;
	@FXML private TableColumn<UsuarioPresenca, String> colNascimento;
	@FXML private TextField txtFiltro;
	@FXML private Label lblContadorPresentes;

	@FXML
	private ListView<String> rankingListView;

	private Evento evento;
	private EventoService eventoService = EventoService.getInstance();
	private ChatService chatService = ChatService.getInstancia();

	private ObservableList<UsuarioPresenca> listaOriginal;
	private FilteredList<UsuarioPresenca> listaFiltrada;

	public void setEvento(Evento evento) {
		this.evento = evento;

		listaOriginal = FXCollections.observableArrayList();

		// Adiciona organizador
		Usuario organizador = evento.getOrganizador();
		if (organizador != null) {
			boolean presenteOrganizador = eventoService.getPresenca(evento.getId(), organizador.getId());
			listaOriginal.add(new UsuarioPresenca(organizador, presenteOrganizador, "organizador"));
		}

		// Adiciona participantes, exceto organizador
		for (Usuario u : evento.getParticipantes()) {
			if (organizador != null && u.getId() == organizador.getId()) {
				continue;
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
		colTelefone.setCellValueFactory(data -> data.getValue().telefoneProperty());
		colCpf.setCellValueFactory(data -> data.getValue().cpfProperty());
		colNascimento.setCellValueFactory(data -> data.getValue().dataNascimentoProperty());

		tabelaParticipantes.setEditable(true);
		colPresente.setEditable(true);
		tabelaParticipantes.setItems(listaOrdenada);

		// Atualiza contador sempre que presença muda
		for (UsuarioPresenca up : listaOriginal) {
			up.presenteProperty().addListener((obs, oldVal, newVal) -> atualizarContador());
		}

		atualizarContador();
		atualizarRanking();
	}

	private void atualizarRanking() {
		if (evento == null || rankingListView == null) {
			return;
		}

		// Ranking só por quantidade de mensagens (sem mãos levantadas ou total)
		class RankingEntry {
			String nome;
			int mensagens;

			public RankingEntry(String nome, int mensagens) {
				this.nome = nome;
				this.mensagens = mensagens;
			}
		}

		List<RankingEntry> ranking = new ArrayList<>();

		for (UsuarioPresenca up : listaOriginal) {
			Usuario u = up.getUsuario();
			int msgs = chatService.getQuantidadeMensagens(evento.getId(), u.getId());
			ranking.add(new RankingEntry(u.getNome(), msgs));
		}

		// Ordena decrescente por mensagens
		ranking.sort(Comparator.comparingInt((RankingEntry r) -> r.mensagens).reversed());

		List<String> rankingStrings = new ArrayList<>();
		int pos = 1;
		for (RankingEntry r : ranking) {
			rankingStrings.add(String.format("%d. %s (Mensagens: %d)", pos++, r.nome, r.mensagens));
		}

		rankingListView.setItems(FXCollections.observableArrayList(rankingStrings));
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

				writer.write("=== Lista de Participantes ===\n");
				writer.write("Nome;E-mail;Telefone;CPF;Nascimento;Gênero;Presente?;Permissão\n");

				for (UsuarioPresenca up : listaOriginal) {
					Usuario u = up.getUsuario();
					writer.write(String.format("%s;%s;%s;%s;%s;%s;%s;%s\n", csvSafe(u.getNome()), csvSafe(u.getEmail()),
							csvSafe(u.getTelefone()), csvSafe(u.getCpf()),
							u.getDataNascimento() != null ? u.getDataNascimento().toString() : "",
							csvSafe(u.getGenero()), up.isPresente() ? "Sim" : "Não", csvSafe(up.getPermissao())));
				}

				writer.write("\n=== Ranking de Participação (Mensagens no chat) ===\n");
				writer.write("Posição;Nome;Mensagens\n");

				List<UsuarioPresenca> participantesOrdenados = new ArrayList<>(listaOriginal);
				participantesOrdenados.sort((a, b) -> {
					int msgsB = chatService.getQuantidadeMensagens(evento.getId(), b.getUsuario().getId());
					int msgsA = chatService.getQuantidadeMensagens(evento.getId(), a.getUsuario().getId());
					return Integer.compare(msgsB, msgsA); // ordem decrescente
				});

				int pos = 1;
				for (UsuarioPresenca up : participantesOrdenados) {
					int mensagens = chatService.getQuantidadeMensagens(evento.getId(), up.getUsuario().getId());
					writer.write(String.format("%d;%s;%d\n", pos++, csvSafe(up.getUsuario().getNome()), mensagens));
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

	// Escapa campos com ; ou " para evitar quebra no CSV
	private String csvSafe(String input) {
		if (input == null) {
			return "";
		}
		if (input.contains(";") || input.contains("\"")) {
			return "\"" + input.replace("\"", "\"\"") + "\"";
		}
		return input;
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
