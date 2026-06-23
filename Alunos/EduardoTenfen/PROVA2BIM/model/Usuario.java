package model;

import java.util.ArrayList;
import java.util.List;

/**
 * CLASSE MODEL: Representa o Usuário do aplicativo.
 * Guarda o nome/apelido do usuário e suas 3 listas de séries.
 * Toda a estrutura de dados do app vive aqui.
 */
public class Usuario {

    // ATRIBUTOS PRIVADOS
    private String nomeApelido; // Nome ou apelido exibido no topo da interface

    // As 3 listas de séries — cada uma é um ArrayList de objetos Serie.
    // ArrayList é uma lista dinâmica (não tem tamanho fixo como array[]).
    private List<Serie> favoritos;          // Lista "Favoritos"
    private List<Serie> jaAssistidas;       // Lista "Séries já assistidas"
    private List<Serie> desejassistir;      // Lista "Séries que deseja assistir"

    // CONSTRUTOR
    // Inicializa as listas como ArrayLists vazios para evitar NullPointerException.
    // Se não inicializássemos aqui, tentar adicionar à lista causaria crash.
    public Usuario(String nomeApelido) {
        this.nomeApelido = nomeApelido;
        this.favoritos = new ArrayList<>();        // Cria lista de favoritos vazia
        this.jaAssistidas = new ArrayList<>();     // Cria lista de já assistidas vazia
        this.desejassistir = new ArrayList<>();    // Cria lista de deseja assistir vazia
    }

    // GETTERS E SETTERS 

    public String getNomeApelido() { return nomeApelido; }
    public void setNomeApelido(String nomeApelido) { this.nomeApelido = nomeApelido; }

    // Retorna a lista de favoritos completa
    public List<Serie> getFavoritos() { return favoritos; }
    public void setFavoritos(List<Serie> favoritos) { this.favoritos = favoritos; }

    // Retorna a lista de séries já assistidas
    public List<Serie> getJaAssistidas() { return jaAssistidas; }
    public void setJaAssistidas(List<Serie> jaAssistidas) { this.jaAssistidas = jaAssistidas; }

    // Retorna a lista de séries que deseja assistir
    public List<Serie> getDesejassistir() { return desejassistir; }
    public void setDesejassistir(List<Serie> desejassistir) { this.desejassistir = desejassistir; }
}