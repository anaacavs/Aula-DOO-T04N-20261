package fag.main.ui;

import fag.main.model.CriterioOrdenacao;
import fag.main.model.ListaTipo;
import fag.main.model.Show;
import fag.main.persistence.PersistenciaException;
import fag.main.service.BibliotecaSeries;
import fag.main.ui.components.BotaoArredondado;
import fag.main.ui.components.CartaoSerie;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

/**
 * Painel responsável por exibir uma das três listas pessoais do usuário
 * (favoritos, já assistidas ou desejo de assistir), permitindo reordenar a
 * lista pelos critérios exigidos pelo enunciado (nome, nota, estado, data
 * de estreia) e remover séries diretamente pela lista.
 *
 * Estende {@link PainelComLista}, reaproveitando a estrutura visual comum
 * (cabeçalho + área de rolagem) e implementando apenas o que é específico
 * desta tela: o combo de ordenação e a lógica de carregamento dos itens.
 */
public class PainelListas extends PainelComLista {

    private final BibliotecaSeries biblioteca;
    private final ListaTipo tipoLista;
    private final Runnable aoMudarListas;

    private JComboBox<CriterioOrdenacao> comboOrdenacao;

    public PainelListas(JFrame janelaPrincipal, BibliotecaSeries biblioteca, ListaTipo tipoLista, Runnable aoMudarListas) {
        super(janelaPrincipal);
        this.biblioteca = biblioteca;
        this.tipoLista = tipoLista;
        this.aoMudarListas = aoMudarListas;
        inicializarCabecalho();
        atualizarLista();
    }

    /**
     * Monta o cabeçalho desta tela: título com o nome da lista à esquerda
     * e o combo de critérios de ordenação à direita.
     */
    @Override
    protected JPanel montarCabecalho() {
        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);

        JLabel titulo = new JLabel(tipoLista.getDescricao());
        titulo.setFont(Tema.FONTE_TITULO);
        titulo.setForeground(Tema.TEXTO_PRIMARIO);
        topo.add(titulo, BorderLayout.WEST);

        JPanel painelOrdenacao = new JPanel();
        painelOrdenacao.setOpaque(false);
        painelOrdenacao.setLayout(new BoxLayout(painelOrdenacao, BoxLayout.X_AXIS));

        JLabel rotuloOrdenar = new JLabel("Ordenar por:  ");
        rotuloOrdenar.setFont(Tema.FONTE_CORPO);
        rotuloOrdenar.setForeground(Tema.TEXTO_SECUNDARIO);
        painelOrdenacao.add(rotuloOrdenar);

        comboOrdenacao = new JComboBox<>(CriterioOrdenacao.values());
        comboOrdenacao.setBackground(Tema.FUNDO_CAMPO);
        comboOrdenacao.setForeground(Tema.TEXTO_PRIMARIO);
        comboOrdenacao.setFont(Tema.FONTE_CORPO);
        comboOrdenacao.setPreferredSize(new Dimension(220, 34));
        comboOrdenacao.addActionListener(e -> atualizarLista());
        painelOrdenacao.add(comboOrdenacao);

        topo.add(painelOrdenacao, BorderLayout.EAST);
        return topo;
    }

    @Override
    protected String mensagemListaVazia() {
        switch (tipoLista) {
            case FAVORITOS:
                return "Você ainda não adicionou nenhuma série aos favoritos.";
            case ASSISTIDAS:
                return "Você ainda não marcou nenhuma série como assistida.";
            case DESEJA_ASSISTIR:
                return "Sua lista de séries para assistir está vazia.";
            default:
                return "Lista vazia.";
        }
    }

    /**
     * Recarrega os itens exibidos a partir dos dados mais atuais do usuário
     * (chamado tanto ao abrir a aba quanto após qualquer alteração de
     * lista feita em outras telas, como busca ou tela de detalhes).
     */
    public void atualizarLista() {
        CriterioOrdenacao criterio = (CriterioOrdenacao) comboOrdenacao.getSelectedItem();
        List<Show> series = biblioteca.obterListaOrdenada(tipoLista, criterio);

        painelItens.removeAll();

        if (series.isEmpty()) {
            painelItens.add(criarMensagemCentralizada(mensagemListaVazia(), Tema.TEXTO_SECUNDARIO));
        } else {
            for (Show show : series) {
                painelItens.add(criarCartaoItem(show));
                painelItens.add(Box.createVerticalStrut(10));
            }
        }

        painelItens.revalidate();
        painelItens.repaint();
    }

    private CartaoSerie criarCartaoItem(Show show) {
        CartaoSerie cartao = new CartaoSerie(show, s ->
                new DetalheSerieDialog(janelaPrincipal, s, biblioteca, () -> {
                    atualizarLista();
                    if (aoMudarListas != null) {
                        aoMudarListas.run();
                    }
                }).setVisible(true));
        cartao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        BotaoArredondado botaoRemover = new BotaoArredondado("Remover", BotaoArredondado.Variante.PERIGO);
        botaoRemover.addActionListener(e -> {
            int confirmacao = JOptionPane.showConfirmDialog(this,
                    "Remover \"" + show.getName() + "\" desta lista?",
                    "Confirmar remoção", JOptionPane.YES_NO_OPTION);
            if (confirmacao == JOptionPane.YES_OPTION) {
                try {
                    biblioteca.removerDaLista(tipoLista, show);
                    atualizarLista();
                    if (aoMudarListas != null) {
                        aoMudarListas.run();
                    }
                } catch (PersistenciaException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Não foi possível salvar a alteração:\n" + ex.getMessage(),
                            "Erro ao salvar", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        cartao.adicionarBotaoAcao(botaoRemover);

        return cartao;
    }
}
