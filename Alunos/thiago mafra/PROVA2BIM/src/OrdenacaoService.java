package service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import model.Serie;

// Classe utilitária responsável por realizar
// diferentes tipos de ordenação das listas de séries.
public class OrdenacaoService {

    // Construtor privado para impedir a criação
    // de objetos desta classe.
    // Como todos os métodos são estáticos,
    // não é necessário instanciar a classe.
    private OrdenacaoService() {
    }

    // Ordena a lista em ordem alfabética pelo nome da série
    public static void ordenarPorNome(List<Serie> lista) {

        Collections.sort(
                lista,

                // Utiliza Comparator para comparar
                // os nomes ignorando letras maiúsculas e minúsculas
                Comparator.comparing(
                        Serie::getNome,
                        String.CASE_INSENSITIVE_ORDER));
    }

    // Ordena a lista pela nota da série
    // da maior nota para a menor
    public static void ordenarPorNota(List<Serie> lista) {

        Collections.sort(
                lista,

                // Ordena pela nota e inverte a ordem
                // usando reversed()
                Comparator.comparing(
                        Serie::getNota)
                        .reversed());
    }

    // Ordena a lista pelo status da série
    // Exemplo: Running, Ended, Canceled
    public static void ordenarPorStatus(List<Serie> lista) {

        Collections.sort(
                lista,

                // Ordena alfabeticamente pelo status
                Comparator.comparing(
                        Serie::getStatus));
    }

    // Ordena a lista pela data de estreia
    public static void ordenarPorEstreia(List<Serie> lista) {

        Collections.sort(
                lista,

                // Ordena pela data de estreia
                // Como a API retorna datas no formato
                // AAAA-MM-DD, a ordenação String funciona corretamente
                Comparator.comparing(
                        Serie::getEstreia));
    }
}