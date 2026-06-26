package service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import model.Serie;

// Classe responsável por realizar a comunicação
// com a API do TVMaze e converter os dados
// recebidos em objetos da classe Serie.
public class TvMazeService {

    // URL base da API do TVMaze utilizada para busca
    private static final String URL_API =
            "https://api.tvmaze.com/search/shows?q=";

    // Cliente HTTP utilizado para realizar requisições
    private final HttpClient client;

    // Construtor da classe
    public TvMazeService() {

        // Cria um cliente HTTP padrão
        client =
                HttpClient.newHttpClient();
    }

    // Método responsável por buscar séries
    // na API a partir do nome informado
    public ArrayList<Serie> buscarSeries(
            String nomeSerie) {

        // Lista que armazenará as séries encontradas
        ArrayList<Serie> lista =
                new ArrayList<>();

        try {

            // Cria a requisição HTTP GET para a API
            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(
                                            URL_API +
                                                    nomeSerie))
                            .GET()
                            .build();

            // Envia a requisição e obtém a resposta
            HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers
                                    .ofString());

            // Cria um objeto do Jackson para manipular JSON
            ObjectMapper mapper =
                    new ObjectMapper();

            // Converte o JSON recebido em uma árvore de nós
            JsonNode raiz =
                    mapper.readTree(
                            response.body());

            // Percorre todos os resultados retornados pela API
            for (JsonNode item : raiz) {

                // Obtém o objeto "show" do JSON
                JsonNode show =
                        item.get("show");

                // Cria uma nova série
                Serie serie =
                        new Serie();

                // Define o ID da série
                serie.setId(
                        show.get("id")
                                .asInt());

                // Define o nome da série
                serie.setNome(
                        show.get("name")
                                .asText());

                // Define o idioma da série
                serie.setIdioma(
                        obterTexto(
                                show,
                                "language"));

                // Verifica se existe nota cadastrada
                if (show.get("rating") != null
                        && show.get("rating")
                        .get("average") != null) {

                    // Define a nota média da série
                    serie.setNota(
                            show.get("rating")
                                    .get("average")
                                    .asDouble());
                }

                // Define o status da série
                // Exemplo: Running, Ended, Canceled
                serie.setStatus(
                        obterTexto(
                                show,
                                "status"));

                // Define a data de estreia
                serie.setEstreia(
                        obterTexto(
                                show,
                                "premiered"));

                // Define a data de término
                serie.setTermino(
                        obterTexto(
                                show,
                                "ended"));

                // Verifica se existe emissora cadastrada
                if (show.get("network")
                        != null
                        && show.get("network")
                        .get("name")
                        != null) {

                    // Define o nome da emissora
                    serie.setEmissora(
                            show.get("network")
                                    .get("name")
                                    .asText());
                }

                // Verifica se existem gêneros cadastrados
                if (show.get("genres")
                        != null) {

                    // Percorre todos os gêneros da série
                    for (JsonNode genero :
                            show.get("genres")) {

                        // Adiciona o gênero na lista
                        serie.getGeneros()
                                .add(
                                        genero.asText());
                    }
                }

                // Adiciona a série à lista de resultados
                lista.add(serie);
            }

        } catch (Exception e) {

            // Exibe mensagem caso ocorra erro na API
            JOptionPane.showMessageDialog(
                    null,
                    "Erro ao acessar a API TVMaze.");

            // Exibe o erro no console
            e.printStackTrace();
        }

        // Retorna a lista de séries encontradas
        return lista;
    }

    // Método auxiliar para obter texto do JSON
    // tratando valores nulos
    private String obterTexto(
            JsonNode node,
            String campo) {

        // Verifica se o campo não existe ou é nulo
        if (node.get(campo) == null
                || node.get(campo).isNull()) {

            // Retorna um texto padrão
            return "Não informado";
        }

        // Retorna o valor do campo como texto
        return node.get(campo)
                .asText();
    }
}