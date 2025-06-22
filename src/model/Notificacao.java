/*
 * Classe Notificacao – representa uma notificação no sistema, podendo ser exibida no histórico do usuário ou enviada por e-mail.
 *
 * Estruturas de dados e conceitos utilizados:
 * - Armazena texto da notificação (String), data e hora (LocalDateTime), remetente e tipo da notificação.
 * - Utiliza enum interno (Tipo) para diferenciar entre notificações de HISTORICO e ALERTA.
 *
 * Métodos principais:
 *
 * - Notificacao(String mensagem, LocalDateTime dataHora, boolean porEmail, Tipo tipo, String remetente)  
 *   Construtor que inicializa todos os atributos da notificação.
 *
 * - getMensagem()  
 *   Retorna o texto da mensagem da notificação.
 *
 * - getDataHora()  
 *   Retorna a data e hora em que a notificação foi criada.
 *
 * - isPorEmail()  
 *   Informa se a notificação também foi enviada por e-mail.
 *
 * - getTipo()  
 *   Retorna o tipo da notificação (HISTORICO ou ALERTA).
 *
 * - getRemetente()  
 *   Retorna o nome ou identificador de quem enviou a notificação.
 *
 * - toString()  
 *   Gera uma string com a data, remetente e mensagem, incluindo a marcação "(email)" caso tenha sido enviada por e-mail.
 */

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
