package controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Evento;
import model.Notificacao;
import model.Usuario;
import service.NotificacaoService;

public class TelaGaleriaController {

	@FXML
	private FlowPane galeriaFotos;

	@FXML
	private Button btnUploadFoto;

	private Evento evento;
	private Usuario usuarioLogado;
	private final Map<String, Set<Integer>> curtidasUsuarioImagem = new HashMap<>();

	public void setEvento(Evento evento, Usuario usuario) {
		this.evento = evento;
		this.usuarioLogado = usuario;
		carregarFotos();
	}

	private void carregarFotos() {
		galeriaFotos.getChildren().clear();

		if (evento == null || evento.getGaleriaFotos() == null) {
			return;
		}

		Map<String, Integer> curtidasPorImagem = evento.getCurtidasPorImagem();
		Map<String, List<String>> comentariosPorImagem = evento.getComentariosPorImagem();

		for (String caminho : evento.getGaleriaFotos()) {
			try {
				Image img = new Image("file:" + caminho, 180, 130, true, true);
				ImageView view = new ImageView(img);
				view.setPreserveRatio(true);
				view.setStyle("-fx-cursor: hand;"); // adiciona o cursor de lupa (mãozinha)

				// Abrir imagem ampliada ao clicar
				view.setOnMouseClicked(e -> {
					ImageView fullImageView = new ImageView(new Image("file:" + caminho));
					fullImageView.setPreserveRatio(true);
					fullImageView.setFitWidth(800); // ou ajuste como quiser

					ScrollPane scrollPane = new ScrollPane(fullImageView);
					scrollPane.setFitToWidth(true);
					scrollPane.setStyle("-fx-background: black;");

					Stage popupStage = new Stage();
					popupStage.setTitle("Visualizar imagem");

					Scene scene = new Scene(scrollPane, 850, 600);
					popupStage.setScene(scene);
					popupStage.show();
				});

				Label lblCurtidas = new Label("Curtidas: " + curtidasPorImagem.getOrDefault(caminho, 0));

				Button btnCurtir = new Button("♡"); // coração vazio

				btnCurtir.setStyle("-fx-background-color: transparent;" + "-fx-text-fill: #46295a;"
						+ "-fx-font-size: 18px;" + "-fx-padding: 0;" + "-fx-border-width: 0;");

				btnCurtir.setOnAction(e -> {
					Set<Integer> usuariosQueCurtiram = curtidasUsuarioImagem.computeIfAbsent(caminho,
							k -> new HashSet<>());

					Notificacao notificacao = new Notificacao(
							"Você curtiu uma foto do evento: '" + evento.getTitulo() + "'", LocalDateTime.now(), false,
							Notificacao.Tipo.HISTORICO, "Sistema");
					NotificacaoService.getInstance().registrarNotificacao(usuarioLogado.getId(), notificacao);

					if (usuariosQueCurtiram.contains(usuarioLogado.getId())) {
						usuariosQueCurtiram.remove(usuarioLogado.getId());
						int c = Math.max(0, curtidasPorImagem.getOrDefault(caminho, 1) - 1);
						curtidasPorImagem.put(caminho, c);
						lblCurtidas.setText("Curtidas: " + c);
						btnCurtir.setText("♡"); // coração vazio
						btnCurtir.setStyle("-fx-background-color: transparent;" + "-fx-text-fill: #46295a;"
								+ "-fx-font-size: 18px;" + "-fx-padding: 0;" + "-fx-border-width: 0;");
					} else {
						usuariosQueCurtiram.add(usuarioLogado.getId());
						int c = curtidasPorImagem.getOrDefault(caminho, 0) + 1;
						curtidasPorImagem.put(caminho, c);
						lblCurtidas.setText("Curtidas: " + c);
						btnCurtir.setText("♥"); // coração cheio
						btnCurtir.setStyle("-fx-background-color: transparent;" + "-fx-text-fill: #e74c3c;" + // vermelho
																												// coração
								"-fx-font-size: 18px;" + "-fx-padding: 0;" + "-fx-border-width: 0;");
					}
				});

				Button btnComentar = new Button("💬"); // ou "📝" se preferir
				btnComentar.setStyle("-fx-background-color: transparent;" + "-fx-text-fill: #46295a;"
						+ "-fx-font-size: 18px;" + "-fx-padding: 0;" + "-fx-border-width: 0;");

				btnComentar.setOnAction(e -> {
					TextInputDialog dialog = new TextInputDialog();
					dialog.setTitle("Comentário");
					dialog.setHeaderText("Deixe seu comentário:");
					dialog.setContentText("Comentário:");
					dialog.showAndWait().ifPresent(comentario -> {
						String completo = usuarioLogado.getNome() + ": " + comentario;
						comentariosPorImagem.computeIfAbsent(caminho, k -> new ArrayList<>()).add(completo);
						carregarFotos();
					});
				});

				Button btnDownload = new Button("📥");
				btnDownload.setStyle("-fx-background-color: transparent;" + "-fx-text-fill: #46295a;"
						+ "-fx-font-size: 18px;" + "-fx-padding: 0;" + "-fx-border-width: 0;");

				btnDownload.setOnAction(e -> {
					FileChooser fc = new FileChooser();
					fc.setTitle("Salvar imagem");
					fc.setInitialFileName(new File(caminho).getName());
					File destino = fc.showSaveDialog(new Stage());
					if (destino != null) {
						try {
							Files.copy(Paths.get(caminho), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
							mostrarMensagem("Imagem salva com sucesso!");

							Notificacao notificacao = new Notificacao(
									"Você baixou uma foto do evento '" + evento.getTitulo() + "'", LocalDateTime.now(),
									false, Notificacao.Tipo.HISTORICO, "Sistema");
							NotificacaoService.getInstance().registrarNotificacao(usuarioLogado.getId(), notificacao);

						} catch (IOException ex) {
							ex.printStackTrace();
							mostrarErro("Erro ao salvar imagem.");
						}
					}
				});

				VBox comentariosBox = new VBox();
				List<String> comentarios = comentariosPorImagem.getOrDefault(caminho, new ArrayList<>());
				for (String c : comentarios) {
					HBox linhaComentario = new HBox();
					Label lblComentario = new Label(c);
					linhaComentario.getChildren().add(lblComentario);

					boolean podeExcluir = c.startsWith(usuarioLogado.getNome() + ":")
							|| usuarioLogado.getId() == evento.getOrganizador().getId();

					if (podeExcluir) {
						Button btnDelComentario = new Button("✖"); // Ícone pequeno em vez do texto "Excluir"
						btnDelComentario.setStyle("-fx-background-color: transparent;" + // fundo transparente
								"-fx-text-fill: #888888;" + // cinza claro
								"-fx-font-size: 12px;" + // fonte menor
								"-fx-padding: 0 4 0 4;" + // padding reduzido
								"-fx-border-width: 0;" // sem borda
						);

						btnDelComentario.setOnAction(ev -> {
							comentarios.remove(c);
							carregarFotos();
						});
						linhaComentario.setSpacing(10);
						linhaComentario.getChildren().add(btnDelComentario);
					}

					comentariosBox.setSpacing(5);
					comentariosBox.getChildren().add(linhaComentario);
				}

				VBox vbox = new VBox(10, view, btnCurtir, lblCurtidas, btnComentar, comentariosBox, btnDownload);

				if (usuarioLogado.getId() == evento.getOrganizador().getId()) {

					Button btnExcluir = new Button("✖️");
					btnExcluir.setStyle("-fx-background-color: transparent;" + "-fx-text-fill: #46295a;"
							+ "-fx-font-size: 18px;" + "-fx-padding: 0;" + "-fx-border-width: 0;");

					btnExcluir.setOnAction(e -> {
						evento.getGaleriaFotos().remove(caminho);
						curtidasPorImagem.remove(caminho);
						comentariosPorImagem.remove(caminho);
						carregarFotos();
					});
					vbox.getChildren().add(btnExcluir);
				}

				vbox.setStyle("-fx-padding: 10;" + "-fx-background-color: white;" + "-fx-border-radius: 10;"
						+ "-fx-background-radius: 10;"
						+ "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);");

				galeriaFotos.getChildren().add(vbox);

			} catch (Exception e) {
				System.err.println("Erro ao carregar imagem: " + caminho);
				e.printStackTrace();
			}
		}
	}

	private void mostrarMensagem(String msg) {
		new Alert(Alert.AlertType.INFORMATION, msg).show();
	}

	private void mostrarErro(String msg) {
		new Alert(Alert.AlertType.ERROR, msg).show();
	}

	@FXML
	private void handleUploadFoto() {
		if (evento == null) {
			mostrarErro("Evento não definido.");
			return;
		}

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Selecionar Imagens");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg"));

		Stage stage = (Stage) galeriaFotos.getScene().getWindow();
		List<File> arquivos = fileChooser.showOpenMultipleDialog(stage);

		if (arquivos != null && !arquivos.isEmpty()) {
			boolean adicionou = false;

			for (File file : arquivos) {
				String caminho = file.getAbsolutePath();
				if (!evento.getGaleriaFotos().contains(caminho)) {
					evento.getGaleriaFotos().add(caminho);
					adicionou = true;
				}
			}

			if (adicionou) {
				carregarFotos();
				mostrarMensagem("Imagem(ns) adicionada(s) com sucesso!");
			} else {
				mostrarMensagem("Nenhuma nova imagem foi adicionada.");
			}
		}
	}
}
