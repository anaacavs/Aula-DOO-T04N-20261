package fag.main.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import fag.main.model.SearchResult;
import fag.main.model.Show;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsável por toda a comunicação com a API pública da TVmaze
 * (https://www.tvmaze.com/api). Centraliza as chamadas HTTP e a conversão
 * (desserialização) das respostas JSON para os objetos do domínio da
 * aplicação, usando a biblioteca Jackson.
 *
 * Esta classe segue o princípio de responsabilidade única: ela só conhece
 * "como conversar com a TVmaze", e não sabe nada sobre listas de usuário,
 * persistência local ou interface gráfica.
 */
public class TvMazeClient {

    private static final String URL_BASE = "https://api.tvmaze.com";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public TvMazeClient() {
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
        this.objectMapper = new ObjectMapper();
        // Ignora campos desconhecidos do JSON que não foram mapeados nas
        // classes do domínio, tornando o parsing mais tolerante a mudanças
        // futuras na API.
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Busca séries pelo nome (busca "fuzzy", igual à busca usada no site
     * da TVmaze). Retorna uma lista vazia caso nenhuma série seja encontrada.
     *
     * @param nomeBusca termo digitado pelo usuário
     * @return lista de séries encontradas, ordenadas por relevância (igual à API)
     * @throws ApiException se houver falha de rede, timeout ou resposta inválida
     */
    public List<Show> buscarSeriesPorNome(String nomeBusca) throws ApiException {
        if (nomeBusca == null || nomeBusca.isBlank()) {
            return new ArrayList<>();
        }

        String query = URLEncoder.encode(nomeBusca.trim(), StandardCharsets.UTF_8);
        String url = URL_BASE + "/search/shows?q=" + query;

        String corpoResposta = executarRequisicaoGet(url);

        try {
            List<SearchResult> resultados = objectMapper.readValue(
                    corpoResposta,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, SearchResult.class)
            );

            List<Show> series = new ArrayList<>();
            for (SearchResult resultado : resultados) {
                if (resultado.getShow() != null) {
                    series.add(resultado.getShow());
                }
            }
            return series;
        } catch (IOException e) {
            throw new ApiException("Não foi possível interpretar a resposta da TVmaze ao buscar séries.", e);
        }
    }

    /**
     * Busca os dados completos e mais atualizados de uma série a partir do
     * seu identificador na TVmaze. Útil para "atualizar" informações de uma
     * série que já está em uma das listas do usuário.
     *
     * @param idSerie identificador da série na TVmaze
     * @return objeto Show com as informações completas
     * @throws ApiException se houver falha de rede, timeout ou série não encontrada
     */
    public Show buscarSeriePorId(int idSerie) throws ApiException {
        String url = URL_BASE + "/shows/" + idSerie;
        String corpoResposta = executarRequisicaoGet(url);

        try {
            return objectMapper.readValue(corpoResposta, Show.class);
        } catch (IOException e) {
            throw new ApiException("Não foi possível interpretar a resposta da TVmaze ao buscar a série.", e);
        }
    }

    /**
     * Executa uma requisição HTTP GET simples e devolve o corpo da resposta
     * como texto, tratando erros de rede e códigos de status HTTP.
     */
    private String executarRequisicaoGet(String url) throws ApiException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .header("User-Agent", "Prova2BIM-DesenvolvimentoOrientadoObjetos/1.0")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 404) {
                throw new ApiException("Nenhum resultado encontrado na TVmaze (HTTP 404).");
            }
            if (response.statusCode() == 429) {
                throw new ApiException("Muitas requisições em pouco tempo. Aguarde alguns segundos e tente novamente.");
            }
            if (response.statusCode() != 200) {
                throw new ApiException("A TVmaze respondeu com um erro inesperado (HTTP " + response.statusCode() + ").");
            }

            return response.body();

        } catch (IOException e) {
            throw new ApiException("Falha de conexão com a internet. Verifique sua rede e tente novamente.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("A requisição à TVmaze foi interrompida.", e);
        } catch (IllegalArgumentException e) {
            throw new ApiException("URL inválida ao consultar a TVmaze.", e);
        }
    }
}
