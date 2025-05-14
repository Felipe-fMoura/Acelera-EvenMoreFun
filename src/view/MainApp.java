package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import service.UsuarioService;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Carrega o usuário de teste antes de carregar a interface
        UsuarioService.getInstance().carregarUsuariosDeTeste();

        // Carrega a primeira tela: Tela de OTP
        Parent root = FXMLLoader.load(getClass().getResource("/view/TelaOTP.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Recuperação de Senha");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
