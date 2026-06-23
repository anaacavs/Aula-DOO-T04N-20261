package fag.main.model;

/**
 * Representa os tipos de listas pessoais que o usuário pode manter.
 * Cada série pode pertencer a zero, uma ou mais dessas listas ao mesmo tempo.
 */
public enum ListaTipo {
    FAVORITOS("Favoritos"),
    ASSISTIDAS("Já assistidas"),
    DESEJA_ASSISTIR("Quero assistir");

    private final String descricao;

    ListaTipo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
