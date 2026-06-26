package model;

import java.util.ArrayList;
import java.util.List;

// Classe responsável por armazenar todos os dados
// do usuário da aplicação.
public class DadosUsuario {

    // Objeto contendo as informações do usuário
    // (nome e apelido)
    private Usuario usuario;

    // Lista de séries favoritas do usuário
    private List<Serie> favoritos;

    // Lista de séries já assistidas pelo usuário
    private List<Serie> assistidas;

    // Lista de séries que o usuário deseja assistir
    private List<Serie> desejoAssistir;

    // Construtor da classe
    public DadosUsuario() {

        // Inicializa a lista de favoritos vazia
        favoritos = new ArrayList<>();

        // Inicializa a lista de assistidas vazia
        assistidas = new ArrayList<>();

        // Inicializa a lista de desejo vazia
        desejoAssistir = new ArrayList<>();
    }

    // Retorna o usuário da aplicação
    public Usuario getUsuario() {
        return usuario;
    }

    // Define o usuário da aplicação
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    // Retorna a lista de favoritos
    public List<Serie> getFavoritos() {
        return favoritos;
    }

    // Define a lista de favoritos
    public void setFavoritos(List<Serie> favoritos) {
        this.favoritos = favoritos;
    }

    // Retorna a lista de séries assistidas
    public List<Serie> getAssistidas() {
        return assistidas;
    }

    // Define a lista de séries assistidas
    public void setAssistidas(List<Serie> assistidas) {
        this.assistidas = assistidas;
    }

    // Retorna a lista de séries desejadas
    public List<Serie> getDesejoAssistir() {
        return desejoAssistir;
    }

    // Define a lista de séries desejadas
    public void setDesejoAssistir(List<Serie> desejoAssistir) {
        this.desejoAssistir = desejoAssistir;
    }

    // Adiciona uma série aos favoritos
    public void adicionarFavorito(Serie serie) {

        // Verifica se a série já não existe na lista
        if (!favoritos.contains(serie)) {

            // Adiciona a série aos favoritos
            favoritos.add(serie);
        }
    }

    // Remove uma série dos favoritos
    public void removerFavorito(Serie serie) {

        // Remove a série da lista
        favoritos.remove(serie);
    }

    // Adiciona uma série à lista de assistidas
    public void adicionarAssistida(Serie serie) {

        // Verifica se a série ainda não está cadastrada
        if (!assistidas.contains(serie)) {

            // Adiciona a série na lista
            assistidas.add(serie);
        }
    }

    // Remove uma série da lista de assistidas
    public void removerAssistida(Serie serie) {

        // Remove a série da lista
        assistidas.remove(serie);
    }

    // Adiciona uma série à lista de desejo
    public void adicionarDesejo(Serie serie) {

        // Verifica se a série ainda não existe na lista
        if (!desejoAssistir.contains(serie)) {

            // Adiciona a série na lista
            desejoAssistir.add(serie);
        }
    }

    // Remove uma série da lista de desejo
    public void removerDesejo(Serie serie) {

        // Remove a série da lista
        desejoAssistir.remove(serie);
    }
}