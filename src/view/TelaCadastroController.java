package view;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Usuario;
import service.Alertas;
import service.UsuarioService;

public class TelaCadastroController {

	private UsuarioService usuarioService = UsuarioService.getInstance();

	@FXML
	private TextField txtUsername;

	@FXML
	private TextField txtSobrenome;

	@FXML
	private TextField txtEmail;

	@FXML
	private TextField txtSenha;

	@FXML
	private TextField txtVerificarSenha;

	@FXML
	private DatePicker txtDataNascimento;

	@FXML
	private Button btnConfirma;

	@FXML
	private Button btListaUsuarios;

	@FXML
	private TextField txtRepitirSenha;

	@FXML
	private Button btnEntrar;

	@FXML
	private void onBtCadastrarUsuario(ActionEvent event) {
		String userName = txtUsername.getText();
		String email = txtEmail.getText();
		String senha = txtSenha.getText();
		String confirmarSenha = txtRepitirSenha.getText(); // ou txtRepitirSenha

		Alertas a = new Alertas();

		if (!usuarioService.validarSenha(senha)) {
			a.mostrarAlerta("Senha fraca",
					"A senha deve conter pelo menos 8 caracteres, incluindo letras maiúsculas, minúsculas, números e caracteres especiais.");
			return;
		} else {
			if (!senha.equals(confirmarSenha)) {
				a.mostrarAlerta("Erro de Cadastro", "As senhas não coincidem. Tente novamente.");
				return;
			}
		}
		boolean emailValido = usuarioService.validarEmail(email);
		if (!emailValido) {
			a.mostrarAlerta("Erro de Cadastro", "Email inválido. Tente novamente");
			return;
		}

		// Verifica se já existe email
		for (Usuario u : usuarioService.getUsuarios()) {
			if (u.getEmail().equalsIgnoreCase(email)) {
				a.mostrarAlerta("Erro de Cadastro", "Email já cadastrado. Tente novamente");
				return;
			}
		}
		// TRANSAÇÃO ENTRE USUARIOS

		Usuario novo = usuarioService.iniciarCadastro(userName, email, senha);
		if (novo == null) {
			a.mostrarAlerta("Erro", "Dados inválidos para cadastro");
			return;
		}

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaCadastro2.fxml"));
			Parent root = loader.load();

			TelaCadastro2Controller controller = loader.getController();
			controller.setUsuario(novo); // Já com ID atribuído

			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.setScene(new Scene(root));
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void onBtnEntrar(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaLogin.fxml"));
			Parent root = loader.load();

			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.setScene(new Scene(root));
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
			// Você pode exibir uma mensagem de erro aqui, se quiser
		}
	}

	@FXML
	private void onBtnEsqueciMinhaSenha(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaEsqueciMinhaSenha.fxml"));
			Parent root = loader.load();

			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.setScene(new Scene(root));
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
			// Você pode exibir uma mensagem de erro aqui, se quiser
		}
	}

	// tirar
	@FXML
	public void onBtListaUsuarios() {

		for (Usuario u : usuarioService.getUsuarios()) {
			System.out.println("Nome: " + u.getNome() + " | E-mail: " + u.getEmail() + " | Senha: " + u.getSenha()
					+ " | Data de nascimento: " + u.getDataNascimento());
		}
	}

}
