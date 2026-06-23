package fag.main.ui;

import fag.main.model.ListaTipo;
import fag.main.service.BibliotecaSeries;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Janela principal da aplicação, exibida após o login. Organiza a
 * navegação entre a tela de busca de séries e as três listas pessoais do
 * usuário (favoritos, já assistidas, desejo de assistir) através de um
 * menu lateral, trocando o conteúdo central com {@link CardLayout}.
 */
public class JanelaPrincipal extends JFrame {

    private static final String CARD_BUSCA = "busca";
    private static final String CARD_FAVORITOS = "favoritos";
    private static final String CARD_ASSISTIDAS = "assistidas";
    private static final String CARD_DESEJOS = "desejos";

    private final BibliotecaSeries biblioteca;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel painelConteudo = new JPanel(cardLayout);

    private PainelListas painelFavoritos;
    private PainelListas painelAssistidas;
    private PainelListas painelDesejos;

    private JPanel itemMenuSelecionado;

    public JanelaPrincipal(BibliotecaSeries biblioteca) {
        super("Minhas Séries de TV — " + biblioteca.getUsuarioAtual().getNome());
        this.biblioteca = biblioteca;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setMinimumSize(new Dimension(880, 560));
        setLocationRelativeTo(null);
        getContentPane().setBackground(Tema.FUNDO_PRINCIPAL);
        setLayout(new BorderLayout());

        add(criarMenuLateral(), BorderLayout.WEST);
        add(criarConteudoCentral(), BorderLayout.CENTER);
    }

    private JPanel criarMenuLateral() {
        JPanel menu = new JPanel();
        menu.setBackground(Tema.FUNDO_CARTAO);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBorder(new EmptyBorder(24, 18, 24, 18));
        menu.setPreferredSize(new Dimension(250, 0));

        JLabel logo = new JLabel("📺 Minhas Séries");
        logo.setFont(Tema.FONTE_SUBTITULO);
        logo.setForeground(Tema.TEXTO_PRIMARIO);
        logo.setAlignmentX(LEFT_ALIGNMENT);
        menu.add(logo);

        JLabel usuario = new JLabel("Olá, " + biblioteca.getUsuarioAtual().getNome() + "!");
        usuario.setFont(Tema.FONTE_PEQUENA);
        usuario.setForeground(Tema.TEXTO_SECUNDARIO);
        usuario.setAlignmentX(LEFT_ALIGNMENT);
        usuario.setBorder(new EmptyBorder(4, 0, 0, 0));
        menu.add(usuario);

        menu.add(Box.createVerticalStrut(30));

        menu.add(itemMenu("Buscar séries", CARD_BUSCA, true));
        menu.add(Box.createVerticalStrut(6));
        menu.add(itemMenu("Favoritos", CARD_FAVORITOS, false));
        menu.add(Box.createVerticalStrut(6));
        menu.add(itemMenu("Já assistidas", CARD_ASSISTIDAS, false));
        menu.add(Box.createVerticalStrut(6));
        menu.add(itemMenu("Quero assistir", CARD_DESEJOS, false));

        menu.add(Box.createVerticalGlue());

        JLabel rodape = new JLabel("<html>Dados fornecidos por<br><b>TVmaze.com</b></html>");
        rodape.setFont(Tema.FONTE_PEQUENA);
        rodape.setForeground(Tema.TEXTO_SECUNDARIO);
        rodape.setAlignmentX(LEFT_ALIGNMENT);
        menu.add(rodape);

        return menu;
    }

    private JPanel itemMenu(String texto, String cardAlvo, boolean selecionadoInicialmente) {
        JPanel item = new JPanel(new BorderLayout());
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        item.setOpaque(true);
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));
        item.setBorder(new EmptyBorder(0, 14, 0, 0));

        JLabel label = new JLabel(texto);
        label.setFont(Tema.FONTE_CORPO_NEGRITO);
        item.add(label, BorderLayout.WEST);

        aplicarEstiloItemMenu(item, label, selecionadoInicialmente);
        if (selecionadoInicialmente) {
            itemMenuSelecionado = item;
        }

        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selecionarItemMenu(item, label);
                cardLayout.show(painelConteudo, cardAlvo);
                if (cardAlvo.equals(CARD_FAVORITOS) && painelFavoritos != null) {
                    painelFavoritos.atualizarLista();
                } else if (cardAlvo.equals(CARD_ASSISTIDAS) && painelAssistidas != null) {
                    painelAssistidas.atualizarLista();
                } else if (cardAlvo.equals(CARD_DESEJOS) && painelDesejos != null) {
                    painelDesejos.atualizarLista();
                }
            }
        });

        item.putClientProperty("label", label);
        return item;
    }

    private void selecionarItemMenu(JPanel novoItem, JLabel novoLabel) {
        if (itemMenuSelecionado != null) {
            JLabel labelAnterior = (JLabel) itemMenuSelecionado.getClientProperty("label");
            aplicarEstiloItemMenu(itemMenuSelecionado, labelAnterior, false);
        }
        aplicarEstiloItemMenu(novoItem, novoLabel, true);
        itemMenuSelecionado = novoItem;
    }

    private void aplicarEstiloItemMenu(JPanel item, JLabel label, boolean selecionado) {
        item.setBackground(selecionado ? Tema.ACENTO_OURO : Tema.FUNDO_CARTAO);
        label.setForeground(selecionado ? Tema.TEXTO_SOBRE_OURO : Tema.TEXTO_PRIMARIO);
        item.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 0, 0, 0),
                new EmptyBorder(0, 14, 0, 0)));
    }

    private JPanel criarConteudoCentral() {
        painelConteudo.setOpaque(true);
        painelConteudo.setBackground(Tema.FUNDO_PRINCIPAL);

        Runnable aoMudarListas = this::atualizarTodasAsListas;

        PainelBusca painelBusca = new PainelBusca(this, biblioteca, aoMudarListas);
        painelFavoritos = new PainelListas(this, biblioteca, ListaTipo.FAVORITOS, aoMudarListas);
        painelAssistidas = new PainelListas(this, biblioteca, ListaTipo.ASSISTIDAS, aoMudarListas);
        painelDesejos = new PainelListas(this, biblioteca, ListaTipo.DESEJA_ASSISTIR, aoMudarListas);

        painelConteudo.add(painelBusca, CARD_BUSCA);
        painelConteudo.add(painelFavoritos, CARD_FAVORITOS);
        painelConteudo.add(painelAssistidas, CARD_ASSISTIDAS);
        painelConteudo.add(painelDesejos, CARD_DESEJOS);

        cardLayout.show(painelConteudo, CARD_BUSCA);
        return painelConteudo;
    }

    /**
     * Atualiza as três listas pessoais simultaneamente. É chamado sempre
     * que uma série é adicionada/removida de qualquer lista, em qualquer
     * tela, para que a navegação entre abas sempre mostre dados atuais.
     */
    private void atualizarTodasAsListas() {
        if (painelFavoritos != null) painelFavoritos.atualizarLista();
        if (painelAssistidas != null) painelAssistidas.atualizarLista();
        if (painelDesejos != null) painelDesejos.atualizarLista();
    }
}
