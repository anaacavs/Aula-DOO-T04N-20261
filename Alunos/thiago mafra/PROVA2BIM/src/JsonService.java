package service;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import model.DadosUsuario;
import model.Usuario;

// Classe responsável por salvar e carregar
// os dados do usuário utilizando arquivos JSON.
public class JsonService {

    // Nome do arquivo JSON que armazenará os dados
    private static final String ARQUIVO =
            "dadosUsuario.json";

    // Objeto do Jackson responsável por converter
    // objetos Java para JSON e vice-versa
    private final ObjectMapper mapper;

    // Construtor da classe
    public JsonService() {

        // Cria uma instância do ObjectMapper
        mapper = new ObjectMapper();

        // Habilita a formatação do JSON
        // deixando o arquivo mais legível
        mapper.enable(
                SerializationFeature.INDENT_OUTPUT);
    }

    // Método responsável por salvar os dados do usuário
    // no arquivo JSON
    public void salvar(DadosUsuario dados) {

        try {

            // Converte o objeto DadosUsuario
            // para JSON e salva no arquivo
            mapper.writeValue(
                    new File(ARQUIVO),
                    dados);

        } catch (IOException e) {

            // Exibe mensagem em caso de erro
            System.out.println(
                    "Erro ao salvar arquivo JSON.");

            // Mostra detalhes do erro no console
            e.printStackTrace();
        }
    }

    // Método responsável por carregar os dados
    // armazenados no arquivo JSON
    public DadosUsuario carregar() {

        try {

            // Cria um objeto File apontando
            // para o arquivo JSON
            File arquivo =
                    new File(ARQUIVO);

            // Verifica se o arquivo ainda não existe
            if (!arquivo.exists()) {

                // Cria um novo objeto de dados
                DadosUsuario dados =
                        new DadosUsuario();

                // Cria um usuário padrão
                dados.setUsuario(
                        new Usuario(
                                "Aluno",
                                "Visitante"));

                // Retorna os dados iniciais
                return dados;
            }

            // Lê o JSON e converte para objeto Java
            return mapper.readValue(
                    arquivo,
                    DadosUsuario.class);

        } catch (Exception e) {

            // Exibe o erro no console
            e.printStackTrace();

            // Caso ocorra erro, cria novos dados
            DadosUsuario dados =
                    new DadosUsuario();

            // Cria um usuário padrão
            dados.setUsuario(
                    new Usuario(
                            "Aluno",
                            "Visitante"));

            // Retorna os dados padrão para evitar
            // que a aplicação seja encerrada
            return dados;
        }
    }
}