package view;

import java.io.IOException;
import java.time.LocalDateTime;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.Notificacao;
import model.Usuario;
import service.NotificacaoService;
import service.Redimensionamento;
import service.UsuarioService;
import session.SessaoUsuario;

public class TelaRedefinirSenhaController {

	@FXML
	private PasswordField novaSenhaField;

	@FXML
	private PasswordField repetirSenhaField;

	private final UsuarioService usuarioService = UsuarioService.getInstance();
	Usuario usuarioLogado = SessaoUsuario.getInstance().getUsuario();

	@FXML
	private ImageView backgroundImage;

	@FXML
	private StackPane telaRedefinirSenha;

	@FXML
	private AnchorPane contentPane;

	@FXML
	private Group grupoCampos;

	@FXML
	public void initialize() {
		// Redimensionar imagem de fundo
		Redimensionamento.aplicarRedimensionamento(telaRedefinirSenha, backgroundImage, grupoCampos);
	}

	@FXML
	private void redefinirSenha() {
		String novaSenha = novaSenhaField.getText().trim();
		String repetirSenha = repetirSenhaField.getText().trim();

		if (!novaSenha.equals(repetirSenha)) {
			new Alert(Alert.AlertType.ERROR, "As senhas não coincidem.").show();
			return;
		}

		if (!usuarioService.validarSenha(novaSenha)) {
			new Alert(Alert.AlertType.ERROR,
					"Senha fraca. Use letras maiúsculas, minúsculas, número e caractere especial.").show();
			return;
		}

		// Recupera o usuário temporário que solicitou redefinição
		Usuario usuario = usuarioService.getUsuarioTemporario();
		if (usuario == null) {
			new Alert(Alert.AlertType.ERROR, "Usuário temporário não encontrado.").show();
			return;
		}

		// Atualiza a senha no serviço
		boolean sucesso = usuarioService.atualizarSenha(usuario.getEmail(), novaSenha);
		if (sucesso) {
			new Alert(Alert.AlertType.INFORMATION, "Senha atualizada com sucesso!").show();

			Notificacao notificacao = new Notificacao("Você redefiniu sua senha", LocalDateTime.now(), false,
					Notificacao.Tipo.HISTORICO, "Sistema");
			NotificacaoService.getInstance().registrarNotificacao(usuarioLogado.getId(), notificacao);

			// Limpa usuário temporário
			usuarioService.setUsuarioTemporario(null);

			// Redireciona para tela de login
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaLogin.fxml"));
				Parent root = loader.load();
				Stage stage = (Stage) telaRedefinirSenha.getScene().getWindow();
				stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
				stage.show();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			new Alert(Alert.AlertType.ERROR, "Erro ao atualizar a senha.").show();
		}
	}
}
