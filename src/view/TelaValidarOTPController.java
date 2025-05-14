package view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import service.UsuarioService;

public class TelaValidarOTPController {

    @FXML private TextField otpField;

    private final UsuarioService usuarioService = UsuarioService.getInstance();
    
    @FXML
    private void redefinirSenha() {
        // Aqui você coloca a lógica para validar OTP e ir para a próxima tela
        System.out.println("Botão Redefinir Senha clicado.");
    }

    @FXML
    private void validarOTP() {
        String inputOtp = otpField.getText().trim();
        String otpCorreto = usuarioService.getOtpTemporario();

        if (otpCorreto != null && inputOtp.equals(otpCorreto)) {
            // Redireciona para tela de redefinir senha
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaRedefinirSenha.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) otpField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Redefinir Senha");
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Erro ao carregar a tela de redefinição.").show();
            }
        } else {
            new Alert(Alert.AlertType.ERROR, "Código incorreto.").show();
        }
    }
}
