package view;

import java.io.IOException;
import java.time.LocalDate;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.Usuario;
import service.Alertas;
import service.UsuarioService;
import service.Redimensionamento;

public class TelaCadastro2Controller {

	private UsuarioService usuarioService = UsuarioService.getInstance();
	Alertas a = new Alertas();

	@FXML
	private ComboBox<String> comboBoxGenero;

	@FXML
	private TextField txtCPF;

	@FXML
	private TextField txtTelefone;

	@FXML
	private DatePicker dataNasc;
	
    @FXML 
    private ImageView backgroundImage;
    
    @FXML 
    private StackPane telaCadastro2;
    
    @FXML 
    private AnchorPane contentPane;
    
    @FXML 
    private Group grupoCampos;

	@FXML
	private void initialize() {
		// Redimensionar imagem de fundo
		Redimensionamento.aplicarRedimensionamento(telaCadastro2, backgroundImage, grupoCampos);

		
		// Adiciona os itens ao ComboBox
		comboBoxGenero.getItems().addAll("Masculino", "Feminino", "Outro");
	}

	// TRANSAÇÃO ENTRE USUARIOS
	private Usuario usuario;

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	@FXML
	private void finalizarCadastro(ActionEvent event) {

		String telefone = txtTelefone.getText();
		String cpf = txtCPF.getText();
		LocalDate dataNascimento = dataNasc.getValue();

		if (!usuarioService.validarTelefone(telefone)) {
			a.mostrarAlerta("Telefone inválido", "Digite apenas números com DDD (10 ou 11 dígitos).");
			return;
		}

		if (!usuarioService.validarCPF(cpf)) {
			a.mostrarAlerta("CPF inválido", "Verifique o número e tente novamente.");
			return;
		}
		
		if (!usuarioService.validarDataNascimento(dataNascimento)) {
			a.mostrarAlerta("Idade inferior ao permitido" , "Usuário não atende ao requisito mínimo de idade (14 anos).");
			return;
		}

		if (usuario != null) {
			usuario.setTelefone(txtTelefone.getText());
			usuario.setCpf(txtCPF.getText());
			usuario.setDataNascimento(dataNasc.getValue());
			usuario.setGenero(comboBoxGenero.getValue());

			if (usuarioService.completarCadastro(usuario)) {

				// imprimir no console, salvar em uma lista ou ir pra próxima tela
				System.out.println("Usuário completo:");
				System.out.println("Username: " + usuario.getUsername());
				System.out.println("Email: " + usuario.getEmail());
				System.out.println("Nome: " + usuario.getNome());
				System.out.println("Telefone: " + usuario.getTelefone());
				System.out.println("CPF: " + usuario.getCpf());
				System.out.println("Nascimento: " + usuario.getDataNascimento().toString());
				System.out.println("Genêro: " + usuario.getGenero());
				System.out.println("Id: " + usuario.getId());

				usuarioService.cadastrar(usuario);

				try {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaMenu.fxml"));
					Parent root = loader.load();

					TelaMenuController menuController = loader.getController();
					menuController.setUsuarioLogado(usuario); // Envia o usuário para o Menu

					Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
					stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
					stage.show();

				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}

	}
}
