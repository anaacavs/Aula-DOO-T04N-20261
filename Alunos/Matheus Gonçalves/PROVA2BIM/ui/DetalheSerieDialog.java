package fag.main.ui;

import fag.main.model.ListaTipo;
import fag.main.model.Show;
import fag.main.persistence.PersistenciaException;
import fag.main.service.BibliotecaSeries;
import fag.main.ui.components.BotaoArredondado;
import fag.main.ui.components.PainelArredondado;
import fag.main.ui.components.Selo;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

/**
 * Janela de detalhes de uma série, exibindo todas as informações exigidas
 * pelo enunciado (nome, idioma, gêneros, nota, estado, datas de estreia e
 * término, emissora) e permitindo adicionar/remover a série das três listas
 * pessoais do usuário (favoritos, assistidas, desejo de assistir).
 */
public class DetalheSerieDialog extends JDialog {

    private final BibliotecaSeries biblioteca;
    private final Show show;
    private final Runnable aoMudarListas;

    public DetalheSerieDialog(JFrame proprietario, Show show, BibliotecaSeries biblioteca, Runnable aoMudarListas) {
        super(proprietario, "Detalhes da série", true);
        this.biblioteca = biblioteca;
        this.show = show;
        this.aoMudarListas = aoMudarListas;

        getContentPane().setBackground(Tema.FUNDO_PRINCIPAL);
        setLayout(new BorderLayout());
        setSize(700, 680);
        setLocationRelativeTo(proprietario);
        setResizable(true);

        montarConteudo();
    }

    private void montarConteudo() {
        JPanel raiz = new JPanel();
        raiz.setBackground(Tema.FUNDO_PRINCIPAL);
        raiz.setLayout(new BoxLayout(raiz, BoxLayout.Y_AXIS));
        raiz.setBorder(BorderFactory.createEmptyBorder(22, 26, 22, 26));

        // ---- Cabeçalho: nome + status ----
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);
        cabecalho.setAlignmentX(LEFT_ALIGNMENT);

        JLabel nome = new JLabel(show.getName());
        nome.setFont(Tema.FONTE_TITULO);
        nome.setForeground(Tema.TEXTO_PRIMARIO);
        cabecalho.add(nome, BorderLayout.WEST);

        Selo selo = new Selo(show.getStatusTraduzido(), CoresStatus.corPara(show));
        JPanel painelSelo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 6));
        painelSelo.setOpaque(false);
        painelSelo.add(selo);
        cabecalho.add(painelSelo, BorderLayout.EAST);

        raiz.add(cabecalho);
        raiz.add(Box.createVerticalStrut(18));

        // ---- Grade de informações principais ----
        PainelArredondado grade = new PainelArredondado(Tema.FUNDO_CARTAO);
        grade.setLayout(new GridLayout(0, 2, 18, 14));
        grade.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        grade.setAlignmentX(LEFT_ALIGNMENT);

        grade.add(campoInfo("Nota geral", show.getNotaFormatada()));
        grade.add(campoInfo("Idioma", show.getLanguage() != null ? show.getLanguage() : "Não informado"));
        grade.add(campoInfo("Gêneros", show.getGenerosFormatados()));
        grade.add(campoInfo("Emissora", show.getNomeEmissora()));
        grade.add(campoInfo("Data de estreia", show.getDataEstreiaFormatada()));
        grade.add(campoInfo("Data de término", show.getDataTerminoFormatada()));

        raiz.add(grade);
        raiz.add(Box.createVerticalStrut(18));

        // ---- Sinopse ----
        JLabel rotuloSinopse = new JLabel("SINOPSE");
        rotuloSinopse.setFont(Tema.FONTE_PEQUENA.deriveFont(Font.BOLD));
        rotuloSinopse.setForeground(Tema.TEXTO_SECUNDARIO);
        rotuloSinopse.setAlignmentX(LEFT_ALIGNMENT);
        raiz.add(rotuloSinopse);
        raiz.add(Box.createVerticalStrut(6));

        JTextArea sinopse = new JTextArea(show.getResumoSemHtml());
        sinopse.setFont(Tema.FONTE_CORPO);
        sinopse.setForeground(Tema.TEXTO_PRIMARIO);
        sinopse.setBackground(Tema.FUNDO_PRINCIPAL);
        sinopse.setLineWrap(true);
        sinopse.setWrapStyleWord(true);
        sinopse.setEditable(false);
        sinopse.setAlignmentX(LEFT_ALIGNMENT);
        sinopse.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JScrollPane scrollSinopse = new JScrollPane(sinopse);
        scrollSinopse.setOpaque(false);
        scrollSinopse.getViewport().setOpaque(false);
        scrollSinopse.setBorder(BorderFactory.createEmptyBorder());
        scrollSinopse.setAlignmentX(LEFT_ALIGNMENT);
        scrollSinopse.setPreferredSize(new Dimension(560, 130));
        scrollSinopse.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        raiz.add(scrollSinopse);

        raiz.add(Box.createVerticalStrut(20));

        // ---- Botões de listas ----
        JLabel rotuloListas = new JLabel("MINHAS LISTAS");
        rotuloListas.setFont(Tema.FONTE_PEQUENA.deriveFont(Font.BOLD));
        rotuloListas.setForeground(Tema.TEXTO_SECUNDARIO);
        rotuloListas.setAlignmentX(LEFT_ALIGNMENT);
        raiz.add(rotuloListas);
        raiz.add(Box.createVerticalStrut(8));

        JPanel painelBotoesListas = new JPanel(new GridLayout(1, 3, 12, 0));
        painelBotoesListas.setOpaque(false);
        painelBotoesListas.setAlignmentX(LEFT_ALIGNMENT);
        painelBotoesListas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        painelBotoesListas.add(criarBotaoToggleLista(ListaTipo.FAVORITOS, "Favoritar"));
        painelBotoesListas.add(criarBotaoToggleLista(ListaTipo.ASSISTIDAS, "Já assisti"));
        painelBotoesListas.add(criarBotaoToggleLista(ListaTipo.DESEJA_ASSISTIR, "+ Quero assistir"));

        raiz.add(painelBotoesListas);
        raiz.add(Box.createVerticalStrut(16));

        // ---- Fechar ----
        BotaoArredondado fechar = new BotaoArredondado("Fechar", BotaoArredondado.Variante.SECUNDARIO);
        fechar.setAlignmentX(LEFT_ALIGNMENT);
        fechar.addActionListener(e -> dispose());
        raiz.add(fechar);

        JScrollPane scrollRaiz = new JScrollPane(raiz);
        scrollRaiz.setBorder(BorderFactory.createEmptyBorder());
        scrollRaiz.getViewport().setBackground(Tema.FUNDO_PRINCIPAL);
        scrollRaiz.getVerticalScrollBar().setUnitIncrement(16);
        scrollRaiz.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollRaiz, BorderLayout.CENTER);
    }

    private JPanel campoInfo(String rotulo, String valor) {
        JPanel painel = new JPanel();
        painel.setOpaque(false);
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));

        JLabel lblRotulo = new JLabel(rotulo.toUpperCase());
        lblRotulo.setFont(Tema.FONTE_PEQUENA.deriveFont(Font.BOLD));
        lblRotulo.setForeground(Tema.TEXTO_SECUNDARIO);
        lblRotulo.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(Tema.FONTE_CORPO_NEGRITO);
        lblValor.setForeground(Tema.TEXTO_PRIMARIO);
        lblValor.setAlignmentX(LEFT_ALIGNMENT);

        painel.add(lblRotulo);
        painel.add(Box.createVerticalStrut(3));
        painel.add(lblValor);
        return painel;
    }

    /**
     * Cria um botão que alterna (adiciona/remove) a série em uma lista
     * específica, atualizando o próprio texto do botão conforme o estado
     * atual, e tratando qualquer falha de persistência sem derrubar a
     * aplicação.
     */
    private BotaoArredondado criarBotaoToggleLista(ListaTipo tipo, String rotuloBase) {
        boolean presente = biblioteca.estaNaLista(tipo, show);
        BotaoArredondado botao = new BotaoArredondado(
                presente ? tipo.getDescricao() : rotuloBase,
                presente ? BotaoArredondado.Variante.PRIMARIO : BotaoArredondado.Variante.SECUNDARIO);

        botao.addActionListener(e -> {
            try {
                boolean jaEsta = biblioteca.estaNaLista(tipo, show);
                if (jaEsta) {
                    biblioteca.removerDaLista(tipo, show);
                    botao.setText(rotuloBase);
                } else {
                    biblioteca.adicionarNaLista(tipo, show);
                    botao.setText(tipo.getDescricao());
                }
                botao.repaint();
                if (aoMudarListas != null) {
                    aoMudarListas.run();
                }
            } catch (PersistenciaException ex) {
                JOptionPane.showMessageDialog(this,
                        "Não foi possível salvar a alteração:\n" + ex.getMessage(),
                        "Erro ao salvar", JOptionPane.ERROR_MESSAGE);
            }
        });
        return botao;
    }
}
