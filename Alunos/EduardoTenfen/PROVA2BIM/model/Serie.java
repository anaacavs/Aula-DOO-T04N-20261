package model;

/**
 * CLASSE MODEL: Representa uma Série de TV.
 * Em POO, o Model é o "molde" (classe) que guarda os dados de um objeto.
 * Segue o princípio de Encapsulamento: todos os campos são 'private'
 * e acessados apenas por métodos públicos (getters e setters).
 */
public class Serie {

    // ATRIBUTOS PRIVADOS 
    // Cada atributo é 'private' para que nenhuma outra classe acesse diretamente,
    // forçando o uso dos getters/setters (encapsulamento).

    private int idTvMaze;           // ID único da série na API TVMaze
    private String nome;            // Nome/título da série
    private String idioma;          // Idioma original da série
    private String generos;         // Gêneros separados por vírgula (ex: "Drama, Crime")
    private double notaGeral;       // Nota de 0 a 10 (rating médio da TVMaze)
    private String status;          // Status: "Running", "Ended", "To Be Determined", etc.
    private String dataEstreia;     // Data de estreia no formato YYYY-MM-DD
    private String dataTermino;     // Data de término (pode ser nula/vazia se ainda está no ar)
    private String emissora;        // Nome da emissora/network (ex: "HBO", "Netflix")
    private String imagemUrl;       // URL da imagem/thumbnail da série (para exibição)
    private String resumo;          // Resumo/sinopse da série em HTML

    //  CONSTRUTOR PADRÃO 
    // Construtor sem argumentos exigido para criar objetos "vazios"
    // e depois preencher via setters (útil ao ler do arquivo JSON).
    public Serie() {
    }

    //  CONSTRUTOR COMPLETO
    // Permite criar um objeto já totalmente preenchido de uma vez.
    // Usado principalmente ao parsear os dados vindos da API TVMaze.
    public Serie(int idTvMaze, String nome, String idioma, String generos,
                 double notaGeral, String status, String dataEstreia,
                 String dataTermino, String emissora, String imagemUrl, String resumo) {
        this.idTvMaze = idTvMaze;
        this.nome = nome;
        this.idioma = idioma;
        this.generos = generos;
        this.notaGeral = notaGeral;
        this.status = status;
        this.dataEstreia = dataEstreia;
        this.dataTermino = dataTermino;
        this.emissora = emissora;
        this.imagemUrl = imagemUrl;
        this.resumo = resumo;
    }

    // GETTERS E SETTERS
    // Getters: retornam o valor do atributo privado (leitura).
    // Setters: alteram o valor do atributo privado (escrita).
    // Isso é encapsulamento: controlamos o acesso aos dados.

    public int getIdTvMaze() { return idTvMaze; }
    public void setIdTvMaze(int idTvMaze) { this.idTvMaze = idTvMaze; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getIdioma() { return idioma; }
    public void setIdioma(String idioma) { this.idioma = idioma; }

    public String getGeneros() { return generos; }
    public void setGeneros(String generos) { this.generos = generos; }

    public double getNotaGeral() { return notaGeral; }
    public void setNotaGeral(double notaGeral) { this.notaGeral = notaGeral; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDataEstreia() { return dataEstreia; }
    public void setDataEstreia(String dataEstreia) { this.dataEstreia = dataEstreia; }

    public String getDataTermino() { return dataTermino; }
    public void setDataTermino(String dataTermino) { this.dataTermino = dataTermino; }

    public String getEmissora() { return emissora; }
    public void setEmissora(String emissora) { this.emissora = emissora; }

    public String getImagemUrl() { return imagemUrl; }
    public void setImagemUrl(String imagemUrl) { this.imagemUrl = imagemUrl; }

    public String getResumo() { return resumo; }
    public void setResumo(String resumo) { this.resumo = resumo; }

    // MÉTODO toString 
    // Sobrescreve o toString padrão do Object para retornar o nome da série.
    // Útil para exibição em JList e outros componentes Swing que chamam toString().
    @Override
    public String toString() {
        return nome != null ? nome : "Série sem nome";
    }

    // MÉTODO equals 
    // Sobrescreve equals para comparar séries pelo ID da TVMaze.
    // Assim, evitamos duplicatas nas listas verificando se o ID já existe.
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;         // Se é o mesmo objeto na memória, são iguais
        if (!(obj instanceof Serie)) return false; // Se não é uma Serie, não são iguais
        Serie outra = (Serie) obj;
        return this.idTvMaze == outra.idTvMaze; // Igualdade baseada no ID único da TVMaze
    }

    // MÉTODO hashCode 
    // Sempre que sobrescrevemos equals, devemos sobrescrever hashCode também.
    // Isso garante consistência ao usar objetos Serie em coleções como HashSet.
    @Override
    public int hashCode() {
        return Integer.hashCode(idTvMaze); // O hash é baseado no ID, assim como o equals
    }
}