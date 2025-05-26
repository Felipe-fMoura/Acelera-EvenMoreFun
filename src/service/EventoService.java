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
    	org1.setId(1);
    	org1.setNome("Fulano Fulanesis");
    	
    	Usuario org2= new Usuario();
    	org2.setId(2);
    	org2.setNome("Ciclano Ciclone");
    	
    	criarEvento(new Evento("Festa de Aniversário", "Venha comemorar conosco!",
    			LocalDateTime.now().plusDays(5),"Casa do João",org1));
    
    	 criarEvento(new Evento("Workshop de JavaFX", "Aprenda a criar interfaces incríveis", 
                 LocalDateTime.now().plusDays(10), "Sala 101", org2));
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
	 
	 
	 
}
