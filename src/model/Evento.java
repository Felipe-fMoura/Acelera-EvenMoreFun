package model;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Representa um evento criado por um organizador, contendo título, descrição, data, local,
 * participantes e controle de presença, acesso e vídeo.
 */
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

    private LocalDateTime data;
    private LocalDateTime dataCriacao;

    private boolean privado;

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

    public Evento(String titulo, String descricao, LocalDateTime data, String local, Usuario organizador, String palestrante) {
        this();
        this.titulo = titulo;
        this.descricao = descricao;
        this.data = data;
        this.local = local;
        this.organizador = organizador;
        this.palestrante = palestrante;
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

    // Representação textual
    @Override
    public String toString() {
        return String.format(
            "Evento[id=%d, titulo='%s', data=%s, local='%s', organizador='%s', palestrante='%s', participantes=%d]",
            id,
            titulo != null ? titulo : "",
            data != null ? data.toString() : "null",
            local != null ? local : "",
            organizador != null ? organizador.getNome() : "null",
            palestrante != null ? palestrante : "",
            getQuantidadeParticipantes()
        );
    }
}
