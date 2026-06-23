package view;

import model.Conta;
import service.ContaService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * CLASSE VIEW: Tela de Login e Cadastro.
 * Exibida ANTES do MainFrame — é a primeira tela que o usuário vê.
 * É um JDialog modal: bloqueia a execução em Main.java até ser fechada.
 *
 * CORREÇÃO: Usa um único JPanel com CardLayout para trocar entre as telas
 * de Login e Cadastro. O painel de LOGIN é exibido por padrão ao abrir.
 */
@SuppressWarnings("serial")
public class LoginFrame extends JDialog {

    // Cores do tema WhatsApp Dark 
    private static final Color COR_FUNDO       = new Color(11, 20, 26);
    private static final Color COR_PAINEL      = new Color(17, 27, 33);
    private static final Color COR_HEADER      = new Color(32, 44, 51);
    private static final Color COR_INPUT       = new Color(42, 57, 66);
    private static final Color COR_VERDE       = new Color(0, 92, 75);
    private static final Color COR_VERDE_HOVER = new Color(0, 110, 90);
    private static final Color COR_VERDE_TEXT  = new Color(37, 211, 102);
    private static final Color COR_BORDA       = new Color(37, 51, 59);
    private static final Color COR_TEXTO       = new Color(233, 237, 239);
    private static final Color COR_TEXTO_SEC   = new Color(134, 150, 160);
    private static final Color COR_ERRO        = new Color(229, 57, 53);

    // Atributos principais
    private List<Conta> contas;         // Lista de todas as contas carregadas do JSON
    private Conta contaAutenticada;     // Preenchida ao fazer login com sucesso

    // CardLayout para trocar entre Login e Cadastro 
    // Guardamos referências ao CardLayout e ao painel que o usa,
    // para poder chamar cardLayout.show() nos ActionListeners dos botões.
    private CardLayout cardLayout;
    private JPanel painelCartoes;

    // Nomes das cartas — constantes evitam erros de digitação
    private static final String CARTA_LOGIN    = "LOGIN";
    private static final String CARTA_CADASTRO = "CADASTRO";

    // Botões de aba (precisamos acessá-los para mudar o estilo ativo/inativo)
    private JButton btnAbaLogin;
    private JButton btnAbaCadastro;

    // Componentes da tela de LOGIN 
    private JTextField    campoLogin;       // Campo onde o usuário digita o login
    private JPasswordField campoSenha;      // Campo da senha (exibe ● no lugar dos chars)
    private JLabel        labelErroLogin;   // Mensagem de erro em vermelho

    // Componentes da tela de CADASTRO 
    private JTextField     campoNovoLogin;    // Login desejado para a nova conta
    private JPasswordField campoNovaSenha;    // Senha da nova conta
    private JPasswordField campoConfirmar;    // Confirmação de senha
    private JLabel         labelErroCadastro; // Mensagem de erro em vermelho

    
    // CONSTRUTOR
    public LoginFrame(List<Conta> contas) {
        // JDialog modal (true): bloqueia o código em Main.java enquanto estiver aberto
        super((Frame) null, "Minhas Séries de TV — Login", true);
        this.contas = contas;
        configurarJanela();
        construirInterface();
    }

    /** Retorna a conta autenticada (null se o usuário fechou sem logar) */
    public Conta getContaAutenticada() {
        return contaAutenticada;
    }

    
    // CONFIGURAÇÃO DA JANELA
    private void configurarJanela() {
        setSize(1200, 700);           // Tamanho fixo
        setLocationRelativeTo(null); // Centraliza na tela
        setResizable(false);         // Não pode redimensionar
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // Fechar só este diálogo
        getContentPane().setBackground(COR_FUNDO);
        getContentPane().setLayout(new BorderLayout());
    }

   
    // CONSTRUÇÃO DA INTERFACE
    private void construirInterface() {
        // 1. Cabeçalho com ícone e título
        getContentPane().add(criarCabecalho(), BorderLayout.NORTH);

        // 2. Barra de abas (Entrar / Criar Conta)
        getContentPane().add(criarBarraAbas(), BorderLayout.CENTER);

        // 3. Rodapé com dica da conta demo
        getContentPane().add(criarRodape(), BorderLayout.SOUTH);
    }

    // Cabeçalho
    private JPanel criarCabecalho() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(COR_PAINEL);
        header.setBorder(new EmptyBorder(25, 20, 18, 20));

        JLabel icone = new JLabel("📺");
        icone.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        icone.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titulo = new JLabel("Minhas Séries de TV");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 19));
        titulo.setForeground(COR_TEXTO);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Sua coleção pessoal de séries");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(COR_TEXTO_SEC);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(icone);
        header.add(Box.createVerticalStrut(8));
        header.add(titulo);
        header.add(Box.createVerticalStrut(3));
        header.add(sub);

        return header;
    }

    // ── Barra de abas + painéis de conteúdo ───────────────────────────────────
    private JPanel criarBarraAbas() {
        // Painel externo: barra de botões em cima, cartões embaixo
        JPanel externo = new JPanel(new BorderLayout());
        externo.setBackground(COR_FUNDO);

        // ── Botões de aba ──────────────────────────────────────────────────────
        JPanel barraAbas = new JPanel(new GridLayout(1, 2));
        // GridLayout(1, 2): 1 linha, 2 colunas — os botões dividem a largura igual
        barraAbas.setBackground(COR_HEADER);

        btnAbaLogin    = criarBotaoAba("Entrar");
        btnAbaCadastro = criarBotaoAba("Criar Conta");

        // Estilo inicial: "Entrar" começa ativo, "Criar Conta" inativo
        estiloBotaoAtivo(btnAbaLogin);
        estiloBotaoInativo(btnAbaCadastro);

        // ActionListeners: ao clicar em cada botão, troca a carta visível
        btnAbaLogin.addActionListener(e -> {
            cardLayout.show(painelCartoes, CARTA_LOGIN); // Mostra o painel de login
            estiloBotaoAtivo(btnAbaLogin);
            estiloBotaoInativo(btnAbaCadastro);
            labelErroLogin.setVisible(false); // Limpa erro ao trocar de aba
        });

        btnAbaCadastro.addActionListener(e -> {
            cardLayout.show(painelCartoes, CARTA_CADASTRO); // Mostra o painel de cadastro
            estiloBotaoAtivo(btnAbaCadastro);
            estiloBotaoInativo(btnAbaLogin);
            labelErroCadastro.setVisible(false); // Limpa erro ao trocar de aba
        });

        barraAbas.add(btnAbaLogin);
        barraAbas.add(btnAbaCadastro);

        // ── Painel de cartões (CardLayout) ─────────────────────────────────────
        // CardLayout empilha os painéis e exibe um de cada vez.
        // Ao adicionar com nome "LOGIN" primeiro, ele começa visível por padrão.
        cardLayout   = new CardLayout();
        painelCartoes = new JPanel(cardLayout);
        painelCartoes.setBackground(COR_FUNDO);

        // ORDEM IMPORTA: o primeiro adicionado é o exibido ao abrir
        painelCartoes.add(criarPainelLogin(),    CARTA_LOGIN);    // visível por padrão
        painelCartoes.add(criarPainelCadastro(), CARTA_CADASTRO); // começa oculto

        // Força explicitamente mostrar LOGIN — garante o estado inicial correto
        cardLayout.show(painelCartoes, CARTA_LOGIN);

        externo.add(barraAbas,    BorderLayout.NORTH);
        externo.add(painelCartoes, BorderLayout.CENTER);

        return externo;
    }

    // ── Painel de LOGIN ────────────────────────────────────────────────────────
    private JPanel criarPainelLogin() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(COR_FUNDO);
        painel.setBorder(new EmptyBorder(28, 35, 20, 35));

        // Rótulo e campo de Login
        painel.add(criarRotulo("Login"));
        campoLogin = new JTextField();
        estilizarCampo(campoLogin);
        painel.add(campoLogin);
        painel.add(Box.createVerticalStrut(16));

        // Rótulo e campo de Senha
        painel.add(criarRotulo("Senha"));
        campoSenha = new JPasswordField();
        campoSenha.setEchoChar('\u2022'); // • substitui cada caractere digitado
        estilizarCampo(campoSenha);
        painel.add(campoSenha);
        painel.add(Box.createVerticalStrut(6));

        // Checkbox "Mostrar senha"
        painel.add(criarCheckMostrar("Mostrar senha", new JPasswordField[]{ campoSenha }));
        painel.add(Box.createVerticalStrut(8));

        // Label de erro (invisível até ocorrer erro)
        labelErroLogin = criarLabelErro();
        painel.add(labelErroLogin);
        painel.add(Box.createVerticalStrut(14));

        // Botão "Entrar"
        JButton btnEntrar = criarBotaoPrincipal("Entrar");
        btnEntrar.addActionListener(e -> executarLogin());
        painel.add(btnEntrar);

        // Pressionar Enter no campo de senha executa o login
        campoSenha.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) executarLogin();
            }
        });
        // Pressionar Enter no campo de login avança para o campo de senha
        campoLogin.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) campoSenha.requestFocus();
            }
        });

        return painel;
    }

    // ── Painel de CADASTRO ─────────────────────────────────────────────────────
    private JPanel criarPainelCadastro() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(COR_FUNDO);
        painel.setBorder(new EmptyBorder(20, 35, 20, 35));

        // Rótulo e campo de Login desejado
        painel.add(criarRotulo("Escolha um Login (sem espaços)"));
        campoNovoLogin = new JTextField();
        estilizarCampo(campoNovoLogin);
        painel.add(campoNovoLogin);
        painel.add(Box.createVerticalStrut(13));

        // Rótulo e campo de Senha
        painel.add(criarRotulo("Escolha uma Senha (mín. 3 caracteres)"));
        campoNovaSenha = new JPasswordField();
        campoNovaSenha.setEchoChar('\u2022');
        estilizarCampo(campoNovaSenha);
        painel.add(campoNovaSenha);
        painel.add(Box.createVerticalStrut(13));

        // Rótulo e campo de Confirmação de senha
        painel.add(criarRotulo("Confirmar Senha"));
        campoConfirmar = new JPasswordField();
        campoConfirmar.setEchoChar('\u2022');
        estilizarCampo(campoConfirmar);
        painel.add(campoConfirmar);
        painel.add(Box.createVerticalStrut(6));

        // Checkbox "Mostrar senhas" (afeta ambos os campos de senha)
        painel.add(criarCheckMostrar("Mostrar senhas",
                new JPasswordField[]{ campoNovaSenha, campoConfirmar }));
        painel.add(Box.createVerticalStrut(8));

        // Label de erro
        labelErroCadastro = criarLabelErro();
        painel.add(labelErroCadastro);
        painel.add(Box.createVerticalStrut(10));

        // Botão "Criar Conta"
        JButton btnCriar = criarBotaoPrincipal("Criar Conta");
        btnCriar.addActionListener(e -> executarCadastro());
        painel.add(btnCriar);

        // Enter no campo de confirmação executa o cadastro
        campoConfirmar.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) executarCadastro();
            }
        });

        return painel;
    }

    // ── Rodapé ────────────────────────────────────────────────────────────────
    private JPanel criarRodape() {
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.CENTER));
        rodape.setBackground(COR_HEADER);
        rodape.setBorder(new EmptyBorder(6, 10, 8, 10));

        JLabel dica = new JLabel("Conta demo: login = demo  |  senha = 1234");
        dica.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        dica.setForeground(COR_TEXTO_SEC);

        rodape.add(dica);
        return rodape;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // LÓGICA DE LOGIN E CADASTRO
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Valida os campos e tenta autenticar com ContaService.
     * ANTI-QUEBRA: valida campos vazios ANTES de chamar o service.
     */
    private void executarLogin() {
        String login = campoLogin.getText().trim();
        // getPassword() retorna char[] — new String() converte para String
        String senha = new String(campoSenha.getPassword());

        // Validação: campo login vazio
        if (login.isEmpty()) {
            mostrarErro(labelErroLogin, "Por favor, digite seu login.");
            campoLogin.requestFocus();
            return;
        }
        // Validação: campo senha vazio
        if (senha.isEmpty()) {
            mostrarErro(labelErroLogin, "Por favor, digite sua senha.");
            campoSenha.requestFocus();
            return;
        }

        // Tenta autenticar — ContaService compara login e senha com o arquivo JSON
        Conta conta = ContaService.autenticar(login, senha, contas);

        if (conta != null) {
            // ✅ Login bem-sucedido
            contaAutenticada = conta; // Armazena para o Main.java ler após dispose()
            dispose();                // Fecha o diálogo — Main.java continua
        } else {
            // ❌ Credenciais erradas — exibe erro, NÃO fecha o diálogo
            mostrarErro(labelErroLogin, "Login ou senha incorretos. Tente novamente.");
            campoSenha.setText(""); // Limpa apenas a senha (mantém o login)
            campoSenha.requestFocus();
        }
    }

    /**
     * Valida os campos e tenta criar uma nova conta via ContaService.
     * ContaService.registrar() retorna null se sucesso, ou String de erro.
     */
    private void executarCadastro() {
        String login     = campoNovoLogin.getText().trim();
        String senha     = new String(campoNovaSenha.getPassword());
        String confirmar = new String(campoConfirmar.getPassword());

        try {
            // ContaService faz todas as validações e cria a conta se válido
            String erro = ContaService.registrar(login, senha, confirmar, contas);

            if (erro != null) {
                // ContaService detectou um problema — exibe a mensagem de erro
                mostrarErro(labelErroCadastro, erro);
            } else {
                // ✅ Conta criada — faz login automático com a nova conta
                contaAutenticada = ContaService.autenticar(login, senha, contas);
                JOptionPane.showMessageDialog(
                    this,
                    "Conta criada com sucesso!\nBem-vindo, " + login + "!",
                    "Cadastro Realizado",
                    JOptionPane.INFORMATION_MESSAGE
                );
                dispose(); // Fecha o diálogo — Main.java abre o MainFrame
            }
        } catch (Exception ex) {
            // ANTI-QUEBRA: erro de I/O ao gravar o arquivo → avisa, não fecha
            mostrarErro(labelErroCadastro, "Erro ao salvar: " + ex.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // MÉTODOS AUXILIARES DE UI
    // ══════════════════════════════════════════════════════════════════════════

    /** Torna o label de erro visível e define o texto */
    private void mostrarErro(JLabel label, String mensagem) {
        label.setText(mensagem);
        label.setVisible(true);
    }

    /** Aplica estilo dark em qualquer JTextField ou JPasswordField */
    private void estilizarCampo(JTextField campo) {
        campo.setBackground(COR_INPUT);
        campo.setForeground(COR_TEXTO);
        campo.setCaretColor(COR_TEXTO); // Cor do cursor piscante
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38)); // Altura fixa
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COR_BORDA, 1), // Borda externa fina
            new EmptyBorder(6, 10, 6, 10)                 // Padding interno
        ));
    }

    /** Cria um rótulo de campo (ex: "Login", "Senha") */
    private JLabel criarRotulo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(COR_TEXTO_SEC);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(new EmptyBorder(0, 0, 4, 0));
        return label;
    }

    /** Cria o label vermelho de erro (começa invisível) */
    private JLabel criarLabelErro() {
        JLabel label = new JLabel(" ");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(COR_ERRO);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setVisible(false); // Invisível até ser necessário
        return label;
    }

    /** Cria um botão verde principal (Entrar / Criar Conta) */
    private JButton criarBotaoPrincipal(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(COR_VERDE);
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Efeito hover: cor mais clara ao passar o mouse
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(COR_VERDE_HOVER); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(COR_VERDE); }
        });
        return btn;
    }

    /** Cria um botão de aba (Entrar / Criar Conta) sem estilo ainda */
    private JButton criarBotaoAba(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(12, 10, 12, 10));
        return btn;
    }

    /** Aplica estilo ATIVO no botão de aba (texto verde, fundo escuro) */
    private void estiloBotaoAtivo(JButton btn) {
        btn.setBackground(COR_FUNDO);      // Fundo igual ao fundo geral — parece "selecionado"
        btn.setForeground(COR_VERDE_TEXT); // Texto verde brilhante
    }

    /** Aplica estilo INATIVO no botão de aba (texto cinza, fundo header) */
    private void estiloBotaoInativo(JButton btn) {
        btn.setBackground(COR_HEADER);   // Fundo cinza escuro
        btn.setForeground(COR_TEXTO_SEC); // Texto cinza apagado
    }

    /**
     * Cria um checkbox "Mostrar senha(s)" que alterna a visibilidade
     * de um ou mais JPasswordField ao mesmo tempo.
     * setEchoChar(0) = exibe os caracteres reais.
     * setEchoChar('•') = volta a ocultar.
     */
    private JPanel criarCheckMostrar(String texto, JPasswordField[] campos) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setBackground(COR_FUNDO);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        JCheckBox chk = new JCheckBox(texto);
        chk.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        chk.setForeground(COR_TEXTO_SEC);
        chk.setBackground(COR_FUNDO);
        chk.setFocusPainted(false);

        chk.addActionListener(e -> {
            // Itera todos os campos passados e alterna o echoChar
            char echo = chk.isSelected() ? (char) 0 : '\u2022';
            for (JPasswordField campo : campos) {
                campo.setEchoChar(echo);
            }
        });

        p.add(chk);
        return p;
    }
}