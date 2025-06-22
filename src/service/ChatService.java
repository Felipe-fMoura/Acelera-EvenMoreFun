/*
 * ChatService
 * 
 * Descrição geral:
 * - Serviço singleton responsável por gerenciar o chat dos eventos.
 * - Armazena as mensagens e informações de "mãos levantadas" por evento.
 * 
 * Estruturas principais:
 * - mensagensPorEvento: mapeia cada Evento para uma lista de mensagens enviadas nele.
 * - maosLevantadasPorEvento: mapeia cada Evento para um mapa que associa usuárioId à quantidade de vezes que levantou a mão.
 * 
 * Métodos e funcionalidades:
 * 
 * getInstancia()
 * - Retorna a instância única (singleton) do ChatService, criando-a se necessário.
 * 
 * Classe interna MensagemChat
 * - Representa uma mensagem do chat, com o id do usuário remetente e o texto da mensagem.
 * 
 * adicionarMensagem(Evento evento, int usuarioId, String mensagem)
 * - Adiciona uma nova mensagem para o evento especificado.
 * - Cria a lista de mensagens para o evento caso não exista.
 * 
 * getMensagens(Evento evento)
 * - Retorna a lista de mensagens associadas ao evento.
 * - Retorna uma lista vazia caso o evento não tenha mensagens.
 * 
 * removerMensagem(Evento evento, MensagemChat mensagem)
 * - Remove uma mensagem específica da lista do evento, se existir.
 * 
 * registrarMaoLevantada(Evento evento, int usuarioId)
 * - Registra que um usuário levantou a mão no evento.
 * - Incrementa o contador de vezes que o usuário levantou a mão naquele evento.
 * - Cria o mapa de usuários para o evento caso não exista.
 * 
 * getQuantidadeMensagens(int eventoId, int usuarioId)
 * - Retorna a quantidade total de mensagens enviadas pelo usuário em um evento identificado pelo id.
 * - Busca o evento pelo id e conta as mensagens do usuário.
 * 
 * Técnicas utilizadas:
 * - Singleton para garantir uma única instância de serviço.
 * - Uso de Mapas e Listas para armazenamento em memória.
 * - Uso de computeIfAbsent e merge para manipulação eficiente de coleções.
 */

package service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Evento;

public class ChatService {

	private static ChatService instancia;

	// Map evento -> lista de mensagens
	// Vamos criar um modelo de mensagem para armazenar remetente e texto
	private Map<Evento, List<MensagemChat>> mensagensPorEvento;

	// Map evento -> map usuarioId -> qtd mãos levantadas
	private Map<Evento, Map<Integer, Integer>> maosLevantadasPorEvento;

	private ChatService() {
		mensagensPorEvento = new HashMap<>();
		maosLevantadasPorEvento = new HashMap<>();
	}

	public static ChatService getInstancia() {
		if (instancia == null) {
			instancia = new ChatService();
		}
		return instancia;
	}

	// Classe interna para representar mensagem com remetente
	public static class MensagemChat {
		private int usuarioId;
		private String texto;

		public MensagemChat(int usuarioId, String texto) {
			this.usuarioId = usuarioId;
			this.texto = texto;
		}

		public int getUsuarioId() {
			return usuarioId;
		}

		public String getTexto() {
			return texto;
		}
	}

	public void adicionarMensagem(Evento evento, int usuarioId, String mensagem) {
		mensagensPorEvento.computeIfAbsent(evento, k -> new ArrayList<>()).add(new MensagemChat(usuarioId, mensagem));
	}

	public List<MensagemChat> getMensagens(Evento evento) {
		return mensagensPorEvento.getOrDefault(evento, new ArrayList<>());
	}

	public void removerMensagem(Evento evento, MensagemChat mensagem) {
		List<MensagemChat> mensagens = mensagensPorEvento.get(evento);
		if (mensagens != null) {
			mensagens.remove(mensagem);
		}
	}

	// Registra que um usuário levantou a mão no evento
	public void registrarMaoLevantada(Evento evento, int usuarioId) {
		maosLevantadasPorEvento.computeIfAbsent(evento, k -> new HashMap<>()).merge(usuarioId, 1, Integer::sum);
	}

	// Retorna a quantidade de mensagens enviadas pelo usuário no evento
	public int getQuantidadeMensagens(int eventoId, int usuarioId) {
		int total = 0;
		for (Evento evento : mensagensPorEvento.keySet()) {
			if (evento.getId() == eventoId) {
				List<MensagemChat> mensagens = mensagensPorEvento.get(evento);
				if (mensagens != null) {
					for (MensagemChat msg : mensagens) {
						if (msg.getUsuarioId() == usuarioId) {
							total++;
						}
					}
				}
			}
		}
		return total;
	}
}
