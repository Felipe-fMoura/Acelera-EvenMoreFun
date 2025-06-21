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
import model.Notificacao;
import service.NotificacaoService;
import service.UsuarioService;
import session.SessaoUsuario;

import java.io.IOException;
import java.util.stream.Collectors;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;


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
    @FXML private Button btnEditarFoto;
    
    
    private UsuarioService usuarioService = UsuarioService.getInstance();
    Usuario usuarioLogado = SessaoUsuario.getInstance().getUsuario();
	private Usuario usuario;
 

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
        if (telaMenuController != null) {
            telaMenuController.fecharPerfil();
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
    
    @FXML
    private void handleAbrirNotificacoes(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaCentralNotificacoes.fxml"));
            Parent root = loader.load();

            // Cria uma nova janela (popup)
            Stage popupStage = new Stage();
            popupStage.setTitle("Notificações"); // Título opcional
            
            // Define um tamanho menor para o popup (ajuste conforme necessário)
            Scene scene = new Scene(root, 400, 300); // Largura x Altura
            
            // 
            popupStage.setResizable(false);
            
            // Faz o popup ficar na frente da janela principal
            Stage mainStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            popupStage.initOwner(mainStage);
            
            popupStage.setScene(scene);
            popupStage.show(); // Mostra o popup sem fechar a tela atual

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    	
    @FXML
    private void handleSelecionarFoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Foto de Perfil");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(imgPerfil.getScene().getWindow());
        if (file != null) {
            String caminho = file.getAbsolutePath();
            usuarioLogado.setCaminhoFotoPerfil(caminho); // Armazena caminho no objeto
            SessaoUsuario.getInstance().setUsuario(usuarioLogado); // <- ATUALIZA NA SESSÃO
            carregarFotoPerfil(); // Atualiza visual
        }
    }

    public void carregarFotoPerfil() {
        String caminho = usuarioLogado.getCaminhoFotoPerfil();
        if (caminho != null && !caminho.isEmpty()) {
            try {
                Image imagem = new Image("file:" + caminho, 150, 150, true, true);
                imgPerfil.setImage(imagem);
                
             // Atualiza o ícone do botão do menu, se o controller principal estiver disponível
                if (telaMenuController != null) {
                    telaMenuController.atualizarFotoPerfilOrganizador(caminho);
                }
                
            } catch (Exception e) {
                System.err.println("Erro ao carregar foto de perfil: " + caminho);
            }
        }
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        carregarUsuario();  
    }

    // acesso ao TelaMenuController
    private TelaMenuController telaMenuController;
    public void setTelaMenuController(TelaMenuController controller) {
        this.telaMenuController = controller;
    }
   
    @FXML
    private void handleEditarDados(ActionEvent event) {
  
    	    try {
    	    	
 	    
    	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaEditarDados.fxml"));
    	        Parent root = loader.load();

                Stage stage = new Stage();

                stage.setTitle("Editar dados");
                stage.setScene(new Scene(root));
                stage.show();
    			
    	    } catch (IOException e) {
    	        e.printStackTrace();
    	    }
    }
    
}