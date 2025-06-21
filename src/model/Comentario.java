package model;

public class Comentario {
    private String texto;
    private int usuarioId;

    public Comentario(String texto, int usuarioId) {
        this.texto = texto;
        this.usuarioId = usuarioId;
    }

    public String getTexto() {
        return texto;
    }

    public int getUsuarioId() {
        return usuarioId;
    }
}
