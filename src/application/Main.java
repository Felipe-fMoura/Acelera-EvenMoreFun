package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Carregar o FXML da tela de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaCadastro.fxml"));
            Parent root = loader.load();

            // Criar a cena
            Scene scene = new Scene(root);

            // Configurar o palco
            primaryStage.setScene(scene);
            primaryStage.setTitle("EvenMoreFun");
            primaryStage.setMaximized(true); // Abre em modo maximizado
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
