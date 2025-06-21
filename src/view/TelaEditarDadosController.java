package view;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Notificacao;
import model.Usuario;
import service.NotificacaoService;
import service.UsuarioService;
import session.SessaoUsuario;

public class TelaEditarDadosController {

	@FXML
	private TextField txtEmail;
	@FXML
	private TextField txtNome;
	@FXML
	private TextField txtNomeUsuario;
	@FXML
	private DatePicker datePickerDataNascimento;
	@FXML
	private ComboBox<String> cbGenero;
	@FXML
	private TextField txtTelefone;
	@FXML
	private Button btnSalvar;

	private UsuarioService usuarioService = UsuarioService.getInstance();
	private Usuario usuarioLogado;

	@FXML
	public void initialize() {
		cbGenero.getItems().addAll("Masculino", "Feminino", "Outro");

		usuarioLogado = SessaoUsuario.getInstance().getUsuario();

		if (usuarioLogado != null) {
			preencherCampos();
		}
	}

	private void preencherCampos() {

		if (!(usuarioLogado.getCpf() == null)) {

			txtNome.setText(usuarioLogado.getNome());
			txtNomeUsuario.setText(usuarioLogado.getUsername());
			txtTelefone.setText(usuarioLogado.getTelefone());
			txtEmail.setText(usuarioLogado.getEmail());
			cbGenero.setValue(usuarioLogado.getGenero());
			datePickerDataNascimento.setValue(usuarioLogado.getDataNascimento());
		} else {
			txtNome.setText(usuarioLogado.getNome());
			txtNomeUsuario.setText(usuarioLogado.getUsername());
			txtTelefone.setVisible(false);
			txtEmail.setText(usuarioLogado.getEmail());
			cbGenero.setVisible(false);
			datePickerDataNascimento.setVisible(false);

		}
	}

	@FXML
	private void handleSalvar() {
		String nome = txtNome.getText();
		String username = txtNomeUsuario.getText();

		if (nome.isEmpty() || username.isEmpty()) {
			mostrarAlerta("Campos obrigatórios", "Nome e nome de usuário devem ser preenchidos.",
					Alert.AlertType.WARNING);
			return;
		}

		usuarioLogado.setNome(nome);
		usuarioLogado.setUsername(username);

		if (usuarioLogado.getCpf() != null) {
			String telefone = txtTelefone.getText();
			String email = txtEmail.getText();
			String genero = cbGenero.getValue();
			LocalDate dataNascimento = datePickerDataNascimento.getValue();

			if (telefone.isEmpty() || email.isEmpty() || genero == null || dataNascimento == null) {
				mostrarAlerta("Campos obrigatórios", "Todos os campos devem ser preenchidos.", Alert.AlertType.WARNING);
				return;
			}

			if (!usuarioService.validarEmail(email)) {
				mostrarAlerta("Campos obrigatórios", "Digite um e-mail válido.", Alert.AlertType.WARNING);
				return;
			}

			if (!usuarioService.validarTelefone(telefone)) {
				mostrarAlerta("Telefone inválido", "Digite um telefone válido com 10 ou 11 dígitos.",
						Alert.AlertType.WARNING);
				return;
			}

			if (!usuarioService.validarDataNascimento(dataNascimento)) {
				mostrarAlerta("Data inválida", "Usuário deve ter pelo menos 14 anos.", Alert.AlertType.WARNING);
				return;
			}

			usuarioLogado.setTelefone(telefone);
			usuarioLogado.setEmail(email);
			usuarioLogado.setGenero(genero);
			usuarioLogado.setDataNascimento(dataNascimento);
		}

		boolean sucesso = usuarioService.completarCadastro(usuarioLogado);

		if (sucesso) {
			SessaoUsuario.getInstance().setUsuario(usuarioLogado);
			mostrarAlerta("Sucesso", "Dados atualizados com sucesso!", Alert.AlertType.INFORMATION);

			Notificacao notificacao = new Notificacao("Você editou seus dados ", LocalDateTime.now(), false,
					Notificacao.Tipo.HISTORICO, "Sistema");
			NotificacaoService.getInstance().registrarNotificacao(usuarioLogado.getId(), notificacao);

			// Fecha a janela
			Stage stage = (Stage) btnSalvar.getScene().getWindow();
			stage.close();
		} else {
			mostrarAlerta("Erro", "Erro ao salvar dados. Tente novamente.", Alert.AlertType.ERROR);
		}
	}

	private void mostrarAlerta(String titulo, String msg, Alert.AlertType tipo) {
		Alert alert = new Alert(tipo);
		alert.setTitle(titulo);
		alert.setHeaderText(null);
		alert.setContentText(msg);
		alert.showAndWait();
	}
}
