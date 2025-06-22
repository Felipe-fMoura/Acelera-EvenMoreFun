/*
 * Classe Alertas
 *
 * Descrição geral:
 * - Classe utilitária para exibir alertas simples na interface JavaFX.
 * - Facilita a criação e exibição de janelas de alerta com título e mensagem customizados.
 *
 * Método principal:
 *
 * mostrarAlerta(String titulo, String mensagem)
 * - Cria um alerta do tipo INFORMATION (pode ser alterado para WARNING, ERROR, etc. conforme necessidade).
 * - Configura o título da janela de alerta com o parâmetro 'titulo'.
 * - Remove o cabeçalho padrão da janela de alerta, deixando-o vazio.
 * - Define o conteúdo do alerta com o texto passado pelo parâmetro 'mensagem'.
 * - Exibe a janela de alerta de forma modal, aguardando o usuário fechar (showAndWait).
 */

package service;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Alertas {

	public void mostrarAlerta(String titulo, String mensagem) {
		Alert alerta = new Alert(AlertType.INFORMATION); // ou WARNING, ERROR, etc.
		alerta.setTitle(titulo);
		alerta.setHeaderText(null); // sem cabeçalho
		alerta.setContentText(mensagem);
		alerta.showAndWait();
	}

}
