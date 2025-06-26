/*
 * Serviço responsável pelo gerenciamento completo de eventos.
 * 
 * Implementa o padrão Singleton para garantir uma única instância.
 * 
 * Principais funcionalidades:
 * - CRUD completo de eventos
 * - Gerenciamento de participantes e presenças
 * - Pesquisa e filtragem de eventos
 * - Controle de permissões e acessos
 * - Integração com serviços de usuário
 * 
 * Estruturas de dados principais:
 * - ArrayList<Evento>: Armazenamento dos eventos
 * - HashMap (implícito): Controle de presenças e permissões
 * 
 * Métodos principais:
 * - criarEvento(), atualizarEvento(), removerEvento(): Operações básicas CRUD
 * - listarEventos*(): Diversos métodos de listagem com filtros
 * - pesquisarEventos*(): Buscas por termo, data e categoria
 * - adicionar/removerParticipante(): Gestão de participantes
 * - getPermissao(): Controle de acesso a eventos
 * - getComentariosDoEvento(): Gestão de comentários
 * 
 * Validações implementadas:
 * - Visibilidade de eventos (públicos/privados)
 * - Permissões de acesso (organizador/participante)
 * - Verificação de participação
 * 
 * Padrões utilizados:
 * - Singleton: Controle de instância única
 * - Builder: Para criação de eventos (via classe Evento.Builder)
 * - Strategy: Diferentes algoritmos de filtragem/pesquisa
 */

package service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import model.Comentario;
import model.Evento;
import model.Usuario;

public class EventoService {

	private static EventoService instancia;
	private List<Evento> eventos;
	private int ultimoId = 0;

	private EventoService() {
		this.eventos = new ArrayList<>();
		inicializarDadosExemplo();
	}

	public static EventoService getInstance() {
		if (instancia == null) {
			synchronized (EventoService.class) {
				if (instancia == null) {
					instancia = new EventoService();
				}
			}
		}
		return instancia;
	}

	private void inicializarDadosExemplo() {
		Usuario org1 = new Usuario();
		org1.setId(1);
		org1.setNome("Eduardo Enari");
		org1.setCpf("12345678900");
		org1.setCaminhoFotoPerfil(getClass().getResource("/resources/profile/iconPadraoUser.png").toExternalForm());

		Usuario org2 = new Usuario();
		org2.setId(2);
		org2.setNome("Luiz Felipe");
		org2.setCpf("32145698700");
		org2.setCaminhoFotoPerfil(getClass().getResource("/resources/profile/iconPadraoUser.png").toExternalForm());

		Usuario org3 = new Usuario();
		org3.setId(3);
		org3.setNome("Joao Ramos");
		org3.setCpf("98765432100");
		org3.setCaminhoFotoPerfil(getClass().getResource("/resources/profile/iconPadraoUser.png").toExternalForm());

		Usuario org4 = new Usuario();
		org4.setId(4);
		org4.setNome("Rafael Duarte");
		org4.setCpf("45612378900");
		org4.setCaminhoFotoPerfil(getClass().getResource("/resources/profile/iconPadraoUser.png").toExternalForm());

		Usuario org5 = new Usuario();
		org5.setId(5);
		org5.setNome("Jéssica Pereira");
		org5.setCpf("15975348600");
		org5.setCaminhoFotoPerfil(getClass().getResource("/resources/profile/iconPadraoUser.png").toExternalForm());

		// Evento 1 - Educação
		criarEvento(new Evento.Builder(
				"Acelera Fatec",
				"Venha prestigiar!",
				LocalDateTime.now().plusDays(5),
				"Fatec Cruzeiro Prof. Waldomiro May",
				org1,
				"Enari")
			.comImagem(getClass().getResource("/resources/exemplos/fotoEvento/educacao.jpg").toExternalForm())
			.comCategoria("Educacao")
			.build()
		);

		// Evento 2 - Jogos
		criarEvento(new Evento.Builder(
				"Gameplay de Valorant",
				"Campeonatinho!",
				LocalDateTime.now().plusDays(5),
				"Fatec Cruzeiro Prof. Waldomiro May",
				org2,
				"Luiz Felipe")
			.comImagem(getClass().getResource("/resources/exemplos/fotoEvento/jogos.jpg").toExternalForm())
			.comCategoria("Jogos")
			.build()
		);

		// Evento 3 - Negócios
		criarEvento(new Evento.Builder(
				"Feira de Startups",
				"Conheça ideias inovadoras e oportunidades de investimento.",
				LocalDateTime.now().plusDays(7),
				"Centro de Inovação de São José",
				org3,
				"Camila Ramos")
			.comImagem(getClass().getResource("/resources/exemplos/fotoEvento/negocios.jpg").toExternalForm())
			.comCategoria("Negócios")
			.build()
		);

		// Evento 4 - Esportes
		criarEvento(new Evento.Builder(
				"Maratona Fatec 5K",
				"Participe da nossa corrida anual!",
				LocalDateTime.now().plusDays(10),
				"Parque Municipal",
				org4,
				"Rafael Costa")
			.comImagem(getClass().getResource("/resources/exemplos/fotoEvento/esportes.jpeg").toExternalForm())
			.comCategoria("Esportes")
			.build()
		);

		// Evento 5 - Festas
		criarEvento(new Evento.Builder(
				"Arraiá da Fatec",
				"Música, dança, comidas típicas e muita diversão!",
				LocalDateTime.now().plusDays(15),
				"Auditório da Fatec Cruzeiro",
				org5,
				"Jéssica Lima")
			.comImagem(getClass().getResource("/resources/exemplos/fotoEvento/festa.jpg").toExternalForm())
			.comCategoria("Festas")
			.build()
		);
	}



	public Evento criarEvento(Evento evento) {
		evento.setId(++ultimoId);
		eventos.add(evento);
		return evento;
	}

	public Evento atualizarEvento(Evento eventoAtualizado) {
		for (int i = 0; i < eventos.size(); i++) {
			if (eventos.get(i).getId() == eventoAtualizado.getId()) {
				eventos.set(i, eventoAtualizado);
				return eventoAtualizado;
			}
		}
		return null;
	}

	public boolean removerEvento(int id) {
		return eventos.removeIf(e -> e.getId() == id);
	}

	public Evento buscarEventoPorId(int id) {
		return eventos.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
	}

	// Métodos de listagem

	public List<Evento> listarTodosEventos() {
		return eventos.stream().sorted(Comparator.comparing(Evento::getData).reversed()).collect(Collectors.toList());
	}

	public List<Evento> listarEventosPublicos() {
		return eventos.stream().filter(e -> !e.isPrivado()).sorted(Comparator.comparing(Evento::getData))
				.collect(Collectors.toList());
	}

	public List<Evento> listarProximosEventos() {
		return eventos.stream().filter(e -> e.getData().isAfter(LocalDateTime.now()))
				.sorted(Comparator.comparing(Evento::getData)).collect(Collectors.toList());
	}

	public List<Evento> listarEventosPorCurtidas() {
		return eventos.stream().sorted(Comparator.comparingInt(Evento::getCurtidas).reversed())
				.collect(Collectors.toList());
	}
	// Métodos de pesquisa

	public List<Evento> pesquisarEventos(String termo, Usuario usuarioLogado) {
		if (termo == null || termo.trim().isEmpty()) {
			return listarEventosParaUsuario(usuarioLogado);
		}

		String termoLower = termo.toLowerCase();
		return eventos.stream().filter(e -> correspondeTermo(e, termoLower))
				.filter(e -> isVisivelParaUsuario(e, usuarioLogado)).sorted(Comparator.comparing(Evento::getData))
				.collect(Collectors.toList());
	}

	public List<Evento> pesquisarEventosPorData(LocalDate data, Usuario usuarioLogado) {
		return eventos.stream().filter(e -> e.getData().toLocalDate().equals(data))
				.filter(e -> isVisivelParaUsuario(e, usuarioLogado)).sorted(Comparator.comparing(Evento::getData))
				.collect(Collectors.toList());
	}

	public List<Evento> pesquisarEventosPorCategoria(String categoria, Usuario usuarioLogado) {
		return eventos.stream().filter(e -> e.getCategoria() != null && e.getCategoria().equalsIgnoreCase(categoria))
				.filter(e -> isVisivelParaUsuario(e, usuarioLogado)).sorted(Comparator.comparing(Evento::getData))
				.collect(Collectors.toList());
	}

	// Métodos auxiliares

	public List<Evento> listarEventosParaUsuario(Usuario usuario) {
		return eventos.stream().filter(e -> isVisivelParaUsuario(e, usuario))
				.sorted(Comparator.comparing(Evento::getData)).collect(Collectors.toList());
	}

	private boolean isVisivelParaUsuario(Evento evento, Usuario usuario) {
		if (!evento.isPrivado()) {
			return true;
		}
		if (usuario == null) {
			return false;
		}
		boolean isOrganizador = evento.getOrganizador() != null && evento.getOrganizador().equals(usuario);
		boolean isParticipante = evento.getParticipantes() != null
				&& evento.getParticipantes().stream().anyMatch(u -> u.equals(usuario));
		return isOrganizador || isParticipante;
	}

	private boolean correspondeTermo(Evento evento, String termoLower) {
		return (evento.getTitulo() != null && evento.getTitulo().toLowerCase().contains(termoLower))
				|| (evento.getDescricao() != null && evento.getDescricao().toLowerCase().contains(termoLower))
				|| (evento.getLocal() != null && evento.getLocal().toLowerCase().contains(termoLower))
				|| (evento.getPalestrante() != null && evento.getPalestrante().toLowerCase().contains(termoLower))
				|| (evento.getCategoria() != null && evento.getCategoria().toLowerCase().contains(termoLower));
	}

	// Métodos de participação

	public boolean adicionarParticipante(int eventoId, int usuarioId) {
		Evento evento = buscarEventoPorId(eventoId);
		Usuario usuario = UsuarioService.getInstance().buscarPorId(usuarioId);

		if (evento != null && usuario != null) {
			boolean addedToEvent = evento.adicionarParticipante(usuario);
			if (addedToEvent) {
				usuario.participarEvento(evento);
			}
			return addedToEvent;
		}
		return false;
	}

	public boolean removerParticipante(int eventoId, int usuarioId) {
		Evento evento = buscarEventoPorId(eventoId);
		Usuario usuario = UsuarioService.getInstance().buscarPorId(usuarioId);

		if (evento != null && usuario != null) {
			return evento.removerParticipante(usuario);
		}
		return false;
	}

	// Estatísticas

	public int contarEventosOrganizadosPorUsuario(int usuarioId) {
		return (int) eventos.stream().filter(e -> e.getOrganizador() != null && e.getOrganizador().getId() == usuarioId)
				.count();
	}

	public int contarParticipacoesUsuario(int usuarioId) {
		return (int) eventos.stream().filter(e -> e.getParticipantes() != null
				&& e.getParticipantes().stream().anyMatch(u -> u.getId() == usuarioId)).count();
	}

	public boolean isParticipante(int eventoId, int usuarioId) {
		Evento evento = buscarEventoPorId(eventoId);
		if (evento == null || evento.getParticipantes() == null) {
			return false;
		}
		return evento.getParticipantes().stream().anyMatch(u -> u.getId() == usuarioId);
	}

	// Presença

	public void setPresenca(int eventoId, int usuarioId, boolean presente) {
		Evento evento = buscarEventoPorId(eventoId);
		if (evento != null && isParticipante(eventoId, usuarioId)) {
			evento.setPresenca(usuarioId, presente);
		}
	}

	public boolean getPresenca(int eventoId, int usuarioId) {
		Evento evento = buscarEventoPorId(eventoId);
		if (evento != null && isParticipante(eventoId, usuarioId)) {
			return evento.getPresenca(usuarioId);
		}
		return false;
	}

	/**
	 * Retorna permissão do usuário para o evento: "organizador" se for organizador,
	 * "participante" se for participante, "nenhuma" caso contrário. Eventos
	 * públicos retornam "participante" para qualquer usuário.
	 */
	public String getPermissao(int eventoId, int usuarioId) {
		Evento evento = buscarEventoPorId(eventoId);
		if (evento == null) {
			return "nenhuma";
		}

		if (!evento.isPrivado()) {
			return "participante";
		}

		Usuario usuario = UsuarioService.getInstance().buscarPorId(usuarioId);
		if (usuario == null) {
			return "nenhuma";
		}

		if (evento.getOrganizador() != null && evento.getOrganizador().equals(usuario)) {
			return "organizador";
		}
		if (evento.getParticipantes() != null && evento.getParticipantes().stream().anyMatch(u -> u.equals(usuario))) {
			return "participante";
		}
		return "nenhuma";
	}

	public boolean hasPermissao(int usuarioId, int eventoId) {
		return !"nenhuma".equals(getPermissao(eventoId, usuarioId));
	}

	public void adicionarParticipanteComPermissao(int eventoId, int usuarioId, String permissao) {
		Evento evento = buscarEventoPorId(eventoId);
		Usuario usuario = UsuarioService.getInstance().buscarPorId(usuarioId);

		if (evento == null || usuario == null) {
			return;
		}

		if ("organizador".equalsIgnoreCase(permissao)) {
			evento.setOrganizador(usuario); // Define como organizador
			usuario.organizarEvento(evento); // Atualiza também na instância do usuário
			// Não adiciona como participante!
		} else if ("participante".equalsIgnoreCase(permissao)) {
			if (evento.adicionarParticipante(usuario)) {
				usuario.participarEvento(evento);
			}
		}
	}

	public void marcarPresenca(int eventoId, int usuarioId) {
		Evento evento = buscarEventoPorId(eventoId);
		if (evento != null) {
			evento.setPresenca(usuarioId, true);
			System.out.println("Presença registrada para usuário " + usuarioId + " no evento " + eventoId);
		}
	}

	public List<Integer> getParticipantesDoEvento(int eventoId) {
		Evento evento = buscarEventoPorId(eventoId);
		if (evento != null && evento.getParticipantes() != null) {
			return evento.getParticipantes().stream().map(Usuario::getId).collect(Collectors.toList());
		}
		return new ArrayList<>();
	}

	public boolean tentarCurtirEvento(Evento evento, Usuario usuario) {
		return evento.curtirEvento(usuario);
	}

	public List<Comentario> getComentariosDoEvento(int eventoId) {
		Evento evento = buscarEventoPorId(eventoId);
		if (evento != null) {
			return evento.getComentarios();
		}
		return new ArrayList<>();
	}

	public void adicionarComentarioAoEvento(int eventoId, Comentario comentario) {
		Evento evento = buscarEventoPorId(eventoId);
		if (evento != null) {
			evento.adicionarComentario(comentario);
		}
	}

}
