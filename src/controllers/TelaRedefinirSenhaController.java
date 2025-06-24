/*
 * Componentes principais: 
 * - Mostrar e ocultar senha nos campos de senha e confirmação
 * 
 * initialize()
 * - Aplica redimensionamento dinâmico da imagem de fundo para a tela usando serviço `Redimensionamento`.
 *
 * redefinirSenha()
 * - Valida se as senhas digitadas nos campos coincidem.
 * - Verifica força da nova senha via `UsuarioService.validarSenha()`.
 * - Obtém o usuário temporário que solicitou a redefinição de senha.
 * - Atualiza a senha no sistema via `UsuarioService.atualizarSenha(email, novaSenha)`.
 * - Em caso de sucesso:
 *   - Exibe alerta de confirmação.
 *   - Registra notificação do evento de redefinição via `NotificacaoService`.
 *   - Limpa o usuário temporário armazenado no serviço.
 *   - Redireciona o usuário para a tela de login (`TelaLogin.fxml`).
 * - Em caso de erro:
 *   - Exibe alerta com mensagem de erro apropriada.
 *
 * Técnicas e estruturas utilizadas:
 * - Validação de dados de formulário (senhas) antes de ação de persistência.
 * - Uso de singleton para serviços (`UsuarioService`, `NotificacaoService`).
 * - Navegação entre telas com FXMLLoader e troca de cena.
 * - Controle visual via JavaFX (`Alert`, `PasswordField`, `ImageView`, `StackPane`).
 * - Tratamento básico de exceções na troca de tela.
 */

package controllers;

import java.io.IOException;
import java.time.LocalDateTime;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
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

    @FXML private PasswordField novaSenhaField;
    @FXML private TextField novaSenhaVisible;
    @FXML private ImageView imgNovaSenha;
    @FXML private Button btnVerNovaSenha;

    @FXML private PasswordField repetirSenhaField;
    @FXML private TextField repetirSenhaVisible;
    @FXML private ImageView imgRepetirSenha;
    @FXML private Button btnVerRepetirSenha;

    @FXML private ImageView backgroundImage;
    @FXML private StackPane telaRedefinirSenha;
    @FXML private AnchorPane contentPane;
    @FXML private Group grupoCampos;

    private final UsuarioService usuarioService = UsuarioService.getInstance();
    Usuario usuarioLogado = SessaoUsuario.getInstance().getUsuario();

    private final Image mostrarSenha = new Image(getClass().getResource("/resources/btnIcons/MostrarSenha.png").toExternalForm());
    private final Image ocultarSenha = new Image(getClass().getResource("/resources/btnIcons/OcultarSenha.png").toExternalForm());

    @FXML
    public void initialize() {
        // Redimensionar imagem de fundo
        Redimensionamento.aplicarRedimensionamento(telaRedefinirSenha, backgroundImage, grupoCampos);
        imgNovaSenha.setImage(ocultarSenha);
        imgRepetirSenha.setImage(ocultarSenha);
    }

    @FXML
    private void toggleNovaSenhaVisibility() {
        if (novaSenhaVisible.isVisible()) {
            novaSenhaField.setText(novaSenhaVisible.getText());
            novaSenhaVisible.setVisible(false);
            novaSenhaVisible.setManaged(false);
            novaSenhaField.setVisible(true);
            novaSenhaField.setManaged(true);
            imgNovaSenha.setImage(ocultarSenha);
        } else {
            novaSenhaVisible.setText(novaSenhaField.getText());
            novaSenhaField.setVisible(false);
            novaSenhaField.setManaged(false);
            novaSenhaVisible.setVisible(true);
            novaSenhaVisible.setManaged(true);
            imgNovaSenha.setImage(mostrarSenha);
        }
    }

    @FXML
    private void toggleRepetirSenhaVisibility() {
        if (repetirSenhaVisible.isVisible()) {
            repetirSenhaField.setText(repetirSenhaVisible.getText());
            repetirSenhaVisible.setVisible(false);
            repetirSenhaVisible.setManaged(false);
            repetirSenhaField.setVisible(true);
            repetirSenhaField.setManaged(true);
            imgRepetirSenha.setImage(ocultarSenha);
        } else {
            repetirSenhaVisible.setText(repetirSenhaField.getText());
            repetirSenhaField.setVisible(false);
            repetirSenhaField.setManaged(false);
            repetirSenhaVisible.setVisible(true);
            repetirSenhaVisible.setManaged(true);
            imgRepetirSenha.setImage(mostrarSenha);
        }
    }

    @FXML
    private void redefinirSenha() {
        String novaSenha = novaSenhaField.isVisible() ? novaSenhaField.getText().trim() : novaSenhaVisible.getText().trim();
        String repetirSenha = repetirSenhaField.isVisible() ? repetirSenhaField.getText().trim() : repetirSenhaVisible.getText().trim();

        if (!novaSenha.equals(repetirSenha)) {
            new Alert(Alert.AlertType.ERROR, "As senhas não coincidem.").show();
            return;
        }

        if (!usuarioService.validarSenha(novaSenha)) {
            new Alert(Alert.AlertType.ERROR,
                    "Senha fraca. Use letras maiúsculas, minúsculas, número e caractere especial.").show();
            return;
        }

        Usuario usuario = usuarioService.getUsuarioTemporario();
        if (usuario == null) {
            new Alert(Alert.AlertType.ERROR, "Usuário temporário não encontrado.").show();
            return;
        }

        boolean sucesso = usuarioService.atualizarSenha(usuario.getEmail(), novaSenha);
        if (sucesso) {
            new Alert(Alert.AlertType.INFORMATION, "Senha atualizada com sucesso!").show();

            Notificacao notificacao = new Notificacao("Você redefiniu sua senha", LocalDateTime.now(), false,
                    Notificacao.Tipo.HISTORICO, "Sistema");
            NotificacaoService.getInstance().registrarNotificacao(usuario.getId(), notificacao);

            usuarioService.setUsuarioTemporario(null);

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