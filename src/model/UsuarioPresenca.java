/*
 * Métodos relevantes criados:
 * - UsuarioPresenca(Usuario usuario, boolean presente, String permissao) (construtor com permissão)
 * - UsuarioPresenca(Usuario usuario, boolean presente) (construtor padrão com permissão "participante")
 * - Getters e setters para propriedades: presente, nome, email, permissao
 * - propriedades JavaFX: BooleanProperty presente, StringProperty nome, email, permissao
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
    private final StringProperty permissao;  // nova propriedade para armazenar a permissão

    // Construtor com permissão explícita
    public UsuarioPresenca(Usuario usuario, boolean presente, String permissao) {
        this.usuario = usuario;
        this.presente = new SimpleBooleanProperty(presente);
        this.nome = new SimpleStringProperty(usuario.getNome());
        this.email = new SimpleStringProperty(usuario.getEmail());
        this.permissao = new SimpleStringProperty(permissao.toLowerCase()); // padroniza minúsculo
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
}
