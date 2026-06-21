import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TVMazeService {

    public String buscarTexto(String nome) throws Exception {
        // esse trecho vai mudar o input do usuario de 'breaking bad' para
        // 'breaking%20bad' ja que a url da requisicao nao aceita espaco
        // %20 = ' ' em UTF8
        String nomeCodificado = URLEncoder.encode(nome, StandardCharsets.UTF_8);
        // o q= significa query=, ou seja, a query que vai ser buscada no banco de dados
        // das series
        String url = "https://api.tvmaze.com/search/shows?q=" + nomeCodificado;

        HttpClient cliente = HttpClient.newHttpClient();
        HttpRequest requisicao = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> resposta = cliente.send(requisicao, HttpResponse.BodyHandlers.ofString());
        return resposta.body();
    }

    public List<Serie> buscarSeries(String nome) throws Exception {
        String json = buscarTexto(nome);
        List<Serie> series = new ArrayList<>();

        List<Object> resultados = (List<Object>) JsonUtil.parse(json);
        for (Object item : resultados) {
            Map<String, Object> obj = (Map<String, Object>) item;
            Map<String, Object> show = (Map<String, Object>) obj.get("show");
            series.add(montarSerie(show));
        }
        return series;
    }

    // Pega um "show" (Map) e monta um objeto Serie
    private Serie montarSerie(Map<String, Object> show) {
        int id = ((Double) show.get("id")).intValue();
        String nome = (String) show.get("name");
        String idioma = (String) show.get("language");
        String status = (String) show.get("status");
        String dataEstreia = (String) show.get("premiered");
        String dataTermino = (String) show.get("ended");

        // generos vem como lista; converte pra lista de String
        List<String> generos = new ArrayList<>();
        List<Object> generosBrutos = (List<Object>) show.get("genres");
        for (Object g : generosBrutos) {
            generos.add((String) g);
        }

        // nota fica dentro de "rating" -> "average" (pode ser null)
        double nota = 0.0;
        Map<String, Object> rating = (Map<String, Object>) show.get("rating");
        if (rating.get("average") != null) {
            nota = (Double) rating.get("average");
        }

        // emissora fica em "network" -> "name"; se for null, tenta "webChannel" (ex: Netflix)
        String emissora = "Desconhecida";
        Map<String, Object> network = (Map<String, Object>) show.get("network");
        if (network != null) {
            emissora = (String) network.get("name");
        } else {
            Map<String, Object> webChannel = (Map<String, Object>) show.get("webChannel");
            if (webChannel != null) {
                emissora = (String) webChannel.get("name");
            }
        }

        return new Serie(id, nome, idioma, generos, nota, status, dataEstreia, dataTermino, emissora);
    }
}