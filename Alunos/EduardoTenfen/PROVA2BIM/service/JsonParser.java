	package service;

import model.Serie;
import model.Usuario;

import java.util.ArrayList;
import java.util.List;

/**
 * CLASSE SERVICE: Responsável por PARSEAR (interpretar) e GERAR JSON nativamente.
 * PROIBIDO usar Gson, Jackson ou qualquer biblioteca externa — tudo é feito
 * com String, StringBuilder, split() e replace() do Java puro (JDK 11).
 *
 * Esta classe traduz:
 *   - Objeto Java  →  texto JSON (serialização)
 *   - Texto JSON   →  Objeto Java (desserialização/parse)
 */
public class JsonParser {

    // ── MÉTODO: Escapar string para JSON ───────────────────────────────────────
    /**
     * Sanitiza um valor String para que seja seguro dentro de um JSON.
     * Remove caracteres que quebrariam a estrutura do JSON, como aspas e barras.
     * Sem isso, um título com aspas (ex: She's a "Hero") quebraria o arquivo.
     */
    private static String escaparJson(String valor) {
        if (valor == null) return ""; // Se o valor for nulo, retorna string vazia
        return valor
                .replace("\\", "\\\\") // Escapa barras invertidas antes de tudo
                .replace("\"", "\\\"") // Escapa aspas duplas → \"
                .replace("\n", "\\n")  // Escapa quebra de linha → \n
                .replace("\r", "\\r")  // Escapa retorno de carro → \r
                .replace("\t", "\\t"); // Escapa tabulação → \t
    }

    // ── MÉTODO: Limpar tags HTML do resumo ─────────────────────────────────────
    /**
     * Remove tags HTML do resumo/sinopse que vem da API.
     * A TVMaze retorna o resumo com HTML (ex: <p><b>texto</b></p>).
     * Usamos replaceAll com regex para remover qualquer tag entre < e >.
     */
    public static String limparHtml(String html) {
        if (html == null || html.isEmpty()) return "Sem resumo disponível.";
        // A regex "<[^>]*>" encontra qualquer sequência que começa com <,
        // tem qualquer caractere que não seja >, e termina com >
        return html.replaceAll("<[^>]*>", "").trim();
    }

    // ── MÉTODO: Serializar uma Serie para JSON ─────────────────────────────────
    /**
     * Converte um objeto Serie em um bloco de texto no formato JSON.
     * StringBuilder é mais eficiente que concatenar String com + em loop.
     */
    public static String serieParaJson(Serie serie) {
        StringBuilder sb = new StringBuilder(); // StringBuilder para montar o JSON
        sb.append("    {\n"); // Abre o objeto JSON com indentação
        // Cada linha abaixo adiciona um campo no formato "chave": "valor"
        sb.append("      \"idTvMaze\": ").append(serie.getIdTvMaze()).append(",\n");
        sb.append("      \"nome\": \"").append(escaparJson(serie.getNome())).append("\",\n");
        sb.append("      \"idioma\": \"").append(escaparJson(serie.getIdioma())).append("\",\n");
        sb.append("      \"generos\": \"").append(escaparJson(serie.getGeneros())).append("\",\n");
        sb.append("      \"notaGeral\": ").append(serie.getNotaGeral()).append(",\n");
        sb.append("      \"status\": \"").append(escaparJson(serie.getStatus())).append("\",\n");
        sb.append("      \"dataEstreia\": \"").append(escaparJson(serie.getDataEstreia())).append("\",\n");
        sb.append("      \"dataTermino\": \"").append(escaparJson(serie.getDataTermino())).append("\",\n");
        sb.append("      \"emissora\": \"").append(escaparJson(serie.getEmissora())).append("\",\n");
        sb.append("      \"imagemUrl\": \"").append(escaparJson(serie.getImagemUrl())).append("\",\n");
        sb.append("      \"resumo\": \"").append(escaparJson(serie.getResumo())).append("\"\n");
        sb.append("    }"); // Fecha o objeto JSON
        return sb.toString(); // Retorna o JSON da série como texto
    }

    // ── MÉTODO: Serializar o Usuario completo para JSON ────────────────────────
    /**
     * Converte o objeto Usuario (com suas 3 listas) em um JSON completo.
     * Este JSON será salvo no arquivo dados.json no disco.
     */
    public static String usuarioParaJson(Usuario usuario) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n"); // Abre o objeto raiz do JSON
        // Salva o nome/apelido do usuário
        sb.append("  \"nomeApelido\": \"").append(escaparJson(usuario.getNomeApelido())).append("\",\n");

        // Serializa a lista de favoritos
        sb.append("  \"favoritos\": [\n");
        List<Serie> favs = usuario.getFavoritos(); // Pega a lista de favoritos
        for (int i = 0; i < favs.size(); i++) {
            sb.append(serieParaJson(favs.get(i))); // Converte cada série para JSON
            if (i < favs.size() - 1) sb.append(","); // Adiciona vírgula entre itens (não no último)
            sb.append("\n");
        }
        sb.append("  ],\n"); // Fecha o array de favoritos

        // Serializa a lista de já assistidas
        sb.append("  \"jaAssistidas\": [\n");
        List<Serie> ja = usuario.getJaAssistidas();
        for (int i = 0; i < ja.size(); i++) {
            sb.append(serieParaJson(ja.get(i)));
            if (i < ja.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("  ],\n"); // Fecha o array de já assistidas

        // Serializa a lista de deseja assistir
        sb.append("  \"desejassistir\": [\n");
        List<Serie> deseja = usuario.getDesejassistir();
        for (int i = 0; i < deseja.size(); i++) {
            sb.append(serieParaJson(deseja.get(i)));
            if (i < deseja.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("  ]\n"); // Fecha o array de deseja assistir (sem vírgula — último campo)
        sb.append("}"); // Fecha o objeto raiz
        return sb.toString(); // Retorna o JSON completo como String
    }

    // ── MÉTODO: Extrair valor de campo JSON por chave ──────────────────────────
    /**
     * Busca o valor de um campo em uma String JSON usando indexOf e substring.
     * Ex: extrairValor("{\"nome\": \"Breaking Bad\"}", "nome") → "Breaking Bad"
     * Esta é a técnica de parse manual sem biblioteca.
     */
    public static String extrairValor(String json, String chave) {
        // Monta o padrão que procuramos: "chave":
        String padrao = "\"" + chave + "\":";
        int inicio = json.indexOf(padrao); // Procura onde o padrão começa
        if (inicio == -1) return ""; // Se não encontrou o campo, retorna vazio

        inicio += padrao.length(); // Avança o índice para depois dos ":"

        // Remove espaços em branco depois dos ":"
        while (inicio < json.length() && json.charAt(inicio) == ' ') inicio++;

        if (inicio >= json.length()) return ""; // Se chegou ao fim sem achar valor, retorna vazio

        char primeiroChar = json.charAt(inicio); // Pega o primeiro caractere do valor

        if (primeiroChar == '"') {
            // O valor é uma STRING (começa com aspas)
            inicio++; // Pula a aspa de abertura
            StringBuilder valor = new StringBuilder();
            // Lê caractere por caractere até encontrar a aspa de fechamento
            while (inicio < json.length()) {
                char c = json.charAt(inicio);
                if (c == '\\') {
                    // Encontrou um caractere de escape (ex: \", \n)
                    inicio++; // Avança para o próximo caractere
                    if (inicio < json.length()) {
                        char escaped = json.charAt(inicio);
                        // Converte o escape de volta para o caractere real
                        if (escaped == '"') valor.append('"');
                        else if (escaped == '\\') valor.append('\\');
                        else if (escaped == 'n') valor.append('\n');
                        else if (escaped == 'r') valor.append('\r');
                        else if (escaped == 't') valor.append('\t');
                        else valor.append(escaped);
                    }
                } else if (c == '"') {
                    break; // Encontrou a aspa de fechamento — valor termina aqui
                } else {
                    valor.append(c); // Adiciona o caractere ao valor
                }
                inicio++;
            }
            return valor.toString(); // Retorna o valor da string sem as aspas
        } else if (primeiroChar == 'n') {
            // O valor é 'null' (começa com 'n')
            return "";
        } else {
            // O valor é NUMÉRICO (sem aspas)
            int fim = inicio;
            // Avança até encontrar vírgula, '}' ou '\n' — que indicam fim do número
            while (fim < json.length() && json.charAt(fim) != ',' && json.charAt(fim) != '}' && json.charAt(fim) != '\n') {
                fim++;
            }
            return json.substring(inicio, fim).trim(); // Retorna o número como String
        }
    }

    // ── MÉTODO: Parsear um bloco JSON de série em objeto Serie ────────────────
    /**
     * Recebe um bloco de texto JSON representando UMA série e cria um objeto Serie.
     * Usa extrairValor() para pegar cada campo do JSON.
     */
    public static Serie parsearSerie(String blocoJson) {
        Serie serie = new Serie(); // Cria objeto vazio
        try {
            // Extrai cada campo do bloco JSON e atribui ao objeto
            String id = extrairValor(blocoJson, "idTvMaze");
            // parseInt converte String para inteiro; usamos 0 como padrão se vazio
            serie.setIdTvMaze(id.isEmpty() ? 0 : Integer.parseInt(id.trim()));

            serie.setNome(extrairValor(blocoJson, "nome"));
            serie.setIdioma(extrairValor(blocoJson, "idioma"));
            serie.setGeneros(extrairValor(blocoJson, "generos"));

            String nota = extrairValor(blocoJson, "notaGeral");
            // parseDouble converte String para decimal; usamos 0.0 como padrão se vazio
            serie.setNotaGeral(nota.isEmpty() ? 0.0 : Double.parseDouble(nota.trim()));

            serie.setStatus(extrairValor(blocoJson, "status"));
            serie.setDataEstreia(extrairValor(blocoJson, "dataEstreia"));
            serie.setDataTermino(extrairValor(blocoJson, "dataTermino"));
            serie.setEmissora(extrairValor(blocoJson, "emissora"));
            serie.setImagemUrl(extrairValor(blocoJson, "imagemUrl"));
            serie.setResumo(extrairValor(blocoJson, "resumo"));
        } catch (NumberFormatException e) {
            // Se um número vier com formato inválido no JSON, capturamos e continuamos
            System.err.println("Erro ao parsear número na série: " + e.getMessage());
        }
        return serie; // Retorna o objeto Serie preenchido
    }

    // ── MÉTODO: Extrair lista de objetos de um array JSON ─────────────────────
    /**
     * Extrai todos os objetos { } de dentro de um array JSON [ { }, { }, { } ].
     * Usamos contagem de chaves para identificar onde cada objeto começa e termina.
     */
    private static List<String> extrairBlocosJson(String arrayJson) {
        List<String> blocos = new ArrayList<>(); // Lista onde guardaremos cada bloco
        int profundidade = 0;    // Conta a "profundidade" de chaves { }
        int inicio = -1;         // Índice de início do bloco atual

        for (int i = 0; i < arrayJson.length(); i++) {
            char c = arrayJson.charAt(i); // Pega o caractere na posição i
            if (c == '{') {
                profundidade++; // Entramos em um objeto — aumenta profundidade
                if (profundidade == 1) inicio = i; // Marca o início do bloco de nível 1
            } else if (c == '}') {
                profundidade--; // Fechamos um objeto — diminui profundidade
                if (profundidade == 0 && inicio != -1) {
                    // Voltamos ao nível 0 — o bloco { } está completo
                    blocos.add(arrayJson.substring(inicio, i + 1)); // Adiciona o bloco à lista
                    inicio = -1; // Reseta o início para o próximo bloco
                }
            }
        }
        return blocos; // Retorna todos os blocos { } encontrados
    }

    // ── MÉTODO: Extrair conteúdo de um array JSON por nome de campo ────────────
    /**
     * Localiza um array JSON pelo nome do campo e retorna seu conteúdo interno [ ... ].
     * Ex: extrairArray(json, "favoritos") → " { ... }, { ... } "
     */
    private static String extrairConteudoArray(String json, String campo) {
        String padrao = "\"" + campo + "\": ["; // Padrão para encontrar o array
        int inicio = json.indexOf(padrao);
        if (inicio == -1) {
            // Tenta sem espaço após ":"
            padrao = "\"" + campo + "\": [";
            inicio = json.indexOf("\"" + campo + "\":");
            if (inicio == -1) return ""; // Campo não encontrado
        }

        // Avança até encontrar o [ de abertura do array
        int aberturaArray = json.indexOf('[', inicio);
        if (aberturaArray == -1) return "";

        // Usa contagem de colchetes para achar o fechamento ]
        int profundidade = 0;
        int fim = -1;
        for (int i = aberturaArray; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '[') profundidade++;
            else if (c == ']') {
                profundidade--;
                if (profundidade == 0) {
                    fim = i; // Achou o ] de fechamento correspondente
                    break;
                }
            }
        }
        if (fim == -1) return ""; // Array não foi fechado corretamente
        // Retorna o conteúdo entre [ e ] (sem incluí-los)
        return json.substring(aberturaArray + 1, fim);
    }

    // ── MÉTODO: Parsear JSON completo para objeto Usuario ─────────────────────
    /**
     * Recebe o conteúdo completo do arquivo JSON e reconstrói o objeto Usuario
     * com todas as 3 listas de séries. É o "carregamento" completo dos dados.
     */
    public static Usuario parsearUsuario(String jsonCompleto) {
        // Extrai o nome/apelido do usuário do JSON
        String nome = extrairValor(jsonCompleto, "nomeApelido");
        if (nome == null || nome.isEmpty()) nome = "Usuário"; // Nome padrão se não encontrado

        Usuario usuario = new Usuario(nome); // Cria o objeto usuario com o nome

        // Extrai e parseia cada uma das 3 listas
        String arrayFavs = extrairConteudoArray(jsonCompleto, "favoritos");
        for (String bloco : extrairBlocosJson(arrayFavs)) { // Para cada { } no array
            usuario.getFavoritos().add(parsearSerie(bloco)); // Adiciona à lista de favoritos
        }

        String arrayJa = extrairConteudoArray(jsonCompleto, "jaAssistidas");
        for (String bloco : extrairBlocosJson(arrayJa)) {
            usuario.getJaAssistidas().add(parsearSerie(bloco)); // Adiciona à lista de já assistidas
        }

        String arrayDeseja = extrairConteudoArray(jsonCompleto, "desejassistir");
        for (String bloco : extrairBlocosJson(arrayDeseja)) {
            usuario.getDesejassistir().add(parsearSerie(bloco)); // Adiciona à lista de deseja assistir
        }

        return usuario; // Retorna o objeto Usuario completamente reconstruído
    }
}