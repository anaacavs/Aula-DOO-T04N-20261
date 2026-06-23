
package service;

import model.Serie;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * CLASSE SERVICE: Responsável por se comunicar com a API pública do TVMaze.
 * Usa apenas classes nativas do JDK 11 (HttpURLConnection, URL, BufferedReader).
 * Sem bibliotecas externas — tudo nativo Java.
 */
public class TvMazeService {

    // ── CONSTANTE: URL Base da API TVMaze ──────────────────────────────────────
    // 'static final' = constante. Centralizada aqui para fácil manutenção.
    // Se a URL da API mudar, atualizamos apenas esta linha.
    private static final String URL_BASE = "https://api.tvmaze.com";

    // ── CONSTANTE: Tempo máximo de espera (em milissegundos) ──────────────────
    // 5000ms = 5 segundos. Se a API não responder em 5s, desistimos e mostramos erro.
    private static final int TIMEOUT_MS = 5000;

    // ── MÉTODO: Buscar séries pelo nome ───────────────────────────────────────
    /**
     * Faz uma busca na API do TVMaze pelo nome da série e retorna uma lista de resultados.
     * Endpoint usado: GET /search/shows?q={nome} — retorna um array JSON com os resultados.
     * @param nomeBusca O nome da série que o usuário digitou
     * @return Lista de objetos Serie com os resultados encontrados
     * @throws Exception Se ocorrer erro de rede ou a API retornar erro
     */
    public static List<Serie> buscarPorNome(String nomeBusca) throws Exception {
        List<Serie> resultados = new ArrayList<>(); // Lista que vai acumular os resultados

        // URLEncoder.encode converte caracteres especiais para o formato de URL
        // Ex: "Breaking Bad" → "Breaking+Bad" (espaços viram + ou %20)
        String nomeCodificado = URLEncoder.encode(nomeBusca, StandardCharsets.UTF_8.name());

        // Monta a URL completa do endpoint de busca
        String urlString = URL_BASE + "/search/shows?q=" + nomeCodificado;

        // ── Abre conexão HTTP com a API ────────────────────────────────────────
        // URI.create().toURL() é a forma moderna (não depreciada) de criar URLs no Java 21
        URL url = URI.create(urlString).toURL(); // Cria objeto URL a partir da String
        HttpURLConnection conexao = (HttpURLConnection) url.openConnection(); // Abre conexão

        // Define o método HTTP como GET (apenas leitura, sem enviar dados)
        conexao.setRequestMethod("GET");
        // Define o tempo máximo para conectar ao servidor (5 segundos)
        conexao.setConnectTimeout(TIMEOUT_MS);
        // Define o tempo máximo para ler a resposta depois de conectado (5 segundos)
        conexao.setReadTimeout(TIMEOUT_MS);
        // Informa ao servidor que aceitamos resposta em formato JSON
        conexao.setRequestProperty("Accept", "application/json");

        // ── Verifica o código de resposta HTTP ────────────────────────────────
        int codigoResposta = conexao.getResponseCode(); // 200 = OK, 404 = Não encontrado, etc.
        if (codigoResposta != HttpURLConnection.HTTP_OK) { // HTTP_OK = 200
            // Se a API não retornou 200, lança exceção com o código de erro
            throw new Exception("API retornou código de erro: " + codigoResposta);
        }

        // ── Lê o corpo da resposta (o JSON) ──────────────────────────────────
        // InputStreamReader lê o fluxo de bytes da resposta como texto
        // BufferedReader adiciona buffer para leitura eficiente linha por linha
        BufferedReader leitor = new BufferedReader(
            new InputStreamReader(conexao.getInputStream(), StandardCharsets.UTF_8)
        );

        StringBuilder resposta = new StringBuilder(); // Acumula o JSON linha por linha
        String linha;
        // Lê linha por linha até o fim (readLine() retorna null quando não há mais linhas)
        while ((linha = leitor.readLine()) != null) {
            resposta.append(linha); // Adiciona a linha ao StringBuilder
        }
        leitor.close(); // Fecha o leitor para liberar recursos
        conexao.disconnect(); // Fecha a conexão HTTP

        // ── Parseia o JSON da resposta ────────────────────────────────────────
        // A resposta do endpoint /search/shows é um ARRAY de objetos com formato:
        // [ { "score": 1.0, "show": { ... } }, { "score": 0.9, "show": { ... } } ]
        String jsonResposta = resposta.toString(); // Converte StringBuilder para String

        // Extrai cada bloco { "score": ..., "show": { ... } } do array raiz
        List<String> blocosPrincipais = extrairBlocosNivel1(jsonResposta);

        for (String bloco : blocosPrincipais) {
            // De cada bloco principal, extrai o sub-objeto "show": { ... }
            String blocoShow = extrairSubObjeto(bloco, "show");
            if (!blocoShow.isEmpty()) {
                // Converte o JSON do show em um objeto Serie
                Serie serie = parsearShowDaApi(blocoShow);
                if (serie != null && serie.getNome() != null && !serie.getNome().isEmpty()) {
                    resultados.add(serie); // Adiciona o resultado à lista
                }
            }
        }

        return resultados; // Retorna a lista de séries encontradas
    }

    // ── MÉTODO PRIVADO: Parsear JSON de um show da API em objeto Serie ─────────
    /**
     * Recebe o bloco JSON de UM show (o objeto interno da resposta da API)
     * e converte em um objeto Serie preenchido com todos os campos.
     */
    private static Serie parsearShowDaApi(String jsonShow) {
        try {
            Serie serie = new Serie(); // Objeto vazio que vamos preencher

            // Extrai o ID do show
            String id = JsonParser.extrairValor(jsonShow, "id");
            serie.setIdTvMaze(id.isEmpty() ? 0 : Integer.parseInt(id.trim()));

            // Extrai o nome do show
            serie.setNome(JsonParser.extrairValor(jsonShow, "name"));

            // Extrai o idioma
            serie.setIdioma(JsonParser.extrairValor(jsonShow, "language"));

            // ── Extrai os gêneros (array de strings) ──────────────────────────
            // Os gêneros na API vêm como array: "genres": ["Drama", "Crime"]
            // Precisamos converter esse array para uma String separada por vírgula
            String generosArray = extrairArrayDeStrings(jsonShow, "genres");
            serie.setGeneros(generosArray);

            // ── Extrai a nota (rating) ────────────────────────────────────────
            // A nota na API está dentro de um sub-objeto: "rating": {"average": 8.9}
            // Precisamos entrar no sub-objeto "rating" e pegar o campo "average"
            String blocoRating = extrairSubObjeto(jsonShow, "rating");
            if (!blocoRating.isEmpty()) {
                String avg = JsonParser.extrairValor(blocoRating, "average");
                try {
                    serie.setNotaGeral(avg.isEmpty() || avg.equals("null") ? 0.0 : Double.parseDouble(avg.trim()));
                } catch (NumberFormatException e) {
                    serie.setNotaGeral(0.0); // Se a nota vier inválida, usa 0.0
                }
            }

            // Extrai o status (Running, Ended, etc.)
            serie.setStatus(JsonParser.extrairValor(jsonShow, "status"));

            // Extrai a data de estreia
            serie.setDataEstreia(JsonParser.extrairValor(jsonShow, "premiered"));

            // Extrai a data de término (pode ser nulo se ainda está no ar)
            serie.setDataTermino(JsonParser.extrairValor(jsonShow, "ended"));

            // ── Extrai a emissora (network) ───────────────────────────────────
            // A emissora está em um sub-objeto: "network": {"id": 8, "name": "HBO", ...}
            // Ou pode estar em "webChannel" se for streaming (Netflix, etc.)
            String blocoNetwork = extrairSubObjeto(jsonShow, "network");
            if (!blocoNetwork.isEmpty()) {
                serie.setEmissora(JsonParser.extrairValor(blocoNetwork, "name")); // Nome da emissora
            } else {
                // Tenta "webChannel" (para séries de streaming)
                String blocoWeb = extrairSubObjeto(jsonShow, "webChannel");
                if (!blocoWeb.isEmpty()) {
                    serie.setEmissora(JsonParser.extrairValor(blocoWeb, "name")); // Nome do serviço de streaming
                } else {
                    serie.setEmissora("N/A"); // Emissora desconhecida
                }
            }

            // ── Extrai a URL da imagem ────────────────────────────────────────
            // A imagem está em: "image": {"medium": "url...", "original": "url..."}
            String blocoImage = extrairSubObjeto(jsonShow, "image");
            if (!blocoImage.isEmpty()) {
                String urlMedium = JsonParser.extrairValor(blocoImage, "medium"); // Imagem média (menor)
                serie.setImagemUrl(urlMedium);
            } else {
                serie.setImagemUrl(""); // Sem imagem disponível
            }

            // Extrai o resumo/sinopse (vem em HTML, limpamos as tags)
            String resumoHtml = JsonParser.extrairValor(jsonShow, "summary");
            serie.setResumo(JsonParser.limparHtml(resumoHtml)); // Remove as tags HTML

            return serie; // Retorna o objeto Serie completamente preenchido

        } catch (Exception e) {
            // Se qualquer erro ocorrer ao parsear, retorna null (será ignorado)
            System.err.println("Erro ao parsear show da API: " + e.getMessage());
            return null;
        }
    }

    // ── MÉTODO PRIVADO: Extrair sub-objeto JSON ────────────────────────────────
    /**
     * Extrai o conteúdo de um sub-objeto JSON { } dado o nome do campo.
     * Ex: extrairSubObjeto(json, "rating") → o conteúdo entre { } do campo "rating"
     * Usa contagem de chaves para encontrar o { } correto.
     */
    private static String extrairSubObjeto(String json, String campo) {
        String padrao = "\"" + campo + "\":"; // Padrão que identifica o campo
        int inicio = json.indexOf(padrao);    // Posição onde o campo começa
        if (inicio == -1) return "";          // Campo não encontrado

        // Avança para o primeiro caractere após ":"
        int pos = inicio + padrao.length();
        // Pula espaços em branco
        while (pos < json.length() && json.charAt(pos) == ' ') pos++;

        if (pos >= json.length()) return "";
        if (json.charAt(pos) != '{') return ""; // O valor não é um objeto, ignora

        // Conta chaves para encontrar o } de fechamento correspondente
        int profundidade = 0;
        int inicioObj = pos;
        for (int i = pos; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') profundidade++;     // Abre um nível de profundidade
            else if (c == '}') {
                profundidade--;               // Fecha um nível de profundidade
                if (profundidade == 0) {
                    // Voltamos ao nível 0 — encontramos o } de fechamento
                    return json.substring(inicioObj, i + 1); // Retorna o sub-objeto completo
                }
            }
        }
        return ""; // Objeto não foi fechado corretamente
    }

    // ── MÉTODO PRIVADO: Extrair array de strings JSON ─────────────────────────
    /**
     * Extrai os valores de um array de strings JSON e os junta com vírgula.
     * Ex: "genres": ["Drama", "Crime"] → "Drama, Crime"
     */
    private static String extrairArrayDeStrings(String json, String campo) {
        String padrao = "\"" + campo + "\":"; // Padrão do campo
        int inicio = json.indexOf(padrao);
        if (inicio == -1) return "";

        // Avança para o [
        int abreturaArray = json.indexOf('[', inicio + padrao.length());
        if (abreturaArray == -1) return "";

        // Avança para o ] correspondente
        int fechaArray = json.indexOf(']', abreturaArray);
        if (fechaArray == -1) return "";

        // Extrai o conteúdo entre [ e ]
        String conteudo = json.substring(abreturaArray + 1, fechaArray);
        if (conteudo.trim().isEmpty()) return ""; // Array vazio

        // Processa cada string no array
        StringBuilder resultado = new StringBuilder();
        String[] partes = conteudo.split(","); // Divide pelo separador de itens
        for (int i = 0; i < partes.length; i++) {
            String parte = partes[i].trim(); // Remove espaços
            // Remove as aspas ao redor da string
            if (parte.startsWith("\"")) parte = parte.substring(1);
            if (parte.endsWith("\"")) parte = parte.substring(0, parte.length() - 1);
            if (!parte.isEmpty()) {
                if (resultado.length() > 0) resultado.append(", "); // Adiciona separador
                resultado.append(parte); // Adiciona o gênero
            }
        }
        return resultado.toString(); // Ex: "Drama, Crime, Thriller"
    }

    // ── MÉTODO PRIVADO: Extrair blocos de nível 1 do array ───────────────────
    /**
     * Extrai os objetos { } de nível superior de um array JSON [ { }, { } ].
     * Similar ao extrairBlocosJson do JsonParser mas para o array raiz da busca.
     */
    private static List<String> extrairBlocosNivel1(String arrayJson) {
        List<String> blocos = new ArrayList<>();
        int profundidade = 0;
        int inicio = -1;

        for (int i = 0; i < arrayJson.length(); i++) {
            char c = arrayJson.charAt(i);
            if (c == '{') {
                profundidade++;
                if (profundidade == 1) inicio = i; // Marca início do bloco principal
            } else if (c == '}') {
                profundidade--;
                if (profundidade == 0 && inicio != -1) {
                    blocos.add(arrayJson.substring(inicio, i + 1)); // Salva o bloco
                    inicio = -1;
                }
            }
        }
        return blocos;
    }
}