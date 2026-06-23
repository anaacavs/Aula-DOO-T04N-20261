package service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import model.Usuario;

/**
 * CLASSE SERVICE: Responsável por ler e escrever o arquivo JSON no disco.
 * Separa a responsabilidade de I/O (Input/Output — entrada/saída de arquivos)
 * do resto da aplicação. Se um dia mudarmos de JSON para XML, só mudamos aqui.
 */
public class PersistenciaService {

    // ── CONSTANTE: Caminho do arquivo JSON ─────────────────────────────────────
    // 'static final' = constante de classe. Centralizado aqui para fácil manutenção.
    // Se o professor quiser mudar o caminho, altera apenas essa linha.
    // Usamos double-backslash (\\) pois em Java a barra invertida é caractere especial.
    public static final String CAMINHO_ARQUIVO = "C:\\QuartoDoSaber\\dados.json";

    // ── MÉTODO: Salvar dados no arquivo JSON ───────────────────────────────────
    /**
     * Converte o objeto Usuario para JSON e grava no arquivo em disco.
     * Chamado automaticamente ao fechar o app (via WindowListener na View).
     */
    public static void salvar(Usuario usuario) throws IOException {
        // Cria os diretórios pai se não existirem (ex: C:\QuartoDoSaber\)
        // Paths.get() cria um objeto Path a partir do caminho em String
        File arquivo = new File(CAMINHO_ARQUIVO);
        arquivo.getParentFile().mkdirs(); // mkdirs() cria todos os diretórios necessários

        // Converte o objeto Usuario completo para uma String JSON
        String jsonConteudo = JsonParser.usuarioParaJson(usuario);

        // FileWriter com UTF-8 para suportar acentos e caracteres especiais
        // 'false' no segundo parâmetro significa que vai SOBRESCREVER o arquivo (não append)
        try (FileWriter fw = new FileWriter(arquivo, StandardCharsets.UTF_8, false)) {
            fw.write(jsonConteudo); // Escreve o JSON no arquivo
        }
        // O bloco try-with-resources fecha o FileWriter automaticamente ao terminar
        // mesmo que ocorra uma exceção — evita vazamento de recursos
    }

    // ── MÉTODO: Carregar dados do arquivo JSON ─────────────────────────────────
    /**
     * Lê o arquivo JSON do disco e reconstrói o objeto Usuario.
     * Se o arquivo não existir, cria um novo com dados de teste (Mock Data).
     * @return Usuario com todos os dados carregados
     * @throws IOException se ocorrer erro de leitura não recuperável
     */
    public static Usuario carregar() throws IOException {
        File arquivo = new File(CAMINHO_ARQUIVO); // Representa o arquivo em disco

        // ── Verifica se o arquivo existe ──────────────────────────────────────
        if (!arquivo.exists()) {
            // REGRA DE OURO: Arquivo não existe → cria com dados de teste
            // Isso permite que o professor teste o sistema imediatamente
            criarArquivoPadrao(); // Cria o arquivo com Mock Data
        }

        // ── Lê o conteúdo do arquivo ──────────────────────────────────────────
        // Files.readAllBytes lê o arquivo inteiro de uma vez como array de bytes
        byte[] bytes = Files.readAllBytes(Paths.get(CAMINHO_ARQUIVO));
        // Converte os bytes para String usando UTF-8 (suporte a acentos)
        String conteudo = new String(bytes, StandardCharsets.UTF_8);

        if (conteudo.trim().isEmpty()) {
            // Arquivo existe mas está vazio — recria com dados padrão
            criarArquivoPadrao();
            bytes = Files.readAllBytes(Paths.get(CAMINHO_ARQUIVO));
            conteudo = new String(bytes, StandardCharsets.UTF_8);
        }

        // Parseia o JSON para objeto Usuario e retorna
        return JsonParser.parsearUsuario(conteudo);
    }

    // ── MÉTODO PRIVADO: Criar arquivo com Mock Data (dados de teste) ───────────
    /**
     * Cria o arquivo JSON do zero já preenchido com dados para o professor testar.
     * Inclui séries conhecidas nas 3 listas com todos os campos preenchidos.
     * Este método é chamado APENAS se o arquivo não existir.
     */
    private static void criarArquivoPadrao() throws IOException {
        // Garante que o diretório existe antes de criar o arquivo
        File dir = new File(CAMINHO_ARQUIVO).getParentFile();
        if (dir != null) dir.mkdirs(); // Cria o diretório C:\QuartoDoSaber\ se não existir

        // ── MOCK DATA: Séries pré-definidas para teste ────────────────────────
        // Dados reais de séries conhecidas para o professor testar imediatamente.
        // Cada campo segue exatamente o que a TVMaze retornaria.

        String jsonPadrao = "{\n" +
            "  \"nomeApelido\": \"Samuel Babinski\",\n" +
            "  \"favoritos\": [\n" +
            "    {\n" +
            "      \"idTvMaze\": 82,\n" +
            "      \"nome\": \"Game of Thrones\",\n" +
            "      \"idioma\": \"English\",\n" +
            "      \"generos\": \"Drama, Adventure, Fantasy\",\n" +
            "      \"notaGeral\": 8.9,\n" +
            "      \"status\": \"Ended\",\n" +
            "      \"dataEstreia\": \"2011-04-17\",\n" +
            "      \"dataTermino\": \"2019-05-19\",\n" +
            "      \"emissora\": \"HBO\",\n" +
            "      \"imagemUrl\": \"https://static.tvmaze.com/uploads/images/medium_portrait/190/476117.jpg\",\n" +
            "      \"resumo\": \"Sete famílias nobres lutam pelo controle da terra mítica de Westeros.\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"idTvMaze\": 169,\n" +
            "      \"nome\": \"Breaking Bad\",\n" +
            "      \"idioma\": \"English\",\n" +
            "      \"generos\": \"Drama, Crime, Thriller\",\n" +
            "      \"notaGeral\": 9.2,\n" +
            "      \"status\": \"Ended\",\n" +
            "      \"dataEstreia\": \"2008-01-20\",\n" +
            "      \"dataTermino\": \"2013-09-29\",\n" +
            "      \"emissora\": \"AMC\",\n" +
            "      \"imagemUrl\": \"https://static.tvmaze.com/uploads/images/medium_portrait/0/2400.jpg\",\n" +
            "      \"resumo\": \"Um professor de química do ensino médio diagnosticado com câncer terminal se torna fabricante de metanfetamina.\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"jaAssistidas\": [\n" +
            "    {\n" +
            "      \"idTvMaze\": 1871,\n" +
            "      \"nome\": \"The Walking Dead\",\n" +
            "      \"idioma\": \"English\",\n" +
            "      \"generos\": \"Drama, Action, Horror\",\n" +
            "      \"notaGeral\": 8.1,\n" +
            "      \"status\": \"Ended\",\n" +
            "      \"dataEstreia\": \"2010-10-31\",\n" +
            "      \"dataTermino\": \"2022-11-20\",\n" +
            "      \"emissora\": \"AMC\",\n" +
            "      \"imagemUrl\": \"https://static.tvmaze.com/uploads/images/medium_portrait/0/2400.jpg\",\n" +
            "      \"resumo\": \"Sobreviventes de um apocalipse zumbi tentam reconstruir a civilização.\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"idTvMaze\": 180,\n" +
            "      \"nome\": \"Lost\",\n" +
            "      \"idioma\": \"English\",\n" +
            "      \"generos\": \"Drama, Adventure, Supernatural\",\n" +
            "      \"notaGeral\": 8.5,\n" +
            "      \"status\": \"Ended\",\n" +
            "      \"dataEstreia\": \"2004-09-22\",\n" +
            "      \"dataTermino\": \"2010-05-23\",\n" +
            "      \"emissora\": \"ABC\",\n" +
            "      \"imagemUrl\": \"https://static.tvmaze.com/uploads/images/medium_portrait/0/2400.jpg\",\n" +
            "      \"resumo\": \"Sobreviventes de um acidente de avião ficam presos em uma ilha misteriosa.\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"desejassistir\": [\n" +
            "    {\n" +
            "      \"idTvMaze\": 1409,\n" +
            "      \"nome\": \"Stranger Things\",\n" +
            "      \"idioma\": \"English\",\n" +
            "      \"generos\": \"Drama, Horror, Thriller\",\n" +
            "      \"notaGeral\": 8.8,\n" +
            "      \"status\": \"Running\",\n" +
            "      \"dataEstreia\": \"2016-07-15\",\n" +
            "      \"dataTermino\": \"\",\n" +
            "      \"emissora\": \"Netflix\",\n" +
            "      \"imagemUrl\": \"https://static.tvmaze.com/uploads/images/medium_portrait/0/2400.jpg\",\n" +
            "      \"resumo\": \"Em uma pequena cidade, eventos sobrenaturais se desencadeiam quando um garoto desaparece.\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"idTvMaze\": 143,\n" +
            "      \"nome\": \"The Wire\",\n" +
            "      \"idioma\": \"English\",\n" +
            "      \"generos\": \"Drama, Crime\",\n" +
            "      \"notaGeral\": 9.3,\n" +
            "      \"status\": \"Ended\",\n" +
            "      \"dataEstreia\": \"2002-06-02\",\n" +
            "      \"dataTermino\": \"2008-03-09\",\n" +
            "      \"emissora\": \"HBO\",\n" +
            "      \"imagemUrl\": \"https://static.tvmaze.com/uploads/images/medium_portrait/0/2400.jpg\",\n" +
            "      \"resumo\": \"Análise aprofundada de Baltimore através das perspectivas de policiais e criminosos.\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";

        // Grava o JSON padrão no arquivo usando UTF-8 para suporte a acentos
        try (FileWriter fw = new FileWriter(CAMINHO_ARQUIVO, StandardCharsets.UTF_8, false)) {
            fw.write(jsonPadrao); // Escreve os dados no arquivo
        }
    }
}