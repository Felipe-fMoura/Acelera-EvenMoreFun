/*
 * Classe Usuario – representa um usuário do sistema com seus dados pessoais,
 * informações de login e eventos que organiza ou participa.
 *
 * Estruturas e conceitos utilizados:
 * - Armazena atributos como nome, email, senha, telefone, CPF, gênero e data de nascimento.
 * - Utiliza listas do tipo ArrayList para gerenciar eventos organizados e eventos participados.
 * - Usa Collections.unmodifiableList() para proteger a integridade das listas retornadas.
 *
 * Métodos principais:
 *
 * - Usuario()  
 *   Construtor padrão que inicializa as listas de eventos organizados e participando.
 *
 * - Usuario(String nome, String sobrenome, String username, String email, String senha)  
 *   Construtor com campos obrigatórios, valida dados essenciais e define imagem padrão de perfil.
 *
 * - Getters e Setters  
 *   Acessam e modificam os dados pessoais do usuário como nome, email, senha, telefone, CPF, etc.
 *
 * - getEventosParticipando(), getEventosOrganizados()  
 *   Retornam listas imutáveis com os eventos em que o usuário participa ou organiza.
 *
 * - participarEvento(Evento), cancelarParticipacao(Evento)  
 *   Adiciona ou remove um evento da lista de participações do usuário.
 *
 * - organizarEvento(Evento)  
 *   Registra um evento como organizado pelo usuário e define o organizador no evento.
 *
 * - getNomeCompleto()  
 *   Retorna nome completo concatenando nome e sobrenome.
 *
 * - equals(Object), hashCode()  
 *   Comparam usuários com base no ID e no email, e geram hash correspondente.
 *
 * - toString()  
 *   Gera uma string com as principais informações do usuário para exibição ou debug.
 *
 * - getCaminhoFotoPerfil(), setCaminhoFotoPerfil(String)  
 *   Retorna ou define o caminho da imagem de perfil, com fallback para imagem padrão.
 */

package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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

	public Usuario() {
		this.eventosParticipando = new ArrayList<>();
		this.eventosOrganizados = new ArrayList<>();
	}

	public Usuario(String nome, String sobrenome, String username, String email, String senha) {
		this();

		this.nome = Objects.requireNonNull(nome, "Nome não pode ser nulo");
		this.sobrenome = Objects.requireNonNull(sobrenome, "Sobrenome não pode ser nulo");
		this.username = Objects.requireNonNull(username, "Username não pode ser nulo");
		this.email = Objects.requireNonNull(email, "Email não pode ser nulo");
		this.senha = Objects.requireNonNull(senha, "Senha não pode ser nula");
		this.caminhoFotoPerfil = getClass().getResource("/resources/profile/iconFotoPerfilDefault.png")
				.toExternalForm(); // Valor padrão

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

	
	public List<Evento> getEventosParticipando() {
		return Collections.unmodifiableList(eventosParticipando);
	}

	
	public List<Evento> getEventosOrganizados() {
		return Collections.unmodifiableList(eventosOrganizados);
	}

	
	public boolean participarEvento(Evento evento) {
		if (evento == null) {
			throw new IllegalArgumentException("Evento não pode ser nulo");
		}
		if (!eventosParticipando.contains(evento)) {
			eventosParticipando.add(evento);
			return true;
		}
		return false;
	}

	
	public boolean cancelarParticipacao(Evento evento) {
		if (evento == null) {
			throw new IllegalArgumentException("Evento não pode ser nulo");
		}
		boolean removido = eventosParticipando.remove(evento);
		if (removido) {
			evento.removerParticipante(this);
		}
		return removido;
	}

	
	public boolean organizarEvento(Evento evento) {
		if (evento == null) {
			throw new IllegalArgumentException("Evento não pode ser nulo");
		}
		if (!eventosOrganizados.contains(evento)) {
			eventosOrganizados.add(evento);
			if (evento.getOrganizador() != this) {
				evento.setOrganizador(this);
			}
			return true;
		}
		return false;
	}

	
	public String getNomeCompleto() {
		return (nome != null ? nome : "") + (sobrenome != null ? " " + sobrenome : "");
	}

	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Usuario usuario = (Usuario) o;
		return id == usuario.id && Objects.equals(email, usuario.email);
	}

	
	@Override
	public int hashCode() {
		return Objects.hash(id, email);
	}

	
	@Override
	public String toString() {
		return "Usuario{" + "id=" + id + ", username='" + username + '\'' + ", email='" + email + '\''
				+ ", nomeCompleto='" + getNomeCompleto() + '\'' + '}';
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
	
	//badge
	private List<Badge> badges = new ArrayList<>();

	public List<Badge> getBadges() {
	    return badges;
	}

	public void adicionarBadge(Badge badge) {
	    if (!badges.contains(badge)) {
	        badges.add(badge);
	    }
	}


}
