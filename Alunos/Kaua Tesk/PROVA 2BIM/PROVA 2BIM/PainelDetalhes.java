import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.*;

public class PainelDetalhes extends JPanel {

    private Usuario  usuario;
    private Serie    serieAtual;
    private Runnable onListaAtualizada;

    private JLabel    lblImagem;
    private JLabel    lblNome;
    private JLabel    lblNota;
    private JLabel    lblStatus;
    private JLabel    lblIdioma;
    private JLabel    lblGeneros;
    private JLabel    lblDatas;
    private JLabel    lblEmissora;
    private JTextArea txtResumo;
    private JButton   btnFavorita;
    private JButton   btnAssistida;
    private JButton   btnQuerAssistir;

    public PainelDetalhes(Usuario usuario, Runnable onListaAtualizada) {
        this.usuario           = usuario;
        this.onListaAtualizada = onListaAtualizada;

        setBackground(new Color(25, 25, 25));
        // BorderLayout sem gap — o scroll vai ocupar CENTER e os botoes vao para SOUTH
        setLayout(new BorderLayout(0, 0));

        // SEM setPreferredSize fixo — deixamos o JSplitPane controlar a largura
        // Isso resolve o problema de nao centralizar e de scroll horizontal indesejado

        construirPainelInfo();
        construirPainelBotoes();
    }

    public void setUsuario(Usuario novoUsuario) {
        this.usuario    = novoUsuario;
        this.serieAtual = null;

        btnFavorita.setText("+ Favoritar");
        btnFavorita.setEnabled(false);
        btnAssistida.setText("+ Ja assisti");
        btnAssistida.setEnabled(false);
        btnQuerAssistir.setText("+ Quero assistir");
        btnQuerAssistir.setEnabled(false);

        lblNome.setText("<html><center>Selecione uma serie<br>para ver os detalhes</center></html>");
        lblNota.setText("");
        lblStatus.setText("");
        lblIdioma.setText("");
        lblGeneros.setText("");
        lblDatas.setText("");
        lblEmissora.setText("");
        txtResumo.setText("Resumo aparecera aqui.");
        lblImagem.setIcon(null);
        lblImagem.setText("Selecione uma serie");

        revalidate();
        repaint();
    }

    private void construirPainelInfo() {
        // painelConteudo: empilha verticalmente todos os componentes de informacao
        JPanel painelConteudo = new JPanel();
        painelConteudo.setLayout(new BoxLayout(painelConteudo, BoxLayout.Y_AXIS));
        painelConteudo.setBackground(new Color(25, 25, 25));
        painelConteudo.setBorder(BorderFactory.createEmptyBorder(16, 14, 12, 14));

        //poster imagem
        lblImagem = new JLabel("Selecione uma serie", SwingConstants.CENTER);
        lblImagem.setForeground(new Color(80, 80, 80));
        lblImagem.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        // Tamanho fixo do poster
        lblImagem.setPreferredSize(new Dimension(148, 208));
        lblImagem.setMaximumSize(new Dimension(148, 208));
        lblImagem.setMinimumSize(new Dimension(148, 208));
        lblImagem.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblImagem.setBorder(BorderFactory.createLineBorder(new Color(55, 55, 55)));
        lblImagem.setBackground(new Color(35, 35, 35));
        lblImagem.setOpaque(true);
        painelConteudo.add(lblImagem);
        painelConteudo.add(Box.createVerticalStrut(12));

        // ── Nome da serie ────────────────────────────────────────────────────────
        lblNome = new JLabel(
            "<html><center>Selecione uma serie<br>para ver os detalhes</center></html>",
            SwingConstants.CENTER
        );
        lblNome.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNome.setForeground(Color.WHITE);
        lblNome.setAlignmentX(Component.CENTER_ALIGNMENT);
        // setMaximumSize garante que o label nao ultrapasse a largura do painel
        // e forca a quebra de linha do HTML a funcionar corretamente
        lblNome.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        painelConteudo.add(lblNome);
        painelConteudo.add(Box.createVerticalStrut(5));

        // ── Nota ─────────────────────────────────────────────────────────────────
        lblNota = new JLabel("", SwingConstants.CENTER);
        lblNota.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNota.setForeground(new Color(255, 200, 0));
        lblNota.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblNota.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        painelConteudo.add(lblNota);
        painelConteudo.add(Box.createVerticalStrut(12));

        // ── Separador ────────────────────────────────────────────────────────────
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(55, 55, 55));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        painelConteudo.add(sep);
        painelConteudo.add(Box.createVerticalStrut(10));

        // ── Labels de informacao ──────────────────────────────────────────────────
        lblStatus   = criarLabelInfo("", new Color(200, 200, 200));
        lblIdioma   = criarLabelInfo("", new Color(180, 180, 180));
        lblGeneros  = criarLabelInfo("", new Color(180, 180, 180));
        lblDatas    = criarLabelInfo("", new Color(180, 180, 180));
        lblEmissora = criarLabelInfo("", new Color(180, 180, 180));

        for (JLabel l : new JLabel[]{lblStatus, lblIdioma, lblGeneros, lblDatas, lblEmissora}) {
            painelConteudo.add(l);
            painelConteudo.add(Box.createVerticalStrut(5));
        }

        painelConteudo.add(Box.createVerticalStrut(10));

        // ── Titulo da sinopse ─────────────────────────────────────────────────────
        JLabel lblResumoTitulo = criarLabelInfo("Sinopse (em ingles):", new Color(160, 160, 160));
        painelConteudo.add(lblResumoTitulo);
        painelConteudo.add(Box.createVerticalStrut(5));

        // ── Area de texto do resumo ───────────────────────────────────────────────
        // O JTextArea cresce conforme o texto — o scroll fica no JScrollPane externo
        txtResumo = new JTextArea("Resumo aparecera aqui.");
        txtResumo.setWrapStyleWord(true);   // quebra por palavra inteira
        txtResumo.setLineWrap(true);        // ativa quebra automatica de linha
        txtResumo.setEditable(false);
        txtResumo.setOpaque(true);
        txtResumo.setBackground(new Color(32, 32, 32));
        txtResumo.setForeground(new Color(165, 165, 165));
        txtResumo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtResumo.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        txtResumo.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Sem setMaximumSize aqui: o texto cresce livremente e o scroll externo cuida disso
        painelConteudo.add(txtResumo);

       
        JScrollPane scroll = new JScrollPane(painelConteudo);
        scroll.setBorder(null);
        scroll.setBackground(new Color(25, 25, 25));
        scroll.getViewport().setBackground(new Color(25, 25, 25));
        // NUNCA mostra barra horizontal — isso resolvia o scroll lateral indesejado
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(14);

        add(scroll, BorderLayout.CENTER);
    }

    private void construirPainelBotoes() {
        JPanel painel = new JPanel(new GridLayout(3, 1, 0, 8));
        painel.setBackground(new Color(25, 25, 25));
        painel.setBorder(BorderFactory.createEmptyBorder(8, 14, 14, 14));

        btnFavorita     = criarBotaoLista("+ Favoritar");
        btnAssistida    = criarBotaoLista("+ Ja assisti");
        btnQuerAssistir = criarBotaoLista("+ Quero assistir");

        // Desabilitados ate o usuario selecionar uma serie
        btnFavorita.setEnabled(false);
        btnAssistida.setEnabled(false);
        btnQuerAssistir.setEnabled(false);

        btnFavorita.addActionListener(    e -> toggleLista("favorita"));
        btnAssistida.addActionListener(   e -> toggleLista("assistida"));
        btnQuerAssistir.addActionListener(e -> toggleLista("querAssistir"));

        painel.add(btnFavorita);
        painel.add(btnAssistida);
        painel.add(btnQuerAssistir);

        add(painel, BorderLayout.SOUTH);
    }

    // ── Exibir serie ─────────────────────────────────────────────────────────────

    public void exibirSerie(Serie serie) {
        this.serieAtual = serie;

        btnFavorita.setEnabled(true);
        btnAssistida.setEnabled(true);
        btnQuerAssistir.setEnabled(true);

        lblNome.setText("<html><center>" + serie.getNome() + "</center></html>");

        lblNota.setText(serie.getNota() > 0
            ? "Nota: " + String.format("%.1f", serie.getNota()) + " / 10"
            : "Sem avaliacao");

        lblStatus.setText("Status: " + serie.getStatusTraduzido());
        lblStatus.setForeground(corDoStatus(serie.getStatus()));

        // HTML nos labels permite quebra de linha se o texto for longo,
        // evitando que ultrapasse a largura do painel
        lblIdioma.setText("<html>Idioma: " + serie.getIdioma() + "</html>");
        lblGeneros.setText("<html>Generos: " + serie.getGeneros() + "</html>");

        String dataFim = (serie.getDataFim() == null || serie.getDataFim().isEmpty()
                         || serie.getDataFim().equals("null"))
                         ? "em curso" : serie.getDataFim();
        lblDatas.setText("<html>Datas: " + serie.getDataEstreia() + " -> " + dataFim + "</html>");
        lblEmissora.setText("<html>Emissora: " + serie.getEmissora() + "</html>");

        txtResumo.setText(serie.getResumoLimpo());
        txtResumo.setCaretPosition(0); // volta para o inicio do texto

        atualizarEstadoBotoes();
        carregarPosterAsync(serie.getImagemUrl());

        revalidate();
        repaint();
    }


    private void toggleLista(String tipo) {
        if (serieAtual == null) return;

        switch (tipo) {
            case "favorita":
                if (usuario.getFavoritas().contains(serieAtual))
                    usuario.removerFavorita(serieAtual);
                else
                    usuario.adicionarFavorita(serieAtual);
                break;
            case "assistida":
                if (usuario.getAssistidas().contains(serieAtual))
                    usuario.removerAssistida(serieAtual);
                else
                    usuario.adicionarAssistida(serieAtual);
                break;
            default:
                if (usuario.getQuerAssistir().contains(serieAtual))
                    usuario.removerQuerAssistir(serieAtual);
                else
                    usuario.adicionarQuerAssistir(serieAtual);
                break;
        }

        atualizarEstadoBotoes();
        if (onListaAtualizada != null) onListaAtualizada.run();
    }

    private void atualizarEstadoBotoes() {
        if (serieAtual == null) return;

        configurarBotao(btnFavorita,
            usuario.getFavoritas().contains(serieAtual),
            "+ Favoritar", "- Remover favorito",
            new Color(180, 130, 0), new Color(50, 45, 30));

        configurarBotao(btnAssistida,
            usuario.getAssistidas().contains(serieAtual),
            "+ Ja assisti", "- Remover assistida",
            new Color(40, 160, 80), new Color(30, 50, 35));

        configurarBotao(btnQuerAssistir,
            usuario.getQuerAssistir().contains(serieAtual),
            "+ Quero assistir", "- Remover da lista",
            new Color(60, 130, 220), new Color(28, 38, 60));
    }

    private void configurarBotao(JButton botao, boolean ativo,
                                  String textoOff, String textoOn,
                                  Color corTextoOn, Color corFundoOn) {
        botao.setText(ativo ? textoOn : textoOff);
        if (ativo) {
            botao.setBackground(corFundoOn);
            botao.setForeground(corTextoOn);
            botao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corTextoOn, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
        } else {
            botao.setBackground(new Color(45, 45, 45));
            botao.setForeground(new Color(210, 210, 210));
            botao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
        }
    }

    // ── Carregamento de imagem ────────────────────────────────────────────────────

    private void carregarPosterAsync(String urlStr) {
        lblImagem.setIcon(null);
        lblImagem.setText("Carregando...");

        if (urlStr == null || urlStr.isEmpty() || urlStr.equals("null")) {
            lblImagem.setText("Sem imagem");
            return;
        }

        // Roda em thread separada para nao travar a interface enquanto baixa a imagem
        new Thread(() -> {
            try {
                URL url = new URL(urlStr);
                BufferedImage img = ImageIO.read(url);

                if (img != null) {
                    Image imgRedim = img.getScaledInstance(148, 208, Image.SCALE_SMOOTH);
                    ImageIcon icone = new ImageIcon(imgRedim);
                    // Atualiza o JLabel na EDT — obrigatorio no Swing
                    SwingUtilities.invokeLater(() -> {
                        lblImagem.setIcon(icone);
                        lblImagem.setText("");
                    });
                } else {
                    SwingUtilities.invokeLater(() -> lblImagem.setText("Sem imagem"));
                }
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> lblImagem.setText("Sem imagem"));
            }
        }).start();
    }

    // ── Utilitarios ──────────────────────────────────────────────────────────────

    private JLabel criarLabelInfo(String texto, Color cor) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(cor);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Impede que o label estica alem da largura do painel
        label.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        return label;
    }

    private JButton criarBotaoLista(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBackground(new Color(45, 45, 45));
        btn.setForeground(new Color(210, 210, 210));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return btn;
    }

    private Color corDoStatus(String status) {
        switch (status) {
            case "Running":          return new Color(70, 200, 110);
            case "To Be Determined": return new Color(255, 195, 0);
            case "Canceled":         return new Color(229, 9, 20);
            default:                 return new Color(140, 140, 140);
        }
    }
}
