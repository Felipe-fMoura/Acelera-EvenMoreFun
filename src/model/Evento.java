/*
 * Classe Evento - modelo dos eventos criados.
 * Métodos/Fluxos principais criados:
 *
 * - getParticipantes(), setParticipantes(List<Usuario>)  
 *   Usa um ArrayList para armazenar os participantes do evento.
 *
 * - adicionarParticipante(Usuario), removerParticipante(Usuario)  
 *   Manipula um ArrayList para adicionar ou remover participantes, evitando duplicação.
 *
 * - getQuantidadeParticipantes()  
 *   Retorna o tamanho do ArrayList de participantes.
 *
 * - setPresenca(int usuarioId, boolean presente), getPresenca(int usuarioId)  
 *   Utiliza um HashMap para mapear o ID do usuário à sua presença (true/false).
 *
 * - curtirEvento(Usuario), descurtirEvento(Usuario)  
 *   Usa um HashSet para registrar os usuários que curtiram, garantindo que não curtam mais de uma vez.
 *
 * - getUsuariosQueCurtiram()  
 *   Retorna o conjunto imutável de usuários que curtiram (HashSet).
 *
 * - getCurtidasPorImagem(), getComentariosPorImagem()  
 *   Armazena curtidas (HashMap<String, Integer>) e comentários por imagem (HashMap<String, List<String>>).
 *
 * - getComentarios(), adicionarComentario(Comentario)  
 *   Armazena comentários do evento em um ArrayList.
 *
 * - getGaleriaFotos()  
 *   Retorna uma lista de imagens (ArrayList<String>) adicionadas ao evento.
 *
 * Métodos de construção e dados:
 *
 * - Evento()  
 *   Construtor padrão que inicializa a data de criação do evento.
 *
 * - Evento(String titulo, String descricao, LocalDateTime data, String local, Usuario organizador, String palestrante)  
 *   Construtor completo com os principais dados do evento.
 *
 * - Getters e Setters  
 *   Permitem acessar e alterar os dados principais do evento: título, descrição, data, local, imagem, categoria, palestrante, tipo, etc.
 *
 * - isPrivado(), setPrivado(boolean)  
 *   Verifica ou define se o evento é privado.
 *
 * - getUrlVideo(), setUrlVideo(String)  
 *   Define ou retorna a URL do vídeo vinculado ao evento.
 *
 * - isAcessoLiberado(), setAcessoLiberado(boolean)  
 *   Indica se o conteúdo do evento está liberado para os participantes.
 *
 * - getCurtidas(), curtir()  
 *   Retorna ou incrementa o número total de curtidas do evento.
 *
 * - Builder (classe interna)  
 *   Utiliza o padrão de projeto *Builder* para criação de eventos com métodos encadeados (ex: comImagem(), comCategoria(), build()).
 *
 * - toString()  
 *   Representação textual do evento com os dados mais relevantes.
 */

package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Evento {
	private int id;
	private Usuario organizador;

	// Dados do evento
	private String titulo;
	private String descricao;
	private String local;
	private String imagem;
	private String categoria;
	private String palestrante;
	private String badgePath;
	private LocalDateTime data;
	private LocalDateTime dataCriacao;
	

	private Map<String, Integer> curtidasPorImagem = new HashMap<>();
	private Map<String, List<String>> comentariosPorImagem = new HashMap<>();

	private int curtidas;

	private boolean privado;

	// Comentarios de eventos
	private List<Comentario> comentarios = new ArrayList<>();

	// Controle de vídeo e acesso
	private String urlVideo;
	private boolean acessoLiberado = false;

	// Participantes e presença
	private List<Usuario> participantes = new ArrayList<>();
	private Map<Integer, Boolean> presencas = new HashMap<>();

	// Construtores
	public Evento() {
		this.dataCriacao = LocalDateTime.now();
	}

	public Evento(String titulo, String descricao, LocalDateTime data, String local, Usuario organizador,
			String palestrante) {
		this();
		this.titulo = titulo;
		this.descricao = descricao;
		this.data = data;
		this.local = local;
		this.organizador = organizador;
		this.palestrante = palestrante;
		this.curtidas = 0;
	}

	// Getters e Setters - Dados principais
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public String getImagem() {
		return imagem;
	}

	public void setImagem(String imagem) {
		this.imagem = imagem;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public String getPalestrante() {
		return palestrante;
	}

	public void setPalestrante(String palestrante) {
		this.palestrante = palestrante;
	}

	public LocalDateTime getData() {
		return data;
	}

	public void setData(LocalDateTime data) {
		this.data = data;
	}

	public LocalDateTime getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(LocalDateTime dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public Usuario getOrganizador() {
		return organizador;
	}

	public void setOrganizador(Usuario organizador) {
		this.organizador = organizador;
	}

	// Getters e Setters - Participantes
	public List<Usuario> getParticipantes() {
		return participantes;
	}

	public void setParticipantes(List<Usuario> participantes) {
		this.participantes = participantes;
	}

	public boolean adicionarParticipante(Usuario usuario) {
		Objects.requireNonNull(usuario, "Usuário não pode ser nulo");
		if (!participantes.contains(usuario)) {
			participantes.add(usuario);
			return true;
		}
		return false;
	}

	public boolean removerParticipante(Usuario usuario) {
		Objects.requireNonNull(usuario, "Usuário não pode ser nulo");
		return participantes.remove(usuario);
	}

	public int getQuantidadeParticipantes() {
		if (participantes == null) {
			throw new IllegalStateException("Lista de participantes não inicializada");
		}
		return participantes.size();
	}

	// Getters e Setters - Presença
	public void setPresenca(int usuarioId, boolean presente) {
		presencas.put(usuarioId, presente);
	}

	public boolean getPresenca(int usuarioId) {
		return presencas.getOrDefault(usuarioId, false);
	}

	// Getters e Setters - Controle e vídeo
	public boolean isPrivado() {
		return privado;
	}

	public void setPrivado(boolean privado) {
		this.privado = privado;
	}

	public String getUrlVideo() {
		return urlVideo;
	}

	public void setUrlVideo(String urlVideo) {
		this.urlVideo = urlVideo;
	}

	public boolean isAcessoLiberado() {
		return acessoLiberado;
	}

	public void setAcessoLiberado(boolean acessoLiberado) {
		this.acessoLiberado = acessoLiberado;
	}

	public Map<String, Integer> getCurtidasPorImagem() {
		return curtidasPorImagem;
	}

	public Map<String, List<String>> getComentariosPorImagem() {
		return comentariosPorImagem;
	}

	private String tipo; // "Presencial", "Online" ou "Híbrido"

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	// Representação textual
	@Override
	public String toString() {
		return String.format(
				"Evento[id=%d, titulo='%s', data=%s, local='%s', organizador='%s', palestrante='%s', participantes=%d]",
				id, titulo != null ? titulo : "", data != null ? data.toString() : "null", local != null ? local : "",
				organizador != null ? organizador.getNome() : "null", palestrante != null ? palestrante : "",
				getQuantidadeParticipantes());
	}

	private List<String> galeriaFotos = new ArrayList<>();

	public List<String> getGaleriaFotos() {
		return galeriaFotos;
	}

	public int getCurtidas() {
		return curtidas;
	}

	public void curtir() {
		this.curtidas++;
	}

	private Set<Integer> usuariosQueCurtiram = new HashSet<>();

	public boolean curtirEvento(Usuario usuario) {
		if (usuario == null) {
			throw new IllegalArgumentException("Usuário não pode ser nulo");
		}

		// Verifica se o usuário já curtiu
		if (usuariosQueCurtiram.contains(usuario.getId())) {
			return false; // já curtiu, não conta de novo
		}

		// Registra a curtida
		usuariosQueCurtiram.add(usuario.getId());
		this.curtidas++; // atualiza o contador

		return true; // sucesso na curtida
	}

	public boolean descurtirEvento(Usuario usuario) {
		if (usuario == null) {
			throw new IllegalArgumentException("Usuário não pode ser nulo");
		}

		if (usuariosQueCurtiram.remove(usuario.getId())) {
			this.curtidas--;
			if (this.curtidas < 0) {
				this.curtidas = 0; // garantir que não fique negativo
			}
			return true;
		}
		return false;
	}

	public Set<Integer> getUsuariosQueCurtiram() {
		return Collections.unmodifiableSet(usuariosQueCurtiram);
	}

	public static class Builder {
		private final Evento evento;

		public Builder(String titulo, String descricao, LocalDateTime data, String local, Usuario organizador,
				String palestrante) {
			this.evento = new Evento(titulo, descricao, data, local, organizador, palestrante);
		}

		public Builder comImagem(String imagem) {
			evento.setImagem(imagem);
			return this;
		}

		// Adicione outros métodos para atributos opcionais
		public Builder comCategoria(String categoria) {
			evento.setCategoria(categoria);
			return this;
		}

		public Evento build() {
			return evento;
		}
	}

	public List<Comentario> getComentarios() {
		return comentarios;
	}

	public void adicionarComentario(Comentario comentario) {
		comentarios.add(comentario);
	}
	
	public String getBadgePath() {
	    return badgePath;
	}

	public void setBadgePath(String badgePath) {
	    this.badgePath = badgePath;
	}


}
