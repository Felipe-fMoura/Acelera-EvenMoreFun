/*
 * Redimensionamento
 *
 * Descrição geral:
 * - Classe utilitária para aplicar redimensionamento e escala responsiva em componentes JavaFX.
 * - Permite que a interface gráfica se ajuste proporcionalmente à resolução da janela do usuário.
 *
 * Constantes:
 * - BASE_WIDTH e BASE_HEIGHT: valores base da resolução para cálculo de escala proporcional.
 *
 * Método principal:
 *
 * aplicarRedimensionamento(StackPane rootPane, ImageView backgroundImage, Group grupoCampos)
 * - Ajusta a imagem de fundo para preencher toda a área disponível do StackPane rootPane.
 *   - Usa bindings para que a largura (fitWidth) e altura (fitHeight) do ImageView acompanhem o tamanho do rootPane em tempo real.
 * - Aplica escala proporcional ao grupo de campos (Group grupoCampos) em X e Y.
 *   - A escala é calculada dividindo a largura/altura atual do rootPane pela base definida (1920x1080).
 *   - Isso mantém os elementos dentro do grupo com tamanho e proporção ajustados conforme o tamanho da janela.
 *
 * Técnicas e conceitos utilizados:
 * - Bindings do JavaFX para ligação dinâmica de propriedades de largura e altura.
 * - Escala proporcional para interfaces responsivas.
 * - Uso de StackPane para facilitar o ajuste da imagem de fundo.
 */

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
