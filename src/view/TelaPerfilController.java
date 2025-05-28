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
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Usuario;
import model.Evento;
import service.UsuarioService;


import java.io.IOException;
import java.util.stream.Collectors;

public class TelaPerfilController {
    @FXML private Text lblNome;
    @FXML private Text lblUsername;
    @FXML private Text lblEmail;
    @FXML private ImageView imgPerfil;
    @FXML private ListView<Evento> listEventosParticipando;
    @FXML private ListView<Evento> listEventosOrganizados;
    @FXML private Button btnFechar;
    
    private UsuarioService usuarioService = UsuarioService.getInstance();
    
    Usuario usuarioLogado;


    public void setUsuario(Usuario usuario) {
        if (usuario != null) {
            // Informações básicas
            lblNome.setText(usuario.getNome());
            lblUsername.setText("@" + usuario.getUsername());
            lblEmail.setText(usuario.getEmail());
            
            // Carrega eventos participando
            listEventosParticipando.getItems().setAll(
                usuarioService.getEventosParticipandoUsuario(usuario.getId())
            );
            
            // Carrega eventos organizados
            listEventosOrganizados.getItems().setAll(
                usuarioService.getEventosOrganizandoUsuario(usuario.getId())
            );
            
            // Configura como os eventos serão exibidos na lista
            configurarCelulasListView();
            
            usuarioLogado = usuario;
        }
        
        usuarioService.dadosCompletosCadastrados(usuario);
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

    
    
    
}