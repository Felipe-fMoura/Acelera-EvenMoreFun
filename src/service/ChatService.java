package service;

import java.util.*;

import model.Evento;

public class ChatService {

    private static ChatService instancia;
    private Map<Evento, List<String>> mensagensPorEvento;

    private ChatService() {
        mensagensPorEvento = new HashMap<>();
    }

    public static ChatService getInstancia() {
        if (instancia == null) {
            instancia = new ChatService();
        }
        return instancia;
    }

    public void adicionarMensagem(Evento evento, String mensagem) {
        mensagensPorEvento.computeIfAbsent(evento, k -> new ArrayList<>()).add(mensagem);
    }

    public List<String> getMensagens(Evento evento) {
        return mensagensPorEvento.getOrDefault(evento, new ArrayList<>());
    }

    public void removerMensagem(Evento evento, String mensagem) {
        List<String> mensagens = mensagensPorEvento.get(evento);
        if (mensagens != null) {
            mensagens.remove(mensagem);
        }
    }
}

