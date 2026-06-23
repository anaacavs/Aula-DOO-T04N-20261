package fag.main.ui.components;

import fag.main.ui.Tema;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Pequeno "selo" colorido (badge) usado para destacar visualmente o status
 * de uma série (em exibição, concluída, etc.) dentro das listas e telas de
 * detalhes, sem depender apenas de texto.
 */
public class Selo extends JLabel {

    private Color corFundo;

    public Selo(String texto, Color corFundo) {
        super(texto, SwingConstants.CENTER);
        this.corFundo = corFundo;
        setFont(Tema.FONTE_PEQUENA.deriveFont(java.awt.Font.BOLD));
        setForeground(Tema.TEXTO_PRIMARIO);
        setOpaque(false);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 10, 4, 10));
    }

    public void atualizar(String texto, Color corFundo) {
        setText(texto);
        this.corFundo = corFundo;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(corFundo);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }
}
