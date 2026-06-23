package fag.main.ui.components;

import fag.main.ui.Tema;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Botão customizado com cantos arredondados, sem o visual "quadrado" padrão
 * do Swing/Look and Feel do sistema operacional. Suporta uma variante
 * "primária" (preenchida, cor de destaque) e uma variante "secundária"
 * (contorno apenas), para criar hierarquia visual clara entre ações.
 */
public class BotaoArredondado extends JButton {

    public enum Variante { PRIMARIO, SECUNDARIO, PERIGO, FANTASMA }

    private final Variante variante;
    private boolean mouseSobre = false;

    public BotaoArredondado(String texto, Variante variante) {
        super(texto);
        this.variante = variante;
        setFont(Tema.FONTE_BOTAO);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setForeground(corTexto());
        setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 18, 10, 18));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                mouseSobre = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                mouseSobre = false;
                repaint();
            }
        });
    }

    private Color corTexto() {
        switch (variante) {
            case PRIMARIO:
                return Tema.TEXTO_SOBRE_OURO;
            case PERIGO:
                return Tema.TEXTO_PRIMARIO;
            case SECUNDARIO:
            case FANTASMA:
            default:
                return Tema.TEXTO_PRIMARIO;
        }
    }

    private Color corFundo() {
        switch (variante) {
            case PRIMARIO:
                return mouseSobre ? Tema.ACENTO_OURO_HOVER : Tema.ACENTO_OURO;
            case PERIGO:
                return mouseSobre ? Tema.ACENTO_VERMELHO.brighter() : Tema.ACENTO_VERMELHO;
            case SECUNDARIO:
                return mouseSobre ? Tema.FUNDO_CARTAO_HOVER : Tema.FUNDO_CAMPO;
            case FANTASMA:
            default:
                return mouseSobre ? new Color(255, 255, 255, 18) : new Color(0, 0, 0, 0);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(corFundo());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), Tema.RAIO_BORDA, Tema.RAIO_BORDA);

        if (variante == Variante.SECUNDARIO) {
            g2.setColor(Tema.BORDA_SUTIL);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, Tema.RAIO_BORDA, Tema.RAIO_BORDA);
        }

        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    public boolean isOpaque() {
        return false;
    }
}
