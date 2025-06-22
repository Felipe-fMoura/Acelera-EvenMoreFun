/*
 * Classe ParticipacaoRanking – representa o desempenho de um usuário com base no número de mensagens enviadas.
 *
 * Estruturas e conceitos utilizados:
 * - Armazena o usuário (objeto Usuario) e sua quantidade de mensagens (int).
 * - Implementa a interface Comparable para permitir ordenação personalizada (ranking).
 *
 * Métodos principais:
 *
 * - ParticipacaoRanking(Usuario usuario, int mensagens)  
 *   Construtor que inicializa o usuário e o total de mensagens enviadas.
 *
 * - getUsuario()  
 *   Retorna o usuário associado à participação.
 *
 * - getMensagens()  
 *   Retorna o número total de mensagens do usuário.
 *
 * - getPontuacao()  
 *   Retorna a pontuação do usuário (neste caso, igual ao número de mensagens).
 *
 * - compareTo(ParticipacaoRanking outro)  
 *   Compara dois rankings para ordenação decrescente com base na pontuação (mais mensagens no topo).
 *
 * - toString()  
 *   Gera uma string com o nome do usuário e a quantidade de mensagens, usada para exibição.
 */

package model;

public class ParticipacaoRanking implements Comparable<ParticipacaoRanking> {
	private Usuario usuario;
	private int mensagens;

	public ParticipacaoRanking(Usuario usuario, int mensagens) {
		this.usuario = usuario;
		this.mensagens = mensagens;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public int getMensagens() {
		return mensagens;
	}

	public int getPontuacao() {
		return mensagens; // apenas mensagens contam agora
	}

	@Override
	public int compareTo(ParticipacaoRanking outro) {
		return Integer.compare(outro.getPontuacao(), this.getPontuacao()); // ordem decrescente
	}

	@Override
	public String toString() {
		return usuario.getNome() + " - Msg: " + mensagens;
	}
}
