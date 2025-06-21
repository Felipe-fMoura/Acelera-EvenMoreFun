package view;

	import javafx.animation.KeyFrame;
	import javafx.animation.Timeline;
	import javafx.fxml.FXML;
	import javafx.scene.control.Button;
	import javafx.scene.layout.AnchorPane;
	import javafx.scene.paint.Color;
	import javafx.scene.shape.Circle;
	import javafx.scene.text.Text;
	import javafx.util.Duration;
	import java.util.Random;
	
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
	        timeline.setCycleCount(Timeline.INDEFINITE);
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
