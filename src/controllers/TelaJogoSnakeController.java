package controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import session.SessaoUsuario;  // seu pacote da sessão
import model.Usuario;          // sua classe de usuário

import java.util.*;
import java.util.stream.Collectors;

public class TelaJogoSnakeController {

    @FXML private Canvas canvas;
    @FXML private Text lblScore;
    @FXML private Button btnReiniciar;

    // Labels para o leaderboard (conectados no FXML)
    @FXML private Label rank1;
    @FXML private Label rank2;
    @FXML private Label rank3;
    @FXML private Label rank4;
    @FXML private Label rank5;

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

    // leaderboard: username -> best score
    private static final Map<String, Integer> leaderboard = new HashMap<>();

    @FXML
    public void initialize() {
        gc = canvas.getGraphicsContext2D();
        configurarTeclas();
        iniciarNovoJogo();

        // Garante que o canvas aceite entrada de teclado logo após a UI carregar
        Platform.runLater(() -> canvas.requestFocus());
        
        atualizarLeaderboard(); // Atualiza a leaderboard na inicialização
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
            salvarScoreSeMelhor();
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

    private void salvarScoreSeMelhor() {
        Usuario user = SessaoUsuario.getUsuarioLogado();
        if (user != null) {
            String username = user.getUsername();
            leaderboard.put(username, Math.max(score, leaderboard.getOrDefault(username, 0)));
            atualizarLeaderboard();
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
        // fundo e área do jogo (30x20 blocos)
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width * TAM, height * TAM);

        // cobra
        gc.setFill(Color.LIMEGREEN);
        for (int[] p : snake) {
            gc.fillRect(p[0] * TAM, p[1] * TAM, TAM - 1, TAM - 1);
        }

        // comida
        gc.setFill(Color.RED);
        gc.fillOval(food[0] * TAM, food[1] * TAM, TAM - 1, TAM - 1);

        // texto Game Over
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

    private void atualizarLeaderboard() {
        // limpa todos os labels
        rank1.setText("1. -");
        rank2.setText("2. -");
        rank3.setText("3. -");
        rank4.setText("4. -");
        rank5.setText("5. -");

        // ordena e pega top5
        List<Map.Entry<String, Integer>> top5 = leaderboard.entrySet()
            .stream()
            .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
            .limit(5)
            .collect(Collectors.toList());

        for (int i = 0; i < top5.size(); i++) {
            Map.Entry<String, Integer> entry = top5.get(i);
            String texto = String.format("%d. %s - %d", i + 1, entry.getKey(), entry.getValue());
            switch (i) {
                case 0 -> rank1.setText(texto);
                case 1 -> rank2.setText(texto);
                case 2 -> rank3.setText(texto);
                case 3 -> rank4.setText(texto);
                case 4 -> rank5.setText(texto);
            }
        }
    }

    @FXML
    private void reiniciarJogo() {
        iniciarNovoJogo();
    }
}
