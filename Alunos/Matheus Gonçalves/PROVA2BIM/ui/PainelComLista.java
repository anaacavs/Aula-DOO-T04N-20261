package fag.main.ui;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;

/**
 * Classe abstrata que representa a estrutura visual comum a todas as telas
 * que exibem uma lista rolável de séries dentro de um cartão por item
 * (a tela de busca e as três telas de listas pessoais). Concentra em um
 * único lugar o "esqueleto" de layout — cabeçalho com título, área de
 * rolagem para os cartões e mensagem central quando não há nada a mostrar
 * — evitando duplicar esse código em cada painel concreto.
 *
 * Esta classe segue o padrão de projeto conhecido como "Template Method":
 * o comportamento variável (qual título mostrar, como montar o cabeçalho
 * extra e qual texto exibir quando a lista está vazia) é delegado às
 * subclasses através de métodos abstratos, enquanto a estrutura geral da
 * tela permanece fixa e reaproveitada por todas elas.
 */
public abstract class PainelComLista extends JPanel {

    protected final JFrame janelaPrincipal;
    protected final JPanel painelItens;
    private final JPanel painelCabecalhoContainer;

    /**
     * Monta apenas a estrutura "casca" comum a todas as telas (área de
     * rolagem e um espaço reservado para o cabeçalho). Propositalmente NÃO
     * chama {@link #montarCabecalho()} aqui: como esse método é sobrescrito
     * pelas subclasses e costuma depender de campos que elas só terminam
     * de inicializar depois de chamar {@code super(...)}, chamá-lo neste
     * construtor arriscaria executá-lo antes desses campos existirem
     * (resultando em {@code NullPointerException}). Por isso, cada
     * subclasse deve chamar {@link #inicializarCabecalho()} explicitamente
     * como a última instrução do seu próprio construtor.
     */
    protected PainelComLista(JFrame janelaPrincipal) {
        this.janelaPrincipal = janelaPrincipal;

        setLayout(new BorderLayout(0, Tema.ESPACAMENTO_MEDIO));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(
                Tema.ESPACAMENTO_GRANDE, Tema.ESPACAMENTO_GRANDE,
                Tema.ESPACAMENTO_GRANDE, Tema.ESPACAMENTO_GRANDE));

        painelCabecalhoContainer = new JPanel(new BorderLayout());
        painelCabecalhoContainer.setOpaque(false);
        add(painelCabecalhoContainer, BorderLayout.NORTH);

        painelItens = new JPanel();
        painelItens.setOpaque(false);
        painelItens.setLayout(new javax.swing.BoxLayout(painelItens, javax.swing.BoxLayout.Y_AXIS));

        JScrollPane scroll = new JScrollPane(painelItens);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    /**
     * Deve ser chamado pela subclasse como última instrução do seu próprio
     * construtor, depois que todos os campos necessários para
     * {@link #montarCabecalho()} já tiverem sido inicializados.
     */
    protected final void inicializarCabecalho() {
        painelCabecalhoContainer.add(montarCabecalho(), BorderLayout.CENTER);
    }

    /**
     * Monta o painel de cabeçalho exibido no topo da tela (título e,
     * opcionalmente, outros controles como campo de busca ou combo de
     * ordenação). Cada subclasse decide o que faz sentido em seu contexto.
     */
    protected abstract JPanel montarCabecalho();

    /**
     * Texto exibido centralizado quando não há nenhum item para mostrar
     * na lista (ex.: "Você ainda não adicionou nenhuma série...").
     */
    protected abstract String mensagemListaVazia();

    /**
     * Componente de texto auxiliar, no padrão visual usado por toda a tela,
     * para exibir mensagens centralizadas (vazio, carregando, erro) dentro
     * da área de rolagem.
     */
    protected JLabel criarMensagemCentralizada(String texto, java.awt.Color cor) {
        JLabel label = new JLabel(texto);
        label.setFont(Tema.FONTE_CORPO);
        label.setForeground(cor);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(40, 20, 0, 20));
        return label;
    }
}
