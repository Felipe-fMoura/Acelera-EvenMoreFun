package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
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

        usuarioService.setUsuarioTemporario(usuario);
        usuarioService.setOtpTemporario(otp);

        // Mostrar o popup estilizado para digitar o código
        mostrarPopupValidacaoOTP();
    }
    
    @FXML
    private void mostrarPopupValidacaoOTP() {
        Label titulo = new Label("Digite o código fornecido por email");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #4B0082;");

        TextField input = new TextField();
        input.setPromptText("Digite o código");
        input.getStyleClass().add("telaotp-txtfield");

        Button confirmarBtn = new Button("Confirmar");
        confirmarBtn.getStyleClass().add("telaotp-btn");

        VBox box = new VBox(15, titulo, input, confirmarBtn);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(30));
        box.setStyle("-fx-background-color: #D9D9D9; -fx-background-radius: 12; -fx-border-radius: 12;");

        StackPane root = new StackPane(box);
        root.setStyle("-fx-background-color: transparent;");

        Scene scene = new Scene(root, 420, 200);
        scene.getStylesheets().add(getClass().getResource("/resources/styles.css").toExternalForm());

        Stage dialog = new Stage();
        dialog.setTitle("Validação de Código");
        dialog.setScene(scene);
        dialog.initOwner(telaOTP.getScene().getWindow());
        dialog.initModality(Modality.WINDOW_MODAL);

        confirmarBtn.setOnAction(e -> {
            String inputOtp = input.getText().trim();
            String otpCorreto = usuarioService.getOtpTemporario();

            if (inputOtp.equals(otpCorreto)) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaRedefinirSenha.fxml"));
                    Parent redefinirSenhaRoot = loader.load();

                    Stage mainStage = (Stage) telaOTP.getScene().getWindow();
                    mainStage.setScene(new Scene(redefinirSenhaRoot, mainStage.getWidth(), mainStage.getHeight()));
                    mainStage.setTitle("Redefinir Senha");

                    dialog.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Erro ao carregar a tela de redefinição.").show();
                }
            } else {
                Alert erro = new Alert(Alert.AlertType.ERROR);
                erro.setHeaderText(null);
                erro.setContentText("Código incorreto.");
                erro.showAndWait();
            }
        });

        dialog.showAndWait();
    }
   
    @FXML
    private void onBtnEntrar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaLogin.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

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
