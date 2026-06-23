package fag.main.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;

/**
 * Centraliza a identidade visual da aplicação: paleta de cores, fontes e
 * pequenas constantes de espaçamento. Mantendo esses valores em um único
 * lugar, toda a interface (telas, painéis, botões) permanece visualmente
 * consistente, e qualquer ajuste de "tema" passa a ser feito em um só ponto.
 *
 * Paleta inspirada em aplicativos de streaming: fundo escuro e elegante,
 * com um acento dourado (lembrando a estrela de avaliação das séries) e um
 * acento vermelho usado com moderação para ações de destaque/perigo.
 */
public final class Tema {

    private Tema() {
        // Classe utilitária, não deve ser instanciada
    }

    // ----------------------------------------------------------------
    // Paleta de cores
    // ----------------------------------------------------------------
    public static final Color FUNDO_PRINCIPAL = new Color(0x14, 0x17, 0x1C);
    public static final Color FUNDO_CARTAO = new Color(0x1E, 0x22, 0x2A);
    public static final Color FUNDO_CARTAO_HOVER = new Color(0x25, 0x2A, 0x34);
    public static final Color FUNDO_CAMPO = new Color(0x26, 0x2B, 0x35);
    public static final Color BORDA_SUTIL = new Color(0x2E, 0x33, 0x3D);

    public static final Color ACENTO_OURO = new Color(0xE8, 0xA3, 0x3D);
    public static final Color ACENTO_OURO_HOVER = new Color(0xF2, 0xB3, 0x57);
    public static final Color ACENTO_VERMELHO = new Color(0xE2, 0x57, 0x4C);
    public static final Color ACENTO_VERDE = new Color(0x4C, 0xAF, 0x82);
    public static final Color ACENTO_AZUL = new Color(0x5B, 0x9B, 0xD5);

    public static final Color TEXTO_PRIMARIO = new Color(0xF2, 0xF0, 0xEA);
    public static final Color TEXTO_SECUNDARIO = new Color(0x9A, 0xA1, 0xAC);
    public static final Color TEXTO_SOBRE_OURO = new Color(0x1A, 0x14, 0x05);

    // ----------------------------------------------------------------
    // Fontes
    // ----------------------------------------------------------------
    private static final String FAMILIA_BASE = escolherFonteDisponivel();

    public static final Font FONTE_TITULO = new Font(FAMILIA_BASE, Font.BOLD, 26);
    public static final Font FONTE_SUBTITULO = new Font(FAMILIA_BASE, Font.BOLD, 16);
    public static final Font FONTE_DESTAQUE_CARTAO = new Font(FAMILIA_BASE, Font.BOLD, 15);
    public static final Font FONTE_CORPO = new Font(FAMILIA_BASE, Font.PLAIN, 13);
    public static final Font FONTE_CORPO_NEGRITO = new Font(FAMILIA_BASE, Font.BOLD, 13);
    public static final Font FONTE_PEQUENA = new Font(FAMILIA_BASE, Font.PLAIN, 11);
    public static final Font FONTE_BOTAO = new Font(FAMILIA_BASE, Font.BOLD, 13);

    /**
     * Tenta usar "Segoe UI" (Windows) por ser uma fonte mais refinada;
     * caso não esteja disponível no sistema operacional, recorre à fonte
     * sans-serif padrão do Java, garantindo que a aplicação nunca quebre
     * por causa de uma fonte ausente.
     */
    private static String escolherFonteDisponivel() {
        String[] disponiveis = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for (String candidata : new String[] {"Segoe UI", "Inter", "Helvetica Neue", "Arial"}) {
            for (String nome : disponiveis) {
                if (nome.equalsIgnoreCase(candidata)) {
                    return candidata;
                }
            }
        }
        return Font.SANS_SERIF;
    }

    public static final int RAIO_BORDA = 12;
    public static final int ESPACAMENTO_PEQUENO = 6;
    public static final int ESPACAMENTO_MEDIO = 12;
    public static final int ESPACAMENTO_GRANDE = 24;
}
