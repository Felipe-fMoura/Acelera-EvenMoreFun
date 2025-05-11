package view;
import service.Alertas;
import service.UsuarioService;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Usuario;
import service.UsuarioService;

public class TelaCadastroController {

	 private UsuarioService usuarioService = UsuarioService.getInstance();

	
	 
  @FXML
  private TextField txtUsername;
  
  @FXML
  private TextField txtSobrenome;
  
  @FXML
  private TextField txtEmail;
	
  @FXML
  private TextField txtSenha;
  
  @FXML
  private TextField txtVerificarSenha;
  
  @FXML
  private DatePicker txtDataNascimento;
  
  @FXML
  private Button btnConfirma;
  
  @FXML
  private Button btListaUsuarios;
  
  @FXML
 private TextField txtRepitirSenha; 
  
  @FXML
private void onBtCadastrarUsuario(ActionEvent event) {
    String userName = txtUsername.getText();
    String email = txtEmail.getText();
    String senha = txtSenha.getText();				
    String confirmarSenha = txtRepitirSenha.getText(); // ou txtRepitirSenha

    Alertas a = new Alertas();

    if (!senha.equals(confirmarSenha)) {
        a.mostrarAlerta("Erro de Cadastro", "As senhas não coincidem. Tente novamente.");
        return;
    }

    
    
    
    
    boolean emailValido = usuarioService.validarEmail(email);
    if (!emailValido) {
        a.mostrarAlerta("Erro de Cadastro", "Email inválido. Tente novamente");
        return;
    }

    // Verifica se já existe email
    for (Usuario u : usuarioService.getUsuarios()) {
        if (u.getEmail().equalsIgnoreCase(email)) {
            a.mostrarAlerta("Erro de Cadastro", "Email já cadastrado. Tente novamente");
            return;
        }
    }

    // Cria o objeto usuário com os dados da primeira tela
    Usuario novo = new Usuario(userName, email, senha);

    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaCadastro2.fxml"));
        Parent root = loader.load();

        // Pega o controller da segunda tela
        TelaCadastro2Controller controller = loader.getController();

        // Envia o usuário para a próxima tela
        controller.setUsuario(novo);

        // Troca de tela
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();

    } catch (IOException e) {
        e.printStackTrace();
    }
}


  //tirar
  @FXML
  public void onBtListaUsuarios() {
	  
	  for (Usuario u : usuarioService.getUsuarios()) {
          System.out.println("Nome: " + u.getNome() + " | E-mail: " + u.getEmail()+ " | Senha: " + u.getSenha() + " | Data de nascimento: " + u.getDataNascimento());
	  }
  }
  
}
