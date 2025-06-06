package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

import com.sun.net.httpserver.HttpServer;
import web.ConfirmacaoHandler;

import java.net.InetSocketAddress;
import java.io.IOException;

public class Main extends Application {

    private HttpServer server;

    @Override
    public void init() throws Exception {
        // Inicia o servidor HTTP na porta 8080 antes da UI aparecer
        try {
            server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/confirmar", new ConfirmacaoHandler());
            server.setExecutor(null);
            server.start();
            System.out.println("Servidor de confirmação iniciado na porta 8080...");
        } catch (IOException e) {
            System.err.println("Erro ao iniciar servidor HTTP: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaCadastro.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            primaryStage.setScene(scene);
            primaryStage.setTitle("EvenMoreFun");
            primaryStage.setMaximized(true);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        // Para o servidor HTTP ao fechar a aplicação
        if (server != null) {
            server.stop(0);
            System.out.println("Servidor de confirmação parado.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
