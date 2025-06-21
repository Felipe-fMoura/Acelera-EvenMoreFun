package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class TelaCentralJogosController {
    @FXML private Button btnJogoPontinho;
    @FXML private Button btnJogoTeclas;

    private void mostrarAlerta(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    @FXML
    private void handleBtnJogoPontinho(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaJogoPontinho.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/resources/styles.css").toExternalForm());

            Stage stage = new Stage();
            stage.setTitle("Joguinho do Clique!");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro ao carregar o joguinho!");
        }
    }

 
   @FXML
private void handleBtnJogoTeclas(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaJogoTeclaCerta.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/resources/styles.css").toExternalForm());

        // INJETAR CENA APÓS LOAD
        TelaJogoTeclaCertaController controller = loader.getController();
        controller.setScene(scene);

        Stage stage = new Stage();
        stage.setTitle("Jogo da Tecla Certa!");
        stage.setScene(scene);
        stage.show();

    } catch (IOException e) {
        e.printStackTrace();
        mostrarAlerta("Erro ao carregar o joguinho!");
    }
}

}
