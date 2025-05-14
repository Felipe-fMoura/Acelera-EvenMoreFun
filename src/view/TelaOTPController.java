package view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import model.Usuario;
import service.UsuarioService;
import otp.EmailSender;
import otp.OTPGenerator;
import jakarta.mail.MessagingException;

public class TelaOTPController {

    @FXML private TextField emailField;
    @FXML private Button enviarOTPButton;

    private UsuarioService usuarioService = UsuarioService.getInstance();

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
}
