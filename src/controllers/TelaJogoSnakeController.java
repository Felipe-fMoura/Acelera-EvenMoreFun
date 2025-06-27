package controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.LinkedList;
import java.util.Random;

public class TelaJogoSnakeController {

    @FXML private Canvas canvas;
    @FXML private Text lblScore;
    @FXML private Button btnReiniciar;

    private GraphicsContext gc;
    private Timeline timeline;

    private static final int TAM = 20;
    private final int width = 30;   // largura lógica em blocos
    private final int height = 20;  // altura lógica em blocos

    private LinkedList<int[]> snake;
    private int[] food;
    private String direction = "RIGHT";
    private String nextDirection = "RIGHT";

    private boolean gameOver = false;
    private int score = 0;

    private int speed = 200;
    private final int minSpeed = 60;

    @FXML
    public void initialize() {
        gc = canvas.getGraphicsContext2D();
        configurarTeclas();
        iniciarNovoJogo();

        // Garante que o canvas aceite entrada de teclado logo após a UI carregar
        Platform.runLater(() -> canvas.requestFocus());
    }

    private void configurarTeclas() {
        canvas.setOnKeyPressed(e -> {
            KeyCode key = e.getCode();

            if ((key == KeyCode.UP || key == KeyCode.W) && !direction.equals("DOWN")) {
                nextDirection = "UP";
            } else if ((key == KeyCode.DOWN || key == KeyCode.S) && !direction.equals("UP")) {
                nextDirection = "DOWN";
            } else if ((key == KeyCode.LEFT || key == KeyCode.A) && !direction.equals("RIGHT")) {
                nextDirection = "LEFT";
            } else if ((key == KeyCode.RIGHT || key == KeyCode.D) && !direction.equals("LEFT")) {
                nextDirection = "RIGHT";
            }
        });
    }

    private void iniciarNovoJogo() {
        if (timeline != null) timeline.stop();

        gameOver = false;
        score = 0;
        speed = 200;
        direction = "RIGHT";
        nextDirection = "RIGHT";
        atualizarPontuacao();

        snake = new LinkedList<>();
        snake.add(new int[]{width / 2, height / 2});
        spawnFood();

        iniciarLoopJogo();

        // Garante foco novamente ao reiniciar
        Platform.runLater(() -> canvas.requestFocus());
    }

    private void iniciarLoopJogo() {
        timeline = new Timeline(new KeyFrame(Duration.millis(speed), e -> {
            if (!gameOver) {
                direction = nextDirection;
                atualizar();
                desenhar();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void spawnFood() {
        Random rand = new Random();
        while (true) {
            int fx = rand.nextInt(width);
            int fy = rand.nextInt(height);
            boolean emCorpo = false;
            for (int[] parte : snake) {
                if (parte[0] == fx && parte[1] == fy) {
                    emCorpo = true;
                    break;
                }
            }
            if (!emCorpo) {
                food = new int[]{fx, fy};
                break;
            }
        }
    }

    private void atualizar() {
        int[] head = snake.peekFirst();
        int x = head[0];
        int y = head[1];

        switch (direction) {
            case "UP" -> y--;
            case "DOWN" -> y++;
            case "LEFT" -> x--;
            case "RIGHT" -> x++;
        }

        int[] newHead = new int[]{x, y};

        if (x < 0 || y < 0 || x >= width || y >= height || contem(newHead)) {
            gameOver = true;
            return;
        }

        snake.addFirst(newHead);

        if (x == food[0] && y == food[1]) {
            score++;
            atualizarPontuacao();
            spawnFood();
            acelerarJogo();
        } else {
            snake.removeLast();
        }
    }

    private void acelerarJogo() {
        speed = Math.max(minSpeed, speed - 10);
        timeline.stop();
        iniciarLoopJogo();
    }

    private boolean contem(int[] pos) {
        for (int[] p : snake) {
            if (p[0] == pos[0] && p[1] == pos[1]) return true;
        }
        return false;
    }

    private void desenhar() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setFill(Color.LIMEGREEN);
        for (int[] p : snake) {
            gc.fillRect(p[0] * TAM, p[1] * TAM, TAM - 1, TAM - 1);
        }

        gc.setFill(Color.RED);
        gc.fillOval(food[0] * TAM, food[1] * TAM, TAM - 1, TAM - 1);

        if (gameOver) {
            gc.setFill(Color.WHITE);
            gc.fillText("Game Over! Pressione Reiniciar", 180, 150);
        }
    }

    private void atualizarPontuacao() {
        if (lblScore != null) {
            lblScore.setText("Pontos: " + score);
        }
    }

    @FXML
    private void reiniciarJogo() {
        iniciarNovoJogo();
    }
}
