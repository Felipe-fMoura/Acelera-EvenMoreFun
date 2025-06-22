/*
 * NotificacaoService
 *
 * Descrição geral:
 * - Serviço singleton responsável por gerenciar notificações dos usuários.
 * - Armazena as notificações em memória, organizadas por ID de usuário.
 *
 * Estruturas principais:
 * - notificacoesPorUsuario: Map que relaciona o ID do usuário a uma lista de notificações.
 *
 * Métodos e funcionalidades:
 *
 * getInstance()
 * - Retorna a instância única (singleton) do serviço.
 * - Cria a instância caso ainda não exista.
 *
 * registrarNotificacao(int userId, Notificacao notificacao)
 * - Adiciona uma nova notificação para o usuário especificado pelo userId.
 * - Se o usuário ainda não tem notificações registradas, cria a lista.
 *
 * enviarNotificacaoParaParticipantes(int eventoId, String mensagem, boolean porEmail, String remetente)
 * - Obtém os participantes de um evento pelo eventoId.
 * - Para cada participante, cria uma notificação do tipo ALERTA, usando a mensagem e remetente fornecidos.
 * - Registra essa notificação para cada participante.
 *
 * getNotificacoes(int userId, Notificacao.Tipo tipo)
 * - Retorna a lista de notificações de um usuário filtradas por tipo (ex: ALERTA, HISTORICO).
 * - Ordena as notificações por data/hora da mais recente para a mais antiga.
 * - Retorna lista vazia caso não haja notificações para o usuário.
 *
 * Técnicas utilizadas:
 * - Singleton para garantir uma única instância do serviço.
 * - Uso de Mapas e Listas para armazenamento e agrupamento.
 * - Uso de streams Java para filtro, ordenação e retorno eficiente dos dados.
 */

package service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Notificacao;

public class NotificacaoService {
	private static NotificacaoService instance;
	private final Map<Integer, List<Notificacao>> notificacoesPorUsuario = new HashMap<>();

	private NotificacaoService() {
	}

	public static NotificacaoService getInstance() {
		if (instance == null) {
			instance = new NotificacaoService();
		}
		return instance;
	}

	public void registrarNotificacao(int userId, Notificacao notificacao) {
		notificacoesPorUsuario.computeIfAbsent(userId, k -> new ArrayList<>()).add(notificacao);
	}

	public void enviarNotificacaoParaParticipantes(int eventoId, String mensagem, boolean porEmail, String remetente) {
		List<Integer> participantes = EventoService.getInstance().getParticipantesDoEvento(eventoId);
		LocalDateTime agora = LocalDateTime.now();

		for (int userId : participantes) {
			Notificacao notificacao = new Notificacao(mensagem, agora, porEmail, Notificacao.Tipo.ALERTA, remetente);
			registrarNotificacao(userId, notificacao);
		}
	}

	public List<Notificacao> getNotificacoes(int userId, Notificacao.Tipo tipo) {
		return notificacoesPorUsuario.getOrDefault(userId, Collections.emptyList()).stream()
				.filter(n -> n.getTipo() == tipo).sorted(Comparator.comparing(Notificacao::getDataHora).reversed())
				.toList();
	}
}
