/*
 * Classe Comentario – representa um comentário feito por um usuário em um evento.
 *
 * Estruturas de dados utilizadas:
 * - Armazena o texto do comentário (String) e o ID do usuário autor (int).
 *
 * Métodos principais:
 * - Comentario(String texto, int usuarioId)  
 *   Construtor que cria um novo comentário com o texto e o ID do usuário.
 *
 * - getTexto()  
 *   Retorna o conteúdo textual do comentário.
 *
 * - getUsuarioId()  
 *   Retorna o ID do usuário que fez o comentário.
 */

package model;

public class Comentario {
	private String texto;
	private int usuarioId;

	public Comentario(String texto, int usuarioId) {
		this.texto = texto;
		this.usuarioId = usuarioId;
	}

	public String getTexto() {
		return texto;
	}

	public int getUsuarioId() {
		return usuarioId;
	}
}
