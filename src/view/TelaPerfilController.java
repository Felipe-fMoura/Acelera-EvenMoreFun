package view;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Evento;
import model.Notificacao;
import model.Usuario;
import service.NotificacaoService;
import service.UsuarioService;
import session.SessaoUsuario;

public class TelaPerfilController {
	@FXML
	private Text lblNome;
	@FXML
	private Text lblUsername;
	@FXML
	private Text lblEmail;
	@FXML
	private Text lblCpf;
	@FXML
	private Text lblGenero;
	@FXML
	private Text lblTelefone;
	@FXML
	private Text lblDataNascimento;
	@FXML
	private Text lblId;
	@FXML
	private ImageView imgPerfil;
	@FXML
	private ListView<Evento> listEventosParticipando;
	@FXML
	private ListView<Evento> listEventosOrganizados;
	@FXML
	private Button btnFechar;
	@FXML
	private Label txtCompletarCadastro;
	@FXML
	private Button btnEditarFoto;

	private UsuarioService usuarioService = UsuarioService.getInstance();
	Usuario usuarioLogado = SessaoUsuario.getInstance().getUsuario();
	private Usuario usuario;

	public void carregarUsuario() {
		Usuario usuario = SessaoUsuario.getInstance().getUsuario();

		if (usuario != null) {

			lblId.setText("" + usuario.getId());
			lblNome.setText(usuario.getNome());
			lblUsername.setText("@" + usuario.getUsername());
			lblEmail.setText(usuario.getEmail());

			if (usuario.getDataNascimento() == null) {
				lblCpf.setText("Dados incompletos");
				lblGenero.setText("Dados incompletos");
				lblTelefone.setText("Dados incompletos");
				lblDataNascimento.setText("Dados incompletos");
			} else {
				lblCpf.setText(usuario.getCpf());
				lblGenero.setText(usuario.getGenero());
				lblTelefone.setText(usuario.getTelefone());
				lblDataNascimento.setText(usuario.getDataNascimento().toString());
			}

			listEventosParticipando.getItems().setAll(usuarioService.getEventosParticipandoUsuario(usuario.getId()));

			listEventosOrganizados.getItems().setAll(usuarioService.getEventosOrganizandoUsuario(usuario.getId()));

			configurarCelulasListView();

			if (usuarioService.dadosCompletosCadastrados(usuario)) {
				txtCompletarCadastro.setText("Cadastro completo:");
			} else {
				txtCompletarCadastro.setText("Cadastro incompleto. Clique aqui para completar");
			}
			System.out.println("Caminho foto : " + usuarioLogado.getCaminhoFotoPerfil());
			carregarFotoPerfil();
		}
	}

	private void configurarCelulasListView() {
		listEventosParticipando.setCellFactory(lv -> new EventoListCell());
		listEventosOrganizados.setCellFactory(lv -> new EventoListCell());
	}

	@FXML
	private void handleFecharPerfil(ActionEvent event) {
		if (telaMenuController != null) {
			telaMenuController.fecharPerfil();
		}
	}

	@FXML
	private void handleCompletarCadastro(MouseEvent event) {

		if (!usuarioService.dadosCompletosCadastrados(usuarioLogado)) {

			try {

				FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaCadastro2.fxml"));
				Parent root = loader.load();

				// Pega o controller da segunda tela
				TelaCadastro2Controller controller = loader.getController();

				// Envia o usuário para a próxima tela
				controller.setUsuario(usuarioLogado);

				// Troca de tela
				Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
				stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
				stage.show();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@FXML
	private void handleAbrirNotificacoes(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaCentralNotificacoes.fxml"));
			Parent root = loader.load();

			// Cria uma nova janela (popup)
			Stage popupStage = new Stage();
			popupStage.setTitle("Notificações"); // Título opcional

			// Define um tamanho menor para o popup (ajuste conforme necessário)
			Scene scene = new Scene(root, 400, 300); // Largura x Altura

			//
			popupStage.setResizable(false);

			// Faz o popup ficar na frente da janela principal
			Stage mainStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			popupStage.initOwner(mainStage);

			popupStage.setScene(scene);
			popupStage.show(); // Mostra o popup sem fechar a tela atual

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void handleSelecionarFoto() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Selecionar Foto de Perfil");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg"));

		File file = fileChooser.showOpenDialog(imgPerfil.getScene().getWindow());
		if (file != null) {
			String caminho = file.getAbsolutePath();
			usuarioLogado.setCaminhoFotoPerfil(caminho); // Armazena caminho no objeto
			SessaoUsuario.getInstance().setUsuario(usuarioLogado); // <- ATUALIZA NA SESSÃO

			System.out.println("[DEBUG] Caminho da foto perfil do usuário: " + usuarioLogado.getCaminhoFotoPerfil());

			carregarFotoPerfil(); // Atualiza visual
		}

		Notificacao notificacao = new Notificacao("Você alterou a foto de perfil", LocalDateTime.now(), false,
				Notificacao.Tipo.HISTORICO, "Sistema");
		NotificacaoService.getInstance().registrarNotificacao(usuarioLogado.getId(), notificacao);

	}

	public void carregarFotoPerfil() {
		String caminho = usuarioLogado.getCaminhoFotoPerfil();

		try {
			Image imagem;

			if (caminho != null && !caminho.isEmpty()) {
				if (caminho.startsWith("file:")) {
					// Caminho absoluto local com prefixo
					imagem = new Image(caminho, 150, 150, true, true);
				} else if (caminho.matches("^[a-zA-Z]:.*") || caminho.startsWith("/")) {
					// Caminho local absoluto sem prefixo
					imagem = new Image("file:" + caminho, 150, 150, true, true);
				} else {
					// Caminho de recurso (classpath)
					imagem = new Image(getClass().getResourceAsStream(caminho), 150, 150, true, true);
				}
			} else {
				// Fallback: imagem padrão
				imagem = new Image(getClass().getResourceAsStream("/resources/profile/iconFotoPerfilDefault.png"), 150,
						150, true, true);
			}

			imgPerfil.setImage(imagem);

			// Atualiza o ícone do botão do menu, se o controller principal estiver
			// disponível
			if (telaMenuController != null) {
				telaMenuController.atualizarFotoPerfilOrganizador(caminho);
			}

		} catch (Exception e) {
			System.err.println("Erro ao carregar foto de perfil: " + caminho);
			e.printStackTrace();
		}
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
		carregarUsuario();
		carregarFotoPerfil();
	}

	// acesso ao TelaMenuController
	private TelaMenuController telaMenuController;

	public void setTelaMenuController(TelaMenuController controller) {
		this.telaMenuController = controller;
	}

	@FXML
	private void handleEditarDados(ActionEvent event) {

		try {

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaEditarDados.fxml"));
			Parent root = loader.load();

			Stage stage = new Stage();

			stage.setTitle("Editar dados");
			stage.setScene(new Scene(root));
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}