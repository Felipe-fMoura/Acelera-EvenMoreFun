package view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import model.Usuario;
import service.UsuarioService;

public class TelaRedefinirSenhaController {

    @FXML 
    private PasswordField novaSenhaField;
    
    @FXML 
    private PasswordField repetirSenhaField;
    
    private final UsuarioService usuarioService = UsuarioService.getInstance();

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
