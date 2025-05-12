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
	Alertas a = new Alertas();

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
	    private void finalizarCadastro() {
		 
		    String telefone = txtTelefone.getText();
		    String cpf = txtCPF.getText();
		    
		    if (!usuarioService.validarTelefone(telefone)) {
		        a.mostrarAlerta("Telefone inválido","Digite apenas números com DDD (10 ou 11 dígitos).");
		        return;
		    }

		    if (!usuarioService.validarCPF(cpf)) {
		        a.mostrarAlerta("CPF inválido","Verifique o número e tente novamente.");
		        return;
		    }
		 
	        if (usuario != null) {
	            usuario.setNome(txtNome.getText());
	            usuario.setSobrenome(txtSobrenome.getText());
	            usuario.setTelefone(txtTelefone.getText());
	            usuario.setCPF(txtCPF.getText());
	            usuario.setDataNascimento(dataNasc.getValue());

	            
	            //imprimir no console, salvar em uma lista ou ir pra próxima tela
	            System.out.println("Usuário completo:");
	            System.out.println("Username: " + usuario.getUsername());
	            System.out.println("Email: " + usuario.getEmail());
	            System.out.println("Nome: " + usuario.getNome());
	            System.out.println("Sobrenome: "+ usuario.getSobrenome());	    
	            System.out.println("Telefone: " + usuario.getTelefone());
	            System.out.println("CPF: " + usuario.getCPF());
	            System.out.println("Nascimento: " + usuario.getDataNascimento().toString());
	            

	            
	            usuarioService.cadastrar(usuario);
	            
	            usuarioService.listarUsuarios();

	        }
	    }
	

}
