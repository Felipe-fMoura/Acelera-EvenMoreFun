/*
 * Métodos relevantes criados:
 * - getInstancia() : ChatService (Singleton para obter instância)
 * - adicionarMensagem(Evento evento, int usuarioId, String mensagem) : void (adiciona mensagem a evento)
 * - getMensagens(Evento evento) : List<MensagemChat> (retorna lista de mensagens de evento)
 * - removerMensagem(Evento evento, MensagemChat mensagem) : void (remove mensagem do evento)
 * - registrarMaoLevantada(Evento evento, int usuarioId) : void (registra mão levantada do usuário no evento)
 * - getQuantidadeMensagens(int eventoId, int usuarioId) : int (quantidade de mensagens do usuário no evento)
 *
 * Classes internas:
 * - MensagemChat (representa uma mensagem com remetente e texto)
 */

package service;

import java.util.*;

import model.Evento;
import model.Usuario;

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
        mensagensPorEvento
            .computeIfAbsent(evento, k -> new ArrayList<>())
            .add(new MensagemChat(usuarioId, mensagem));
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
        maosLevantadasPorEvento
            .computeIfAbsent(evento, k -> new HashMap<>())
            .merge(usuarioId, 1, Integer::sum);
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
