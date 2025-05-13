package view;
import service.*;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class TelaLoginController {
	
	
	private UsuarioService usuarioService = UsuarioService.getInstance();

	@FXML
	private TextField txtUsuarioLogin;
	
	@FXML
	private TextField txtSenhaLogin;
	
	@FXML
	private Button btnLogar;
	
	
	@FXML
	
	private void onBtnLogar() {
		Alertas a = new Alertas();
		String email = txtUsuarioLogin.getText();
		String senha = txtSenhaLogin.getText();
		
		
		if (usuarioService.fazerLogin(email, senha)) {
			
			System.out.println("email digitado: "+email);//LOG
			System.out.println("Senha digitada: "+senha);//LOG
			
			usuarioService.listarUsuarios();//LOG
			
			System.out.println("Senha correta");//LOG
			a.mostrarAlerta("Sucesso!!", "Usuario logado com sucesso");
			
			
			
			
			
		} else {
			System.out.println("email digitado: "+email);//LOG
			System.out.println("Senha digitada: "+senha);//LOG
			usuarioService.listarUsuarios();
			
			System.out.println("Senha incorreta");//LOG
			a.mostrarAlerta("Erro!!", "Senha incorreta ou email inexistente	");

		}
		
		
	}
	

}
