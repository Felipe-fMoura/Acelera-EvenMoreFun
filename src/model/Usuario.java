package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Representa um usuário do sistema com seus dados pessoais e relacionamento com eventos.
 * Permite gerenciar eventos organizados e participações em eventos.
 */
public class Usuario {
	
    private int id;
    private String username;
    private String nome;
    private String sobrenome;
    private String email;
    private String senha;
    private String telefone;
    private String cpf;
    private String genero;
    private LocalDate dataNascimento;
    private final List<Evento> eventosParticipando;
    private final List<Evento> eventosOrganizados;
    private String caminhoFotoPerfil; 


    /**
     * Construtor padrão que inicializa as listas de eventos.
     * As listas são inicializadas vazias e imutáveis quanto à instância (final).
     * As referências a essas listas não podem ser trocadas após a construção do objeto.
     */
    public Usuario() {
        this.eventosParticipando = new ArrayList<>();
        this.eventosOrganizados = new ArrayList<>();
    }

    /**
     * Construtor para criação básica de usuário com validação de campos obrigatórios,
     * para a primeira parte do cadastro.
     * 
     * @param username Identificador único do usuário (não pode ser nulo)
     * @param email Email do usuário (não pode ser nulo)
     * @param senha Senha do usuário (não pode ser nula)
     * @throws NullPointerException se qualquer parâmetro obrigatório for nulo
     */
    public Usuario(String nome, String sobrenome,String username, String email, String senha) {
        this();
        
        this.nome = Objects.requireNonNull(nome, "Nome não pode ser nulo");
        this.sobrenome = Objects.requireNonNull(sobrenome, "Sobrenome não pode ser nulo");       
        this.username = Objects.requireNonNull(username, "Username não pode ser nulo");
        this.email = Objects.requireNonNull(email, "Email não pode ser nulo");
        this.senha = Objects.requireNonNull(senha, "Senha não pode ser nula");
        this.caminhoFotoPerfil = getClass().getResource("/resources/profile/iconFotoPerfilDefault.png").toExternalForm(); // Valor padrão

        
        
    }

	// Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = Objects.requireNonNull(username, "Username não pode ser nulo");
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome; // Pode ser nulo se não for obrigatório
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome; // Pode ser nulo se não for obrigatório
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = Objects.requireNonNull(email, "Email não pode ser nulo");
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = Objects.requireNonNull(senha, "Senha não pode ser nula");
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone; // Pode ser nulo se não for obrigatório
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf; // Pode ser nulo se não for obrigatório
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero; // Pode ser nulo se não for obrigatório
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento; // Pode ser nulo se não for obrigatório
    }

    // Métodos para gerenciamento de eventos
    
    
    
    /**
     * Retorna uma visão imutável da lista de eventos que o usuário está participando.
     * 
     * <p>A lista retornada é um "wrapper" imutável ao redor da lista original. Isso significa que:</p>
     * <ul>
     *   <li><b>Não é possível modificar</b> a lista retornada (add/remove/set lançam UnsupportedOperationException)</li>
     *   <li><b>Reflete automaticamente</b> alterações feitas na lista interna do usuário</li>
     * </ul>
     * 
     * <p>Exemplo de uso:</p>
     * <pre>{@code
     * for (Evento evento : usuario.getEventosParticipando()) {
     *     System.out.println(evento.getTitulo());
     * }
     * }</pre>
     * 
     * @return Lista <b>não-modificável</b> (mas sempre atualizada) dos eventos participados
     * @see Collections#unmodifiableList(List)
     */
    public List<Evento> getEventosParticipando() {
        return Collections.unmodifiableList(eventosParticipando);
    }

    
    
    /**
     * Retorna uma visão imutável da lista de eventos que o usuário está organizando.
     * 
     * <p>Características importantes:</p>
     * <ul>
     *   <li><b>Protege o encapsulamento</b>: previne que listas internas sejam modificadas externamente</li>
     *   <li><b>Thread-safe</b>: a lista imutável pode ser compartilhada entre threads sem risco de concorrência</li>
     *   <li><b>Eficiente</b>: não cria cópia dos dados, apenas uma "view" imutável</li>
     * </ul>
     * 
     * @return Lista <b>somente-leitura</b> dos eventos organizados
     * @see #getEventosParticipando() 
     */
    public List<Evento> getEventosOrganizados() {
        return Collections.unmodifiableList(eventosOrganizados);
    }

    
    
    
    
    /**
     * Adiciona um evento à lista de participações do usuário.
     * @param evento Evento a ser participado (não pode ser nulo)
     * @return true se a participação foi registrada com sucesso, false se já participava
     * @throws IllegalArgumentException se o evento for nulo
     */
    public boolean participarEvento(Evento evento) {
        if (evento == null) throw new IllegalArgumentException("Evento não pode ser nulo");
        if (!eventosParticipando.contains(evento)) {
            eventosParticipando.add(evento);
            return true;
        }
        return false;
    }

    /**
     * Remove uma participação em evento.
     * @param evento Evento a ser removido (não pode ser nulo)
     * @return true se a participação foi cancelada, false se não estava participando
     * @throws IllegalArgumentException se o evento for nulo
     */
    public boolean cancelarParticipacao(Evento evento) {
        if (evento == null) throw new IllegalArgumentException("Evento não pode ser nulo");
        boolean removido = eventosParticipando.remove(evento);
        if (removido) {
            evento.removerParticipante(this);
        }
        return removido;
    }

    /**
     * Registra um evento como organizado pelo usuário.
     * @param evento Evento a ser organizado (não pode ser nulo)
     * @return true se o evento foi registrado, false se já era organizador
     * @throws IllegalArgumentException se o evento for nulo
     */
    public boolean organizarEvento(Evento evento) {
        if (evento == null) throw new IllegalArgumentException("Evento não pode ser nulo");
        if (!eventosOrganizados.contains(evento)) {
            eventosOrganizados.add(evento);
            if (evento.getOrganizador() != this) {
                evento.setOrganizador(this);
            }
            return true;
        }
        return false;
    }

    /**
     * Retorna o nome completo concatenando nome e sobrenome.
     * @return String formatada "Nome Sobrenome" ou "Nome" se sobrenome for nulo
     */
    public String getNomeCompleto() {
        return (nome != null ? nome : "") + 
               (sobrenome != null ? " " + sobrenome : "");
    }

    /**
     * Compara usuários por ID e email.
     * @param o Objeto a ser comparado
     * @return true se os usuários forem equivalentes
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return id == usuario.id && Objects.equals(email, usuario.email);
    }

    /**
     * Gera hash code baseado em ID e email.
     * @return Valor hash para o usuário
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    /**
     * Retorna representação string do usuário (teste).
     * @return String no formato: Usuario{id=1, username='john', email='john@example.com', nomeCompleto='John Doe'}
     */
    @Override
    public String toString() {
        return "Usuario{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", email='" + email + '\'' +
               ", nomeCompleto='" + getNomeCompleto() + '\'' +
               '}';
    }
    
    public String getCaminhoFotoPerfil() {
        return caminhoFotoPerfil;
    }

    public void setCaminhoFotoPerfil(String caminhoFotoPerfil) {
        if (caminhoFotoPerfil == null || caminhoFotoPerfil.isBlank()) {
            this.caminhoFotoPerfil = "/resources/profile/iconFotoPerfilDefault.png";
        } else {
            this.caminhoFotoPerfil = caminhoFotoPerfil;
        }
    }

}
