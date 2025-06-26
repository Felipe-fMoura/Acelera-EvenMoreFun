package model;

import java.util.Objects;

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
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Badge badge = (Badge) obj;
        return Objects.equals(nome, badge.nome); // Pode adicionar outros campos se necessário
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome); // Mesmos campos do equals
    }

    
}
