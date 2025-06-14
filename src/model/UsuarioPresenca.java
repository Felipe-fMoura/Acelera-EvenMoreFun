package model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class UsuarioPresenca {
    private Usuario usuario;
    private BooleanProperty presente;

    public UsuarioPresenca(Usuario usuario, boolean presente) {
        this.usuario = usuario;
        this.presente = new SimpleBooleanProperty(presente);
    }

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

    // Adiciona propriedades nome e email para funcionar com TableView
    public StringProperty nomeProperty() {
        return new SimpleStringProperty(usuario.getNome());
    }

    public StringProperty emailProperty() {
        return new SimpleStringProperty(usuario.getEmail());
    }
}
