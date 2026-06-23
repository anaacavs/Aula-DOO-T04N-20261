import java.awt.*;       // JDialog, JPanel, JLabel, JTextField, JButton, etc.
import java.awt.event.*;          // BorderLayout, BoxLayout, Color, Font, Insets, etc.
import javax.swing.*;    // ActionListener, KeyAdapter, KeyEvent


public class TelaLogin extends JDialog {

    // Guarda o nome digitado 
    private String nomeUsuario = null;

  
    public TelaLogin(Frame parent) {
        super(parent, "Bem-vindo ao SeriesTracker", true); // true = modal (bloqueante)

        // Fundo do conteúdo principal
        JPanel conteudo = new JPanel(new BorderLayout(0, 0));
        conteudo.setBackground(new Color(18, 18, 18));
        setContentPane(conteudo); // substitui o conteúdo padrão pelo nosso painel

        conteudo.add(criarPainelTopo(),       BorderLayout.NORTH);
        conteudo.add(criarPainelFormulario(), BorderLayout.CENTER);

        // Configurações da janela
        setSize(420, 380);
        setLocationRelativeTo(parent); // centraliza (na tela se parent for null)
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // X fecha sem travar
        setResizable(false);
    }

    // Painel superior

    private JPanel criarPainelTopo() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(new Color(18, 18, 18));
        painel.setBorder(BorderFactory.createEmptyBorder(35, 40, 15, 40));

        // ── Bloco decorativo superior com barras coloridas ───────────────────────
        // Usa texto puro (sem emoji) para garantir compatibilidade em qualquer SO
        JLabel lblDecor = new JLabel("▶  SERIES  TRACKER", SwingConstants.CENTER);
        lblDecor.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblDecor.setForeground(new Color(229, 9, 20)); // vermelho
        lblDecor.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(lblDecor);

        painel.add(Box.createVerticalStrut(6));

        // Linha decorativa vermelha abaixo do logo (simulada com JPanel colorido)
        JPanel linhaDeco = new JPanel();
        linhaDeco.setBackground(new Color(229, 9, 20));
        linhaDeco.setMaximumSize(new Dimension(60, 3));
        linhaDeco.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(linhaDeco);

        painel.add(Box.createVerticalStrut(18));

        // Título principal
        JLabel lblTitulo = new JLabel("Bem-vindo!", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(lblTitulo);

        painel.add(Box.createVerticalStrut(6));

        // Subtítulo descritivo
        JLabel lblSub = new JLabel("Acompanhe suas series favoritas", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(110, 110, 110));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(lblSub);

        return painel;
    }

    // Painel do formulário

    private JPanel criarPainelFormulario() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(new Color(18, 18, 18));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 45, 35, 45));

        // Label do campo
        JLabel lblNome = new JLabel("Como quer ser chamado?");
        lblNome.setForeground(new Color(180, 180, 180));
        lblNome.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblNome.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(lblNome);

        painel.add(Box.createVerticalStrut(8));

        // Campo de texto — estilizado manualmente para não depender do tema do SO
        JTextField txtNome = new JTextField();
        txtNome.setBackground(new Color(35, 35, 35));
        txtNome.setForeground(Color.WHITE);
        txtNome.setCaretColor(Color.WHITE);         // cursor de digitação branco
        txtNome.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNome.setOpaque(true);                    // ESSENCIAL: garante que o fundo seja pintado
        txtNome.setBorder(BorderFactory.createCompoundBorder(
            // Borda com destaque vermelho para indicar campo ativo
            BorderFactory.createLineBorder(new Color(229, 9, 20), 1),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        txtNome.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        txtNome.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Pressionar Enter equivale a clicar no botão
        txtNome.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) confirmar(txtNome);
            }
        });
        painel.add(txtNome);

        painel.add(Box.createVerticalStrut(22));

        // Botão de entrar — cor manual para não depender do tema do SO
        JButton btnEntrar = new JButton("ENTRAR NO SISTEMA");
        btnEntrar.setBackground(new Color(229, 9, 20));   // vermelho
        btnEntrar.setForeground(Color.WHITE);
        btnEntrar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnEntrar.setOpaque(true);                        // ESSENCIAL: pinta o fundo
        btnEntrar.setContentAreaFilled(true);             // preenche a área interna
        btnEntrar.setBorderPainted(false);                // sem borda padrão do look&feel
        btnEntrar.setFocusPainted(false);                 // sem contorno pontilhado de foco
        btnEntrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEntrar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btnEntrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btnEntrar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnEntrar.addActionListener(e -> confirmar(txtNome));
        painel.add(btnEntrar);

        return painel;
    }

    // ── Lógica de confirmação ─────────────────────────────────────────────────────

    /**
     * Valida o campo e armazena o nome do usuário.
     * Exibe aviso se o campo estiver vazio.
     * Fecha a janela (dispose) para liberar o bloqueio do modal.
     */
    private void confirmar(JTextField txtNome) {
        String nome = txtNome.getText().trim(); // trim() remove espaços das pontas

        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Por favor, insira seu nome ou apelido.",
                "Campo obrigatorio",
                JOptionPane.WARNING_MESSAGE
            );
            txtNome.requestFocus(); // devolve o foco ao campo
            return;
        }

        nomeUsuario = nome; // armazena para quem chamou setVisible(true)
        dispose();          // fecha a janela, liberando o bloqueio do modal em Main.java
    }

    /**
     * Retorna o nome digitado após setVisible(true) retornar.
     * Retorna null se o usuário fechou a janela sem confirmar.
     */
    public String getNomeUsuario() {
        return nomeUsuario;
    }
}
