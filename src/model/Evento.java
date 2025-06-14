package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.HashMap;
import java.util.Map;


public class Evento {
    private int id;
    
    private Usuario organizador;

    
    private String titulo;
    private String descricao;
    private String local;
    private String imagem;
    private String categoria;
    private String palestrante;

    private LocalDateTime data;
    private LocalDateTime dataCriacao;

    private Map<Integer, Boolean> presencas = new HashMap<>();

    private boolean privado;
    
    private List<Usuario> participantes;


    // Construtores
    public Evento() {
        this.participantes = new ArrayList<>();
        this.dataCriacao = LocalDateTime.now();
    }

    public Evento(String titulo, String descricao, LocalDateTime data, String local, Usuario organizador, String palestrante) {
        this();
        this.titulo = titulo;
        this.descricao = descricao;
        this.data = data;
        this.local = local;
        this.palestrante = palestrante;
        this.organizador = organizador;
    }

    // Getters e Setters
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

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public Usuario getOrganizador() {
        return organizador;
    }

    public void setOrganizador(Usuario organizador) {
        this.organizador = organizador;
    }

    public List<Usuario> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(List<Usuario> participantes) {
        this.participantes = participantes;
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

    public boolean isPrivado() {
        return privado;
    }

    public void setPrivado(boolean privado) {
        this.privado = privado;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
    
    public String getPalestrante() {
		return palestrante;
	}

	public void setPalestrante(String palestrante) {
		this.palestrante = palestrante;
	}
    

    // Métodos auxiliares
    
   

	/**
     * Adiciona um participante ao evento caso ele não esteja já cadastrado.
     * 
     * @param usuario Objeto Usuario a ser adicionado como participante
     * @return true se o participante foi adicionado com sucesso,
     *         false se o usuário já estava na lista de participantes
     * @throws NullPointerException se o parâmetro usuario for nulo
     */
    public boolean adicionarParticipante(Usuario usuario) {
        if (usuario == null) {
            throw new NullPointerException("Usuário não pode ser nulo");
        }
        
        if (!participantes.contains(usuario)) {
            participantes.add(usuario);
            return true;
        }
        return false;
    }
    
    /**
     * Remove um participante da lista de participantes do evento.
     * 
     * @param usuario Objeto Usuario a ser removido (não pode ser nulo)
     * @return true se o participante foi encontrado e removido com sucesso,
     *         false se o usuário não estava na lista de participantes
     * @throws NullPointerException se o parâmetro usuario for nulo
     * @see #adicionarParticipante(Usuario) Método relacionado para adicionar participantes
     */
    public boolean removerParticipante(Usuario usuario) {
        Objects.requireNonNull(usuario, "O parâmetro 'usuario' não pode ser nulo");
        return participantes.remove(usuario);
    }

    
    /**
     * Retorna a quantidade de participantes do evento.
     * 
     * @return número inteiro representando a quantidade de participantes (0 se a lista estiver vazia)
     * @throws IllegalStateException se a lista de participantes não foi inicializada
     */
    public int getQuantidadeParticipantes() {
        if (participantes == null) {
            throw new IllegalStateException("Lista de participantes não inicializada");
        }
        return participantes.size();
    }
    

    /**
     * Retorna os principais atributos do evento em formato string.
     * @return String no formato: Evento[id=1, titulo='Festa', ..., participantes=5]
     */
    @Override
    public String toString() {
        return String.format(
            "Evento[id=%d, titulo='%s', data=%s, local='%s', organizador=%s, participantes=%d], palestrante='%s'",
            id,
            titulo != null ? titulo : "",
            data != null ? data : "null",
            local != null ? local : "",
            organizador != null ? organizador.getNome() : "null",
            palestrante != null ? palestrante : "",
            getQuantidadeParticipantes()
        );
    }
    
    public void setPresenca(int usuarioId, boolean presente) {
        presencas.put(usuarioId, presente);
    }

    public boolean getPresenca(int usuarioId) {
        return presencas.getOrDefault(usuarioId, false);
    }

}