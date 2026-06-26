package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// Classe responsável por representar uma série de TV.
// Os dados desta classe são obtidos pela API do TVMaze.
public class Serie {

    // Identificador único da série na API
    private int id;

    // Nome da série
    private String nome;

    // Idioma original da série
    private String idioma;

    // Lista contendo os gêneros da série
    // Exemplo: Drama, Action, Comedy
    private List<String> generos;

    // Nota média da série
    private double nota;

    // Status da série
    // Exemplo: Running, Ended ou Canceled
    private String status;

    // Data de estreia da série
    private String estreia;

    // Data de término da série
    private String termino;

    // Nome da emissora responsável pela transmissão
    private String emissora;

    // Construtor vazio.
    // Necessário para o funcionamento do Jackson
    // ao converter JSON em objetos Java.
    public Serie() {

        // Inicializa a lista de gêneros
        generos = new ArrayList<>();
    }

    // Construtor completo da classe
    public Serie(int id, String nome, String idioma,
                 List<String> generos,
                 double nota,
                 String status,
                 String estreia,
                 String termino,
                 String emissora) {

        // Atribui os valores recebidos aos atributos
        this.id = id;
        this.nome = nome;
        this.idioma = idioma;
        this.generos = generos;
        this.nota = nota;
        this.status = status;
        this.estreia = estreia;
        this.termino = termino;
        this.emissora = emissora;
    }

    // Retorna o ID da série
    public int getId() {
        return id;
    }

    // Define o ID da série
    public void setId(int id) {
        this.id = id;
    }

    // Retorna o nome da série
    public String getNome() {
        return nome;
    }

    // Define o nome da série
    public void setNome(String nome) {
        this.nome = nome;
    }

    // Retorna o idioma da série
    public String getIdioma() {
        return idioma;
    }

    // Define o idioma da série
    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    // Retorna a lista de gêneros
    public List<String> getGeneros() {
        return generos;
    }

    // Define a lista de gêneros
    public void setGeneros(List<String> generos) {
        this.generos = generos;
    }

    // Retorna a nota média da série
    public double getNota() {
        return nota;
    }

    // Define a nota da série
    public void setNota(double nota) {
        this.nota = nota;
    }

    // Retorna o status da série
    public String getStatus() {
        return status;
    }

    // Define o status da série
    public void setStatus(String status) {
        this.status = status;
    }

    // Retorna a data de estreia
    public String getEstreia() {
        return estreia;
    }

    // Define a data de estreia
    public void setEstreia(String estreia) {
        this.estreia = estreia;
    }

    // Retorna a data de término
    public String getTermino() {
        return termino;
    }

    // Define a data de término
    public void setTermino(String termino) {
        this.termino = termino;
    }

    // Retorna o nome da emissora
    public String getEmissora() {
        return emissora;
    }

    // Define a emissora da série
    public void setEmissora(String emissora) {
        this.emissora = emissora;
    }

    // Sobrescreve o método toString().
    // Define como a série será exibida nas listas do sistema.
    @Override
    public String toString() {

        return nome +
                " | Nota: " + nota +
                " | Status: " + status;
    }

    // Sobrescreve o método equals().
    // Duas séries serão consideradas iguais
    // se possuírem o mesmo ID da API.
    @Override
    public boolean equals(Object o) {

        // Verifica se é o mesmo objeto
        if (this == o) return true;

        // Verifica se o objeto é nulo
        // ou de classe diferente
        if (o == null || getClass() != o.getClass())
            return false;

        // Realiza o cast para Serie
        Serie serie = (Serie) o;

        // Compara os IDs
        return id == serie.id;
    }

    // Sobrescreve o método hashCode().
    // Necessário quando equals() é sobrescrito.
    // Utilizado por estruturas como HashSet e HashMap.
    @Override
    public int hashCode() {

        // Gera um hash baseado no ID da série
        return Objects.hash(id);
    }
}