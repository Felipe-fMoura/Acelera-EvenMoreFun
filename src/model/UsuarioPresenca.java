/*
 * Classe UsuarioPresenca – representa a presença de um usuário em um evento,
 * com dados pessoais e controle via propriedades reativas do JavaFX.
 *
 * Estruturas e conceitos utilizados:
 * - Usa propriedades do JavaFX (StringProperty, BooleanProperty) para facilitar binding com a interface gráfica.
 * - Armazena dados do usuário como nome, email, telefone, CPF, data de nascimento e presença.
 *
 * Métodos relevantes criados:
 *
 * - UsuarioPresenca(Usuario usuario, boolean presente, String permissao)  
 *   Construtor que inicializa os dados e define a permissão explicitamente.
 *
 * - UsuarioPresenca(Usuario usuario, boolean presente)  
 *   Construtor alternativo que define permissão padrão como "participante".
 *
 * - getUsuario()  
 *   Retorna o objeto Usuario associado.
 *
 * - isPresente(), setPresente(boolean), presenteProperty()  
 *   Verifica, altera ou expõe a propriedade booleana de presença.
 *
 * - nomeProperty(), emailProperty(), telefoneProperty(), cpfProperty(), dataNascimentoProperty()  
 *   Expõem propriedades reativas com os dados do usuário para uso em telas JavaFX.
 *
 * - getPermissao(), setPermissao(String), permissaoProperty()  
 *   Permite acesso e modificação à permissão do usuário no evento (ex: participante, organizador).
 */

package model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class UsuarioPresenca {

	private final Usuario usuario;
	private final BooleanProperty presente;
	private final StringProperty nome;
	private final StringProperty email;
	private final StringProperty permissao; // nova propriedade para armazenar a permissão
	private final StringProperty telefone = new SimpleStringProperty();
	private final StringProperty cpf = new SimpleStringProperty();
	private final StringProperty dataNascimento = new SimpleStringProperty();

	// Construtor com permissão explícita
	public UsuarioPresenca(Usuario usuario, boolean presente, String permissao) {
		this.usuario = usuario;
		this.presente = new SimpleBooleanProperty(presente);
		this.nome = new SimpleStringProperty(usuario.getNome());
		this.email = new SimpleStringProperty(usuario.getEmail());
		this.permissao = new SimpleStringProperty(permissao.toLowerCase()); // padroniza minúsculo
		this.telefone.set(usuario.getTelefone());
		this.cpf.set(usuario.getCpf());
		this.dataNascimento.set(usuario.getDataNascimento() != null ? usuario.getDataNascimento().toString() : "");
	}

	// Construtor antigo para compatibilidade, padrão "participante"
	public UsuarioPresenca(Usuario usuario, boolean presente) {
		this(usuario, presente, "participante");
	}

	// Getters e setters

	public Usuario getUsuario() {
		return usuario;
	}

	public boolean isPresente() {
		return presente.get();
	}

	public void setPresente(boolean value) {
		presente.set(value);
	}

	public BooleanProperty presenteProperty() {
		return presente;
	}

	public StringProperty nomeProperty() {
		return nome;
	}

	public StringProperty emailProperty() {
		return email;
	}

	public StringProperty permissaoProperty() {
		return permissao;
	}

	public String getPermissao() {
		return permissao.get();
	}

	public void setPermissao(String permissao) {
		this.permissao.set(permissao.toLowerCase());
	}

	public StringProperty telefoneProperty() {
		return telefone;
	}

	public StringProperty cpfProperty() {
		return cpf;
	}

	public StringProperty dataNascimentoProperty() {
		return dataNascimento;
	}

}
