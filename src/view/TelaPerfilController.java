package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Usuario;
import model.Evento;
import service.UsuarioService;
import session.SessaoUsuario;

import java.io.IOException;
import java.util.stream.Collectors;

public class TelaPerfilController {
    @FXML private Text lblNome;
    @FXML private Text lblUsername;
    @FXML private Text lblEmail;
    @FXML private Text lblCpf;
    @FXML private Text lblGenero;
    @FXML private Text lblTelefone;
    @FXML private Text lblDataNascimento;
    @FXML private Text lblId;
    @FXML private ImageView imgPerfil;
    @FXML private ListView<Evento> listEventosParticipando;
    @FXML private ListView<Evento> listEventosOrganizados;
    @FXML private Button btnFechar;
    @FXML private Label txtCompletarCadastro;
    
    private UsuarioService usuarioService = UsuarioService.getInstance();
    Usuario usuarioLogado = SessaoUsuario.getInstance().getUsuario();
 

    public void carregarUsuario() {
        Usuario usuario = SessaoUsuario.getInstance().getUsuario();
        
        if (usuario != null) {
        	
        	lblId.setText("" + usuario.getId());
            // Informações básicas
            lblNome.setText(usuario.getNome());
            lblUsername.setText("@" + usuario.getUsername());
            lblEmail.setText(usuario.getEmail());
            
            if (usuario.getDataNascimento() == null) {
            	lblId.setText(String.valueOf(usuario.getId()));
                lblCpf.setText("Dados incompletos");
                lblGenero.setText("Dados incompletos");
                lblTelefone.setText("Dados incompletos");
                lblDataNascimento.setText("Dados incompletos");
            } else {
                lblCpf.setText(usuario.getCpf());
                lblGenero.setText(usuario.getGenero());
                lblTelefone.setText(usuario.getTelefone());
                lblDataNascimento.setText(usuario.getDataNascimento().toString());
            }

            // Carrega eventos participando
            listEventosParticipando.getItems().setAll(
                usuarioService.getEventosParticipandoUsuario(usuario.getId())
            );

            // Carrega eventos organizados
            listEventosOrganizados.getItems().setAll(
                usuarioService.getEventosOrganizandoUsuario(usuario.getId())
            );

            configurarCelulasListView();

            if (usuarioService.dadosCompletosCadastrados(usuario)) {
                txtCompletarCadastro.setText("Cadastro completo:");
            } else {
                txtCompletarCadastro.setText("Cadastro incompleto. Clique aqui para completar");
            }
        }
    }

    private void configurarCelulasListView() {
        listEventosParticipando.setCellFactory(lv -> new EventoListCell());
        listEventosOrganizados.setCellFactory(lv -> new EventoListCell());
    }
    
    @FXML
    private void handleFecharPerfil(ActionEvent event) {
    
	try {
	
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaMenu.fxml"));
		Parent root = loader.load();

		TelaMenuController controller = loader.getController();
		controller.setUsuarioLogado(usuarioLogado); // Já com ID atribuído

		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
		stage.show();
	} catch (IOException e) {
		e.printStackTrace();
	}
        
        
    }

    
    @FXML
    private void handleCompletarCadastro(MouseEvent event) {
    	
    	if (!usuarioService.dadosCompletosCadastrados(usuarioLogado)){
    	
    	try {
    		
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaCadastro2.fxml"));
			Parent root = loader.load();

			// Pega o controller da segunda tela
			TelaCadastro2Controller controller = loader.getController();
			
			// Envia o usuário para a próxima tela
			controller.setUsuario(usuarioLogado);

			// Troca de tela
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
     }
    }
   
}