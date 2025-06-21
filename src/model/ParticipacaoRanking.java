/*
 * Métodos relevantes criados:
 * - getUsuario(): retorna o usuário associado ao ranking de participação.
 * - getMensagens(): retorna o número de mensagens do usuário.
 * - getPontuacao(): retorna a pontuação (neste caso, o número de mensagens).
 * - compareTo(ParticipacaoRanking outro): compara rankings para ordenar em ordem decrescente.
 * - toString(): representação textual do ranking.
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
