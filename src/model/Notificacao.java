package model;

import java.time.LocalDateTime;

public class Notificacao {
	private String mensagem;
	private LocalDateTime dataHora;
	private boolean porEmail;
	private Tipo tipo; // HISTORICO ou ALERTA
	private String remetente;

	public enum Tipo {
		HISTORICO, ALERTA
	}

	public Notificacao(String mensagem, LocalDateTime dataHora, boolean porEmail, Tipo tipo, String remetente) {
		this.mensagem = mensagem;
		this.dataHora = dataHora;
		this.porEmail = porEmail;
		this.tipo = tipo;
		this.remetente = remetente;
	}

	// Getters
	public String getMensagem() {
		return mensagem;
	}

	public LocalDateTime getDataHora() {
		return dataHora;
	}

	public boolean isPorEmail() {
		return porEmail;
	}

	public Tipo getTipo() {
		return tipo;
	}

	public String getRemetente() {
		return remetente;
	}

	@Override
	public String toString() {
		return "[" + dataHora.toLocalDate() + "] " + remetente + ": " + mensagem + (porEmail ? " (email)" : "");
	}
}
