package fag.main.model;

/**
 * Critérios disponíveis para ordenar as listas de séries exibidas ao usuário.
 */
public enum CriterioOrdenacao {
    NOME("Nome (ordem alfabética)"),
    NOTA("Nota geral"),
    ESTADO("Estado da série"),
    DATA_ESTREIA("Data de estreia");

    private final String descricao;

    CriterioOrdenacao(String descricao) {
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
