/*
 * startGame()
 * Inicializa o jogo de clicar nos "pontinhos":
 * - Zera a pontuação.
 * - Esconde o botão de início.
 * - Inicia uma Timeline que gera círculos (pontinhos) aleatoriamente a cada 1 segundo.
 * 
 * spawnCircle()
 * Método central do jogo:
 * - Cria círculos com tamanho, cor e posição aleatória dentro do `AnchorPane`.
 * - Associa um evento de clique ao círculo que:
 *     - Incrementa a pontuação.
 *     - Atualiza o texto de pontuação.
 *     - Remove o círculo da tela.
 * - Caso o círculo não seja clicado, é removido após 2 segundos automaticamente usando outra Timeline.
 * 
 * Estruturas e técnicas utilizadas:
 * - Random: para geração de tamanhos, posições e cores aleatórias.
 * - Timeline (JavaFX): usada para animação contínua (gerar e remover círculos).
 * - Event Handling: detecção e resposta ao clique do usuário no círculo.
 * - AnchorPane como container dinâmico dos elementos gráficos do jogo.
 * - Encapsulamento do loop principal de jogo no método `spawnCircle`.
 */

package controllers;

import java.util.Random;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class TelaJogoPontinhoController {
	@FXML private AnchorPane root;
	@FXML private Text scoreText;
	@FXML private Button startBtn;

	private int score = 0;
	private Timeline timeline;
	private final Random random = new Random();

	public void startGame() {
		score = 0;
		scoreText.setText("Pontos: 0");
		startBtn.setVisible(false);

		timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> spawnCircle()));
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();
	}

	private void spawnCircle() {
		Circle circle = new Circle(20 + random.nextInt(30)); // Raio entre 20 e 50
		circle.setLayoutX(50 + random.nextInt((int) root.getWidth() - 100));
		circle.setLayoutY(50 + random.nextInt((int) root.getHeight() - 100));
		circle.setFill(Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble()));
		circle.getStyleClass().add("circle");

		circle.setOnMouseClicked(e -> {
			score++;
			scoreText.setText("Pontos: " + score);
			root.getChildren().remove(circle);
		});

		root.getChildren().add(circle);

		// Remover depois de 2 segundos se não for clicado
		Timeline remover = new Timeline(new KeyFrame(Duration.seconds(2), e -> root.getChildren().remove(circle)));
		remover.play();
	}
}
