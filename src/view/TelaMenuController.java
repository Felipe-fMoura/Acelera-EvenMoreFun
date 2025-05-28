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
import javafx.event.ActionEvent;

import java.io.IOException;
import java.util.List;

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
    public void initialize() {
        // Configura o campo de pesquisa para buscar ao pressionar Enter
        campoPesquisa.setOnAction(event -> handlePesquisarEventos());
        
        // Carrega os eventos quando a tela é inicializada
        carregarEventos();
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

    private void carregarEventos() {
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
            controller.setEvento(evento, usuarioLogado); // Agora passando o usuário logado
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
            controller.setUsuario(usuarioLogado);
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
}