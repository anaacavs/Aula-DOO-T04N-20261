import java.io.*;           // File, FileWriter, FileReader, BufferedReader, etc.
import java.nio.charset.StandardCharsets;     // Files, Paths - API moderna para manipulação de arquivos
import java.nio.file.*; // encoding UTF-8
import java.util.ArrayList;
import java.util.List;

//persistencia = salvar os dados do usuario

public class PersistenciaService {

    // Nome do arquivo onde os dados são salvos (onde chama o json)
    private static final String ARQUIVO = "dados.json";

    public void salvar(Usuario usuario) throws IOException {
        StringBuilder json = new StringBuilder();

        // Abre o objeto JSON principal
        json.append("{\n");

        // Campo "nome" do usuário
        json.append("  \"nome\": \"").append(escaparJson(usuario.getNome())).append("\",\n");

        // Lista de favoritas
        json.append("  \"favoritas\": ");
        json.append(serializarLista(usuario.getFavoritas()));
        json.append(",\n");

        // Lista de assistidas
        json.append("  \"assistidas\": ");
        json.append(serializarLista(usuario.getAssistidas()));
        json.append(",\n");

        // Lista de séries que quer assistir
        json.append("  \"querAssistir\": ");
        json.append(serializarLista(usuario.getQuerAssistir()));
        json.append("\n");

        // Fecha o objeto JSON principal
        json.append("}");

        // Escreve o texto no arquivo com encoding UTF-8
        
        Files.write(
            Paths.get(ARQUIVO),
            json.toString().getBytes(StandardCharsets.UTF_8),
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING
        );
    }

    /**
     * Carrega os dados do arquivo dados.json e reconstrói o objeto Usuario.
     * Se o arquivo não existir, retorna null (indica primeiro acesso).
     *
     * @return Usuario carregado, ou null se o arquivo não existir
     * @throws Exception se o arquivo existir mas estiver corrompido/inválido
     */
    public Usuario carregar() throws Exception {
        File arquivo = new File(ARQUIVO);

        // Se o arquivo não existe, é o primeiro acesso → retorna null
        if (!arquivo.exists()) return null;

        // Lê todo o conteúdo do arquivo como String
        String json = new String(Files.readAllBytes(Paths.get(ARQUIVO)), StandardCharsets.UTF_8);

        // Extrai o nome do usuário do JSON
        String nome = extrairValorSimples(json, "\"nome\"");

        // Cria o objeto Usuario com o nome recuperado
        Usuario usuario = new Usuario(nome);

        // Recupera e popula cada lista
        usuario.getFavoritas().addAll(   deserializarLista(extrairBlocoLista(json, "\"favoritas\"")));
        usuario.getAssistidas().addAll(  deserializarLista(extrairBlocoLista(json, "\"assistidas\"")));
        usuario.getQuerAssistir().addAll(deserializarLista(extrairBlocoLista(json, "\"querAssistir\"")));

        return usuario;
    }

    // ── Serialização (Objeto Java → JSON String) ─────────────────────────────────

    //converte uma lista de objetos Serie em um array JSON
   
    private String serializarLista(List<Serie> lista) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");

        for (int i = 0; i < lista.size(); i++) {
            sb.append(serializarSerie(lista.get(i)));
            if (i < lista.size() - 1) sb.append(","); // vírgula entre itens, menos no último
            sb.append("\n");
        }

        sb.append("  ]");
        return sb.toString();
    }

   //converte um objeto Serie em um bloco JSON

    private String serializarSerie(Serie s) {
        return "    {\n" +
               "      \"id\": "          + s.getId()                               + ",\n" +
               "      \"nome\": \""      + escaparJson(s.getNome())                + "\",\n" +
               "      \"idioma\": \""    + escaparJson(s.getIdioma())              + "\",\n" +
               "      \"generos\": \""   + escaparJson(s.getGeneros())             + "\",\n" +
               "      \"nota\": "        + s.getNota()                             + ",\n" +
               "      \"status\": \""    + escaparJson(s.getStatus())              + "\",\n" +
               "      \"dataEstreia\": \"" + escaparJson(s.getDataEstreia())       + "\",\n" +
               "      \"dataFim\": \""   + escaparJson(nvl(s.getDataFim()))        + "\",\n" +
               "      \"emissora\": \""  + escaparJson(s.getEmissora())            + "\",\n" +
               "      \"resumo\": \""    + escaparJson(nvl(s.getResumo()))         + "\",\n" +
               "      \"imagemUrl\": \"" + escaparJson(nvl(s.getImagemUrl()))      + "\"\n" +
               "    }";
    }

    // ── Deserialização (JSON String → Objeto Java) ───────────────────────────────

   //converte um array JSON em uma lista de objetos Serie

    private List<Serie> deserializarLista(String arrayJson) {
        List<Serie> lista = new ArrayList<>();
        if (arrayJson == null || arrayJson.trim().equals("[]")) return lista;

        // Divide o array em blocos individuais de objeto Serie
        
        int i = 0;
        while (i < arrayJson.length()) {
            int inicio = arrayJson.indexOf("{", i);
            if (inicio == -1) break; // não há mais objetos

            // Encontra o } correspondente contando níveis de aninhamento
            int nivel = 0, fim = inicio;
            for (int j = inicio; j < arrayJson.length(); j++) {
                if (arrayJson.charAt(j) == '{') nivel++;
                else if (arrayJson.charAt(j) == '}') nivel--;
                if (nivel == 0) { fim = j; break; }
            }

            String blocoSerie = arrayJson.substring(inicio, fim + 1);
            Serie serie = deserializarSerie(blocoSerie);
            if (serie != null) lista.add(serie);

            i = fim + 1; // avança para depois do objeto processado
        }

        return lista;
    }

    /**
     * Converte um objeto JSON de uma série em objeto Java Serie.
     */
    private Serie deserializarSerie(String json) {
        try {
            int    id          = Integer.parseInt(extrairValorSimples(json, "\"id\""));
            String nome        = extrairValorSimples(json, "\"nome\"");
            String idioma      = extrairValorSimples(json, "\"idioma\"");
            String generos     = extrairValorSimples(json, "\"generos\"");
            double nota        = Double.parseDouble(extrairValorSimples(json, "\"nota\""));
            String status      = extrairValorSimples(json, "\"status\"");
            String dataEstreia = extrairValorSimples(json, "\"dataEstreia\"");
            String dataFim     = extrairValorSimples(json, "\"dataFim\"");
            String emissora    = extrairValorSimples(json, "\"emissora\"");
            String resumo      = extrairValorSimples(json, "\"resumo\"");
            String imagemUrl   = extrairValorSimples(json, "\"imagemUrl\"");

            return new Serie(id, nome, idioma, generos, nota, status,
                             dataEstreia, dataFim, emissora, resumo, imagemUrl);
        } catch (Exception e) {
            System.err.println("Erro ao deserializar série: " + e.getMessage());
            return null;
        }
    }

    // ── Utilitários ──────────────────────────────────────────────────────────────

   
    private String extrairValorSimples(String json, String chave) {
        int idx = json.indexOf(chave);
        if (idx == -1) return "";

        idx = json.indexOf(":", idx) + 1;
        while (idx < json.length() && json.charAt(idx) == ' ') idx++;

        if (idx >= json.length()) return "";

        if (json.charAt(idx) == '"') {
            // valor string: lê entre aspas respeitando escapes
            idx++;
            StringBuilder val = new StringBuilder();
            while (idx < json.length() && json.charAt(idx) != '"') {
                if (json.charAt(idx) == '\\' && idx + 1 < json.length()) {
                    idx++;
                    // converte sequências de escape comuns de volta
                    switch (json.charAt(idx)) {
                        case 'n': val.append('\n'); break;
                        case 't': val.append('\t'); break;
                        case '"': val.append('"'); break;
                        case '\\': val.append('\\'); break;
                        default: val.append(json.charAt(idx)); break;
                    }
                } else {
                    val.append(json.charAt(idx));
                }
                idx++;
            }
            return val.toString();
        } else {
            // valor numérico ou null
            int fim = idx;
            while (fim < json.length() && ",}\n".indexOf(json.charAt(fim)) == -1) fim++;
            return json.substring(idx, fim).trim();
        }
    }

   
    private String extrairBlocoLista(String json, String chave) {
        int inicio = json.indexOf(chave);
        if (inicio == -1) return "[]";

        inicio = json.indexOf("[", inicio);
        if (inicio == -1) return "[]";

        // Conta colchetes para pegar o array completo
        int nivel = 0, fim = inicio;
        for (int i = inicio; i < json.length(); i++) {
            if (json.charAt(i) == '[') nivel++;
            else if (json.charAt(i) == ']') nivel--;
            if (nivel == 0) { fim = i; break; }
        }

        return json.substring(inicio, fim + 1);
    }

    
    private String escaparJson(String texto) {
        if (texto == null) return "";
        return texto
            .replace("\\", "\\\\")  // \ → \\   (deve ser o primeiro!)
            .replace("\"", "\\\"")  // " → \"
            .replace("\n", "\\n")   // nova linha → \n
            .replace("\r", "\\r")   // retorno de carro → \r
            .replace("\t", "\\t");  // tabulação → \t
    }

    /**
     * Null-safe: retorna string vazia se o valor for null.
     * NVL = Null Value Logic (conceito vindo de SQL).
     */
    private String nvl(String valor) {
        return valor != null ? valor : "";
    }
}
