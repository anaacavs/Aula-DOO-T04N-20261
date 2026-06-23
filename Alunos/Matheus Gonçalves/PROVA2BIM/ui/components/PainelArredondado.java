package fag.main.ui.components;

import fag.main.ui.Tema;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Painel customizado com cantos arredondados e cor de fundo própria,
 * usado como "cartão" visual para destacar blocos de conteúdo (como o
 * cartão de detalhes de uma série, ou cada seção das telas).
 */
public class PainelArredondado extends JPanel {

    private Color corFundo;
    private int raio;

    public PainelArredondado(Color corFundo) {
        this(corFundo, Tema.RAIO_BORDA);
    }

    public PainelArredondado(Color corFundo, int raio) {
        this.corFundo = corFundo;
        this.raio = raio;
        setOpaque(false);
    }

    public void setCorFundo(Color corFundo) {
        this.corFundo = corFundo;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(corFundo);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), raio, raio);
        g2.dispose();
        super.paintComponent(g);
    }
}
