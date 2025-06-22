/*
 * Controller responsável pela tela de cadastro inicial de usuários.
 * 
 * Principais responsabilidades:
 * - Validação dos dados básicos de cadastro (usuário, email, senha)
 * - Integração com serviços de usuário e confirmação por email
 * - Navegação entre telas de cadastro e login
 * 
 * Componentes principais:
 * - Campos de formulário (nome, email, senha, confirmação de senha)
 * - Validação de dados de entrada
 * - Redimensionamento responsivo da interface
 * 
 * Serviços utilizados:
 * - UsuarioService: Validações e persistência de usuários
 * - EmailConfirmationService: Envio de confirmação por email
 * - Alertas: Exibição de mensagens para o usuário
 * - Redimensionamento: Ajuste de layout responsivo
 * 
 * Fluxos principais:
 * - onBtCadastrarUsuario(): Processa e valida o cadastro inicial
 * - onBtnEntrar(): Navegação para tela de login
 * - initialize(): Configuração inicial dos componentes
 * 
 * Validações realizadas:
 * - Força da senha (complexidade)
 * - Correspondência entre senhas
 * - Formato de email válido
 * - Email já cadastrado
 */

package controllers;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.Usuario;
import otp.EmailConfirmationService;
import service.Alertas;
import service.Redimensionamento;
import service.UsuarioService;

public class TelaCadastroController {

	private UsuarioService usuarioService = UsuarioService.getInstance();

	@FXML private TextField txtUsername;
	@FXML private TextField txtNome;
	@FXML private TextField txtSobrenome;
	@FXML private TextField txtEmail;
	@FXML private TextField txtSenha;
	@FXML private TextField txtVerificarSenha;
	@FXML private Button btnConfirma;
	@FXML private TextField txtRepitirSenha;
	@FXML private Button btnEntrar;
	@FXML private ImageView backgroundImage;
	@FXML private StackPane telaCadastro;
	@FXML private AnchorPane contentPane;
	@FXML private Group grupoCampos;

	@FXML
	public void initialize() {
		Redimensionamento.aplicarRedimensionamento(telaCadastro, backgroundImage, grupoCampos);
	}

	@FXML
	private void onBtCadastrarUsuario(ActionEvent event) {
		String nome = txtNome.getText();
		String sobrenome = txtSobrenome.getText();
		String userName = txtUsername.getText();
		String email = txtEmail.getText();
		String senha = txtSenha.getText();
		String confirmarSenha = txtRepitirSenha.getText();

		Alertas a = new Alertas();

		if (!usuarioService.validarSenha(senha)) {
			a.mostrarAlerta("Senha fraca",
					"A senha deve conter pelo menos 8 caracteres, incluindo letras maiúsculas, minúsculas, números e caracteres especiais.");
			return;
		} else if (!senha.equals(confirmarSenha)) {
			a.mostrarAlerta("Erro de Cadastro", "As senhas não coincidem. Tente novamente.");
			return;
		}

		if (!usuarioService.validarEmail(email)) {
			a.mostrarAlerta("Erro de Cadastro", "Email inválido. Tente novamente");
			return;
		}

		for (Usuario u : usuarioService.getUsuarios()) {
			if (u.getEmail().equalsIgnoreCase(email)) {
				a.mostrarAlerta("Erro de Cadastro", "Email já cadastrado. Tente novamente");
				return;
			}
		}

		Usuario novo = usuarioService.iniciarCadastro(nome, sobrenome, userName, email, senha);

		if (novo == null) {
			a.mostrarAlerta("Erro", "Dados inválidos para cadastro");
			return;
		}
		usuarioService.completarCadastro(novo);

		// e-mail de confirmação
		EmailConfirmationService.iniciarConfirmacaoEmail(email, nome);
		a.mostrarAlerta("Cadastro efetuado", "Um e-mail de confirmação foi enviado para " + email
				+ ". Por favor, confirme seu e-mail antes de acessar o sistema.");

		// Voltar para tela de login após cadastro
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaLogin.fxml"));
			Parent root = loader.load();

			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
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

			Scene newScene = new Scene(root, stage.getWidth(), stage.getHeight());

			stage.setScene(newScene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void onBtListaUsuarios() {
		for (Usuario u : usuarioService.getUsuarios()) {
			System.out.println("Nome: " + u.getNome() + " | E-mail: " + u.getEmail() + " | Senha: " + u.getSenha()
					+ " | Data de nascimento: " + u.getDataNascimento());
		}
	}
}
