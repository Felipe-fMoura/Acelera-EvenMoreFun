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
