import java.net.URI;              // representa o endereco da requisicao (substitui URL no Java 11)
import java.net.URLEncoder;       // codifica texto para uso seguro em URLs (espacos → %20)
import java.net.http.HttpClient;  // cliente HTTP moderno do Java 11
import java.net.http.HttpRequest; // representa a requisicao HTTP que vamos enviar
import java.net.http.HttpResponse;// representa a resposta recebida da API
import java.nio.charset.StandardCharsets; // encoding UTF-8
import java.time.Duration;        // para definir o tempo limite (timeout) da requisicao
import java.util.ArrayList;
import java.util.List;

//chamar api    
public class ApiService {

    private static final String BASE_URL = "https://api.tvmaze.com";

   
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    
    public List<Serie> buscarSeries(String nomeBusca) throws Exception {
        // Codifica o nome para URL segura: "breaking bad" → "breaking+bad"
        String nomeEncoded = URLEncoder.encode(nomeBusca, StandardCharsets.UTF_8.toString());

        // Monta a URL completa
        String urlStr = BASE_URL + "/search/shows?q=" + nomeEncoded;

        // Faz a requisicao e obtem o JSON como texto
        String json = fazerRequisicao(urlStr);

        // Converte o JSON em lista de objetos Serie
        return parseListaSeries(json);
    }

    /**
     * Faz uma requisicao HTTP GET usando o HttpClient do Java 11.
     *
     * Fluxo:
     * 1. Monta o HttpRequest com a URL e cabecalhos
     * 2. Envia via httpClient.send() — bloqueante (espera a resposta)
     * 3. Verifica o codigo de status (200 = OK)
     * 4. Retorna o corpo da resposta como String
     *
     * @param urlStr  endereco completo da requisicao
     * @return        corpo da resposta (JSON) como String
     * @throws Exception em caso de erro de conexao ou resposta invalida
     */
    private String fazerRequisicao(String urlStr) throws Exception {

        // Monta a requisicao HTTP
        // HttpRequest.newBuilder() → configura a requisicao passo a passo
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlStr))                          // define a URL de destino
                .timeout(Duration.ofSeconds(10))                  // timeout de leitura de 10s
                .header("Accept", "application/json")             // esperamos JSON de volta
                .header("User-Agent", "SeriesTrackerApp/1.0")    // identifica nossa aplicacao
                .GET()                                            // metodo HTTP GET
                .build();                                         // finaliza a configuracao

        // Envia a requisicao e aguarda a resposta
        // HttpResponse.BodyHandlers.ofString() → le o corpo da resposta como String UTF-8
        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );

        // Verifica se a resposta foi bem-sucedida (codigo 200 = OK)
        if (response.statusCode() != 200) {
            throw new Exception("Erro na API: codigo " + response.statusCode());
        }

        // Retorna o corpo da resposta (o JSON completo como String)
        return response.body();
    }

    /**
     * Converte o JSON de busca em lista de objetos Serie.
     *
     * O JSON retornado pelo TVMaze tem este formato:
     * [
     *   { "score": 0.9, "show": { "id": 1, "name": "...", ... } },
     *   { "score": 0.7, "show": { "id": 2, "name": "...", ... } }
     * ]
     *
     * Dividimos pelo delimitador "show": para isolar cada serie.
     */
    private List<Serie> parseListaSeries(String json) {
        List<Serie> lista = new ArrayList<>();

        // Cada resultado de busca contem "show": antes do objeto da serie
        String[] blocos = json.split("\"show\":");

        // indice 0 e o inicio do array antes do primeiro "show" — pulamos com i=1
        for (int i = 1; i < blocos.length; i++) {
            try {
                String bloco = extrairObjeto(blocos[i]); // pega o objeto { ... } completo
                Serie serie  = parseSerie(bloco);        // converte em objeto Java
                if (serie != null) lista.add(serie);
            } catch (Exception e) {
                // Se uma serie falhar, continua para a proxima sem interromper a busca
                System.err.println("Erro ao parsear serie: " + e.getMessage());
            }
        }

        return lista;
    }

    /**
     * Converte um bloco JSON de um unico show em objeto Serie.
     */
    public Serie parseSerie(String json) {
        try {
            int    id          = Integer.parseInt(extrairValor(json, "\"id\""));
            String nome        = extrairValor(json, "\"name\"");
            String idioma      = extrairValor(json, "\"language\"");
            String status      = extrairValor(json, "\"status\"");
            String dataEstreia = extrairValor(json, "\"premiered\"");
            String dataFim     = extrairValor(json, "\"ended\"");

            // Generos vem como array: ["Drama","Crime"] → "Drama, Crime"
            String generosRaw = extrairArray(json, "\"genres\"");
            String generos    = generosRaw.replace("[", "").replace("]", "")
                                          .replace("\"", "").replace(",", ", ").trim();

            // Nota fica dentro de "rating": { "average": 8.5 }
            double nota = 0.0;
            try {
                String ratingBloco = extrairObjeto(json.substring(
                    json.indexOf("\"rating\"") > -1 ? json.indexOf("\"rating\"") : 0
                ));
                String notaStr = extrairValor(ratingBloco, "\"average\"");
                if (!notaStr.isEmpty() && !notaStr.equals("null")) {
                    nota = Double.parseDouble(notaStr);
                }
            } catch (Exception ignored) {}

            // Emissora fica em "network": { "name": "AMC" }
            String emissora = "Desconhecida";
            if (json.contains("\"network\":{") || json.contains("\"network\": {")) {
                int idx = json.indexOf("\"network\"");
                if (idx > -1) {
                    String networkBloco = extrairObjeto(json.substring(idx));
                    String netNome = extrairValor(networkBloco, "\"name\"");
                    if (!netNome.isEmpty() && !netNome.equals("null")) emissora = netNome;
                }
            }

            String resumo = extrairValor(json, "\"summary\"");

            // URL da imagem fica em "image": { "medium": "https://..." }
            String imagemUrl = "";
            if (json.contains("\"image\"")) {
                int idx = json.indexOf("\"image\"");
                if (idx > -1) {
                    String imgBloco = extrairObjeto(json.substring(idx));
                    imagemUrl = extrairValor(imgBloco, "\"medium\"");
                }
            }

            return new Serie(id, nome, idioma, generos, nota, status,
                             dataEstreia, dataFim, emissora, resumo, imagemUrl);

        } catch (Exception e) {
            System.err.println("Erro no parseSerie: " + e.getMessage());
            return null;
        }
    }

    // ── Metodos auxiliares de parsing JSON ────────────────────────────────────────

    /**
     * Extrai o valor de uma chave simples no JSON (string ou numero).
     *
     * Exemplo: extrairValor(json, "\"name\"") → "Breaking Bad"
     */
    private String extrairValor(String json, String chave) {
        int inicio = json.indexOf(chave);
        if (inicio == -1) return "";

        inicio = json.indexOf(":", inicio) + 1;
        while (inicio < json.length() && json.charAt(inicio) == ' ') inicio++;
        if (inicio >= json.length()) return "";

        char primeiro = json.charAt(inicio);

        if (primeiro == '"') {
            // valor string: le entre aspas respeitando escapes (\")
            inicio++;
            StringBuilder valor = new StringBuilder();
            while (inicio < json.length() && json.charAt(inicio) != '"') {
                if (json.charAt(inicio) == '\\' && inicio + 1 < json.length()) {
                    inicio++; // pula o backslash
                }
                valor.append(json.charAt(inicio));
                inicio++;
            }
            return valor.toString();
        } else if (primeiro == 'n') {
            return "null"; // valor null
        } else {
            // valor numerico: le ate encontrar , } ]
            int fim = inicio;
            while (fim < json.length() && ",}]".indexOf(json.charAt(fim)) == -1) fim++;
            return json.substring(inicio, fim).trim();
        }
    }

    /**
     * Extrai um array JSON como String.
     * Exemplo: ["Drama","Crime"] → retorna "["Drama","Crime"]"
     */
    private String extrairArray(String json, String chave) {
        int inicio = json.indexOf(chave);
        if (inicio == -1) return "[]";
        inicio = json.indexOf("[", inicio);
        if (inicio == -1) return "[]";
        int fim = json.indexOf("]", inicio);
        if (fim == -1) return "[]";
        return json.substring(inicio, fim + 1);
    }

    /**
     * Extrai um objeto JSON completo (entre { e seu } correspondente).
     * Conta abertura/fechamento de chaves para lidar com objetos aninhados.
     */
    private String extrairObjeto(String json) {
        int inicio = json.indexOf("{");
        if (inicio == -1) return "{}";

        int nivel = 0;
        for (int i = inicio; i < json.length(); i++) {
            if      (json.charAt(i) == '{') nivel++; // abre nivel
            else if (json.charAt(i) == '}') nivel--; // fecha nivel
            if (nivel == 0) return json.substring(inicio, i + 1); // objeto completo
        }
        return "{}";
    }
}
