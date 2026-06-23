package fag.main.service;

import fag.main.model.CriterioOrdenacao;
import fag.main.model.ListaTipo;
import fag.main.model.Show;
import fag.main.model.Usuario;
import fag.main.persistence.DadosPersistidos;
import fag.main.persistence.PersistenciaException;
import fag.main.persistence.PersistenciaService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Classe de serviço que concentra toda a regra de negócio da aplicação:
 * controle do usuário atualmente logado, manipulação das três listas de
 * séries (favoritos, assistidas e desejo de assistir) e ordenação dessas
 * listas.
 *
 * Esta classe atua como uma fachada (Facade) entre a interface gráfica e as
 * camadas de persistência, mantendo a UI livre de regras de negócio.
 */
public class BibliotecaSeries {

    private final PersistenciaService persistenciaService;
    private DadosPersistidos dados;
    private Usuario usuarioAtual;

    public BibliotecaSeries() {
        this.persistenciaService = new PersistenciaService();
    }

    /**
     * Carrega o arquivo de dados local do disco. Deve ser chamado uma única
     * vez, logo na inicialização do programa. Mesmo em caso de falha, o
     * estado interno é deixado pronto para uso (com uma base vazia), para
     * que a aplicação possa optar por continuar funcionando normalmente
     * em vez de fechar.
     *
     * Na primeiríssima execução (quando o arquivo de dados ainda não existe
     * ou não contém nenhum usuário), um usuário de exemplo chamado
     * "Convidado" é criado automaticamente com algumas séries já
     * organizadas em suas listas.
     */
    public void carregarDados() throws PersistenciaException {
        this.dados = new DadosPersistidos();
        this.dados = persistenciaService.carregar();

        if (this.dados.getUsuarios().isEmpty()) {
            this.dados.getUsuarios().add(DadosIniciais.criarUsuarioExemplo());
            salvar();
        }
    }

    /**
     * Devolve a lista de nomes de todos os usuários já cadastrados
     * localmente, para ser exibida na tela inicial de login/seleção.
     */
    public List<String> listarNomesUsuarios() {
        List<String> nomes = new ArrayList<>();
        for (Usuario u : dados.getUsuarios()) {
            nomes.add(u.getNome());
        }
        return nomes;
    }

    /**
     * Define qual usuário está em uso. Caso o nome informado ainda não
     * exista, um novo usuário é criado automaticamente.
     */
    public void selecionarOuCriarUsuario(String nome) throws PersistenciaException {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome de usuário não pode ser vazio.");
        }

        String nomeTratado = nome.trim();
        this.usuarioAtual = persistenciaService.buscarUsuarioPorNome(dados, nomeTratado)
			.orElseGet(() -> {
			    Usuario novo = new Usuario(nomeTratado);
			    dados.getUsuarios().add(novo);
			    return novo;
			});

        salvar();
    }

    public Usuario getUsuarioAtual() {
        return usuarioAtual;
    }

    /**
     * Persiste imediatamente o estado atual em disco. Chamado após qualquer
     * alteração nas listas do usuário, garantindo que nada se perca caso o
     * programa seja fechado de forma abrupta.
     */
    public void salvar() throws PersistenciaException {
        persistenciaService.salvar(dados);
    }

    // ----------------------------------------------------------------
    // Manipulação das listas (adicionar / remover / verificar)
    // ----------------------------------------------------------------

    /**
     * Adiciona uma série à lista indicada do usuário atual. Caso a série já
     * esteja na lista, seus dados são apenas atualizados (evita duplicidade).
     */
    public void adicionarNaLista(ListaTipo tipo, Show show) throws PersistenciaException {
        validarUsuarioSelecionado();
        usuarioAtual.getLista(tipo).put(show.getId(), show);
        salvar();
    }

    /**
     * Remove uma série da lista indicada do usuário atual, caso ela esteja
     * presente. Não faz nada (e não lança erro) caso a série não esteja na lista.
     */
    public void removerDaLista(ListaTipo tipo, Show show) throws PersistenciaException {
        validarUsuarioSelecionado();
        usuarioAtual.getLista(tipo).remove(show.getId());
        salvar();
    }

    /**
     * Indica se a série já está presente na lista informada.
     */
    public boolean estaNaLista(ListaTipo tipo, Show show) {
        validarUsuarioSelecionado();
        return usuarioAtual.getLista(tipo).containsKey(show.getId());
    }

    /**
     * Devolve a lista (já convertida para List, em vez de Map) de séries do
     * usuário atual para o tipo informado, ordenada conforme o critério
     * solicitado.
     */
    public List<Show> obterListaOrdenada(ListaTipo tipo, CriterioOrdenacao criterio) {
        validarUsuarioSelecionado();
        Map<Integer, Show> mapa = usuarioAtual.getLista(tipo);
        List<Show> lista = new ArrayList<>(mapa.values());
        ordenar(lista, criterio);
        return lista;
    }

    /**
     * Ordena, em memória, a lista de séries informada de acordo com o
     * critério escolhido pelo usuário. A ordenação é feita "in-place"
     * (altera a própria lista recebida).
     */
    public void ordenar(List<Show> series, CriterioOrdenacao criterio) {
        if (criterio == null) {
            return;
        }
        Comparator<Show> comparador;
        switch (criterio) {
            case NOME:
                comparador = Comparator.comparing(s -> s.getName() == null ? "" : s.getName().toLowerCase());
                break;
            case NOTA:
                // Da maior nota para a menor (mais relevante para o usuário)
                comparador = Comparator.comparingDouble(Show::getNotaNumerica).reversed();
                break;
            case ESTADO:
                comparador = Comparator.comparingInt(Show::getOrdemStatus).thenComparing(s -> s.getName() == null ? "" : s.getName().toLowerCase());
                break;
            case DATA_ESTREIA:
                // Séries sem data de estreia conhecida vão para o final da lista
                comparador = Comparator.comparing(Show::getDataEstreia,Comparator.nullsLast(Comparator.naturalOrder()));
                break;
            default:
                return;
        }
        series.sort(comparador);
    }

    private void validarUsuarioSelecionado() {
        if (usuarioAtual == null) {
            throw new IllegalStateException("Nenhum usuário selecionado. Faça login antes de continuar.");
        }
    }
}
