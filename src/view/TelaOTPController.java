package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import model.Usuario;
import service.Redimensionamento;
import service.UsuarioService;
import otp.EmailSender;
import otp.OTPGenerator;

import java.io.IOException;

import jakarta.mail.MessagingException;

public class TelaOTPController {

    @FXML 
    private TextField emailField;
    
    @FXML 
    private Button enviarOTPButton;
    
    @FXML 
    private ImageView backgroundImage;
    
    @FXML 
    private StackPane telaOTP;
    
    @FXML 
    private AnchorPane contentPane;
    
    @FXML 
    private Group grupoCampos;

    private UsuarioService usuarioService = UsuarioService.getInstance();
    
    @FXML
    public void initialize() {
        // Redimensionar imagem de fundo
    	Redimensionamento.aplicarRedimensionamento(telaOTP, backgroundImage, grupoCampos);
    }

    @FXML
    private void enviarOTP() {
        String email = emailField.getText().trim();
        Usuario usuario = usuarioService.getUsuarioPorEmail(email);

        if (usuario == null) {
            new Alert(Alert.AlertType.ERROR, "E-mail não encontrado!").show();
            return;
        }

        String otp = OTPGenerator.generateOTP();
        try {
            EmailSender.sendOTP(email, otp);
        } catch (MessagingException e) {
            new Alert(Alert.AlertType.ERROR, "Erro ao enviar e-mail: " + e.getMessage()).show();
            return;
        }

        // Salva dados temporariamente no serviço
        usuarioService.setUsuarioTemporario(usuario);
        usuarioService.setOtpTemporario(otp);

        // Redireciona para tela de validação de OTP
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaOTPValidation.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Validar Código OTP");
        } catch (Exception e) {
        	e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erro ao carregar a próxima tela.").show();
        }
    }
    
    @FXML
	private void onBtnEntrar(ActionEvent event) {
	    try {
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaLogin.fxml"));
	        Parent root = loader.load();

	        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

	        // Herda o tamanho atual da janela
	        Scene newScene = new Scene(root, stage.getWidth(), stage.getHeight());

	        stage.setScene(newScene);
	        stage.show();
	    } catch (IOException e) {
	        e.printStackTrace();
	        // Você pode exibir uma mensagem de erro aqui, se quiser
	    }
	}
}
