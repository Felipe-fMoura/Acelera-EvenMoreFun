package controllers;

import java.io.File;
import java.time.LocalDateTime;
import java.util.function.UnaryOperator;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.FileChooser;
import model.Evento;
import model.Notificacao;
import model.Usuario;
import service.EventoService;
import service.NotificacaoService;

public class TelaEditarEventoController {
	@FXML
	private TextField txtTitulo;
	@FXML
	private TextArea txtDescricao;
	@FXML
	private DatePicker dateData;
	@FXML
	private TextField txtHora;
	@FXML
	private TextField txtLocal;
	@FXML
	private TextField txtImagem;
	@FXML
	private ComboBox<String> cbCategoria;
	@FXML
	private CheckBox checkPrivado;
	@FXML
	private TextField txtPalestrante;
	@FXML
	private ComboBox<String> cbTipo;

	private Usuario usuarioLogado;
	private EventoService eventoService = EventoService.getInstance();
	private Evento evento;

	public void setUsuarioLogado(Usuario usuario) {
		this.usuarioLogado = usuario;
	}

	public void setEvento(Evento evento) {
		this.evento = evento;
		if (evento != null) {
			preencherCamposComEvento(evento);
		}
	}

	private void preencherCamposComEvento(Evento evento) {
		txtTitulo.setText(evento.getTitulo());
		txtDescricao.setText(evento.getDescricao());
		dateData.setValue(evento.getData().toLocalDate());
		// Formata o horário para HH:mm, sem os segundos que LocalTime.toString()
		// retorna
		String horaFormatada = evento.getData().toLocalTime().toString();
		if (horaFormatada.length() > 5) {
			horaFormatada = horaFormatada.substring(0, 5);
		}
		txtHora.setText(horaFormatada);
		txtLocal.setText(evento.getLocal());
		txtImagem.setText(evento.getImagem());
		cbCategoria.setValue(evento.getCategoria());
		checkPrivado.setSelected(evento.isPrivado());
		txtPalestrante.setText(evento.getPalestrante());
		cbTipo.setValue(evento.getTipo());
	}

	@FXML
	private void initialize() {
		cbCategoria.getItems().addAll("Festas", "Esportes", "Educação", "Negócios", "Outros");
		cbTipo.getItems().addAll("Presencial", "Online");
		// TextFormatter para aceitar apenas números e até 4 dígitos no txtHora
		UnaryOperator<TextFormatter.Change> filter = change -> {
			String text = change.getControlNewText();
			if (text.matches("\\d{0,4}")) {
				return change;
			}
			return null;
		};
		TextFormatter<String> textFormatter = new TextFormatter<>(filter);
		txtHora.setTextFormatter(textFormatter);

		// Listener para formatar com ":" enquanto digita no txtHora
		txtHora.textProperty().addListener((obs, oldText, newText) -> {
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
	private void handleEditarEvento() {
		if (evento == null) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Erro");
			alert.setHeaderText("Evento não carregado");
			alert.setContentText("Não foi possível editar o evento porque ele não foi carregado corretamente.");
			alert.showAndWait();
			return;
		}

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

			evento.setTitulo(txtTitulo.getText());
			evento.setDescricao(txtDescricao.getText());
			evento.setData(dataHora);
			evento.setLocal(txtLocal.getText());
			evento.setImagem(txtImagem.getText());
			evento.setCategoria(cbCategoria.getValue());
			evento.setPrivado(checkPrivado.isSelected());
			evento.setPalestrante(txtPalestrante.getText());
			evento.setTipo(cbTipo.getValue());

			eventoService.atualizarEvento(evento);

			Notificacao notificacao = new Notificacao("Você editou o evento '" + evento.getTitulo() + "'",
					LocalDateTime.now(), false, Notificacao.Tipo.HISTORICO, "Sistema");
			NotificacaoService.getInstance().registrarNotificacao(usuarioLogado.getId(), notificacao);

			txtTitulo.getScene().getWindow().hide();

		} catch (Exception e) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Erro");
			alert.setHeaderText("Hora inválida");
			alert.setContentText("Por favor, digite a hora no formato HHmm (ex: 1000) ou HH:mm (ex: 10:00).");
			alert.showAndWait();
		}
	}

	@FXML
	private void handleExcluirEvento() {
		if (evento == null) {
			return;
		}

		Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
		confirmacao.setTitle("Confirmação");
		confirmacao.setHeaderText("Deseja realmente excluir este evento?");
		confirmacao.setContentText("Essa ação não poderá ser desfeita.");

		confirmacao.showAndWait().ifPresent(resposta -> {
			if (resposta == ButtonType.OK) {

				Notificacao notificacao = new Notificacao("Você excluiu o evento '" + evento.getTitulo() + "'",
						LocalDateTime.now(), false, Notificacao.Tipo.HISTORICO, "Sistema");
				NotificacaoService.getInstance().registrarNotificacao(usuarioLogado.getId(), notificacao);

				eventoService.removerEvento(evento.getId());
				txtTitulo.getScene().getWindow().hide(); // Fecha a janela
			}
		});
	}

	@FXML
	private void handleSelecionarImagem() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Selecionar imagem do evento");
		fileChooser.getExtensionFilters()
				.addAll(new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg", "*.gif"));

		File arquivo = fileChooser.showOpenDialog(txtImagem.getScene().getWindow());
		if (arquivo != null) {
			txtImagem.setText(arquivo.toURI().toString());
		}
	}

}