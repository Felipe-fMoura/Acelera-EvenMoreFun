package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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
import service.UsuarioService;

public class TelaValidarOTPController {

    @FXML private TextField otpField;

    private final UsuarioService usuarioService = UsuarioService.getInstance();
    
    @FXML 
    private ImageView backgroundImage;
    
    @FXML 
    private StackPane telaOTPValidation;
    
    @FXML 
    private AnchorPane contentPane;
    
    @FXML 
    private Group grupoCampos;

    @FXML
    public void initialize() {
        // Redimensionar imagem de fundo
        backgroundImage.fitWidthProperty().bind(telaOTPValidation.widthProperty());
        backgroundImage.fitHeightProperty().bind(telaOTPValidation.heightProperty());

        // Escalar proporcionalmente o grupo de campos (base: 1920x1080)
        grupoCampos.scaleXProperty().bind(
        		telaOTPValidation.widthProperty().divide(1920.0)
        );
        grupoCampos.scaleYProperty().bind(
        		telaOTPValidation.heightProperty().divide(1080.0)
        );
    }
    
    @FXML
    private void redefinirSenha() {
        // Aqui você coloca a lógica para validar OTP e ir para a próxima tela
        System.out.println("Botão Redefinir Senha clicado.");
    }

    @FXML
    private void validarOTP(ActionEvent event) {
        String inputOtp = otpField.getText().trim();
        String otpCorreto = usuarioService.getOtpTemporario();

        //TESTE APAGAR DPEOIS
        inputOtp = otpCorreto;
        if (otpCorreto != null && inputOtp.equals(otpCorreto)) {
            // Redireciona para tela de redefinir senha
            try {
            	
            	
            
            	
            
              
                 FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaRedefinirSenha.fxml"));
                Parent root = loader.load();
                
                Stage stage = (Stage) otpField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Redefinir Senha");
            
           
     	        
            } catch (Exception e) {
            	
            	System.out.println("OTPINUPUT:  "+inputOtp);
            	System.out.println("OTPCORRETO:  "+otpCorreto);
            	e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Erro ao carregar a tela de redefinição.").show();
            }
        } else {
            new Alert(Alert.AlertType.ERROR, "Código incorreto.").show();
        }
    }
}
