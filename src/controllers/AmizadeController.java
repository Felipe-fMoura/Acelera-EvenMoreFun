package controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;

import model.Usuario;

import java.util.*;

public class AmizadeController {

    @FXML private TextField txtBuscarUsuario;
    @FXML private VBox boxAmigos;
    @FXML private Label lblAmigoSelecionado;
    @FXML private TextArea txtChat;
    @FXML private TextField txtMensagem;

    private Usuario usuarioLogado;

    private final List<Usuario> amigos = new ArrayList<>();
    private final Map<String, Usuario> mapaUsuarios = new HashMap<>();
    private final Map<String, List<String>> historicoChat = new HashMap<>();

    private Usuario amigoSelecionado;

    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
        carregarAmigos(usuarioLogado.getAmigos());
    }

    public void setMapaUsuarios(Map<String, Usuario> usuarios) {
        this.mapaUsuarios.clear();
        this.mapaUsuarios.putAll(usuarios);
    }

    public void carregarAmigos(List<Usuario> lista) {
        amigos.clear();
        amigos.addAll(lista);
        boxAmigos.getChildren().clear();

        for (Usuario amigo : amigos) {
            Button btn = new Button(amigo.getNomeCompleto() + " (@" + amigo.getUsername() + ")");
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setOnMouseClicked(e -> selecionarAmigo(amigo));
            VBox.setVgrow(btn, Priority.NEVER);
            boxAmigos.getChildren().add(btn);
        }
    }

    private void selecionarAmigo(Usuario amigo) {
        this.amigoSelecionado = amigo;
        lblAmigoSelecionado.setText("💬 Conversa com " + amigo.getNomeCompleto());
        atualizarChat(amigo);
    }

    private void atualizarChat(Usuario amigo) {
        List<String> historico = usuarioLogado.getMensagensCom(amigo.getUsername());
        txtChat.setText(String.join("\n", historico));
    }

    @FXML
    private void handleEnviarMensagem() {
        if (amigoSelecionado == null) return;

        String msg = txtMensagem.getText().trim();
        if (!msg.isEmpty()) {
            String linha = "Você: " + msg;
            usuarioLogado.adicionarMensagem(amigoSelecionado.getUsername(), linha);

            // simulando conversa bidirecional
            String linhaAmigo = usuarioLogado.getNomeCompleto() + ": " + msg;
            amigoSelecionado.adicionarMensagem(usuarioLogado.getUsername(), linhaAmigo);

            atualizarChat(amigoSelecionado);
            txtMensagem.clear();
        }
    }


    @FXML
    private void handleBuscarUsuario() {
        String busca = txtBuscarUsuario.getText().trim().toLowerCase();

        if (busca.isEmpty()) {
            mostrarAlerta("Digite o @username", Alert.AlertType.INFORMATION);
            return;
        }

        Usuario encontrado = mapaUsuarios.get(busca);

        if (encontrado == null) {
            mostrarAlerta("Usuário não encontrado", Alert.AlertType.WARNING);
            return;
        }

        if (encontrado.equals(usuarioLogado)) {
            mostrarAlerta("Você não pode adicionar a si mesmo", Alert.AlertType.INFORMATION);
            return;
        }

        if (amigos.contains(encontrado)) {
            mostrarAlerta("Esse usuário já é seu amigo", Alert.AlertType.INFORMATION);
            return;
        }

        mostrarPopupPedido(encontrado);
    }

    private void mostrarPopupPedido(Usuario user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Pedido de Amizade");
        alert.setHeaderText("Deseja adicionar @" + user.getUsername() + " como amigo?");
        alert.setContentText("Nome: " + user.getNomeCompleto());
        
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/logo/LOGOROXA.png")));
        
        
        
        Optional<ButtonType> resultado = alert.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            usuarioLogado.enviarPedido(user);
            mostrarAlerta("Pedido enviado para @" + user.getUsername(), Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void abrirPopupPedidos() {
        List<Usuario> pedidos = usuarioLogado.getPedidosRecebidos();

        VBox boxPedidos = new VBox(10);
        boxPedidos.setPadding(new Insets(10));

        if (pedidos.isEmpty()) {
            Label label = new Label("Nenhum pedido novo por enquanto 👀");
            label.setStyle("-fx-font-size: 14px;");
            boxPedidos.getChildren().add(label);
        } else {
            // Criar cópia para evitar ConcurrentModificationException se removermos durante iteração
            for (Usuario remetente : new ArrayList<>(pedidos)) {
                HBox pedidoBox = new HBox(10);
                Label lblNome = new Label(remetente.getNomeCompleto() + " (@" + remetente.getUsername() + ")");
                Button btnAceitar = new Button("Aceitar");
                Button btnRejeitar = new Button("Rejeitar");

                btnAceitar.setOnAction(e -> {
                    usuarioLogado.aceitarPedido(remetente);
                    carregarAmigos(usuarioLogado.getAmigos());
                    boxPedidos.getChildren().remove(pedidoBox);
                    mostrarAlerta("Agora vocês são amigos!", Alert.AlertType.INFORMATION);
                });

                btnRejeitar.setOnAction(e -> {
                    usuarioLogado.getPedidosRecebidos().remove(remetente);
                    boxPedidos.getChildren().remove(pedidoBox);
                });

                pedidoBox.getChildren().addAll(lblNome, btnAceitar, btnRejeitar);
                boxPedidos.getChildren().add(pedidoBox);
            }
        }

        Stage popup = new Stage();
		popup.getIcons().add(new Image(getClass().getResourceAsStream("/resources/logo/LOGOROXA.png")));
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Pedidos de Amizade");
        popup.setScene(new Scene(boxPedidos, 350, 200));
        popup.showAndWait();
    }

    private void mostrarAlerta(String msg, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/logo/LOGOROXA.png")));

        alert.showAndWait();
        
    }
}
