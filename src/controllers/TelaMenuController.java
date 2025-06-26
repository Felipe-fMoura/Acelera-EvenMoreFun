/*
 * initialize()
 * - Inicializa elementos da tela, como:
 *   - Ação do botão de easter egg com contador de cliques.
 *   - Carregamento de frase aleatória do arquivo "Frases.txt".
 *   - Configuração do campo de pesquisa para buscar ao pressionar Enter.
 * 
 * setUsuarioLogado(Usuario)
 * - Define o usuário logado no sistema.
 * - Atualiza a interface com nome e imagem de perfil.
 * - Carrega eventos disponíveis para o usuário.
 * 
 * carregarEventos()
 * - Recupera a lista de eventos via `EventoService` com base no usuário logado.
 * - Para cada evento, gera dinamicamente um card visual com `criarCardEvento()`.
 * - Exibe mensagem informativa se nenhum evento for encontrado.
 *
 * criarCardEvento(Evento)
 * - Carrega visual do evento via `CardEvento.fxml`.
 * - Injeta o controller com o evento e o usuário logado.
 * - Em caso de erro, mostra um card com mensagem de falha.
 *
 * handleCriarEvento(ActionEvent)
 * - Verifica se o cadastro do usuário está completo.
 * - Abre nova janela com a tela de criação de evento (`TelaCriarEvento.fxml`).
 * - Ao fechar a janela de criação, recarrega eventos e imagem de perfil.
 *
 * handleAbrirPerfil(ActionEvent)
 * - Carrega e exibe a tela de perfil (`TelaPerfil.fxml`) dentro da área `testeVbox`.
 * - Injeta o controller do perfil com a referência da tela menu e o usuário logado.
 *
 * handlePesquisarEventos()
 * - Pesquisa eventos com base no termo digitado no campo.
 * - Exibe resultado filtrado ou mensagem se nenhum for encontrado.
 *
 * handleListarPor()
 * - Exibe um diálogo de confirmação perguntando como o usuário deseja listar os eventos (por curtidas ou por data).
 * - Dispara métodos de listagem conforme a opção escolhida.
 *
 * listarEventosPorCurtidas()
 * - Lista eventos ordenados por quantidade de curtidas.
 *
 * listarTodosEventos()
 * - Lista todos os eventos sem filtro, ordenados por data (padrão).
 *
 * handleRefresh(ActionEvent)
 * - Recarrega manualmente os eventos e atualiza imagem do perfil.
 *
 * onBtnLogout(ActionEvent)
 * - Retorna o usuário para a tela de login (`TelaLogin.fxml`), encerrando a sessão.
 *
 * handleBtnJogo(ActionEvent)
 * - Abre uma nova janela com a central de jogos (`TelaCentralJogos.fxml`).
 *
 * atualizarFotoPerfilOrganizador(String)
 * - Atualiza a imagem do perfil nos botões e imagem principal.
 * - Usa imagem padrão se ocorrer erro de carregamento ou caminho for inválido.
 *
 * fecharPerfil()
 * - Remove a subview de perfil carregada no `testeVbox`.
 *
 * Estruturas e técnicas utilizadas:
 * - Leitura de arquivos de texto via `InputStream` para frases aleatórias.
 * - Navegação e carregamento dinâmico de FXMLs (`FXMLLoader`) com injeção de controller.
 * - Controle de sessão com `SessaoUsuario`.
 * - Comunicação entre controllers via injeção de referência (`setTelaMenuController()`).
 * - Manipulação de interface com componentes JavaFX (`VBox`, `TextField`, `ImageView`, `ScrollPane`, etc.).
 * - Tratamento de exceções com `try/catch` para garantir robustez.
 * - Utilização de `Alert`, `ButtonType` e `Dialog` para interações com o usuário.
 */

package controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Evento;
import model.Usuario;
import service.EventoService;
import service.UsuarioService;
import session.SessaoUsuario;

public class TelaMenuController {
	private UsuarioService usuarioService = UsuarioService.getInstance();
	private EventoService eventoService = EventoService.getInstance();
	private Usuario usuarioLogado;
	private int contadorCliques = 0;

	@FXML private Text txtUserName;
	@FXML private Button btnCriarEvento;
	@FXML private Button btnPerfil;
	@FXML private VBox containerEventos;
	@FXML private TextField campoPesquisa;
	@FXML private VBox testeVbox;
	@FXML private ScrollPane scrollPane;
	@FXML private Text txtRandom;
	@FXML private Button btnLogout;
	@FXML private Button btnListarPor;
	@FXML private ImageView imgFotoPerfilMenu;
	@FXML private Button btnEasterEgg;
	@FXML private Button btnJogo;

	@FXML
	public void initialize() {

		btnEasterEgg.setOnAction(event -> {
			contadorCliques++;
			if (contadorCliques == 10) {
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("Easter Egg");
				alert.setHeaderText(null);
				alert.setContentText("Obrigado por usar EvenMoreFun!");
				alert.showAndWait();
				contadorCliques = 0; // resetar contador
			}
		});

		try {
			// Carrega o arquivo de recursos
			InputStream inputStream = getClass().getResourceAsStream("/resources/frases/Frases.txt");
			if (inputStream != null) {
				List<String> frases = new BufferedReader(new InputStreamReader(inputStream)).lines()
						.collect(Collectors.toList());

				String fraseAleatoria = selecionarFraseAleatoria(frases);
				txtRandom.setText(fraseAleatoria);
			} else {
				txtRandom.setText("Bem-vindo ao software!");
				System.err.println("Arquivo Frases.txt não encontrado nos recursos");
			}
		} catch (Exception e) {
			System.err.println("Erro ao ler o arquivo de frases: " + e.getMessage());
			txtRandom.setText("Bem-vindo ao software!");
		}

		// Configura o campo de pesquisa para buscar ao pressionar Enter
		campoPesquisa.setOnAction(event -> handlePesquisarEventos());

	}

	private String selecionarFraseAleatoria(List<String> frases) {
		if (frases == null || frases.isEmpty()) {
			return "Bem-vindo ao software!";
		}
		Random random = new Random();
		return frases.get(random.nextInt(frases.size()));
	}

	public void setUsuarioLogado(Usuario usuario) {
		this.usuarioLogado = usuario;
		atualizarInterfaceUsuario();
		atualizarFotoPerfilOrganizador(usuarioLogado.getCaminhoFotoPerfil());
		carregarEventos();
		
	}
	

	private void atualizarInterfaceUsuario() {
		if (usuarioLogado != null) {
			txtUserName.setText("Olá, " + usuarioLogado.getUsername());
			// btnPerfil.setText(usuarioLogado.getUsername().split(" ")[0]);
		}
	}

	public void carregarEventos() {
		containerEventos.getChildren().clear();

		List<Evento> eventos = eventoService.listarEventosParaUsuario(usuarioLogado);

		if (eventos.isEmpty()) {
			Text txtNenhumEvento = new Text("Nenhum evento encontrado");
			txtNenhumEvento.setStyle("-fx-fill: #666; -fx-font-size: 14px;");
			containerEventos.getChildren().add(txtNenhumEvento);
		} else {
			for (Evento evento : eventos) {
				containerEventos.getChildren().add(criarCardEvento(evento));
			}
		}
	}

	private Node criarCardEvento(Evento evento) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CardEvento.fxml"));
			Node card = loader.load();
			CardEventoController controller = loader.getController();
			controller.setTelaMenuController(this); // Passa a referência da própria TelaMenuController
			controller.setEvento(evento, usuarioLogado); // Passa o evento e o usuário logado

			return card;
		} catch (IOException e) {
			e.printStackTrace();
			return criarCardErro(evento.getTitulo());
		}
	}

	private Node criarCardErro(String tituloEvento) {
		Text txtErro = new Text("Erro ao carregar evento: " + tituloEvento);
		txtErro.setStyle("-fx-fill: #e74c3c; -fx-font-size: 14px;");
		return txtErro;
	}

	@FXML
	private void handleCriarEvento(ActionEvent event) {
		usuarioLogado = SessaoUsuario.getInstance().getUsuario();

		if (!UsuarioService.getInstance().isCadastroCompleto(usuarioLogado)) {
			mostrarAlerta("Complete seu cadastro (CPF, telefone, etc.) antes de criar um evento.");
			return;
		}

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaCriarEvento.fxml"));
			Parent root = loader.load();

			TelaCriarEventoController controller = loader.getController();
			controller.setUsuarioLogado(usuarioLogado);

			Stage stage = new Stage();
			stage.setScene(new Scene(root));
			stage.setTitle("Criar Novo Evento");
			stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/logo/LOGOROXA.png")));
			stage.show();

			// Quando fechar a tela de criação, recarrega eventos e atualiza foto do perfil
			stage.setOnHidden(e -> {
				carregarEventos();
				if (usuarioLogado != null) {
					atualizarFotoPerfilOrganizador(usuarioLogado.getCaminhoFotoPerfil());
				}
			});

		} catch (IOException e) {
			e.printStackTrace();
			mostrarAlerta("Erro ao abrir tela de criação de evento");
		}
	}

	@FXML
	private void handleAbrirPerfil(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaPerfil.fxml"));
			Parent subTelaPerfil = loader.load();

			TelaPerfilController controller = loader.getController();
			controller.setTelaMenuController(this); // <-- AQUI INJETA A REFERÊNCIA
			Usuario usuarioLogado = SessaoUsuario.getUsuarioLogado();

			controller.setUsuario(usuarioLogado); // Assumindo que exista esse método para passar o usuário

			testeVbox.getChildren().clear();
			testeVbox.getChildren().add(subTelaPerfil);

		} catch (IOException e) {
			e.printStackTrace();
			mostrarAlerta("Erro ao abrir perfil");
		}
	}

	@FXML
	private void handlePesquisarEventos() {
		String termo = campoPesquisa.getText().trim();
		containerEventos.getChildren().clear();

		List<Evento> eventos = termo.isEmpty() ? eventoService.listarEventosParaUsuario(usuarioLogado)
				: eventoService.pesquisarEventos(termo, usuarioLogado);

		if (eventos.isEmpty()) {
			Text txtNenhumResultado = new Text("Nenhum evento encontrado");
			txtNenhumResultado.setStyle("-fx-fill: #666; -fx-font-size: 14px;");
			containerEventos.getChildren().add(txtNenhumResultado);
		} else {
			for (Evento evento : eventos) {
				containerEventos.getChildren().add(criarCardEvento(evento));
			}
		}
	}

	private void mostrarAlerta(String mensagem) {
		javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
				javafx.scene.control.Alert.AlertType.INFORMATION);
		alert.setTitle("Aviso");
		alert.setHeaderText(null);
		alert.setContentText(mensagem);
		alert.showAndWait();
	}

	@FXML
	private void onBtnLogout(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaLogin.fxml"));
			Parent root = loader.load();

			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

			// Usa a largura/altura da janela atual
			Scene newScene = new Scene(root, stage.getWidth(), stage.getHeight());

			stage.setScene(newScene);
			stage.setTitle("EvenMoreFun");
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void handleRefresh(ActionEvent event) {
		carregarEventos();
		if (usuarioLogado != null) {
			atualizarFotoPerfilOrganizador(usuarioLogado.getCaminhoFotoPerfil());
		}
	}

	@FXML
	private void handleListarPor() {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Escolher filtro de listagem");
		alert.setHeaderText("Como deseja listar os eventos?");
		alert.setContentText("Escolha o critério:");

		ButtonType buttonPorCurtidas = new ButtonType("Por Curtidas");
		ButtonType buttonPorData = new ButtonType("Por Data");
		ButtonType buttonCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

		alert.getButtonTypes().setAll(buttonPorCurtidas, buttonPorData, buttonCancelar);
		
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/logo/LOGOROXA.png")));


		alert.showAndWait().ifPresent(response -> {
			if (response == buttonPorCurtidas) {
				listarEventosPorCurtidas();
			} else if (response == buttonPorData) {
				listarTodosEventos();
			}
			// se cancelar, não faz nada
		});
	}

	private void listarEventosPorCurtidas() {
		containerEventos.getChildren().clear();

		List<Evento> eventos = eventoService.listarEventosPorCurtidas();

		if (eventos.isEmpty()) {
			Text txtNenhumEvento = new Text("Nenhum evento encontrado");
			txtNenhumEvento.setStyle("-fx-fill: #666; -fx-font-size: 14px;");
			containerEventos.getChildren().add(txtNenhumEvento);
		} else {
			for (Evento evento : eventos) {
				containerEventos.getChildren().add(criarCardEvento(evento));
			}
		}
	}

	private void listarTodosEventos() {
		containerEventos.getChildren().clear();

		List<Evento> eventos = eventoService.listarTodosEventos();

		if (eventos.isEmpty()) {
			Text txtNenhumEvento = new Text("Nenhum evento encontrado");
			txtNenhumEvento.setStyle("-fx-fill: #666; -fx-font-size: 14px;");
			containerEventos.getChildren().add(txtNenhumEvento);
		} else {
			for (Evento evento : eventos) {
				containerEventos.getChildren().add(criarCardEvento(evento));
			}
		}
	}

	public void atualizarFotoPerfilOrganizador(String caminhoFoto) {
		try {
			Image imagem;

			if (caminhoFoto != null && !caminhoFoto.trim().isEmpty()) {
				String caminhoUrl = new File(caminhoFoto).toURI().toString();
				imagem = new Image(caminhoUrl);

				// Verifica erro de carregamento
				if (imagem.isError()) {
					throw new IOException("Erro ao carregar imagem personalizada.");
				}
			} else {
				throw new IOException("Caminho da imagem vazio ou nulo.");
			}

			// Atualiza os dois locais
			ImageView miniatura = new ImageView(imagem);
			miniatura.setFitWidth(30);
			miniatura.setFitHeight(30);
			btnPerfil.setGraphic(miniatura);

			imgFotoPerfilMenu.setImage(imagem);

		} catch (Exception e) {
			// Carrega imagem padrão
			InputStream padrao = getClass().getResourceAsStream("/resources/profile/iconFotoPerfilDefault.png");
			Image imagemPadrao = new Image(padrao);

			ImageView miniatura = new ImageView(imagemPadrao);
			miniatura.setFitWidth(30);
			miniatura.setFitHeight(30);
			btnPerfil.setGraphic(miniatura);

			imgFotoPerfilMenu.setImage(imagemPadrao);
		}
	}

	public void fecharPerfil() {
		testeVbox.getChildren().clear(); // Remove a subTela do perfil
	}

	@FXML
	private void handleBtnJogo(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaCentralJogos.fxml"));
			Parent root = loader.load();

			Scene scene = new Scene(root);
			Stage stage = new Stage();
			stage.setTitle("Central de Jogos");
			stage.setScene(scene);
			stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/logo/LOGOROXA.png")));
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
			mostrarAlerta("Erro ao carregar a central de jogos");
		}
	}
	
	@FXML
	private void handleBtnamizade(ActionEvent event) {
	    try {
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Amizade.fxml"));
	        Parent root = loader.load();

	        // Obtém o controller e passa os dados necessários
	        controllers.AmizadeController controller = loader.getController();
	        controller.setUsuarioLogado(usuarioLogado);
	        controller.setMapaUsuarios(usuarioService.getUsuariosMapeados()); // método que você pode criar para retornar Map<String, Usuario>
	        controller.carregarAmigos(usuarioService.listarAmigosDoUsuario(usuarioLogado)); // ex: amigos salvos na memória

	        Stage stage = new Stage();
	        stage.setTitle("Sistema de Amizade");
	        stage.setScene(new Scene(root));
	        stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/logo/LOGOROXA.png")));
	        stage.show();

	    } catch (IOException e) {
	        e.printStackTrace();
	        Alert alert = new Alert(Alert.AlertType.ERROR);
	        alert.setTitle("Erro");
	        alert.setHeaderText("Erro ao abrir o sistema de amizades");
	        alert.setContentText(e.getMessage());
	        alert.showAndWait();
	    }
	}


}