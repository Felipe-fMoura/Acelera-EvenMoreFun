package view;

import service.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;
import javafx.event.ActionEvent;

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
    
    @FXML
	private void onBtnCadastro(ActionEvent event) {
	    try {
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaCadastro.fxml"));
	        Parent root = loader.load();

	        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

	        // Herda o tamanho atual da janela

	        stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
			stage.show();
			
	    } catch (IOException e) {
	        e.printStackTrace();
	        // Você pode exibir uma mensagem de erro aqui, se quiser
	    }
	}
    
    @FXML
	private void onBtnEsqueciSenha(ActionEvent event) {
	    try {
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaOTP.fxml"));
	        Parent root = loader.load();

	        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

	        // Herda o tamanho atual da janela

	        stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
			stage.show();
			
	    } catch (IOException e) {
	        e.printStackTrace();
	        // Você pode exibir uma mensagem de erro aqui, se quiser
	    }
	}
}
