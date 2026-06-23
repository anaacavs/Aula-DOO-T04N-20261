package fag.main.ui;

import fag.main.api.ApiException;
import fag.main.api.TvMazeClient;
import fag.main.model.ListaTipo;
import fag.main.model.Show;
import fag.main.persistence.PersistenciaException;
import fag.main.service.BibliotecaSeries;
import fag.main.ui.components.BotaoArredondado;
import fag.main.ui.components.CartaoSerie;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

/**
 * Painel responsável por permitir que o usuário procure séries pelo nome
 * (consumindo a API da TVmaze) e visualize/gerencie rapidamente suas
 * listas pessoais a partir dos resultados encontrados.
 *
 * Estende {@link PainelComLista}, reaproveitando a estrutura visual comum
 * (cabeçalho + área de rolagem) e implementando apenas o que é específico
 * desta tela: o campo de busca e a chamada à API.
 *
 * As chamadas de rede são executadas em uma {@link SwingWorker}, garantindo
 * que a interface gráfica não trave enquanto aguarda a resposta da API, e
 * que qualquer falha de rede seja tratada com uma mensagem amigável em vez
 * de derrubar o programa.
 */
public class PainelBusca extends PainelComLista {

    private final TvMazeClient apiClient = new TvMazeClient();
    private final BibliotecaSeries biblioteca;
    private final Runnable aoMudarListas;

    private JTextField campoBusca;

    public PainelBusca(JFrame janelaPrincipal, BibliotecaSeries biblioteca, Runnable aoMudarListas) {
        super(janelaPrincipal);
        this.biblioteca = biblioteca;
        this.aoMudarListas = aoMudarListas;
        inicializarCabecalho();

        painelItens.add(criarMensagemCentralizada(
                "Digite o nome de uma série e pressione Enter ou clique em Buscar.",
                Tema.TEXTO_SECUNDARIO));
    }

    /**
     * Monta o cabeçalho desta tela: título e, logo abaixo, o campo de
     * busca com o botão "Buscar".
     */
    @Override
    protected JPanel montarCabecalho() {
        JPanel topo = new JPanel(new BorderLayout(Tema.ESPACAMENTO_MEDIO, 0));
        topo.setOpaque(false);

        JLabel titulo = new JLabel("Buscar séries");
        titulo.setFont(Tema.FONTE_TITULO);
        titulo.setForeground(Tema.TEXTO_PRIMARIO);
        topo.add(titulo, BorderLayout.NORTH);

        JPanel linhaBusca = new JPanel(new BorderLayout(Tema.ESPACAMENTO_MEDIO, 0));
        linhaBusca.setOpaque(false);
        linhaBusca.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));

        campoBusca = new JTextField();
        campoBusca.setFont(Tema.FONTE_CORPO);
        campoBusca.setForeground(Tema.TEXTO_PRIMARIO);
        campoBusca.setBackground(Tema.FUNDO_CAMPO);
        campoBusca.setCaretColor(Tema.TEXTO_PRIMARIO);
        campoBusca.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Tema.BORDA_SUTIL, 1, true),
        		BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        campoBusca.addActionListener(e -> executarBusca());
        linhaBusca.add(campoBusca, BorderLayout.CENTER);

        BotaoArredondado botaoBuscar = new BotaoArredondado("Buscar", BotaoArredondado.Variante.PRIMARIO);
        botaoBuscar.addActionListener(e -> executarBusca());
        linhaBusca.add(botaoBuscar, BorderLayout.EAST);

        topo.add(linhaBusca, BorderLayout.CENTER);
        return topo;
    }

    @Override
    protected String mensagemListaVazia() {
        return "Nenhuma série encontrada com esse nome.";
    }

    /**
     * Dispara a busca na API em segundo plano (SwingWorker), mantendo a
     * interface responsiva e tratando qualquer erro de forma amigável.
     */
    private void executarBusca() {
        String termo = campoBusca.getText();
        if (termo == null || termo.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "Digite o nome de uma série para buscar.",
                    "Campo vazio", JOptionPane.WARNING_MESSAGE);
            return;
        }

        exibirCarregando();

        SwingWorker<List<Show>, Void> worker = new SwingWorker<>() {
            private ApiException erroOcorrido;

            @Override
            protected List<Show> doInBackground() {
                try {
                    return apiClient.buscarSeriesPorNome(termo);
                } catch (ApiException e) {
                    this.erroOcorrido = e;
                    return null;
                }
            }

            @Override
            protected void done() {
                if (erroOcorrido != null) {
                    exibirErro(erroOcorrido.getMessage());
                    return;
                }
                try {
                    List<Show> resultados = get();
                    exibirResultados(resultados);
                } catch (Exception e) {
                    // InterruptedException ou ExecutionException inesperada:
                    exibirErro("Ocorreu um erro inesperado ao processar a busca: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void exibirCarregando() {
        painelItens.removeAll();
        painelItens.add(criarMensagemCentralizada("Buscando na TVmaze...", Tema.TEXTO_SECUNDARIO));
        painelItens.revalidate();
        painelItens.repaint();
    }

    private void exibirErro(String mensagem) {
        painelItens.removeAll();
        painelItens.add(criarMensagemCentralizada(
                "<html><div style='text-align:center;'>Não foi possível buscar as séries.<br>"
                        + mensagem + "</div></html>",
                Tema.ACENTO_VERMELHO));
        painelItens.revalidate();
        painelItens.repaint();
    }

    private void exibirResultados(List<Show> resultados) {
        painelItens.removeAll();

        if (resultados == null || resultados.isEmpty()) {
            painelItens.add(criarMensagemCentralizada(mensagemListaVazia(), Tema.TEXTO_SECUNDARIO));
        } else {
            for (Show show : resultados) {
                painelItens.add(criarCartaoResultado(show));
                painelItens.add(Box.createVerticalStrut(10));
            }
        }

        painelItens.revalidate();
        painelItens.repaint();
    }

    private CartaoSerie criarCartaoResultado(Show show) {
        CartaoSerie cartao = new CartaoSerie(show, s ->
        	new DetalheSerieDialog(janelaPrincipal, s, biblioteca, aoMudarListas).setVisible(true));
        cartao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        BotaoArredondado botaoFavorito = new BotaoArredondado("★ Favoritar", BotaoArredondado.Variante.SECUNDARIO);
        botaoFavorito.addActionListener(e -> alternarLista(ListaTipo.FAVORITOS, show, botaoFavorito,
                "Favoritar", " Favoritado"));

        BotaoArredondado botaoDesejo = new BotaoArredondado("+ Quero assistir", BotaoArredondado.Variante.SECUNDARIO);
        botaoDesejo.addActionListener(e -> alternarLista(ListaTipo.DESEJA_ASSISTIR, show, botaoDesejo,
                "+ Quero assistir", " Na lista de desejos"));

        atualizarTextoBotao(botaoFavorito, biblioteca.estaNaLista(ListaTipo.FAVORITOS, show), "Favoritar", "Favoritado");
        atualizarTextoBotao(botaoDesejo, biblioteca.estaNaLista(ListaTipo.DESEJA_ASSISTIR, show), "Quero assistir", "Na lista de desejos");

        cartao.adicionarBotaoAcao(botaoFavorito);
        cartao.adicionarBotaoAcao(botaoDesejo);

        return cartao;
    }

    private void alternarLista(ListaTipo tipo, Show show, BotaoArredondado botao, String rotuloInativo, String rotuloAtivo) {
        try {
            if (biblioteca.estaNaLista(tipo, show)) {
                biblioteca.removerDaLista(tipo, show);
            } else {
                biblioteca.adicionarNaLista(tipo, show);
            }
            atualizarTextoBotao(botao, biblioteca.estaNaLista(tipo, show), rotuloInativo, rotuloAtivo);
            if (aoMudarListas != null) {
                aoMudarListas.run();
            }
        } catch (PersistenciaException ex) {
            JOptionPane.showMessageDialog(this,
                    "Não foi possível salvar a alteração:\n" + ex.getMessage(),
                    "Erro ao salvar", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarTextoBotao(BotaoArredondado botao, boolean ativo, String rotuloInativo, String rotuloAtivo) {
        botao.setText(ativo ? rotuloAtivo : rotuloInativo);
        botao.repaint();
    }
}
