package view;

import controller.SeriesController;
import model.Serie;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * CLASSE VIEW: A janela principal do aplicativo (MainFrame).
 * Estende JFrame — que é a janela base do Swing.
 * Toda a interface gráfica está aqui: sidebar estilo WhatsApp,
 * painel de busca, tabelas com listas e tela de detalhes.
 *
 * Arquitetura de telas: usa CardLayout no painel central, que funciona como
 * um "baralho de cartas" — exibe uma tela de cada vez e troca entre elas.
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame {

    // ═══════════════════════════════════════════════════════════════════════════
    // CONSTANTES DE CORES — Paleta WhatsApp Dark
    // ═══════════════════════════════════════════════════════════════════════════
    // 'static final' = constantes imutáveis. Centralizadas para fácil manutenção.
    // Color(r, g, b) cria uma cor com valores RGB de 0 a 255.
    private static final Color COR_FUNDO_SIDEBAR    = new Color(17, 27, 33);   // Fundo escuro da sidebar
    private static final Color COR_FUNDO_PRINCIPAL  = new Color(11, 20, 26);   // Fundo da área principal
    private static final Color COR_HEADER           = new Color(32, 44, 51);   // Cor do cabeçalho
    private static final Color COR_BOTAO_ATIVO      = new Color(0, 92, 75);    // Verde WhatsApp — botão selecionado
    private static final Color COR_BOTAO_HOVER      = new Color(42, 57, 66);   // Cor ao passar o mouse
    private static final Color COR_TEXTO_PRINCIPAL  = new Color(233, 237, 239);// Texto claro principal
    private static final Color COR_TEXTO_SECUNDARIO = new Color(134, 150, 160);// Texto cinza secundário
    private static final Color COR_INPUT            = new Color(42, 57, 66);   // Fundo dos campos de entrada
    private static final Color COR_LINHA_TABELA_PAR = new Color(20, 30, 38);   // Linhas pares da tabela
    private static final Color COR_LINHA_TABELA_IMPAR = new Color(17, 27, 33); // Linhas ímpares da tabela
    private static final Color COR_BORDA            = new Color(37, 51, 59);   // Cor das bordas/separadores
    private static final Color COR_VERDE            = new Color(37, 211, 102); // Verde para "Running"
    private static final Color COR_CINZA            = new Color(134, 150, 160);// Cinza para "Ended"
    private static final Color COR_AMARELO          = new Color(255, 213, 79); // Amarelo para "TBD"

    // ATRIBUTOS DA CLASSE — Componentes que precisam ser acessados por vários métodos
   
    private SeriesController controller;   // Controller que processa a lógica de negócio

    // Painel central com CardLayout (troca de telas) 
    private JPanel painelCentral;          // Painel que contém todas as "cartas" (telas)
    private CardLayout cardLayout;         // Gerenciador de layout que troca as telas

    // Nomes das "cartas" (constantes para evitar erros de digitação) 
    private static final String TELA_LISTA   = "LISTA";   // Tela que mostra a lista de séries
    private static final String TELA_BUSCA   = "BUSCA";   // Tela de pesquisa
    private static final String TELA_DETALHE = "DETALHE"; // Tela de detalhes da série

    // Componentes da Sidebar
    private JLabel labelNomeUsuario;       // Label que exibe o nome/apelido do usuário
    private JButton[] botoesSidebar;       // Array com os 3 botões de lista da sidebar
    private DefaultListModel<String> modeloListaRapida; // Modelo da lista rápida de nomes
    private JList<String> listaRapida;     // JList que exibe os nomes das séries na sidebar

    // Componentes da Tela de Busca 
    private JTextField campoBusca;         // Campo de texto onde o usuário digita a busca
    private DefaultTableModel modeloTabelaBusca; // Modelo de dados da tabela de resultados
    private JTable tabelaBusca;            // Tabela que exibe os resultados da busca
    private List<Serie> resultadosBusca;   // Lista com os objetos Serie retornados pela API

    // Componentes da Tela de Lista
    private DefaultTableModel modeloTabelaLista; // Modelo de dados da tabela da lista atual
    private JTable tabelaLista;            // Tabela que exibe as séries da lista selecionada
    private JLabel labelTituloLista;       // Título dinâmico ("Favoritos", "Assistidas", etc.)
    private JComboBox<String> comboOrdenacao; // Menu dropdown para escolher a ordenação
    private int indiceListaAtual = 0;      // Índice da lista sendo exibida (0, 1 ou 2)

    // Componentes da Tela de Detalhes
    private JLabel lblDetNome;             // Label do nome da série nos detalhes
    private JLabel lblDetNota;             // Label da nota da série
    private JLabel lblDetStatus;           // Label do status da série
    private JLabel lblDetEstreia;          // Label da data de estreia
    private JLabel lblDetTermino;          // Label da data de término
    private JLabel lblDetEmissora;         // Label da emissora
    private JLabel lblDetIdioma;           // Label do idioma
    private JLabel lblDetGeneros;          // Label dos gêneros
    private JTextArea areaResumo;          // Área de texto para o resumo/sinopse
    private JLabel lblDetImagem;           // Label que exibe a imagem da série
    private Serie serieSendoExibida;       // Objeto da série atualmente nos detalhes
    private String origemDetalhe;          // "busca" ou "lista" — de onde viemos

    // CONSTRUTOR
    /**
     * Constrói a janela principal.
     * @param controller O controller já inicializado com o usuario carregado do JSON
     */
    public MainFrame(SeriesController controller) {
        this.controller = controller; // Guarda referência ao controller

        configurarJanela();   // Define título, tamanho, comportamento de fechamento
        construirInterface(); // Monta todos os painéis e componentes na tela
        carregarListaAtual(); // Exibe a primeira lista (Favoritos) ao abrir
    }

 
    // CONFIGURAÇÃO DA JANELA
    private void configurarJanela() {
        // Define o título exibido na barra de título do sistema operacional
        setTitle("📺 Minhas Séries de TV");

        // Define o tamanho inicial da janela (largura x altura em pixels)
        setSize(1200, 750);

        // Centraliza a janela na tela do computador
        setLocationRelativeTo(null);

        // Define o tamanho mínimo para que o usuário não encolha a janela demais
        setMinimumSize(new Dimension(900, 600));

        // DO_NOTHING_ON_CLOSE: impede que a janela feche automaticamente ao clicar no X.
        // Fazemos isso para interceptar o fechamento e salvar os dados primeiro.
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // WindowListener: captura o evento de fechar a janela 
        // addWindowListener registra um "ouvinte" de eventos de janela.
        // WindowAdapter é uma classe abstrata que implementa WindowListener com métodos vazios,
        // permitindo sobrescrever apenas o windowClosing que nos interessa.
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Usuário clicou no X — pergunta se quer sair
                int resposta = JOptionPane.showConfirmDialog(
                    MainFrame.this,             // Janela pai do diálogo
                    "Deseja salvar e sair?",    // Mensagem
                    "Confirmar Saída",          // Título do diálogo
                    JOptionPane.YES_NO_CANCEL_OPTION, // Botões: Sim / Não / Cancelar
                    JOptionPane.QUESTION_MESSAGE      // Ícone de pergunta
                );

                if (resposta == JOptionPane.YES_OPTION) {
                    // Usuário escolheu "Sim" — tenta salvar antes de fechar
                    try {
                        controller.salvarDados(); // Chama o controller para salvar o JSON
                        dispose();               // Fecha a janela e libera recursos
                        System.exit(0);          // Encerra o processo Java completamente
                    } catch (Exception ex) {
                        // Se salvar falhar, avisa o usuário mas ainda assim fecha
                        JOptionPane.showMessageDialog(
                            MainFrame.this,
                            "Erro ao salvar: " + ex.getMessage() + "\nO programa será fechado mesmo assim.",
                            "Erro ao Salvar",
                            JOptionPane.ERROR_MESSAGE
                        );
                        System.exit(1); // Código 1 indica saída com erro
                    }
                } else if (resposta == JOptionPane.NO_OPTION) {
                    // Usuário escolheu "Não" — fecha sem salvar
                    dispose();
                    System.exit(0);
                }
                // Se "Cancelar" (ou fechou o diálogo), não faz nada — janela continua aberta
            }
        });
    }

   
    // MONTAGEM DA INTERFACE
    private void construirInterface() {
        // Define o layout da janela como BorderLayout.
        // BorderLayout divide a janela em 5 regiões: NORTH, SOUTH, EAST, WEST, CENTER
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(COR_FUNDO_PRINCIPAL);

        // Adiciona o painel lateral (sidebar) na região WEST (esquerda)
        getContentPane().add(criarSidebar(), BorderLayout.WEST);

        // Adiciona o painel central (área principal) na região CENTER
        getContentPane().add(criarPainelCentral(), BorderLayout.CENTER);
    }

    // SIDEBAR (PAINEL LATERAL)  
    private JPanel criarSidebar() {
        // JPanel com BoxLayout.Y_AXIS organiza os filhos em coluna (de cima para baixo)
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS)); // Empilha verticalmente
        sidebar.setBackground(COR_FUNDO_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(280, 0)); // Largura fixa de 280px
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, COR_BORDA)); // Borda direita

        // Cabeçalho da Sidebar (perfil do usuário) 
        sidebar.add(criarHeaderSidebar());

        // Separador visual entre cabeçalho e botões 
        sidebar.add(criarSeparador());

        // Botão de Busca na sidebar 
        JButton btnBuscar = criarBotaoSidebar("🔍  Buscar Séries", false);
        btnBuscar.addActionListener(e -> {
            // Ao clicar em "Buscar", exibe a tela de busca no painel central
            cardLayout.show(painelCentral, TELA_BUSCA); // Troca para a carta "BUSCA"
            desativarTodosBotoesSidebar(); // Remove destaque de todos os botões
            campoBusca.requestFocus();    // Foca o cursor no campo de digitação
        });
        sidebar.add(btnBuscar);
        sidebar.add(Box.createVerticalStrut(8)); // Espaço vertical de 8 pixels

        // Rótulo "MINHAS LISTAS" 
        JLabel labelSecao = new JLabel("  MINHAS LISTAS");
        labelSecao.setFont(new Font("Segoe UI", Font.BOLD, 11));
        labelSecao.setForeground(COR_TEXTO_SECUNDARIO);
        labelSecao.setBorder(new EmptyBorder(8, 15, 4, 0));
        labelSecao.setAlignmentX(Component.LEFT_ALIGNMENT); // Alinha à esquerda
        sidebar.add(labelSecao);

        // 3 Botões de Lista 
        // Cada botão corresponde a uma lista. Criamos um array para acessá-los depois.
        String[] nomesListas = {"⭐   Favoritos", "✅   Já Assistidas", "📺   Desejo Assistir"};
        botoesSidebar = new JButton[3]; // Array de 3 botões

        for (int i = 0; i < 3; i++) {
            final int indice = i; // 'final' necessário para usar dentro do lambda
            JButton btn = criarBotaoSidebar(nomesListas[i], i == 0); // Primeiro ativo por padrão
            botoesSidebar[i] = btn; // Armazena no array para controlar qual está ativo

            // ActionListener: executa quando o botão é clicado
            btn.addActionListener(e -> {
                indiceListaAtual = indice;         // Atualiza qual lista está selecionada
                destacarBotaoSidebar(indice);      // Destaca o botão clicado
                carregarListaAtual();              // Carrega os dados da lista na tabela
                cardLayout.show(painelCentral, TELA_LISTA); // Troca para a carta "LISTA"
            });
            sidebar.add(btn);
        }

        sidebar.add(Box.createVerticalStrut(12)); // Espaço antes da lista rápida
        sidebar.add(criarSeparador());

        // Lista rápida (JList com nomes das séries) 
        sidebar.add(criarListaRapida());

        return sidebar; // Retorna o painel sidebar completo
    }

    /**
     * Cria o cabeçalho da sidebar com o nome do usuário e botão de editar.
     */
    private JPanel criarHeaderSidebar() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COR_HEADER);
        header.setBorder(new EmptyBorder(15, 15, 15, 15)); // Padding interno
        header.setMaximumSize(new Dimension(280, 75));     // Altura máxima do header

        // Avatar circular (simulado com label) 
        JLabel avatar = new JLabel("👤");
        avatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32)); // Emoji grande
        avatar.setForeground(COR_TEXTO_PRINCIPAL);

        // Painel com nome e subtítulo 
        JPanel painelNome = new JPanel();
        painelNome.setLayout(new BoxLayout(painelNome, BoxLayout.Y_AXIS));
        painelNome.setBackground(COR_HEADER);
        painelNome.setBorder(new EmptyBorder(0, 10, 0, 0));

        // Label com o nome do usuário — carregado do JSON
        labelNomeUsuario = new JLabel(controller.getUsuario().getNomeApelido());
        labelNomeUsuario.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelNomeUsuario.setForeground(COR_TEXTO_PRINCIPAL);

        // Subtítulo fixo abaixo do nome
        JLabel subTitulo = new JLabel("Minhas Séries");
        subTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subTitulo.setForeground(COR_TEXTO_SECUNDARIO);

        painelNome.add(labelNomeUsuario); // Adiciona o nome ao painel
        painelNome.add(subTitulo);        // Adiciona o subtítulo ao painel

        // Botão de editar nome
        JButton btnEditar = new JButton("✏");
        btnEditar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        btnEditar.setBackground(COR_HEADER);
        btnEditar.setForeground(COR_TEXTO_SECUNDARIO);
        btnEditar.setBorderPainted(false);  // Remove borda padrão do botão
        btnEditar.setFocusPainted(false);   // Remove contorno de foco
        btnEditar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Cursor de mão
        btnEditar.setToolTipText("Editar nome/apelido"); // Dica ao passar o mouse

        // Ao clicar no lápis, abre um diálogo para editar o nome do usuário
        btnEditar.addActionListener(e -> {
            // JOptionPane.showInputDialog exibe uma caixa com campo de texto
            String novoNome = JOptionPane.showInputDialog(
                this,                                       // Janela pai
                "Digite seu nome ou apelido:",              // Mensagem
                controller.getUsuario().getNomeApelido()    // Valor inicial (nome atual)
            );
            // Só atualiza se o usuário digitou algo e não clicou em Cancelar
            if (novoNome != null && !novoNome.trim().isEmpty()) {
                controller.setNomeUsuario(novoNome.trim()); // Atualiza no modelo
                labelNomeUsuario.setText(novoNome.trim()); // Atualiza na tela imediatamente
            }
        });

        header.add(avatar, BorderLayout.WEST);    // Avatar na esquerda
        header.add(painelNome, BorderLayout.CENTER); // Nome no centro
        header.add(btnEditar, BorderLayout.EAST); // Botão editar na direita

        return header;
    }

    /**
     * Cria a lista rápida na parte inferior da sidebar.
     * Exibe os nomes das séries da lista atual para navegação rápida.
     */
    private JScrollPane criarListaRapida() {
        modeloListaRapida = new DefaultListModel<>(); // Modelo de dados da JList
        listaRapida = new JList<>(modeloListaRapida); // JList ligada ao modelo

        // Estilização da JList para combinar com o tema dark
        listaRapida.setBackground(COR_FUNDO_SIDEBAR);
        listaRapida.setForeground(COR_TEXTO_SECUNDARIO);
        listaRapida.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        listaRapida.setFixedCellHeight(36); // Altura fixa de cada item na lista
        listaRapida.setBorder(new EmptyBorder(0, 5, 0, 5));

        // Renderer customizado para deixar os itens mais bonitos
        listaRapida.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(new EmptyBorder(0, 15, 0, 5)); // Indentação interna
                if (isSelected) {
                    setBackground(COR_BOTAO_HOVER); // Fundo do item selecionado
                    setForeground(COR_TEXTO_PRINCIPAL);
                } else {
                    setBackground(COR_FUNDO_SIDEBAR);
                    setForeground(COR_TEXTO_SECUNDARIO);
                }
                setText("▸ " + value); // Adiciona marcador antes do nome
                return this;
            }
        });

        // MouseListener: ao dar duplo clique em um item da lista rápida,
        // abre a tela de detalhes daquela série.
        listaRapida.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Verifica se foi duplo clique
                    int idx = listaRapida.getSelectedIndex(); // Índice do item clicado
                    if (idx >= 0) { // Garante que há um item selecionado (evita -1)
                        List<Serie> listaAtual = controller.getListaPorIndice(indiceListaAtual);
                        if (idx < listaAtual.size()) { // Evita IndexOutOfBoundsException
                            exibirDetalhes(listaAtual.get(idx), "lista"); // Abre detalhes
                        }
                    }
                }
            }
        });

        // JScrollPane adiciona barra de rolagem à JList quando há muitos itens
        JScrollPane scroll = new JScrollPane(listaRapida);
        scroll.setBackground(COR_FUNDO_SIDEBAR);
        scroll.setBorder(BorderFactory.createEmptyBorder()); // Remove borda padrão do scroll
        scroll.getVerticalScrollBar().setBackground(COR_FUNDO_SIDEBAR);
        // Torna os botões da scrollbar invisíveis (scrollbar minimalista)
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(4, 0));

        return scroll; // Retorna o scroll com a lista dentro
    }

    /**
     * Factory method: cria um botão estilizado para a sidebar.
     * @param texto Texto/ícone a exibir no botão
     * @param ativo Se true, começa com o estilo de botão ativo (selecionado)
     */
    private JButton criarBotaoSidebar(String texto, boolean ativo) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setHorizontalAlignment(SwingConstants.LEFT); // Texto alinhado à esquerda
        btn.setBorderPainted(false);   // Sem borda
        btn.setFocusPainted(false);    // Sem contorno de foco ao ser clicado
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Cursor de mão
        btn.setMaximumSize(new Dimension(280, 48)); // Largura total, altura de 48px
        btn.setBorder(new EmptyBorder(12, 20, 12, 10)); // Padding interno
        btn.setAlignmentX(Component.LEFT_ALIGNMENT); // Alinha à esquerda no BoxLayout

        // Aplica o estilo de acordo com o estado (ativo/inativo)
        if (ativo) {
            btn.setBackground(COR_BOTAO_ATIVO);
            btn.setForeground(COR_TEXTO_PRINCIPAL);
        } else {
            btn.setBackground(COR_FUNDO_SIDEBAR);
            btn.setForeground(COR_TEXTO_SECUNDARIO);
        }

        // MouseListener para efeito hover (muda cor ao passar o mouse)
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Só muda cor se o botão NÃO é o ativo atualmente
                if (!btn.getBackground().equals(COR_BOTAO_ATIVO)) {
                    btn.setBackground(COR_BOTAO_HOVER);
                    btn.setForeground(COR_TEXTO_PRINCIPAL);
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                // Quando o mouse sai, restaura a cor padrão (se não for o ativo)
                if (!btn.getBackground().equals(COR_BOTAO_ATIVO)) {
                    btn.setBackground(COR_FUNDO_SIDEBAR);
                    btn.setForeground(COR_TEXTO_SECUNDARIO);
                }
            }
        });

        return btn; // Retorna o botão estilizado
    }

    /**
     * Destaca visualmente o botão da sidebar no índice especificado,
     * e remove o destaque dos outros.
     */
    private void destacarBotaoSidebar(int indice) {
        for (int i = 0; i < botoesSidebar.length; i++) {
            if (i == indice) {
                // Botão selecionado: fundo verde WhatsApp
                botoesSidebar[i].setBackground(COR_BOTAO_ATIVO);
                botoesSidebar[i].setForeground(COR_TEXTO_PRINCIPAL);
            } else {
                // Outros botões: volta ao estilo inativo
                botoesSidebar[i].setBackground(COR_FUNDO_SIDEBAR);
                botoesSidebar[i].setForeground(COR_TEXTO_SECUNDARIO);
            }
        }
    }

    /** Remove o destaque de todos os botões da sidebar (usado ao ir para Busca) */
    private void desativarTodosBotoesSidebar() {
        for (JButton btn : botoesSidebar) {
            btn.setBackground(COR_FUNDO_SIDEBAR);
            btn.setForeground(COR_TEXTO_SECUNDARIO);
        }
    }

    /** Cria um separador horizontal estilizado para a sidebar */
    private JSeparator criarSeparador() {
        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setForeground(COR_BORDA);       // Cor da linha
        sep.setBackground(COR_FUNDO_SIDEBAR);
        sep.setMaximumSize(new Dimension(280, 1)); // Altura de 1px (linha fina)
        return sep;
    }

    // PAINEL CENTRAL (CardLayout — troca de telas)
    private JPanel criarPainelCentral() {
        // CardLayout é o gerenciador que permite "empilhar" painéis e mostrar um de cada vez
        cardLayout = new CardLayout();
        painelCentral = new JPanel(cardLayout); // Painel usando CardLayout
        painelCentral.setBackground(COR_FUNDO_PRINCIPAL);

        // Adiciona cada "carta" (tela) ao painel central com um nome identificador
        // O nome é usado depois em cardLayout.show(painelCentral, NOME) para trocar de tela
        painelCentral.add(criarTelaBusca(),   TELA_BUSCA);   // Carta da busca
        painelCentral.add(criarTelaLista(),   TELA_LISTA);   // Carta da lista
        painelCentral.add(criarTelaDetalhe(), TELA_DETALHE); // Carta dos detalhes

        return painelCentral; // Retorna o painel com todas as telas empilhadas
    }

   
    // TELA DE BUSCA
    private JPanel criarTelaBusca() {
        JPanel tela = new JPanel(new BorderLayout()); // Layout BorderLayout
        tela.setBackground(COR_FUNDO_PRINCIPAL);

        // Cabeçalho da tela de busca
        JPanel header = criarHeader("🔍  Buscar Séries na TVMaze");
        tela.add(header, BorderLayout.NORTH); // Adiciona o cabeçalho no topo

        // Barra de pesquisa 
        JPanel barraBusca = new JPanel(new BorderLayout(10, 0)); // 10px de gap horizontal
        barraBusca.setBackground(COR_HEADER);
        barraBusca.setBorder(new EmptyBorder(15, 20, 15, 20)); // Padding

        // Campo de texto para digitar o nome da série
        campoBusca = new JTextField();
        campoBusca.setBackground(COR_INPUT);
        campoBusca.setForeground(COR_TEXTO_PRINCIPAL);
        campoBusca.setCaretColor(COR_TEXTO_PRINCIPAL); // Cor do cursor piscante
        campoBusca.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campoBusca.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COR_BORDA, 1), // Borda externa de 1px
            new EmptyBorder(8, 12, 8, 12)                 // Padding interno
        ));

        // KeyListener: permite buscar pressionando Enter (sem precisar clicar no botão)
        campoBusca.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) { // VK_ENTER = tecla Enter
                    realizarBusca(); // Chama o mesmo método que o botão Buscar
                }
            }
        });

        // Botão "Buscar" estilizado
        JButton btnBuscar = new JButton("  Buscar");
        estilizarBotaoPrincipal(btnBuscar); // Aplica estilo verde WhatsApp
        btnBuscar.addActionListener(e -> realizarBusca()); // Ao clicar, busca

        barraBusca.add(campoBusca, BorderLayout.CENTER); // Campo ocupa o centro
        barraBusca.add(btnBuscar, BorderLayout.EAST);   // Botão fica à direita

        // Tabela de resultados 
        // Define as colunas da tabela de resultados de busca
        String[] colunas = {"Nome", "Emissora", "Nota", "Status", "Estreia"};
        modeloTabelaBusca = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false; // Impede que o usuário edite as células da tabela
            }
        };

        tabelaBusca = new JTable(modeloTabelaBusca); // Cria tabela com o modelo
        estilizarTabela(tabelaBusca); // Aplica o tema dark na tabela

        // MouseListener: ao dar duplo clique em uma linha, abre os detalhes
        tabelaBusca.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Duplo clique
                    int linha = tabelaBusca.getSelectedRow(); // Linha clicada
                    // Valida linha >= 0 (evita NullPointerException quando clica em área vazia)
                    if (linha >= 0 && resultadosBusca != null && linha < resultadosBusca.size()) {
                        exibirDetalhes(resultadosBusca.get(linha), "busca"); // Abre detalhes
                    }
                }
            }
        });

        JScrollPane scrollBusca = new JScrollPane(tabelaBusca); // Adiciona scroll à tabela
        scrollBusca.setBackground(COR_FUNDO_PRINCIPAL);
        scrollBusca.setBorder(BorderFactory.createEmptyBorder());
        estilizarScrollPane(scrollBusca); // Estiliza a barra de rolagem

        // Monta o painel combinando barra de busca + tabela
        JPanel painelBusca = new JPanel(new BorderLayout());
        painelBusca.setBackground(COR_FUNDO_PRINCIPAL);
        painelBusca.add(barraBusca, BorderLayout.NORTH); // Barra de busca no topo
        painelBusca.add(scrollBusca, BorderLayout.CENTER); // Tabela no centro (expande)

        tela.add(painelBusca, BorderLayout.CENTER); // Adiciona à tela
        return tela;
    }


    // TELA DE LISTA
   
    private JPanel criarTelaLista() {
        JPanel tela = new JPanel(new BorderLayout());
        tela.setBackground(COR_FUNDO_PRINCIPAL);

        // Cabeçalho com título dinâmico
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COR_HEADER);
        header.setBorder(new EmptyBorder(0, 0, 0, 20));

        // Título da lista (muda conforme a lista selecionada na sidebar)
        labelTituloLista = new JLabel("  ⭐  Favoritos");
        labelTituloLista.setFont(new Font("Segoe UI", Font.BOLD, 18));
        labelTituloLista.setForeground(COR_TEXTO_PRINCIPAL);
        labelTituloLista.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Barra de controles (ordenação + botão remover) 
        JPanel controles = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        // FlowLayout.RIGHT alinha os componentes à direita; 10px horizontal, 15px vertical
        controles.setBackground(COR_HEADER);

        // Label "Ordenar por:" antes do combobox
        JLabel lblOrdenar = new JLabel("Ordenar por:");
        lblOrdenar.setForeground(COR_TEXTO_SECUNDARIO);
        lblOrdenar.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // JComboBox: menu suspenso com as opções de ordenação
        String[] opcoes = {"🔤 Alfabético", "⭐ Maior Nota", "📡 Status", "📅 Data de Estreia"};
        comboOrdenacao = new JComboBox<>(opcoes); // Cria combobox com as opções
        estilizarComboBox(comboOrdenacao);         // Aplica tema dark

        // ActionListener do combobox: ao mudar a seleção, reordena a tabela imediatamente
        comboOrdenacao.addActionListener(e -> carregarListaAtual()); // Recarrega com nova ordenação

        // Botão para remover a série selecionada da lista
        JButton btnRemover = new JButton("🗑 Remover");
        estilizarBotaoDanger(btnRemover); // Estilo vermelho para ação destrutiva
        btnRemover.addActionListener(e -> removerSerieSelecionada()); // Chama o método de remoção

        controles.add(lblOrdenar);
        controles.add(comboOrdenacao);
        controles.add(Box.createHorizontalStrut(10)); // Espaço horizontal de 10px
        controles.add(btnRemover);

        header.add(labelTituloLista, BorderLayout.WEST); // Título à esquerda
        header.add(controles, BorderLayout.EAST);        // Controles à direita

        // Tabela da lista
        String[] colunas = {"Nome", "Emissora", "Nota", "Status", "Data de Estreia"};
        modeloTabelaLista = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false; // Tabela somente leitura
            }
        };

        tabelaLista = new JTable(modeloTabelaLista);
        estilizarTabela(tabelaLista);

        // Duplo clique na tabela de lista → abre tela de detalhes da série
        tabelaLista.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int linha = tabelaLista.getSelectedRow();
                    if (linha >= 0) {
                        // Pega a lista atual JÁ ORDENADA para garantir que o índice bate
                        List<Serie> listaOrdenada = controller.ordenarLista(
                            controller.getListaPorIndice(indiceListaAtual),
                            comboOrdenacao.getSelectedIndex()
                        );
                        if (linha < listaOrdenada.size()) { // Evita IndexOutOfBoundsException
                            exibirDetalhes(listaOrdenada.get(linha), "lista");
                        }
                    }
                }
            }
        });

        JScrollPane scrollLista = new JScrollPane(tabelaLista);
        estilizarScrollPane(scrollLista);

        tela.add(header, BorderLayout.NORTH);
        tela.add(scrollLista, BorderLayout.CENTER);
        return tela;
    }
    
    // TELA DE DETALHES
    private JPanel criarTelaDetalhe() {
        JPanel tela = new JPanel(new BorderLayout());
        tela.setBackground(COR_FUNDO_PRINCIPAL);

        // Cabeçalho com botão Voltar 
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COR_HEADER);
        header.setBorder(new EmptyBorder(10, 15, 10, 15));

        JButton btnVoltar = new JButton("← Voltar");
        btnVoltar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnVoltar.setBackground(COR_INPUT);
        btnVoltar.setForeground(COR_TEXTO_PRINCIPAL);
        btnVoltar.setBorderPainted(false);
        btnVoltar.setFocusPainted(false);
        btnVoltar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnVoltar.setBorder(new EmptyBorder(8, 15, 8, 15));

        // Ao clicar em Voltar, retorna à tela de origem (busca ou lista)
        btnVoltar.addActionListener(e -> {
            if ("busca".equals(origemDetalhe)) {
                cardLayout.show(painelCentral, TELA_BUSCA); // Volta para a busca
            } else {
                cardLayout.show(painelCentral, TELA_LISTA); // Volta para a lista
            }
        });

        JLabel tituloHeader = new JLabel("Detalhes da Série");
        tituloHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tituloHeader.setForeground(COR_TEXTO_PRINCIPAL);
        tituloHeader.setBorder(new EmptyBorder(0, 15, 0, 0));

        header.add(btnVoltar, BorderLayout.WEST);
        header.add(tituloHeader, BorderLayout.CENTER);

        // Painel de conteúdo dos detalhes
        JPanel conteudo = new JPanel(new BorderLayout(20, 0));
        conteudo.setBackground(COR_FUNDO_PRINCIPAL);
        conteudo.setBorder(new EmptyBorder(25, 30, 25, 30));

        // Coluna esquerda: imagem e botões de ação
        JPanel colunaEsquerda = new JPanel();
        colunaEsquerda.setLayout(new BoxLayout(colunaEsquerda, BoxLayout.Y_AXIS));
        colunaEsquerda.setBackground(COR_FUNDO_PRINCIPAL);
        colunaEsquerda.setPreferredSize(new Dimension(220, 0)); // Largura fixa

        // Label para exibir a imagem/thumbnail da série
        lblDetImagem = new JLabel();
        lblDetImagem.setPreferredSize(new Dimension(210, 295)); // Proporção poster (2:3)
        lblDetImagem.setMinimumSize(new Dimension(210, 295));
        lblDetImagem.setMaximumSize(new Dimension(210, 295));
        lblDetImagem.setHorizontalAlignment(SwingConstants.CENTER);
        lblDetImagem.setBackground(COR_INPUT);
        lblDetImagem.setOpaque(true); // Permite que o fundo apareça
        lblDetImagem.setBorder(BorderFactory.createLineBorder(COR_BORDA, 1));
        lblDetImagem.setText("🎬"); // Placeholder enquanto a imagem não carrega
        lblDetImagem.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        lblDetImagem.setForeground(COR_TEXTO_SECUNDARIO);
        lblDetImagem.setAlignmentX(Component.CENTER_ALIGNMENT);

        colunaEsquerda.add(lblDetImagem);
        colunaEsquerda.add(Box.createVerticalStrut(20)); // Espaço entre imagem e botões

        // Botões de ação (Adicionar/Remover de cada lista)
        String[] labelsListas = {"⭐ Favoritos", "✅ Assistidas", "📺 Desejo Assistir"};
        String[] nomesInternosListas = {"favoritos", "assistidas", "desejassistir"};

        for (int i = 0; i < 3; i++) {
            final String nomeLista = nomesInternosListas[i];
            final String labelLista = labelsListas[i];
            final int indLista = i;

            // Cada botão tem dois modos: Adicionar ou Remover
            JButton btnAcao = new JButton("+ " + labelLista);
            btnAcao.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            btnAcao.setBackground(COR_INPUT);
            btnAcao.setForeground(COR_TEXTO_PRINCIPAL);
            btnAcao.setBorderPainted(false);
            btnAcao.setFocusPainted(false);
            btnAcao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnAcao.setMaximumSize(new Dimension(210, 36));
            btnAcao.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnAcao.setBorder(new EmptyBorder(8, 12, 8, 12));

            // Ao clicar, verifica se já está na lista e adiciona ou remove
            btnAcao.addActionListener(e -> {
                if (serieSendoExibida == null) return; // Segurança: série não pode ser nula

                List<Serie> lista = controller.getListaPorIndice(indLista);
                String mensagem;

                if (lista.contains(serieSendoExibida)) {
                    // Série já está na lista → Remove
                    mensagem = controller.removerDaLista(serieSendoExibida, nomeLista);
                    btnAcao.setText("+ " + labelLista); // Muda o botão de volta para "Adicionar"
                    btnAcao.setBackground(COR_INPUT);
                } else {
                    // Série NÃO está na lista → Adiciona
                    mensagem = controller.adicionarNaLista(serieSendoExibida, nomeLista);
                    btnAcao.setText("✓ " + labelLista); // Marca como adicionado
                    btnAcao.setBackground(new Color(0, 70, 57)); // Verde escuro
                }

                // Exibe mensagem de feedback ao usuário
                JOptionPane.showMessageDialog(this, mensagem, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                atualizarListaRapida(); // Atualiza a sidebar imediatamente
            });

            // Armazena referência no array para atualizar o estado ao abrir os detalhes
            // Usamos putClientProperty para guardar metadados no próprio botão
            btnAcao.putClientProperty("indice_lista", indLista);
            colunaEsquerda.add(btnAcao);
            colunaEsquerda.add(Box.createVerticalStrut(8));
        }

        // Coluna direita: informações da série
        JPanel colunaDireita = criarPainelInfoDetalhe();

        conteudo.add(colunaEsquerda, BorderLayout.WEST);
        conteudo.add(colunaDireita, BorderLayout.CENTER);

        // Envolve o conteúdo em um JScrollPane (caso a janela seja pequena)
        JScrollPane scrollDetalhe = new JScrollPane(conteudo);
        scrollDetalhe.setBackground(COR_FUNDO_PRINCIPAL);
        scrollDetalhe.setBorder(BorderFactory.createEmptyBorder());
        estilizarScrollPane(scrollDetalhe);

        tela.add(header, BorderLayout.NORTH);
        tela.add(scrollDetalhe, BorderLayout.CENTER);
        return tela;
    }

    /**
     * Cria o painel direito da tela de detalhes com todos os labels de informação.
     */
    private JPanel criarPainelInfoDetalhe() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS)); // Coluna vertical
        painel.setBackground(COR_FUNDO_PRINCIPAL);

        // Nome da série (destaque)
        lblDetNome = new JLabel("Nome da Série");
        lblDetNome.setFont(new Font("Segoe UI", Font.BOLD, 24)); // Fonte grande em negrito
        lblDetNome.setForeground(COR_TEXTO_PRINCIPAL);
        lblDetNome.setBorder(new EmptyBorder(0, 0, 5, 0));

        // Nota com estrela
        lblDetNota = new JLabel("⭐ 0.0 / 10");
        lblDetNota.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblDetNota.setForeground(new Color(255, 213, 79)); // Amarelo dourado
        lblDetNota.setBorder(new EmptyBorder(0, 0, 15, 0));

        painel.add(lblDetNome);
        painel.add(lblDetNota);
        painel.add(criarSeparadorHorizontal()); // Linha separadora
        painel.add(Box.createVerticalStrut(15));

        // Grade de informações
        // Usamos GridLayout para alinhar os campos em 2 colunas (rótulo + valor)
        JPanel grade = new JPanel(new GridLayout(0, 2, 10, 12));
        // GridLayout(0, 2) = linhas dinâmicas, 2 colunas; 10px entre colunas, 12px entre linhas
        grade.setBackground(COR_FUNDO_PRINCIPAL);
        grade.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Cria cada par rótulo + valor e adiciona na grade
        lblDetStatus   = criarLabelValor(grade, "Status:");
        lblDetEstreia  = criarLabelValor(grade, "Data de Estreia:");
        lblDetTermino  = criarLabelValor(grade, "Data de Término:");
        lblDetEmissora = criarLabelValor(grade, "Emissora:");
        lblDetIdioma   = criarLabelValor(grade, "Idioma:");
        lblDetGeneros  = criarLabelValor(grade, "Gêneros:");

        painel.add(grade);
        painel.add(Box.createVerticalStrut(20));
        painel.add(criarSeparadorHorizontal());
        painel.add(Box.createVerticalStrut(15));

        // Resumo/Sinopse 
        JLabel lblResumoTitulo = new JLabel("Resumo");
        lblResumoTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblResumoTitulo.setForeground(COR_TEXTO_SECUNDARIO);
        lblResumoTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        // JTextArea para exibir o resumo com quebra de linha automática
        areaResumo = new JTextArea("Sem resumo disponível.");
        areaResumo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        areaResumo.setForeground(COR_TEXTO_PRINCIPAL);
        areaResumo.setBackground(COR_FUNDO_PRINCIPAL);
        areaResumo.setEditable(false);      // Somente leitura
        areaResumo.setLineWrap(true);       // Quebra linha ao chegar na borda
        areaResumo.setWrapStyleWord(true);  // Quebra na palavra inteira (não no meio)
        areaResumo.setBorder(new EmptyBorder(8, 0, 8, 0));

        painel.add(lblResumoTitulo);
        painel.add(areaResumo);
        painel.add(Box.createVerticalGlue()); // Espaço elástico no final

        return painel;
    }

    /**
     * Cria um par rótulo + label de valor na grade de informações.
     * @return O JLabel de valor, para podermos atualizar depois
     */
    private JLabel criarLabelValor(JPanel grade, String rotulo) {
        // Label do rótulo (ex: "Status:")
        JLabel lblRotulo = new JLabel(rotulo);
        lblRotulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblRotulo.setForeground(COR_TEXTO_SECUNDARIO);
        grade.add(lblRotulo); // Adiciona na primeira coluna da grade

        // Label do valor (ex: "Running") — começa vazio
        JLabel lblValor = new JLabel("-");
        lblValor.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblValor.setForeground(COR_TEXTO_PRINCIPAL);
        grade.add(lblValor); // Adiciona na segunda coluna da grade

        return lblValor; // Retorna apenas o label de valor para atualizarmos
    }

    /** Cria um separador horizontal fino para a tela de detalhes */
    private JPanel criarSeparadorHorizontal() {
        JPanel sep = new JPanel();
        sep.setBackground(COR_BORDA);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1)); // 1px de altura, largura máxima
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        return sep;
    }

    
    // LÓGICA DE NEGÓCIO — Métodos chamados pelos listeners
    /**
     * Executa a busca na API TVMaze com o texto digitado no campoBusca.
     * Toda a lógica de validação e erro está aqui com try-catch.
     */
    private void realizarBusca() {
        String termoBusca = campoBusca.getText(); // Pega o texto digitado pelo usuário

        // Validação: campo de busca não pode estar vazio 
        if (termoBusca == null || termoBusca.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Por favor, digite o nome de uma série para buscar.",
                "Campo Vazio",
                JOptionPane.WARNING_MESSAGE // Ícone de aviso (triangulo amarelo)
            );
            campoBusca.requestFocus(); // Foca o campo para o usuário digitar
            return; // Interrompe a execução — não faz a requisição HTTP
        }

        // Exibe cursor de espera durante a busca
        // O cursor muda para uma ampulheta enquanto a requisição HTTP ocorre
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        modeloTabelaBusca.setRowCount(0); // Limpa a tabela antes de buscar

        // SwingWorker: executa a busca em uma thread separada 
        // OBRIGATÓRIO: chamadas de rede (HTTP) NÃO podem rodar na thread do Swing (EDT).
        // Se rodassem na EDT, a interface travaria completamente durante a espera.
        // SwingWorker executa o trabalho pesado em background e atualiza a UI na EDT.
        SwingWorker<List<Serie>, Void> worker = new SwingWorker<List<Serie>, Void>() {
            @Override
            protected List<Serie> doInBackground() throws Exception {
                // Este método roda em BACKGROUND (thread separada — não bloqueia a UI)
                return controller.buscarNaApi(termoBusca); // Faz a requisição HTTP
            }

            @Override
            protected void done() {
                // Este método roda de volta na EDT (thread do Swing) após o doInBackground
                setCursor(Cursor.getDefaultCursor()); // Restaura o cursor normal
                try {
                    resultadosBusca = get(); // Pega o resultado do doInBackground

                    if (resultadosBusca == null || resultadosBusca.isEmpty()) {
                        // Nenhum resultado encontrado — avisa o usuário
                        JOptionPane.showMessageDialog(
                            MainFrame.this,
                            "Nenhuma série encontrada para: \"" + termoBusca + "\"",
                            "Sem Resultados",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                        return;
                    }

                    // Preenche a tabela com os resultados
                    for (Serie s : resultadosBusca) {
                        // addRow adiciona uma linha na tabela com os valores das colunas
                        modeloTabelaBusca.addRow(new Object[]{
                            s.getNome(),
                            s.getEmissora() != null ? s.getEmissora() : "N/A",
                            s.getNotaGeral() > 0 ? String.format("%.1f", s.getNotaGeral()) : "N/A",
                            traduzirStatus(s.getStatus()),
                            s.getDataEstreia() != null ? s.getDataEstreia() : "N/A"
                        });
                    }

                } catch (Exception ex) {
                    // Captura erros de rede, timeout, parse, etc.
                    // ANTI-QUEBRA: o programa NÃO fecha, apenas exibe o erro ao usuário
                    String mensagemErro = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    JOptionPane.showMessageDialog(
                        MainFrame.this,
                        "Erro ao buscar séries:\n" + mensagemErro +
                        "\n\nVerifique sua conexão com a internet.",
                        "Erro de Conexão",
                        JOptionPane.ERROR_MESSAGE // Ícone de erro (X vermelho)
                    );
                }
            }
        };

        worker.execute(); // Dispara o SwingWorker (inicia a thread de background)
    }

    /**
     * Carrega e exibe a lista atual (conforme indiceListaAtual) na tabela.
     * Aplica a ordenação selecionada no combobox antes de exibir.
     */
    private void carregarListaAtual() {
        // Define os títulos para cada lista
        String[] titulos = {"⭐  Favoritos", "✅  Já Assistidas", "📺  Desejo Assistir"};
        // Atualiza o título da tela com o nome da lista atual
        if (labelTituloLista != null) {
            labelTituloLista.setText("  " + titulos[indiceListaAtual]);
        }

        // Pega a lista do usuário e ordena conforme o critério selecionado no combo
        List<Serie> lista = controller.getListaPorIndice(indiceListaAtual);
        int criterioOrdenacao = comboOrdenacao != null ? comboOrdenacao.getSelectedIndex() : 0;
        List<Serie> listaOrdenada = controller.ordenarLista(lista, criterioOrdenacao);

        // Limpa a tabela antes de recarregar (remove todas as linhas)
        modeloTabelaLista.setRowCount(0);

        // Limpa e recarrega a lista rápida na sidebar
        modeloListaRapida.clear();

        // Preenche a tabela e a lista rápida com os dados ordenados
        for (Serie s : listaOrdenada) {
            modeloTabelaLista.addRow(new Object[]{
                s.getNome(),
                s.getEmissora() != null ? s.getEmissora() : "N/A",
                s.getNotaGeral() > 0 ? String.format("%.1f", s.getNotaGeral()) : "N/A",
                traduzirStatus(s.getStatus()),
                s.getDataEstreia() != null ? s.getDataEstreia() : "N/A"
            });
            // Adiciona o nome à lista rápida da sidebar (sem o número de nota)
            modeloListaRapida.addElement(s.getNome());
        }
    }

    /**
     * Abre a tela de detalhes para a série especificada.
     * Preenche todos os labels com os dados da série.
     * @param serie A série a exibir
     * @param origem "busca" ou "lista" — define para onde o botão Voltar vai
     */
    private void exibirDetalhes(Serie serie, String origem) {
        if (serie == null) return; // Segurança: não faz nada se a série for nula

        serieSendoExibida = serie; // Armazena para uso nos botões de adicionar/remover
        origemDetalhe = origem;   // Armazena para o botão Voltar saber para onde ir

        // ── Preenche os labels com os dados da série ──────────────────────────
        lblDetNome.setText(serie.getNome() != null ? serie.getNome() : "N/A");
        lblDetNota.setText("⭐ " + (serie.getNotaGeral() > 0
            ? String.format("%.1f", serie.getNotaGeral()) + " / 10"
            : "Sem avaliação"));
        // Coloriza o status da série
        String statusTraduzido = traduzirStatus(serie.getStatus());
        lblDetStatus.setText(statusTraduzido);
        lblDetStatus.setForeground(corParaStatus(serie.getStatus())); // Verde/Cinza/Amarelo
        lblDetEstreia.setText(serie.getDataEstreia() != null && !serie.getDataEstreia().isEmpty()
            ? serie.getDataEstreia() : "N/A");
        lblDetTermino.setText(serie.getDataTermino() != null && !serie.getDataTermino().isEmpty()
            ? serie.getDataTermino() : "Em exibição");
        lblDetEmissora.setText(serie.getEmissora() != null && !serie.getEmissora().isEmpty()
            ? serie.getEmissora() : "N/A");
        lblDetIdioma.setText(serie.getIdioma() != null && !serie.getIdioma().isEmpty()
            ? serie.getIdioma() : "N/A");
        lblDetGeneros.setText(serie.getGeneros() != null && !serie.getGeneros().isEmpty()
            ? serie.getGeneros() : "N/A");
        areaResumo.setText(serie.getResumo() != null && !serie.getResumo().isEmpty()
            ? serie.getResumo() : "Sem resumo disponível.");
        areaResumo.setCaretPosition(0); // Garante que o scroll do resumo começa no topo

        // Carrega a imagem da série em background 
        carregarImagemAsync(serie.getImagemUrl()); // Não bloqueia a UI enquanto baixa

        // Atualiza o estado dos botões de lista
        atualizarBotoesDetalhes(); // Marca ✓ nos botões das listas onde a série já está

        // Troca para a tela de detalhes
        cardLayout.show(painelCentral, TELA_DETALHE); // Exibe a "carta" de detalhes
    }

    /**
     * Atualiza os botões de adicionar/remover na tela de detalhes
     * para refletir em quais listas a série já está.
     */
    private void atualizarBotoesDetalhes() {
        // Percorre todos os componentes da tela de detalhes buscando os botões de lista
      
        // Encontra o componente da tela de detalhe no CardLayout
        for (Component comp : painelCentral.getComponents()) {
            if (comp instanceof JPanel) {
                // Tenta encontrar os botões percorrendo a hierarquia de componentes
            }
        }
        // NOTA: A atualização dos botões é feita quando são criados com listeners que
        // verificam controller.getListaPorIndice(indLista).contains(serieSendoExibida)
        // em tempo real. Nenhuma ação adicional necessária aqui.
    }

    /**
     * Baixa e exibe a imagem da série em uma thread de background.
     * Evita travar a UI durante o download da imagem.
     */
    private void carregarImagemAsync(String urlImagem) {
        lblDetImagem.setText("🎬"); // Reset para o placeholder enquanto carrega
        lblDetImagem.setIcon(null); // Remove ícone anterior

        if (urlImagem == null || urlImagem.isEmpty()) return; // Sem URL, não carrega

        // SwingWorker para download da imagem em background
        SwingWorker<ImageIcon, Void> imgWorker = new SwingWorker<ImageIcon, Void>() {
            @Override
            protected ImageIcon doInBackground() throws Exception {
                // Baixa a imagem da URL e a redimensiona para 210x295 pixels
                // URI.create().toURL() é a forma moderna (não depreciada) de criar URLs no Java 21
                java.net.URL url = java.net.URI.create(urlImagem).toURL();
                java.awt.image.BufferedImage imgOriginal = javax.imageio.ImageIO.read(url);
                if (imgOriginal == null) return null;
                // Image.SCALE_SMOOTH: redimensiona com qualidade suave (antialiasing)
                java.awt.Image imgRedimensionada = imgOriginal.getScaledInstance(210, 295, java.awt.Image.SCALE_SMOOTH);
                return new ImageIcon(imgRedimensionada); // Converte para ImageIcon do Swing
            }

            @Override
            protected void done() {
                try {
                    ImageIcon icone = get(); // Pega o resultado do doInBackground
                    if (icone != null) {
                        lblDetImagem.setIcon(icone); // Define a imagem no label
                        lblDetImagem.setText("");    // Remove o texto placeholder 🎬
                    }
                } catch (Exception e) {
                    // Se falhar ao baixar a imagem, mantém o placeholder 🎬
                    // Não exibe erro — imagem é secundária, não crítica
                }
            }
        };
        imgWorker.execute(); // Inicia o download em background
    }

    /**
     * Remove a série selecionada na tabelaLista da lista atual.
     * ANTI-QUEBRA: valida se há uma linha selecionada antes de agir.
     */
    private void removerSerieSelecionada() {
        int linhaSelecionada = tabelaLista.getSelectedRow(); // Índice da linha selecionada

        // Valida: -1 significa que nenhuma linha está selecionada
        if (linhaSelecionada < 0) {
            JOptionPane.showMessageDialog(
                this,
                "Selecione uma série na tabela para remover.",
                "Nenhuma Seleção",
                JOptionPane.WARNING_MESSAGE
            );
            return; // Interrompe — sem seleção, não faz nada
        }

        // Pega a lista ordenada para garantir que o índice da tabela bate com o da lista
        List<Serie> listaOrdenada = controller.ordenarLista(
            controller.getListaPorIndice(indiceListaAtual),
            comboOrdenacao.getSelectedIndex()
        );

        if (linhaSelecionada >= listaOrdenada.size()) return; // Evita IndexOutOfBoundsException

        Serie serieParaRemover = listaOrdenada.get(linhaSelecionada);

        // Confirmação: pergunta antes de remover (evita remoção acidental)
        int confirmacao = JOptionPane.showConfirmDialog(
            this,
            "Remover \"" + serieParaRemover.getNome() + "\" da lista?",
            "Confirmar Remoção",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (confirmacao == JOptionPane.YES_OPTION) {
            // Usuário confirmou — remove da lista original (não da cópia ordenada)
            String nomeLista = controller.getNomeListaPorIndice(indiceListaAtual);
            String mensagem = controller.removerDaLista(serieParaRemover, nomeLista);
            carregarListaAtual(); // Recarrega a tabela para refletir a remoção
            JOptionPane.showMessageDialog(this, mensagem, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    
    // MÉTODOS AUXILIARES DE UI
    /** Atualiza a lista rápida da sidebar com os nomes da lista atual */
    private void atualizarListaRapida() {
        carregarListaAtual(); // Recarrega tudo (tabela + lista rápida)
    }

    /**
     * Cria um painel de cabeçalho com título para as telas.
     */
    private JPanel criarHeader(String titulo) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COR_HEADER);
        header.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel label = new JLabel(titulo);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(COR_TEXTO_PRINCIPAL);
        header.add(label, BorderLayout.WEST);
        return header;
    }

    /**
     * Traduz o status da série do inglês (como vem da API) para português.
     */
    private String traduzirStatus(String status) {
        if (status == null) return "Desconhecido";
        switch (status) {
            case "Running":            return "Em Exibição 🟢";
            case "Ended":              return "Encerrada ⚫";
            case "To Be Determined":   return "Indefinido 🟡";
            case "In Development":     return "Em Desenvolvimento 🔵";
            default:                   return status; // Retorna o original se não reconhecer
        }
    }

    /**
     * Retorna a cor correspondente ao status da série para colorização na UI.
     */
    private Color corParaStatus(String status) {
        if (status == null) return COR_TEXTO_SECUNDARIO;
        switch (status) {
            case "Running":          return COR_VERDE;   // Verde: em exibição
            case "Ended":            return COR_CINZA;   // Cinza: encerrada
            case "To Be Determined": return COR_AMARELO; // Amarelo: indefinido
            default:                 return COR_TEXTO_PRINCIPAL;
        }
    }

    
    // MÉTODOS DE ESTILIZAÇÃO — Aplicam o tema dark WhatsApp nos componentes
    /**
     * Aplica estilo dark WhatsApp numa JTable.
     * Configura cores de fundo, seleção, cabeçalho e renderização de linhas alternadas.
     */
    private void estilizarTabela(JTable tabela) {
        tabela.setBackground(COR_FUNDO_PRINCIPAL);
        tabela.setForeground(COR_TEXTO_PRINCIPAL);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setGridColor(COR_BORDA); // Cor das linhas de grade
        tabela.setRowHeight(42);        // Altura de cada linha em pixels
        tabela.setSelectionBackground(COR_BOTAO_ATIVO); // Cor da linha selecionada
        tabela.setSelectionForeground(Color.WHITE);      // Texto da linha selecionada
        tabela.setShowHorizontalLines(true);  // Exibe linhas horizontais de grade
        tabela.setShowVerticalLines(false);   // Oculta linhas verticais (visual mais limpo)
        tabela.setIntercellSpacing(new Dimension(0, 1)); // Espaço entre células

        // Estiliza o cabeçalho da tabela (JTableHeader)
        tabela.getTableHeader().setBackground(COR_HEADER);
        tabela.getTableHeader().setForeground(COR_TEXTO_SECUNDARIO);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabela.getTableHeader().setReorderingAllowed(false); // Impede rearranjar colunas
        tabela.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COR_BORDA));

        // Renderer de linhas alternadas (zebra stripes) 
        // TableCellRenderer personalizado para alternar a cor de fundo das linhas
        tabela.setDefaultRenderer(Object.class, new TableCellRenderer() {
            // Reutilizamos um JLabel como componente de renderização (eficiência)
            private final JLabel label = new JLabel();

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                label.setText(value != null ? value.toString() : ""); // Define o texto
                label.setOpaque(true); // Necessário para que o fundo apareça
                label.setBorder(new EmptyBorder(0, 10, 0, 10)); // Padding nas células
                label.setFont(new Font("Segoe UI", Font.PLAIN, 13));

                if (isSelected) {
                    // Linha selecionada: fundo verde WhatsApp
                    label.setBackground(COR_BOTAO_ATIVO);
                    label.setForeground(Color.WHITE);
                } else {
                    // Linhas não selecionadas: alternam entre duas cores (zebra)
                    label.setBackground(row % 2 == 0 ? COR_LINHA_TABELA_IMPAR : COR_LINHA_TABELA_PAR);
                    label.setForeground(COR_TEXTO_PRINCIPAL);
                }
                return label; // Retorna o label como componente visual da célula
            }
        });
    }

    /**
     * Aplica estilo dark na barra de rolagem de um JScrollPane.
     */
    private void estilizarScrollPane(JScrollPane scroll) {
        scroll.setBackground(COR_FUNDO_PRINCIPAL);
        scroll.setBorder(BorderFactory.createEmptyBorder()); // Remove borda padrão
        scroll.getViewport().setBackground(COR_FUNDO_PRINCIPAL); // Fundo do viewport
        scroll.getVerticalScrollBar().setBackground(COR_FUNDO_SIDEBAR);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(6, 0)); // Barra fina
    }

    /**
     * Aplica estilo dark num JComboBox.
     */
    private void estilizarComboBox(JComboBox<String> combo) {
        combo.setBackground(COR_INPUT);
        combo.setForeground(COR_TEXTO_PRINCIPAL);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setBorder(BorderFactory.createLineBorder(COR_BORDA, 1));
        // Renderer do JComboBox para garantir que os itens do dropdown também sejam dark
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? COR_BOTAO_ATIVO : COR_INPUT);
                setForeground(COR_TEXTO_PRINCIPAL);
                setBorder(new EmptyBorder(5, 10, 5, 10));
                return this;
            }
        });
    }

    /** Estiliza um botão com a cor de ação principal (verde WhatsApp) */
    private void estilizarBotaoPrincipal(JButton btn) {
        btn.setBackground(COR_BOTAO_ATIVO);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
    }

    /** Estiliza um botão com a cor de perigo (vermelho — para ações destrutivas) */
    private void estilizarBotaoDanger(JButton btn) {
        btn.setBackground(new Color(211, 47, 47)); // Vermelho Material Design
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
    }
}