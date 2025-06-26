package model;

public class Badge {
    private String nome;
    private String iconePath;  // agora é o path completo (file:/...)

    private String descricao;

    public Badge(String nome, String iconePath, String descricao) {
        this.nome = nome;
        this.iconePath = iconePath;
        this.descricao = descricao;
    }

    public String getNome() { return nome; }

    public String getIconePath() {
        return iconePath;
    }

    public String getDescricao() { return descricao; }
}
