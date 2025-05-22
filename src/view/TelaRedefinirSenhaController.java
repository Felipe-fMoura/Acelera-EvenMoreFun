package view;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import model.Usuario;
import service.UsuarioService;

public class TelaRedefinirSenhaController {

    @FXML 
    private PasswordField novaSenhaField;
    
    @FXML 
    private PasswordField repetirSenhaField;
    
    private final UsuarioService usuarioService = UsuarioService.getInstance();
    
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
        backgroundImage.fitWidthProperty().bind(telaRedefinirSenha.widthProperty());
        backgroundImage.fitHeightProperty().bind(telaRedefinirSenha.heightProperty());

        // Escalar proporcionalmente o grupo de campos (base: 1920x1080)
        grupoCampos.scaleXProperty().bind(
        		telaRedefinirSenha.widthProperty().divide(1920.0)
        );
        grupoCampos.scaleYProperty().bind(
        		telaRedefinirSenha.heightProperty().divide(1080.0)
        );
    }

    @FXML
    private void redefinirSenha() {
        String novaSenha = novaSenhaField.getText().trim();

        if (!usuarioService.validarSenha(novaSenha)) {
            new Alert(Alert.AlertType.ERROR, "Senha fraca. Use letras maiúsculas, minúsculas, número e caractere especial.").show();
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
        } else {
            new Alert(Alert.AlertType.ERROR, "Erro ao atualizar a senha.").show();
        }
    }
}
