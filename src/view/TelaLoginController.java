package view;

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
import otp.EmailTokenStore;
import service.Alertas;
import service.Redimensionamento;
import service.UsuarioService;
import session.SessaoUsuario;

public class TelaLoginController {

	private UsuarioService usuarioService = UsuarioService.getInstance();

	@FXML
	private TextField txtUsuarioLogin;

	@FXML
	private TextField txtSenhaLogin;

	@FXML
	private Button btnLogar;

	@FXML
	private Button btnCadastro;

	@FXML
	private Button btnEsqueciSenha;

	@FXML
	private ImageView backgroundImage;

	@FXML
	private StackPane telaLogin;

	@FXML
	private AnchorPane contentPane;

	@FXML
	private Group grupoCampos;

	@FXML
	public void initialize() {
		// Redimensionar imagem de fundo
		Redimensionamento.aplicarRedimensionamento(telaLogin, backgroundImage, grupoCampos);
	}

	@FXML
	private void onBtnLogar(ActionEvent event) {
		Alertas a = new Alertas();
		String email = txtUsuarioLogin.getText();
		String senha = txtSenhaLogin.getText();

		// Verifica se usuário existe e senha está correta
		if (usuarioService.fazerLogin(email, senha)) {
			// Verifica se o e-mail está confirmado
			if (!EmailTokenStore.isEmailConfirmed(email)) {
				a.mostrarAlerta("Acesso negado", "Você precisa confirmar seu e-mail antes de acessar.");
				return;
			}

			// a.mostrarAlerta("Sucesso!!", "Usuário logado com sucesso");

			try {
				Usuario usuario = usuarioService.getUsuarioPorEmail(email);
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaMenu.fxml"));
				Parent root = loader.load();

				TelaMenuController controller = loader.getController();
				controller.setUsuarioLogado(usuario);
				SessaoUsuario.getInstance().setUsuario(usuario);

				Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
				stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
				stage.show();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			a.mostrarAlerta("Erro!!", "Senha incorreta ou email inexistente");
		}

	}

	@FXML
	private void onBtnCadastro(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaCadastro.fxml"));
			Parent root = loader.load();

			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

			stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void onBtnEsqueciSenha(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaOTP.fxml"));
			Parent root = loader.load();

			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

			stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void onBtnNovaSessao(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaLogin.fxml"));
			Parent root = loader.load();

			Stage novaJanela = new Stage();
			novaJanela.setTitle("EvenMoreFun");
			novaJanela.setScene(new Scene(root));
			novaJanela.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
