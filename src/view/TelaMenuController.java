package view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Evento;
import model.Usuario;
import service.EventoService;
import service.UsuarioService;
import session.SessaoUsuario;
import javafx.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class TelaMenuController {
    private UsuarioService usuarioService = UsuarioService.getInstance();
    private EventoService eventoService = EventoService.getInstance();
    private Usuario usuarioLogado;

    @FXML
    private Text txtUserName;

    @FXML
    private Button btnCriarEvento;

    @FXML
    private Button btnPerfil;

    @FXML
    private VBox containerEventos;

    @FXML
    private TextField campoPesquisa;
    
    @FXML
    private VBox testeVbox;
    
    @FXML
    private Text txtRandom;
    
    @FXML
    private Button btnLogout;

    @FXML
    public void initialize() {
    	
    	try {
    	    // Carrega o arquivo de recursos
    	    InputStream inputStream = getClass().getResourceAsStream("/resources/Frases.txt");
    	    if (inputStream != null) {
    	        List<String> frases = new BufferedReader(new InputStreamReader(inputStream))
    	                                .lines()
    	                                .collect(Collectors.toList());
    	        
    	        String fraseAleatoria = selecionarFraseAleatoria(frases);
    	        txtRandom.setText(fraseAleatoria);
    	    } else {
    	        txtRandom.setText("Bem-vindo ao software!");
    	        System.err.println("Arquivo Frases.txt não encontrado nos recursos");
    	    }
    	} catch (Exception e) {
    	    System.err.println("Erro ao ler o arquivo de frases: " + e.getMessage());
    	    txtRandom.setText("Bem-vindo ao software!");
    	}
    	
    	
    	
    	
        // Configura o campo de pesquisa para buscar ao pressionar Enter
        campoPesquisa.setOnAction(event -> handlePesquisarEventos());
        
        // Carrega os eventos quando a tela é inicializada
        carregarEventos();
              
    }
    
    private String selecionarFraseAleatoria(List<String> frases) {
        if (frases == null || frases.isEmpty()) {
            return "Bem-vindo ao software!";
        }
        Random random = new Random();
        return frases.get(random.nextInt(frases.size()));
    }
    
    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
        atualizarInterfaceUsuario();
    }

    private void atualizarInterfaceUsuario() {
        if (usuarioLogado != null) {
            txtUserName.setText("Olá, " + usuarioLogado.getUsername());
            btnPerfil.setText(usuarioLogado.getUsername().split(" ")[0]); 
        }
    }

    public void carregarEventos() {
        containerEventos.getChildren().clear();
        
        List<Evento> eventos = eventoService.listarEventosParaUsuario(usuarioLogado);
        
        if (eventos.isEmpty()) {
            Text txtNenhumEvento = new Text("Nenhum evento encontrado");
            txtNenhumEvento.setStyle("-fx-fill: #666; -fx-font-size: 14px;");
            containerEventos.getChildren().add(txtNenhumEvento);
        } else {
            for (Evento evento : eventos) {
                containerEventos.getChildren().add(criarCardEvento(evento));
            }
        }
    }

    private Node criarCardEvento(Evento evento) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CardEvento.fxml"));
            Node card = loader.load();
            CardEventoController controller = loader.getController();
            controller.setEvento(evento, usuarioLogado); // Passa o evento e o usuário logado
            controller.setTelaMenuController(this); // Passa a referência da própria TelaMenuController
            return card;
        } catch (IOException e) {
            e.printStackTrace();
            return criarCardErro(evento.getTitulo());
        }
    }


    private Node criarCardErro(String tituloEvento) {
        Text txtErro = new Text("Erro ao carregar evento: " + tituloEvento);
        txtErro.setStyle("-fx-fill: #e74c3c; -fx-font-size: 14px;");
        return txtErro;
    }

    @FXML
    private void handleCriarEvento(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaCriarEvento.fxml"));
            Parent root = loader.load();
            
            TelaCriarEventoController controller = loader.getController();
            controller.setUsuarioLogado(usuarioLogado);
            
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Criar Novo Evento");
            stage.show();
            
            stage.setOnHidden(e -> carregarEventos());
            
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro ao abrir tela de criação de evento");
        }
    }

    
    @FXML private void handleAbrirPerfil(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaPerfil.fxml"));
            Parent subTelaPerfil = loader.load();

            TelaPerfilController controller = loader.getController();
            controller.carregarUsuario();
            testeVbox.getChildren().clear();
            testeVbox.getChildren().add(subTelaPerfil);

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro ao abrir perfil");
        }
    }



    @FXML
    private void handlePesquisarEventos() {
        String termo = campoPesquisa.getText().trim();
        containerEventos.getChildren().clear();
        
        List<Evento> eventos = termo.isEmpty() ? 
            eventoService.listarEventosParaUsuario(usuarioLogado) : 
            eventoService.pesquisarEventos(termo, usuarioLogado);
        
        if (eventos.isEmpty()) {
            Text txtNenhumResultado = new Text("Nenhum evento encontrado");
            txtNenhumResultado.setStyle("-fx-fill: #666; -fx-font-size: 14px;");
            containerEventos.getChildren().add(txtNenhumResultado);
        } else {
            for (Evento evento : eventos) {
                containerEventos.getChildren().add(criarCardEvento(evento));
            }
        }
    }

    private void mostrarAlerta(String mensagem) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
    
    @FXML
    private void onBtnLogout(ActionEvent event) {
    	    try {
    	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaLogin.fxml"));
    	        Parent root = loader.load();

    	        Stage stage = new Stage();
    	        stage.setScene(new Scene(root));
    	        stage.setTitle("Login");
    	        stage.show();

    	        Stage telaAtual = (Stage) ((Node) event.getSource()).getScene().getWindow();
    	        telaAtual.close();

    	    } catch (IOException e) {
    	        e.printStackTrace();   	       
    	    }
    	}

}
