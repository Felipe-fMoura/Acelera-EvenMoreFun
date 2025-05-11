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
        	
            // Carregar o arquivo FXML
        	
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaCadastro.fxml"));
            Parent root = loader.load();  // Carrega o layout definido no FXML

            
            // Configurar a cena com o layout FXML
            Scene scene = new Scene(root, 400, 400);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Tela Cadastro");
            primaryStage.setMaximized(true);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
    	
        launch(args);
    }
}
