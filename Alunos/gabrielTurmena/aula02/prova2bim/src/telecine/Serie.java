package telecine;

import java.util.List;

public class Serie {
	private Integer id;
    private String nome;
    private String idioma;
    private List<String> generos;
    private Double notaGeral;
    private String estado;
    private String dataEstreia;
    private String dataTermino;
    private String emissora;

 // construtor da classe ele armazena os valores da mesma dentro do objeto
    public Serie(Integer id, String nome, String idioma, List<String> generos, Double notaGeral, String estado, String dataEstreia, String dataTermino, String emissora){
    	
        this.id = id;
        this.nome = nome;
        this.idioma = idioma;
        this.generos = generos;
        this.notaGeral = notaGeral;
        this.estado = estado;
        this.dataEstreia = dataEstreia;
        this.dataTermino = dataTermino;
        this.emissora = emissora;
    	
    }
    
    public Serie(){
    	
    }
 // set para setar nome de série quando a classe receber
   //da mesma forma que tem o setter de set nome tem o getter que entrega o nome quando chamado um método
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public Double getNotaGeral() {
        return notaGeral;
    }

    public void setNotaGeral(Double notaGeral) {
        this.notaGeral = notaGeral;
    }
    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }
    
    public List<String> getGenero() {
        return generos;
    }

    public void setGeneros(List<String> generos) {
        this.generos = generos;
    }
    
    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public String getDataEstreia() {
        return dataEstreia;
    }

    public void setDataEstreia(String dataEstreia) {
        this.dataEstreia = dataEstreia;
    }
    
    public String getDataTermino() {
        return dataTermino;
    }

    public void setDataTermino(String dataTermino) {
        this.dataTermino = dataTermino;
    }
    
    public String getEmissora() {
        return emissora;
    }

    public void setEmissora(String emissora) {
        this.emissora = emissora;
    }
    
@Override
    public String toString() {
        return "Serie{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", idioma='" + idioma + '\'' +
                ", generos=" + generos +
                ", notaGeral=" + notaGeral +
                ", estado='" + estado + '\'' +
                ", dataEstreia='" + dataEstreia + '\'' +
                ", dataTermino='" + dataTermino + '\'' +
                ", emissora='" + emissora + '\'' +
                '}';
    }
    
}
