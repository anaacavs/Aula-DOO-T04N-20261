package fag.main.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import fag.main.model.Usuario;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * Responsável por toda a persistência local da aplicação. Os dados (lista de
 * usuários e suas respectivas listas de séries) são salvos em um único arquivo
 * JSON ("dados_series.json"), localizado na mesma pasta onde o programa é
 * executado, permitindo que as informações sejam mantidas entre uma abertura e
 * outra do sistema, conforme exigido pelo enunciado.
 */
public class PersistenciaService {

	private static final String NOME_ARQUIVO = "dados_series.json";

	private final File arquivoDados;
	private final ObjectMapper objectMapper;

	public PersistenciaService() {
		this.arquivoDados = new File(NOME_ARQUIVO);
		this.objectMapper = new ObjectMapper();
		// Deixa o JSON salvo em disco legível/identado, facilitando a
		// conferência manual do arquivo durante o desenvolvimento e correção.
		this.objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
	}

	/**
	 * Carrega todos os dados persistidos do disco. Caso o arquivo ainda não exista
	 * (primeira execução do programa), devolve uma estrutura vazia em vez de lançar
	 * exceção.
	 *
	 * @throws PersistenciaException se o arquivo existir mas estiver
	 *                               corrompido/ilegível
	 */
	public DadosPersistidos carregar() throws PersistenciaException {
		if (!arquivoDados.exists()) {
			return new DadosPersistidos();
		}

		try {
			if (arquivoDados.length() == 0) {
				return new DadosPersistidos();
			}
			return objectMapper.readValue(arquivoDados, DadosPersistidos.class);
		} catch (IOException e) {
			throw new PersistenciaException("Não foi possível ler o arquivo de dados local (" + NOME_ARQUIVO + "). "
					+ "O arquivo pode estar corrompido.", e);
		}
	}

	/**
	 * Salva a estrutura completa de dados no disco, sobrescrevendo o arquivo
	 * anterior.
	 *
	 * @throws PersistenciaException se não for possível gravar no disco (ex.: sem
	 *                               permissão de escrita)
	 */
	public void salvar(DadosPersistidos dados) throws PersistenciaException {
		try {
			objectMapper.writeValue(arquivoDados, dados);
		} catch (IOException e) {
			throw new PersistenciaException("Não foi possível salvar em " + arquivoDados.getAbsolutePath() + ". Causa: "
					+ e.getClass().getSimpleName() + " - " + e.getMessage(), e);
		}
	}

	/**
	 * Procura, dentro dos dados persistidos, um usuário com o nome informado
	 * (comparação ignorando maiúsculas/minúsculas e espaços nas pontas).
	 */
	public Optional<Usuario> buscarUsuarioPorNome(DadosPersistidos dados, String nome) {
		if (nome == null) {
			return Optional.empty();
		}
		String nomeBuscado = nome.trim();
		return dados.getUsuarios().stream()
				.filter(u -> u.getNome() != null && u.getNome().trim().equalsIgnoreCase(nomeBuscado)).findFirst();
	}
}
