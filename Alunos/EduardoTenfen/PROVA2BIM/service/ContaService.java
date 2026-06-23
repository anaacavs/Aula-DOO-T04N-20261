package service;

import model.Conta;
import model.Serie;
import model.Usuario;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * CLASSE SERVICE: Gerencia o arquivo de contas (contas.json).
 * Responsável por:
 *   - Carregar todas as contas do arquivo JSON
 *   - Salvar todas as contas de volta no arquivo
 *   - Registrar novas contas
 *   - Autenticar login + senha
 *
 * Arquivo separado do dados.json original para manter compatibilidade.
 * As contas ficam em: C:\QuartoDoSaber\contas.json
 */
public class ContaService {

    // CONSTANTE: Caminho do arquivo de contas 
    // Arquivo separado do dados.json — cada conta tem seu bloco com login, senha e listas
    public static final String CAMINHO_CONTAS = "C:\\QuartoDoSaber\\contas.json";

    // MÉTODO: Carregar todas as contas do arquivo 
    /**
     * Lê o arquivo contas.json e reconstrói a lista de objetos Conta.
     * Se o arquivo não existir, cria um com uma conta padrão de demonstração.
     * @return Lista com todas as contas cadastradas
     * @throws IOException se ocorrer erro de leitura não recuperável
     */
    public static List<Conta> carregarContas() throws IOException {
        File arquivo = new File(CAMINHO_CONTAS);

        // Garante que o diretório pai existe
        arquivo.getParentFile().mkdirs();

        if (!arquivo.exists() || arquivo.length() == 0) {
            // Arquivo não existe → cria com conta padrão de demonstração
            criarArquivoPadrao();
        }

        // Lê o conteúdo completo do arquivo
        byte[] bytes = Files.readAllBytes(Paths.get(CAMINHO_CONTAS));
        String json = new String(bytes, StandardCharsets.UTF_8);

        // Parseia o JSON e retorna a lista de contas
        return parsearContas(json);
    }

    // MÉTODO: Salvar todas as contas no arquivo
    /**
     * Converte a lista de Conta em JSON e grava no arquivo contas.json.
     * Chamado após qualquer operação que modifique as contas (login, cadastro, alteração de listas).
     * @param contas Lista completa de contas a salvar
     * @throws IOException se ocorrer erro de escrita
     */
    public static void salvarContas(List<Conta> contas) throws IOException {
        File arquivo = new File(CAMINHO_CONTAS);
        arquivo.getParentFile().mkdirs(); // Cria o diretório se não existir

        // Serializa a lista completa para JSON
        String json = contasParaJson(contas);

        // try-with-resources: fecha o FileWriter automaticamente mesmo com exceção
        try (FileWriter fw = new FileWriter(arquivo, StandardCharsets.UTF_8, false)) {
            fw.write(json); // Sobrescreve o arquivo com os dados atualizados
        }
    }

    // MÉTODO: Autenticar (verificar login + senha) 
    /**
     * Procura uma conta com o login fornecido e verifica se a senha bate.
     * @param login Login digitado pelo usuário
     * @param senha Senha digitada pelo usuário
     * @param contas Lista de todas as contas carregadas
     * @return A Conta encontrada, ou null se login/senha inválidos
     */
    public static Conta autenticar(String login, String senha, List<Conta> contas) {
        if (login == null || senha == null) return null; // Segurança: nulos não autenticam

        for (Conta conta : contas) {
            // equalsIgnoreCase: login não diferencia maiúsculas/minúsculas
            if (conta.getLogin().equalsIgnoreCase(login.trim())) {
                // Login encontrado — verifica a senha (case-sensitive)
                if (conta.verificarSenha(senha)) {
                    return conta; // Autenticação bem-sucedida
                } else {
                    return null; // Login correto mas senha errada
                }
            }
        }
        return null; // Login não encontrado
    }

    //  MÉTODO: Verificar se login já existe 
    /**
     * Verifica se já existe uma conta com o login informado.
     * Usado no cadastro para evitar duplicatas.
     * @return true se o login já está em uso
     */
    public static boolean loginExiste(String login, List<Conta> contas) {
        for (Conta conta : contas) {
            if (conta.getLogin().equalsIgnoreCase(login.trim())) {
                return true; // Login já cadastrado
            }
        }
        return false; // Login disponível
    }

    // MÉTODO: Registrar nova conta
    /**
     * Cria uma nova Conta, adiciona na lista e salva no arquivo.
     * Retorna mensagem de erro se o login já existir ou campos inválidos.
     * @return null se sucesso, ou String com a mensagem de erro
     */
    public static String registrar(String login, String senha, String confirmarSenha,
                                    List<Conta> contas) throws IOException {
        // Validações ANTI-QUEBRA 
        // Verifica login vazio
        if (login == null || login.trim().isEmpty()) {
            return "O campo Login não pode estar vazio.";
        }
        // Verifica senha vazia
        if (senha == null || senha.trim().isEmpty()) {
            return "O campo Senha não pode estar vazio.";
        }
        // Verifica confirmação de senha
        if (!senha.equals(confirmarSenha)) {
            return "As senhas não coincidem. Digite novamente.";
        }
        // Verifica tamanho mínimo da senha
        if (senha.length() < 3) {
            return "A senha deve ter pelo menos 3 caracteres.";
        }
        // Verifica se o login já está em uso
        if (loginExiste(login, contas)) {
            return "Este login já está em uso. Escolha outro.";
        }
        // Verifica caracteres inválidos no login (espaço quebra o JSON)
        if (login.contains(" ") || login.contains("\"")) {
            return "O login não pode conter espaços ou aspas.";
        }

        // Cria a conta e salva 
        Conta novaConta = new Conta(login.trim(), senha); // Cria objeto Conta
        contas.add(novaConta);    // Adiciona na lista em memória
        salvarContas(contas);     // Persiste no arquivo JSON

        return null; // null = sem erro = cadastro bem-sucedido
    }

    
    // SEÇÃO: Serialização e Parse de JSON (sem bibliotecas externas)

    // MÉTODO PRIVADO: Serializar lista de contas para JSON 
    /**
     * Converte a lista completa de Conta em texto JSON.
     * Estrutura: { "contas": [ { "login": "...", "senha": "...", "usuario": {...} }, ... ] }
     */
    private static String contasParaJson(List<Conta> contas) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"contas\": [\n"); // Array de contas

        for (int i = 0; i < contas.size(); i++) {
            Conta c = contas.get(i);
            sb.append("    {\n");
            // Serializa login e senha
            sb.append("      \"login\": \"").append(escapar(c.getLogin())).append("\",\n");
            sb.append("      \"senha\": \"").append(escapar(c.getSenha())).append("\",\n");
            // Serializa o objeto Usuario aninhado (com as 3 listas de séries)
            sb.append("      \"usuario\": ").append(usuarioParaJsonAninhado(c.getUsuario()));
            sb.append("\n    }");
            if (i < contas.size() - 1) sb.append(","); // Vírgula entre itens
            sb.append("\n");
        }

        sb.append("  ]\n");
        sb.append("}");
        return sb.toString();
    }

    // MÉTODO PRIVADO: Serializar Usuario para JSON (formato aninhado)
    /**
     * Converte um Usuario (com suas 3 listas) para JSON de forma aninhada.
     * É usado dentro do bloco de cada conta, diferente do formato do dados.json original.
     */
    private static String usuarioParaJsonAninhado(Usuario u) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("        \"nomeApelido\": \"").append(escapar(u.getNomeApelido())).append("\",\n");

        // Serializa cada lista de séries
        sb.append("        \"favoritos\": ").append(listaParaJson(u.getFavoritos(), "        ")).append(",\n");
        sb.append("        \"jaAssistidas\": ").append(listaParaJson(u.getJaAssistidas(), "        ")).append(",\n");
        sb.append("        \"desejassistir\": ").append(listaParaJson(u.getDesejassistir(), "        ")).append("\n");
        sb.append("      }");
        return sb.toString();
    }

    // MÉTODO PRIVADO: Serializar uma lista de séries para JSON 
    /**
     * Converte um List<Serie> em um array JSON: [ {...}, {...} ]
     * @param indent Indentação para formatação (espaços)
     */
    private static String listaParaJson(List<Serie> series, String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < series.size(); i++) {
            sb.append(serieParaJson(series.get(i), indent + "  ")); // Serializa cada série
            if (i < series.size() - 1) sb.append(","); // Vírgula entre itens
            sb.append("\n");
        }
        sb.append(indent).append("]");
        return sb.toString();
    }

    // MÉTODO PRIVADO: Serializar uma Serie para JSON 
    private static String serieParaJson(Serie s, String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append("{\n");
        sb.append(indent).append("  \"idTvMaze\": ").append(s.getIdTvMaze()).append(",\n");
        sb.append(indent).append("  \"nome\": \"").append(escapar(s.getNome())).append("\",\n");
        sb.append(indent).append("  \"idioma\": \"").append(escapar(s.getIdioma())).append("\",\n");
        sb.append(indent).append("  \"generos\": \"").append(escapar(s.getGeneros())).append("\",\n");
        sb.append(indent).append("  \"notaGeral\": ").append(s.getNotaGeral()).append(",\n");
        sb.append(indent).append("  \"status\": \"").append(escapar(s.getStatus())).append("\",\n");
        sb.append(indent).append("  \"dataEstreia\": \"").append(escapar(s.getDataEstreia())).append("\",\n");
        sb.append(indent).append("  \"dataTermino\": \"").append(escapar(s.getDataTermino())).append("\",\n");
        sb.append(indent).append("  \"emissora\": \"").append(escapar(s.getEmissora())).append("\",\n");
        sb.append(indent).append("  \"imagemUrl\": \"").append(escapar(s.getImagemUrl())).append("\",\n");
        sb.append(indent).append("  \"resumo\": \"").append(escapar(s.getResumo())).append("\"\n");
        sb.append(indent).append("}");
        return sb.toString();
    }

    // MÉTODO PRIVADO: Parse do JSON de contas para lista de objetos 
    /**
     * Recebe o conteúdo completo do arquivo contas.json e reconstrói a lista de Conta.
     * Usa as técnicas de parse manual com indexOf e substring (sem biblioteca externa).
     */
    private static List<Conta> parsearContas(String json) {
        List<Conta> contas = new ArrayList<>();

        // Localiza o array "contas": [ ... ]
        String arrayContas = extrairConteudoArray(json, "contas");
        if (arrayContas.trim().isEmpty()) return contas; // Arquivo sem contas

        // Divide o array em blocos individuais de conta { ... }
        List<String> blocos = extrairBlocos(arrayContas);

        for (String bloco : blocos) {
            try {
                // Extrai login e senha do bloco da conta
                String login = extrairValor(bloco, "login");
                String senha  = extrairValor(bloco, "senha");

                if (login.isEmpty()) continue; // Pula conta com login vazio (corrompida)

                // Extrai o sub-objeto "usuario": { ... }
                String blocoUsuario = extrairSubObjeto(bloco, "usuario");
                Usuario usuario;

                if (blocoUsuario.isEmpty()) {
                    // Usuario corrompido → cria um vazio com o login como nome
                    usuario = new Usuario(login);
                } else {
                    usuario = parsearUsuario(blocoUsuario, login);
                }

                contas.add(new Conta(login, senha, usuario)); // Adiciona à lista
            } catch (Exception e) {
                // Bloco corrompido → ignora esta conta e continua as demais
                System.err.println("Erro ao parsear conta: " + e.getMessage());
            }
        }
        return contas;
    }

    // MÉTODO PRIVADO: Parse do bloco de Usuario
    /**
     * Recebe o JSON do sub-objeto usuario e reconstrói o objeto Usuario com as 3 listas.
     */
    private static Usuario parsearUsuario(String blocoUsuario, String loginPadrao) {
        String nome = extrairValor(blocoUsuario, "nomeApelido");
        if (nome.isEmpty()) nome = loginPadrao; // Usa o login se nome estiver vazio

        Usuario usuario = new Usuario(nome);

        // Extrai e parseia cada lista de séries
        String arrayFavs = extrairConteudoArray(blocoUsuario, "favoritos");
        for (String b : extrairBlocos(arrayFavs)) {
            usuario.getFavoritos().add(parsearSerie(b));
        }

        String arrayJa = extrairConteudoArray(blocoUsuario, "jaAssistidas");
        for (String b : extrairBlocos(arrayJa)) {
            usuario.getJaAssistidas().add(parsearSerie(b));
        }

        String arrayDeseja = extrairConteudoArray(blocoUsuario, "desejassistir");
        for (String b : extrairBlocos(arrayDeseja)) {
            usuario.getDesejassistir().add(parsearSerie(b));
        }

        return usuario;
    }

    // MÉTODO PRIVADO: Parse de um bloco JSON de série 
    private static Serie parsearSerie(String bloco) {
        Serie s = new Serie();
        try {
            String id = extrairValor(bloco, "idTvMaze");
            s.setIdTvMaze(id.isEmpty() ? 0 : Integer.parseInt(id.trim()));
            s.setNome(extrairValor(bloco, "nome"));
            s.setIdioma(extrairValor(bloco, "idioma"));
            s.setGeneros(extrairValor(bloco, "generos"));
            String nota = extrairValor(bloco, "notaGeral");
            s.setNotaGeral(nota.isEmpty() ? 0.0 : Double.parseDouble(nota.trim()));
            s.setStatus(extrairValor(bloco, "status"));
            s.setDataEstreia(extrairValor(bloco, "dataEstreia"));
            s.setDataTermino(extrairValor(bloco, "dataTermino"));
            s.setEmissora(extrairValor(bloco, "emissora"));
            s.setImagemUrl(extrairValor(bloco, "imagemUrl"));
            s.setResumo(extrairValor(bloco, "resumo"));
        } catch (NumberFormatException e) {
            System.err.println("Numero invalido ao parsear serie: " + e.getMessage());
        }
        return s;
    }

    // SEÇÃO: Utilitários de parse de JSON (duplicados do JsonParser para autonomia)
    // Extrai o valor de um campo por nome (string ou número)
    private static String extrairValor(String json, String chave) {
        String padrao = "\"" + chave + "\":";
        int inicio = json.indexOf(padrao);
        if (inicio == -1) return "";
        inicio += padrao.length();
        while (inicio < json.length() && json.charAt(inicio) == ' ') inicio++;
        if (inicio >= json.length()) return "";
        char primeiro = json.charAt(inicio);
        if (primeiro == '"') {
            inicio++;
            StringBuilder val = new StringBuilder();
            while (inicio < json.length()) {
                char c = json.charAt(inicio);
                if (c == '\\') {
                    inicio++;
                    if (inicio < json.length()) {
                        char esc = json.charAt(inicio);
                        if      (esc == '"')  val.append('"');
                        else if (esc == '\\') val.append('\\');
                        else if (esc == 'n')  val.append('\n');
                        else if (esc == 'r')  val.append('\r');
                        else if (esc == 't')  val.append('\t');
                        else                  val.append(esc);
                    }
                } else if (c == '"') {
                    break;
                } else {
                    val.append(c);
                }
                inicio++;
            }
            return val.toString();
        } else if (primeiro == 'n') {
            return ""; // null
        } else {
            int fim = inicio;
            while (fim < json.length() && json.charAt(fim) != ',' &&
                   json.charAt(fim) != '}' && json.charAt(fim) != '\n') fim++;
            return json.substring(inicio, fim).trim();
        }
    }

    // Extrai o conteúdo de um array JSON pelo nome do campo
    private static String extrairConteudoArray(String json, String campo) {
        int inicio = json.indexOf("\"" + campo + "\":");
        if (inicio == -1) return "";
        int abre = json.indexOf('[', inicio);
        if (abre == -1) return "";
        int prof = 0, fim = -1;
        for (int i = abre; i < json.length(); i++) {
            if (json.charAt(i) == '[') prof++;
            else if (json.charAt(i) == ']') {
                prof--;
                if (prof == 0) { fim = i; break; }
            }
        }
        if (fim == -1) return "";
        return json.substring(abre + 1, fim);
    }

    // Extrai o conteúdo de um sub-objeto JSON pelo nome do campo
    private static String extrairSubObjeto(String json, String campo) {
        String padrao = "\"" + campo + "\":";
        int inicio = json.indexOf(padrao);
        if (inicio == -1) return "";
        int pos = inicio + padrao.length();
        while (pos < json.length() && json.charAt(pos) == ' ') pos++;
        if (pos >= json.length() || json.charAt(pos) != '{') return "";
        int prof = 0, fim = -1;
        for (int i = pos; i < json.length(); i++) {
            if (json.charAt(i) == '{') prof++;
            else if (json.charAt(i) == '}') {
                prof--;
                if (prof == 0) { fim = i; break; }
            }
        }
        if (fim == -1) return "";
        return json.substring(pos, fim + 1);
    }

    // Divide um array JSON em lista de blocos { }
    private static List<String> extrairBlocos(String arrayJson) {
        List<String> blocos = new ArrayList<>();
        int prof = 0, inicio = -1;
        for (int i = 0; i < arrayJson.length(); i++) {
            char c = arrayJson.charAt(i);
            if (c == '{') {
                prof++;
                if (prof == 1) inicio = i;
            } else if (c == '}') {
                prof--;
                if (prof == 0 && inicio != -1) {
                    blocos.add(arrayJson.substring(inicio, i + 1));
                    inicio = -1;
                }
            }
        }
        return blocos;
    }

    // Sanitiza string para JSON (igual ao JsonParser)
    private static String escapar(String valor) {
        if (valor == null) return "";
        return valor
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }

    // MÉTODO PRIVADO: Criar arquivo padrão com conta de demonstração
    /**
     * Cria o arquivo contas.json com uma conta demo pré-cadastrada.
     * Permite que o professor teste o login imediatamente sem cadastrar.
     * Login: demo | Senha: 1234
     */
    private static void criarArquivoPadrao() throws IOException {
        new File(CAMINHO_CONTAS).getParentFile().mkdirs();

        // Conta demo com séries pré-cadastradas para teste imediato
        String jsonPadrao = "{\n" +
            "  \"contas\": [\n" +
            "    {\n" +
            "      \"login\": \"demo\",\n" +
            "      \"senha\": \"1234\",\n" +
            "      \"usuario\": {\n" +
            "        \"nomeApelido\": \"Professor Avaliador\",\n" +
            "        \"favoritos\": [\n" +
            "          {\n" +
            "            \"idTvMaze\": 82,\n" +
            "            \"nome\": \"Game of Thrones\",\n" +
            "            \"idioma\": \"English\",\n" +
            "            \"generos\": \"Drama, Adventure, Fantasy\",\n" +
            "            \"notaGeral\": 8.9,\n" +
            "            \"status\": \"Ended\",\n" +
            "            \"dataEstreia\": \"2011-04-17\",\n" +
            "            \"dataTermino\": \"2019-05-19\",\n" +
            "            \"emissora\": \"HBO\",\n" +
            "            \"imagemUrl\": \"https://static.tvmaze.com/uploads/images/medium_portrait/190/476117.jpg\",\n" +
            "            \"resumo\": \"Sete familias nobres lutam pelo controle de Westeros.\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"idTvMaze\": 169,\n" +
            "            \"nome\": \"Breaking Bad\",\n" +
            "            \"idioma\": \"English\",\n" +
            "            \"generos\": \"Drama, Crime, Thriller\",\n" +
            "            \"notaGeral\": 9.2,\n" +
            "            \"status\": \"Ended\",\n" +
            "            \"dataEstreia\": \"2008-01-20\",\n" +
            "            \"dataTermino\": \"2013-09-29\",\n" +
            "            \"emissora\": \"AMC\",\n" +
            "            \"imagemUrl\": \"https://static.tvmaze.com/uploads/images/medium_portrait/0/2400.jpg\",\n" +
            "            \"resumo\": \"Professor de quimica se torna fabricante de metanfetamina.\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"jaAssistidas\": [\n" +
            "          {\n" +
            "            \"idTvMaze\": 1871,\n" +
            "            \"nome\": \"The Walking Dead\",\n" +
            "            \"idioma\": \"English\",\n" +
            "            \"generos\": \"Drama, Action, Horror\",\n" +
            "            \"notaGeral\": 8.1,\n" +
            "            \"status\": \"Ended\",\n" +
            "            \"dataEstreia\": \"2010-10-31\",\n" +
            "            \"dataTermino\": \"2022-11-20\",\n" +
            "            \"emissora\": \"AMC\",\n" +
            "            \"imagemUrl\": \"\",\n" +
            "            \"resumo\": \"Sobreviventes de um apocalipse zumbi.\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"desejassistir\": [\n" +
            "          {\n" +
            "            \"idTvMaze\": 1409,\n" +
            "            \"nome\": \"Stranger Things\",\n" +
            "            \"idioma\": \"English\",\n" +
            "            \"generos\": \"Drama, Horror, Thriller\",\n" +
            "            \"notaGeral\": 8.8,\n" +
            "            \"status\": \"Running\",\n" +
            "            \"dataEstreia\": \"2016-07-15\",\n" +
            "            \"dataTermino\": \"\",\n" +
            "            \"emissora\": \"Netflix\",\n" +
            "            \"imagemUrl\": \"\",\n" +
            "            \"resumo\": \"Eventos sobrenaturais em uma pequena cidade.\"\n" +
            "          }\n" +
            "        ]\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";

        try (FileWriter fw = new FileWriter(CAMINHO_CONTAS, StandardCharsets.UTF_8, false)) {
            fw.write(jsonPadrao);
        }
    }
}