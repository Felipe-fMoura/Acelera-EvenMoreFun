package view;

import javafx.fxml.FXML;

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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Usuario;
import service.UsuarioService;

public class TelaMenuController {
    private UsuarioService usuarioService = UsuarioService.getInstance();
    private Usuario usuarioLogado; // Guarda o usuário atual

    @FXML
    private Text TxtUserName; 

    @FXML
    private Button testeCadastro;
    
    @FXML
    private Button testeLista;
    
    @FXML
    private void onBtTesteCadastro(ActionEvent event) {
    	try {
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaCadastro.fxml"));
	        Parent root = loader.load();
	        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
	        stage.setScene(new Scene(root));
	        stage.show();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
    	
    }
    
    @FXML
    public void onBtTesteLista() {
	System.out.println("-----LOG TELA MENU-----");
	usuarioService.listarUsuarios();
  	
    }
    
    
    
    
    // Recebe o usuário da tela de cadastro
    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
        exibirDadosUsuario();
    }

    // Exibe os dados do usuário na tela
    private void exibirDadosUsuario() {
        if (usuarioLogado != null) {
        	TxtUserName.setText("Bem-vindo, " + usuarioLogado.getUsername());
       
        }
    }

}