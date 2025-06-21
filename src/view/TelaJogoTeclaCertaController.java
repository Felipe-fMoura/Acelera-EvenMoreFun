package view;

import java.util.Random;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class TelaJogoTeclaCertaController {

	@FXML
	private Text txtLetra;
	@FXML
	private Text txtPontuacao;
	@FXML
	private Text txtTempo;
	@FXML
	private Text txtFeedback;

	private final Random random = new Random();
	private char letraAtual;
	private int pontuacao = 0;
	private int tempoRestante = 30;
	private Timeline timeline;

	@FXML
	public void initialize() {
		gerarNovaLetra();
		iniciarTemporizador();

	}

	private void iniciarTemporizador() {
		timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
			tempoRestante--;
			txtTempo.setText("Tempo: " + tempoRestante + "s");
			if (tempoRestante <= 0) {
				timeline.stop();
				txtLetra.setText("");
				txtFeedback.setText("Fim de jogo! Pontuação: " + pontuacao);
			}
		}));
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();
	}

	private void gerarNovaLetra() {
		letraAtual = (char) (random.nextInt(26) + 'A');
		txtLetra.setText(String.valueOf(letraAtual));
		txtFeedback.setText("");
	}

	private void verificarTecla(KeyEvent event) {
		if (tempoRestante <= 0) {
			return;
		}

		char teclaPressionada = Character.toUpperCase(event.getText().charAt(0));

		if (teclaPressionada == letraAtual) {
			pontuacao++;
			txtPontuacao.setText("Pontos: " + pontuacao);
			txtFeedback.setText("Acertou!");
			gerarNovaLetra();
		} else {
			txtFeedback.setText("Errado! Era: " + letraAtual);
		}
	}

	public void setScene(javafx.scene.Scene scene) {
		scene.setOnKeyPressed(this::verificarTecla);
	}

}
