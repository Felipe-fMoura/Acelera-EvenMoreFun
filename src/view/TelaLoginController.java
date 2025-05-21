package view;

import service.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.Group;
import javafx.beans.binding.Bindings;

public class TelaLoginController {

    private UsuarioService usuarioService = UsuarioService.getInstance();

    @FXML 
    private TextField txtUsuarioLogin;
    
    @FXML 
    private TextField txtSenhaLogin;
    
    @FXML
    private Button btnLogar;

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
        backgroundImage.fitWidthProperty().bind(telaLogin.widthProperty());
        backgroundImage.fitHeightProperty().bind(telaLogin.heightProperty());

        // Escalar proporcionalmente o grupo de campos (base: 1920x1080)
        grupoCampos.scaleXProperty().bind(
            telaLogin.widthProperty().divide(1920.0)
        );
        grupoCampos.scaleYProperty().bind(
            telaLogin.heightProperty().divide(1080.0)
        );
    }

    @FXML
    private void onBtnLogar() {
        Alertas a = new Alertas();
        String email = txtUsuarioLogin.getText();
        String senha = txtSenhaLogin.getText();

        if (usuarioService.fazerLogin(email, senha)) {
            a.mostrarAlerta("Sucesso!!", "Usuário logado com sucesso");
        } else {
            a.mostrarAlerta("Erro!!", "Senha incorreta ou email inexistente");
        }
    }
}
