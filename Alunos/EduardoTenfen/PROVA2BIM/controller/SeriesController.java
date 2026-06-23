package controller;

import model.Conta;
import model.Serie;
import model.Usuario;
import service.ContaService;
import service.TvMazeService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * CLASSE CONTROLLER: "Maestro" da aplicação.
 * Recebe ações da View, processa usando os Services e devolve resultados.
 * A View NUNCA fala diretamente com os Services — sempre passa pelo Controller.
 *
 * ATUALIZADO: agora recebe também a Conta logada e a lista completa de contas,
 * para salvar via ContaService (que grava todas as contas no contas.json).
 */
public class SeriesController {

    // Atributos
    private Usuario usuario;      // Dados do usuário logado (séries nas 3 listas)
    private Conta contaLogada;    // Conta completa (login + senha + usuario)
    private List<Conta> todasContas; // Todas as contas do arquivo (para salvar junto)

    // Construtor atualizado (com suporte a múltiplas contas)
    /**
     * @param usuario     Objeto Usuario com as listas de séries do usuário logado
     * @param contaLogada Conta autenticada (necessária para salvar corretamente)
     * @param todasContas Lista completa de contas (o arquivo salva TODAS juntas)
     */
    public SeriesController(Usuario usuario, Conta contaLogada, List<Conta> todasContas) {
        this.usuario      = usuario;
        this.contaLogada  = contaLogada;
        this.todasContas  = todasContas;
    }

    // Getters
    public Usuario getUsuario() { return usuario; }
    public Conta getContaLogada() { return contaLogada; }
    
    // SEÇÃO: BUSCA NA API    
    /**
     * Chama o TvMazeService para buscar séries pelo nome.
     * Valida o campo antes de fazer a requisição HTTP.
     */
    public List<Serie> buscarNaApi(String nomeBusca) throws Exception {
        if (nomeBusca == null || nomeBusca.trim().isEmpty()) {
            throw new IllegalArgumentException("Digite um nome para buscar.");
        }
        return TvMazeService.buscarPorNome(nomeBusca.trim());
    }

    
    // SEÇÃO: GERENCIAMENTO DAS LISTAS  
    /**
     * Adiciona uma série em uma das 3 listas do usuário logado.
     * Detecta duplicatas usando o equals() baseado no idTvMaze.
     */
    public String adicionarNaLista(Serie serie, String lista) {
        List<Serie> listaAlvo = pegarListaPorNome(lista);
        if (listaAlvo == null) return "Lista inválida.";
        if (listaAlvo.contains(serie)) {
            return "\"" + serie.getNome() + "\" já está nesta lista!";
        }
        listaAlvo.add(serie); // Adiciona no ArrayList do usuario em memória
        return "\"" + serie.getNome() + "\" adicionada com sucesso!";
    }

    /**
     * Remove uma série de uma das 3 listas do usuário logado.
     * remove() usa o equals() por idTvMaze para encontrar o item correto.
     */
    public String removerDaLista(Serie serie, String lista) {
        List<Serie> listaAlvo = pegarListaPorNome(lista);
        if (listaAlvo == null) return "Lista inválida.";
        boolean removeu = listaAlvo.remove(serie);
        return removeu
            ? "\"" + serie.getNome() + "\" removida da lista."
            : "Série não encontrada na lista.";
    }

    /** Mapeia nome de lista para o objeto List<Serie> correspondente */
    private List<Serie> pegarListaPorNome(String lista) {
        switch (lista.toLowerCase()) {
            case "favoritos":     return usuario.getFavoritos();
            case "assistidas":    return usuario.getJaAssistidas();
            case "desejassistir": return usuario.getDesejassistir();
            default:              return null;
        }
    }

    /** Retorna a lista de séries pelo índice do botão da sidebar (0, 1 ou 2) */
    public List<Serie> getListaPorIndice(int indice) {
        switch (indice) {
            case 0: return usuario.getFavoritos();
            case 1: return usuario.getJaAssistidas();
            case 2: return usuario.getDesejassistir();
            default: return new ArrayList<>();
        }
    }

    /** Retorna o nome interno da lista pelo índice (usado em adicionar/remover) */
    public String getNomeListaPorIndice(int indice) {
        switch (indice) {
            case 0: return "favoritos";
            case 1: return "assistidas";
            case 2: return "desejassistir";
            default: return "";
        }
    }

   
    // SEÇÃO: ORDENAÇÃO
    /**
     * Ordena uma cópia da lista conforme o critério escolhido.
     * Cria CÓPIA para não alterar a ordem original dos dados do usuário.
     * @param criterio 0=Nome, 1=Nota desc, 2=Status, 3=Data estreia
     */
    public List<Serie> ordenarLista(List<Serie> series, int criterio) {
        List<Serie> copia = new ArrayList<>(series); // Cópia independente
        switch (criterio) {
            case 0: // Alfabético A→Z (toLowerCase para ignorar maiúsculas)
                copia.sort(Comparator.comparing(s -> s.getNome() != null
                    ? s.getNome().toLowerCase() : ""));
                break;
            case 1: // Maior nota primeiro (decrescente) — Double.compare(b,a)
                copia.sort((a, b) -> Double.compare(b.getNotaGeral(), a.getNotaGeral()));
                break;
            case 2: // Por status: Running=0, TBD=1, Ended=2
                copia.sort(Comparator.comparing(s -> ordemStatus(s.getStatus())));
                break;
            case 3: // Por data de estreia (YYYY-MM-DD ordena corretamente como String)
                copia.sort(Comparator.comparing(s -> s.getDataEstreia() != null
                    ? s.getDataEstreia() : ""));
                break;
        }
        return copia;
    }

    /** Converte status em número para fins de ordenação */
    private int ordemStatus(String status) {
        if (status == null) return 99;
        switch (status) {
            case "Running":           return 0; // Em exibição — primeiro
            case "To Be Determined":  return 1; // Indefinido — segundo
            case "Ended":             return 2; // Encerrada — terceiro
            default:                  return 3;
        }
    }

 
    // SEÇÃO: PERSISTÊNCIA (salva via ContaService)
    /**
     * Salva TODAS as contas no arquivo contas.json.
     * O usuario da conta logada já está atualizado em memória (as listas foram
     * modificadas por referência), então simplesmente salvamos a lista completa.
     * @throws IOException se ocorrer erro ao gravar o arquivo
     */
    public void salvarDados() throws IOException {
        // garante que o usuario da conta logada está atualizado
        contaLogada.setUsuario(usuario);
        // Salva todas as contas (incluindo a logada com seus dados atualizados)
        ContaService.salvarContas(todasContas);
    }

    /**
     * Atualiza o nome/apelido exibido na sidebar.
     * Chamado quando o usuário edita o nome na interface.
     */
    public void setNomeUsuario(String novoNome) {
        if (novoNome != null && !novoNome.trim().isEmpty()) {
            usuario.setNomeApelido(novoNome.trim());
        }
    }
}