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
