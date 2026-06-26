package model;

// Classe responsável por representar o usuário
// que utiliza o sistema.
public class Usuario {

    // Nome do usuário
    private String nome;

    // Apelido do usuário
    private String apelido;

    // Construtor vazio.
    // Necessário para o funcionamento do Jackson
    // durante a leitura do arquivo JSON.
    public Usuario() {
    }

    // Construtor que recebe nome e apelido
    public Usuario(String nome, String apelido) {

        // Atribui o nome recebido ao atributo da classe
        this.nome = nome;

        // Atribui o apelido recebido ao atributo da classe
        this.apelido = apelido;
    }

    // Retorna o nome do usuário
    public String getNome() {
        return nome;
    }

    // Define ou altera o nome do usuário
    public void setNome(String nome) {
        this.nome = nome;
    }

    // Retorna o apelido do usuário
    public String getApelido() {
        return apelido;
    }

    // Define ou altera o apelido do usuário
    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    // Sobrescreve o método toString().
    // Define como o objeto será exibido em texto.
    @Override
    public String toString() {

        // Retorna o nome seguido do apelido
        // entre parênteses
        return nome + " (" + apelido + ")";
    }
}