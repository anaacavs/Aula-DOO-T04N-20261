package fag.main.ui.components;

import fag.main.model.Show;
import fag.main.ui.CoresStatus;
import fag.main.ui.Tema;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Cartão visual que representa uma série dentro de uma lista (resultado de
 * busca, favoritos, assistidas ou desejo de assistir). Mostra um resumo das
 * principais informações da série e permite acoplar botões de ação
 * específicos do contexto onde o cartão está sendo usado (ex.: "Adicionar
 * aos favoritos" na busca, ou "Remover" dentro da própria lista).
 */
public class CartaoSerie extends PainelArredondado {

    public interface AoClicar {
        void clicado(Show show);
    }

    private final Show show;
    private final JPanel painelBotoes;

    public CartaoSerie(Show show, AoClicar aoClicarDetalhes) {
        super(Tema.FUNDO_CARTAO);
        this.show = show;

        setLayout(new BorderLayout(Tema.ESPACAMENTO_MEDIO, 0));
        setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // ---- Coluna central: nome + metadados ----
        JPanel painelInfo = new JPanel();
        painelInfo.setOpaque(false);
        painelInfo.setLayout(new BoxLayout(painelInfo, BoxLayout.Y_AXIS));

        JLabel nome = new JLabel(show.getName() != null ? show.getName() : "(sem nome)");
        nome.setFont(Tema.FONTE_DESTAQUE_CARTAO);
        nome.setForeground(Tema.TEXTO_PRIMARIO);
        nome.setAlignmentX(LEFT_ALIGNMENT);
        painelInfo.add(nome);

        painelInfo.add(Box.createVerticalStrut(4));

        String linhaDois = show.getGenerosFormatados() + "  •  " + show.getNomeEmissora();
        JLabel detalhes = new JLabel(linhaDois);
        detalhes.setFont(Tema.FONTE_CORPO);
        detalhes.setForeground(Tema.TEXTO_SECUNDARIO);
        detalhes.setAlignmentX(LEFT_ALIGNMENT);
        painelInfo.add(detalhes);

        painelInfo.add(Box.createVerticalStrut(8));

        JPanel linhaBadges = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        linhaBadges.setOpaque(false);
        linhaBadges.setAlignmentX(LEFT_ALIGNMENT);

        Selo seloStatus = new Selo(show.getStatusTraduzido(), CoresStatus.corPara(show));
        linhaBadges.add(seloStatus);

        JLabel nota = new JLabel("★ " + show.getNotaFormatada());
        nota.setFont(Tema.FONTE_CORPO_NEGRITO);
        nota.setForeground(Tema.ACENTO_OURO);
        linhaBadges.add(nota);

        JLabel estreia = new JLabel(show.getDataEstreiaFormatada());
        estreia.setFont(Tema.FONTE_PEQUENA);
        estreia.setForeground(Tema.TEXTO_SECUNDARIO);
        estreia.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
        linhaBadges.add(estreia);

        painelInfo.add(linhaBadges);

        add(painelInfo, BorderLayout.CENTER);

        // ---- Coluna direita: botões de ação (preenchidos externamente) ----
        painelBotoes = new JPanel();
        painelBotoes.setOpaque(false);
        painelBotoes.setLayout(new BoxLayout(painelBotoes, BoxLayout.Y_AXIS));
        add(painelBotoes, BorderLayout.EAST);

        setMaximumSize(new Dimension(Integer.MAX_VALUE, getPreferredSize().height));
        setAlignmentX(LEFT_ALIGNMENT);

        if (aoClicarDetalhes != null) {
            MouseAdapter listenerClique = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    aoClicarDetalhes.clicado(show);
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    setCorFundo(Tema.FUNDO_CARTAO_HOVER);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setCorFundo(Tema.FUNDO_CARTAO);
                }
            };
            addMouseListener(listenerClique);
            nome.addMouseListener(listenerClique);
            detalhes.addMouseListener(listenerClique);
            painelInfo.addMouseListener(listenerClique);
        }
    }

    /**
     * Permite que a tela que está montando a lista adicione botões de ação
     * específicos do contexto (ex.: favoritar, remover, marcar como
     * assistida) sem que este componente precise conhecer essas regras.
     */
    public void adicionarBotaoAcao(BotaoArredondado botao) {
        botao.setAlignmentX(RIGHT_ALIGNMENT);
        painelBotoes.add(botao);
        painelBotoes.add(Box.createVerticalStrut(6));
    }

    public Show getShow() {
        return show;
    }
}
