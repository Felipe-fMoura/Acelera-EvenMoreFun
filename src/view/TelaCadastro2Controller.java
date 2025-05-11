package view;
import service.*;

import java.time.LocalDate;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Usuario;

public class TelaCadastro2Controller {
	
	
	private UsuarioService usuarioService = UsuarioService.getInstance();

	@FXML
	private TextField txtNome;
	
	@FXML
	private TextField txtSobrenome;
	
	@FXML
	private TextField txtCPF;
	
	@FXML
	private TextField txtTelefone;
	
	@FXML
	private DatePicker dataNasc;
	
	private Usuario usuario;

	public void setUsuario(Usuario usuario) {
	    this.usuario = usuario;
	}

	
	
	@FXML
	private void onBtCadastrarUsuario(ActionEvent event) {
		  
  		String nome = txtNome.getText();
		String sobrenome = txtSobrenome.getText();
		String cpf = txtCPF.getText();		
		String telefone = txtTelefone.getText()  ;
		LocalDate dataNascimento = dataNasc.getValue();
		Alertas a = new Alertas();

		
		
			
			
}
	  
	 @FXML
	    private void finalizarCadastro() {
	        if (usuario != null) {
	            usuario.setNome(txtNome.getText());
	            usuario.setSobrenome(txtSobrenome.getText());
	            usuario.setTelefone(txtTelefone.getText());
	            usuario.setDataNascimento(dataNasc.getValue());

	            //imprimir no console, salvar em uma lista ou ir pra próxima tela
	            System.out.println("Usuário completo:");
	            System.out.println("Username: " + usuario.getUsername());
	            System.out.println("Email: " + usuario.getEmail());
	            System.out.println("Nome: " + usuario.getNome());
	            System.out.println("Sobrenome "+ usuario.getSobrenome());	    
	            System.out.println("Telefone: " + usuario.getTelefone());
	            System.out.println("Nascimento: " + usuario.getDataNascimento().toString());

	            
	            usuarioService.cadastrar(usuario);
	            usuarioService.listarUsuarios();

	        }
	    }
	

}
