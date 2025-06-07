package service;
import model.Evento;
import model.Usuario;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

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
    	org1.setId(3);
    	org1.setNome("Fulano Fulanesis");
    	
    	Usuario org2= new Usuario();
    	org2.setId(2);
    	org2.setNome("Ciclano Ciclone");
    	
    	criarEvento(new Evento("Festa de Aniversário", "Venha comemorar conosco!",
    			LocalDateTime.now().plusDays(5),"Casa do João",org1,"Mãe do Jõao"));
    
    	 criarEvento(new Evento("Workshop de JavaFX", "Aprenda a criar interfaces incríveis", 
                 LocalDateTime.now().plusDays(10), "Sala 101", org2, "Prof. Eduardo Enari"));
    }
    
    //Crud básico
    /**
     * Cria um novo evento e adiciona à lista de eventos.
     * Um ID único é atribuído automaticamente ao evento.
     *
     * @param evento (obj) o evento a ser criado
     * @return o evento criado com o ID atribuído
     */
    public Evento criarEvento(Evento evento) {
    	evento.setId(++ultimoId);
    	eventos.add(evento);
    	return evento;
    }

    /**
     * Atualiza um evento existente na lista com base no ID.
     *
     * @param eventoAtualizado (obj) o evento com os dados atualizados
     * @return o evento atualizado, ou null se o evento não foi encontrado
     */
	public Evento atualizarEvento(Evento eventoAtualizado) {
		for (int i = 0; i < eventos.size(); i++) {
			if (eventos.get(i).getId() == eventoAtualizado.getId()) {
				eventos.set(i, eventoAtualizado);
				return eventoAtualizado;
			}
		}
		return null;
	}
	
	/**
	 * Remove um evento da lista com base no ID informado.
	 *
	 * @param id o ID do evento a ser removido
	 * @return true se o evento foi encontrado e removido, false caso contrário
	 */	
	/* e é um nome temporario para cada obj da lista 
	  eventos, "->" significa "Com isso, faca aquilo"
	  que é remover se a condição e.getId()==id for true
	 */
	 
	public boolean removerEvento(int id) {
        return eventos.removeIf(e -> e.getId() == id);
    }
	
	/**
	 * Busca um evento na lista com base no ID informado.
	 *
	 * @param id o ID do evento a ser buscado
	 * @return o evento com o ID correspondente, ou null se não for encontrado
	 */
	public Evento buscarEventoPorId(int id) {
        return eventos.stream()
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElse(null);
    }
	
	// Métodos de listagem
	
	
	/**
	 * Retorna uma cópia da lista com todos os eventos cadastrados.
	 *
	 * @return lista de eventos
	 */
	public List<Evento> listarTodosEventos(){
		return new ArrayList<>(eventos);
	}
	
	/**
	 * Retorna uma lista de eventos públicos, ordenados por data.
	 *
	 * @return lista de eventos não privados em ordem crescente de data
	 */
	 public List<Evento> listarEventosPublicos() {
	        return eventos.stream()
	                .filter(e -> !e.isPrivado())
	                .sorted(Comparator.comparing(Evento::getData))
	                .collect(Collectors.toList());
	    }
	
	 /**
	  * Retorna uma lista de eventos que ainda vão acontecer, ordenados por data.
	  *
	  * @return lista de eventos com data futura em ordem crescente
	  */
	 public List<Evento> listarProximosEventos() {
	        return eventos.stream()
	                .filter(e -> e.getData().isAfter(LocalDateTime.now()))
	                .sorted(Comparator.comparing(Evento::getData))
	                .collect(Collectors.toList());
	    }
	 
	 // Métodos de pesquisa
	 
	 
	 /**
	  * Pesquisa eventos que correspondem a um termo e são visíveis para o usuário.
	  * Se o termo for vazio, retorna todos os eventos visíveis para o usuário.
	  *
	  * @param termo texto para busca nos eventos
	  * @param usuarioLogado usuário que está consultando os eventos
	  * @return lista de eventos que correspondem ao termo e são acessíveis ao usuário
	  */
	 public List<Evento> pesquisarEventos(String termo, Usuario usuarioLogado){
		 if (termo == null || termo.trim().isEmpty()) {
			 return listarEventosParaUsuario(usuarioLogado);
		 }
		 
		 String termoLower = termo.toLowerCase();
		 return eventos.stream()
				 .filter(e -> correspondeTermo(e, termoLower))
				 .filter(e -> isVisivelParaUsuario(e, usuarioLogado))
				 .sorted(Comparator.comparing(Evento::getData))
				 .collect(Collectors.toList());
	 }
	 
	 /**
	  * Pesquisa eventos que ocorrem na data especificada e são visíveis para o usuário.
	  *
	  * @param data data a ser buscada
	  * @param usuarioLogado usuário que está realizando a busca
	  * @return lista de eventos na data informada e acessíveis ao usuário
	  */
	 public List<Evento> pesquisarEventosPorData(LocalDate data, Usuario usuarioLogado) {
	        return eventos.stream()
	                .filter(e -> e.getData().toLocalDate().equals(data))
	                .filter(e -> isVisivelParaUsuario(e, usuarioLogado))
	                .sorted(Comparator.comparing(Evento::getData))
	                .collect(Collectors.toList());
	    }

	 /**
	  * Pesquisa eventos da categoria especificada que são visíveis para o usuário.
	  *
	  * @param categoria categoria a ser buscada
	  * @param usuarioLogado usuário que está realizando a busca
	  * @return lista de eventos da categoria informada e acessíveis ao usuário
	  */
	 public List<Evento> pesquisarEventosPorCategoria(String categoria, Usuario usuarioLogado) {
	        return eventos.stream()
	                .filter(e -> e.getCategoria() != null && e.getCategoria().equalsIgnoreCase(categoria))
	                .filter(e -> isVisivelParaUsuario(e, usuarioLogado))
	                .sorted(Comparator.comparing(Evento::getData))
	                .collect(Collectors.toList());
	    }
	 
	 
	 // Métodos auxiliares
	 
	 
	 /**
	  * Retorna a lista de eventos visíveis para um dado usuário, ordenados por data.
	  *
	  * @param usuario usuário para quem os eventos devem ser visíveis
	  * @return lista de eventos acessíveis ao usuário
	  */
	 public List<Evento> listarEventosParaUsuario(Usuario usuario) {
	        return eventos.stream()
	                .filter(e -> isVisivelParaUsuario(e, usuario))
	                .sorted(Comparator.comparing(Evento::getData))
	                .collect(Collectors.toList());
	    }
	 
	 /**
	  * Verifica se um evento é visível para o usuário informado.
	  * Um evento é visível se for público ou se o usuário for organizador ou participante.
	  *
	  * @param evento evento a ser verificado
	  * @param usuario usuário para checar permissão
	  * @return true se o evento é visível para o usuário, false caso contrário
	  */
	 private boolean isVisivelParaUsuario(Evento evento, Usuario usuario) {
	        return !evento.isPrivado() || 
	               (usuario != null && 
	                (evento.getOrganizador().equals(usuario) || 
	                 evento.getParticipantes().contains(usuario)));
	    }
	
	 /**
	  * Verifica se o termo está presente em algum campo relevante do evento.
	  * Busca é feita em título, descrição, local e categoria (se existir).
	  *
	  * @param evento evento onde será feita a busca
	  * @param termoLower termo de busca em minúsculas
	  * @return true se algum campo contém o termo, false caso contrário
	  */
	 private boolean correspondeTermo(Evento evento, String termoLower) {
	        return (evento.getTitulo().toLowerCase().contains(termoLower)) ||
	               (evento.getDescricao() != null && evento.getDescricao().toLowerCase().contains(termoLower)) ||
	               (evento.getLocal().toLowerCase().contains(termoLower)) ||
	               (evento.getPalestrante().toLowerCase().contains(termoLower)) ||
	               (evento.getCategoria() != null && evento.getCategoria().toLowerCase().contains(termoLower));
	    }
	 
	 
	 // Métodos de participação
	 
	 	 
	 /**
	  * Adiciona um usuário como participante de um evento.
	  *
	  * @param eventoId ID do evento
	  * @param usuarioId ID do usuário a ser adicionado
	  * @return true se o usuário foi adicionado com sucesso, false caso contrário
	  */
	 public boolean adicionarParticipante(int eventoId, int usuarioId) {
	        Evento evento = buscarEventoPorId(eventoId);
	        Usuario usuario = UsuarioService.getInstance().buscarPorId(usuarioId);
	        
	        if (evento != null && usuario != null) {
	            // Atualiza no evento
	            boolean addedToEvent = evento.adicionarParticipante(usuario);
	            // Atualiza no usuário
	            if (addedToEvent) {
	                usuario.participarEvento(evento);
	            }
	            return addedToEvent;
	        }
	        return false;
	    }
	 
	
	 /**
	  * Remove um usuário da lista de participantes de um evento.
	  *
	  * @param eventoId ID do evento
	  * @param usuarioId ID do usuário a ser removido
	  * @return true se o usuário foi removido com sucesso, false caso contrário
	  */
	 public boolean removerParticipante(int eventoId, int usuarioId) {
	        Evento evento = buscarEventoPorId(eventoId);
	        Usuario usuario = UsuarioService.getInstance().buscarPorId(usuarioId);
	        
	        if (evento != null && usuario != null) {
	            return evento.removerParticipante(usuario);
	        }
	        return false;
	    }
	 
	 
	 // Métodos para estátistica
	 
	 
	 /**
	  * Conta quantos eventos foram organizados por um determinado usuário.
	  *
	  * @param usuarioId ID do usuário organizador
	  * @return número de eventos organizados pelo usuário
	  */
	 public int contarEventosOrganizadosPorUsuario(int usuarioId) {
	        return (int) eventos.stream()
	                .filter(e -> e.getOrganizador().getId() == usuarioId)
	                .count();
	    }
	 
	 
	 /**
	  * Conta quantos eventos um determinado usuário participa.
	  *
	  * @param usuarioId ID do usuário participante
	  * @return número de eventos em que o usuário participa
	  */
	  public int contarParticipacoesUsuario(int usuarioId) {
	        return (int) eventos.stream()
	                .filter(e -> e.getParticipantes().stream().anyMatch(u -> u.getId() == usuarioId))
	                .count();
	    }
	  
	   public boolean isParticipante(int eventoId, int usuarioId) {
	        Evento evento = buscarEventoPorId(eventoId);
	        return evento.getParticipantes().stream()
	                .anyMatch(u -> u.getId() == usuarioId);
	    }
	    

	  
}
