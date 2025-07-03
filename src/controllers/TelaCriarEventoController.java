/*
 * Controller responsável pela tela de criação de eventos.
 * 
 * Principais responsabilidades:
 * - Validação e coleta de dados para criação de eventos
 * - Gerenciamento de upload de imagens
 * - Integração com serviços de eventos e notificações
 * 
 * Componentes principais:
 * - Campos de formulário (título, descrição, data, local, etc.)
 * - Validação e formatação de dados (especialmente para horário)
 * - Seleção de imagem via FileChooser
 * 
 * Serviços utilizados:
 * - EventoService: Criação e gestão de eventos
 * - NotificacaoService: Registro de atividades
 * 
 * Validações implementadas:
 * - Formato de horário (HHmm ou HH:mm)
 * - Campos obrigatórios
 * - Tipos de arquivo para imagem
 * 
 * Fluxos principais:
 * - handleCriarEvento(): Processa todos os dados e cria o evento
 * - handleSelecionarImagem(): Abre diálogo para seleção de imagem
 * - initialize(): Configura componentes iniciais e máscaras
 * 
 * Padrões utilizados:
 * - Observer: Atualização de notificações
 */

package controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.function.UnaryOperator;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.FileChooser;
import model.Badge;
import model.Evento;
import model.Notificacao;
import model.Usuario;
import service.EventoService;
import service.NotificacaoService;
import session.SessaoUsuario;

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
	@FXML private ComboBox<String> cbTipoEvento;
	@FXML private TextField txtBadge;


	private Usuario usuarioLogado;
	private EventoService eventoService = EventoService.getInstance();

	public void setUsuarioLogado(Usuario usuario) {
		this.usuarioLogado = usuario;
	}

	@FXML
	private void initialize() {
		cbCategoria.getItems().addAll("Festas", "Esportes", "Educação", "Negócios","Jogos", "Outros");

		cbTipoEvento.getItems().addAll("Presencial", "Online", "Híbrido");
		cbTipoEvento.setValue("Presencial");

		// TextFormatter para aceitar apenas números e até 4 dígitos
		UnaryOperator<TextFormatter.Change> filter = change -> {
			String text = change.getControlNewText();
			if (text.matches("\\d{0,4}")) { // aceita 0 a 4 dígitos
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

			Evento evento = new Evento(txtTitulo.getText(), txtDescricao.getText(), dataHora, txtLocal.getText(),
					usuarioLogado, txtPalestrante.getText());

			evento.setOrganizador(usuarioLogado);
			evento.setCategoria(cbCategoria.getValue());
			evento.setPrivado(checkPrivado.isSelected());
			evento.setImagem(txtImagem.getText());
			evento.setBadgePath(txtBadge.getText());
			evento.setTipo(cbTipoEvento.getValue());

			eventoService.criarEvento(evento);
			usuarioLogado.organizarEvento(evento);

			// Adiciona o organizador como participante com presença confirmada
			evento.adicionarParticipante(usuarioLogado);
			usuarioLogado.participarEvento(evento);
			eventoService.marcarPresenca(evento.getId(), usuarioLogado.getId());

			txtTitulo.getScene().getWindow().hide();

		} catch (Exception e) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Erro");
			alert.setHeaderText("Hora inválida");
			alert.setContentText("Por favor, digite a hora no formato HHmm (ex: 1000) ou HH:mm (ex: 10:00).");
			alert.showAndWait();
		}

		int userId = SessaoUsuario.getUsuarioLogado().getId();

		Notificacao notificacao = new Notificacao("Você criou o evento '" + txtTitulo.getText() + "'",
				LocalDateTime.now(), false, Notificacao.Tipo.HISTORICO, "Sistema");

		NotificacaoService.getInstance().registrarNotificacao(userId, notificacao);
		
		// Badge
        Badge badge = new Badge(
            "Organizador", 
            "/resources/badges/badgeCriarEvento.png", 
            "Criou seu primeiro evento"
        );

        if (!usuarioLogado.getBadges().contains(badge)) {
            usuarioLogado.adicionarBadge(badge);
        }
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
	
	@FXML
	private void handleSelecionarBadge() {
	    FileChooser fileChooser = new FileChooser();
	    fileChooser.setTitle("Selecionar badge do evento");
	    fileChooser.getExtensionFilters()
	            .addAll(new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg", "*.gif"));

	    File arquivo = fileChooser.showOpenDialog(txtBadge.getScene().getWindow());
	    if (arquivo != null) {
	        txtBadge.setText(arquivo.toURI().toString()); // caminho file:/... completo
	    }
	}
}



