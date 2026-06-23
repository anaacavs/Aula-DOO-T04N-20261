
public class Serie {

    // ── Atributos privados (encapsulamento: só acessíveis pelos getters) ──────────
    private int    id;          // ID único da série na API TVMaze
    private String nome;        // Nome da série (ex: "Breaking Bad")
    private String idioma;      // Idioma original (ex: "English")
    private String generos;     // Gêneros separados por vírgula (ex: "Drama, Crime")
    private double nota;        // Nota de 0 a 10 (rating.average da API)
    private String status;      // Estado: "Running", "Ended", "Canceled", "To Be Determined"
    private String dataEstreia; // Data de estreia no formato "YYYY-MM-DD"
    private String dataFim;     // Data de encerramento (null se ainda transmitindo)
    private String emissora;    // Nome da emissora/rede (ex: "AMC")
    private String resumo;      // Resumo/sinopse (vem com tags HTML da API)
    private String imagemUrl;   // URL do poster da série

    // ── Construtor completo ───────────────────────────────────────────────────────
 
    public Serie(int id, String nome, String idioma, String generos,
                 double nota, String status, String dataEstreia,
                 String dataFim, String emissora, String resumo, String imagemUrl) {
        this.id          = id;
        this.nome        = nome;
        this.idioma      = idioma;
        this.generos     = generos;
        this.nota        = nota;
        this.status      = status;
        this.dataEstreia = dataEstreia;
        this.dataFim     = dataFim;
        this.emissora    = emissora;
        this.resumo      = resumo;
        this.imagemUrl   = imagemUrl;
    }

    // ── Getters 
    public int    getId()          { return id; }
    public String getNome()        { return nome; }
    public String getIdioma()      { return idioma; }
    public String getGeneros()     { return generos; }
    public double getNota()        { return nota; }
    public String getStatus()      { return status; }
    public String getDataEstreia() { return dataEstreia; }
    public String getDataFim()     { return dataFim; }
    public String getEmissora()    { return emissora; }
    public String getResumo()      { return resumo; }
    public String getImagemUrl()   { return imagemUrl; }

    // ── Métodos utilitários ───────────────────────────────────────────────────────

  
    public String getStatusTraduzido() {
        switch (status) {
            case "Running":          return "Em exibição";
            case "Ended":            return "Encerrada";
            case "Canceled":         return "Cancelada";
            case "To Be Determined": return "Indefinido";
            default:                 return status; // retorna o original se não reconhecer
        }
    }

  
    public String getResumoLimpo() {
        if (resumo == null || resumo.isEmpty()) return "Sem descrição disponível.";
        return resumo.replaceAll("<[^>]*>", "").trim();
    }

  
    @Override
    public String toString() {
        return nome + " (" + dataEstreia + ") - " + getStatusTraduzido();
    }

    /**
     * equals(): dois objetos Serie são considerados iguais se tiverem o mesmo ID.
     * Necessário para que contains(), remove() e add() das listas funcionem corretamente
     * e evitem duplicatas.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;              // mesmo objeto na memória
        if (!(obj instanceof Serie)) return false; // tipo diferente
        Serie outra = (Serie) obj;
        return this.id == outra.id;                // compara pelo ID único da API
    }

    /**
     * hashCode(): obrigatório sempre que equals() é sobrescrito.
     * Garante que HashMap, HashSet e outras coleções hash funcionem corretamente.
     * Como equals() compara pelo id, hashCode() também usa o id.
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
