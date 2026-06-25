package telecine;
//tratamento de JSON
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


//formato de requsições aceitas pelo java.
import java.net.URI;
//Serve para formatar textos para serem usados dentro de URLs.
import java.net.URLEncoder;
//permite o cliente fazer chamada http permite chamar
import java.net.http.HttpClient;
//requests http
import java.net.http.HttpRequest;
//acessar dados da requisição permite receber
import java.net.http.HttpResponse;
//para tratar UTF-8
import java.nio.charset.StandardCharsets;
//pra retornar lista vazia quando não tiver série
import java.util.ArrayList;
import java.util.List;

public class ServicoTvMaze {

    public List<Serie> buscarSeriesPorNome(String nome) throws Exception { //throws Exception diz que pode trazer erros e temos que tratar no main

        if (nome == null || nome.trim().isEmpty()) { //validação básica de nome se tem e se não é só espaço
            throw new IllegalArgumentException("Informe o nome da série.");
        }
        
        
        // formatando nome para passar como parametro na consulta
        
        String nomeFormatado = URLEncoder.encode(nome, StandardCharsets.UTF_8);
        //embaixo jaz a consulta em si
        String url = "https://api.tvmaze.com/search/shows?q=" + nomeFormatado;
        
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //teste
        System.out.println(response.body());
        
        //realidade
        String json = response.body();

        JsonArray resultados = JsonParser.parseString(json).getAsJsonArray();
        List<Serie> series = new ArrayList<>();
        
        if (resultados.size() == 0) {
            return series;
        }
        //for para percorrer as séries todas dentro dos for tem ifs para verificar se o valor é null pq tava quebrando código

        for (int i = 0; i < resultados.size(); i++) {
            JsonObject item = resultados.get(i).getAsJsonObject();
            JsonObject show = item.getAsJsonObject("show");

            Serie serie = new Serie();

            serie.setId(show.get("id").getAsInt());
            serie.setNome(show.get("name").getAsString());

            if (!show.get("language").isJsonNull()) {
                serie.setIdioma(show.get("language").getAsString());
            } else {
                serie.setIdioma("Não informado");
            }

            List<String> generos = new ArrayList<>();

            JsonArray generosJson = show.getAsJsonArray("genres");

            for (int j = 0; j < generosJson.size(); j++) {
                generos.add(generosJson.get(j).getAsString());
            }

            serie.setGeneros(generos);

            JsonObject rating = show.getAsJsonObject("rating");

            if (rating != null && !rating.get("average").isJsonNull()) {
                serie.setNotaGeral(rating.get("average").getAsDouble());
            } else {
                serie.setNotaGeral(0.0);
            }

            if (!show.get("status").isJsonNull()) {
                serie.setEstado(show.get("status").getAsString());
            } else {
                serie.setEstado("Não informado");
            }

            if (!show.get("premiered").isJsonNull()) {
                serie.setDataEstreia(show.get("premiered").getAsString());
            } else {
                serie.setDataEstreia("Não informado");
            }

            if (!show.get("ended").isJsonNull()) {
                serie.setDataTermino(show.get("ended").getAsString());
            } else {
                serie.setDataTermino("Não informado");
            }

            if (!show.get("network").isJsonNull()) {
                JsonObject network = show.getAsJsonObject("network");
                serie.setEmissora(network.get("name").getAsString());
            } else if (!show.get("webChannel").isJsonNull()) {
                JsonObject webChannel = show.getAsJsonObject("webChannel");
                serie.setEmissora(webChannel.get("name").getAsString());
            } else {
                serie.setEmissora("Não informado");
            }

            series.add(serie);
        }

        return series;
    }
}
