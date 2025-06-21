package service;

import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class Redimensionamento {

	private static final double BASE_WIDTH = 1920.0;
	private static final double BASE_HEIGHT = 1080.0;

	public static void aplicarRedimensionamento(StackPane rootPane, ImageView backgroundImage, Group grupoCampos) {
		// Ajusta a imagem de fundo para preencher toda a tela
		backgroundImage.fitWidthProperty().bind(rootPane.widthProperty());
		backgroundImage.fitHeightProperty().bind(rootPane.heightProperty());

		// Escala proporcionalmente o grupo de campos
		grupoCampos.scaleXProperty().bind(rootPane.widthProperty().divide(BASE_WIDTH));
		grupoCampos.scaleYProperty().bind(rootPane.heightProperty().divide(BASE_HEIGHT));
	}
}
