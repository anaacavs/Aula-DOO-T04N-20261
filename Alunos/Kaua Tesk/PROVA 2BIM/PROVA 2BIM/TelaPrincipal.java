import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;


public class TelaPrincipal extends JFrame {

    
    private Usuario            usuario;      // perfil do usuário logado
    private ApiService         api;          // comunicação com a API TVMaze
    private PersistenciaService persistencia;// salvar/carregar JSON
    private OrdenacaoService   ordenacao;    // ordenar listas de séries

   
    private JTextField    txtBusca;          // campo de busca por nome
    private JButton       btnBuscar;         // botão que dispara a busca
    private JTabbedPane   abas;              // abas: Busca | Favoritas | Assistidas | Quer Assistir
    private JList<Serie>  listaBusca;        // resultados de busca
    private JList<Serie>  listaFavoritas;    // séries favoritas do usuário
    private JList<Serie>  listaAssistidas;   // séries que o usuário já assistiu
    private JList<Serie>  listaQuerAssistir; // séries que o usuário quer assistir
    private PainelDetalhes painelDetalhes;   // painel lateral de detalhes
    private JLabel        lblStatus;         // mensagem no rodapé
    private JLabel        lblUsuarioHeader;  // exibe o nome do usuário no header



    public TelaPrincipal(Usuario usuario) {
        this.usuario      = usuario;
        this.api          = new ApiService();
        this.persistencia = new PersistenciaService();
        this.ordenacao    = new OrdenacaoService();

        configurarJanela();
        construirInterface();
        atualizarListas(); // popula as JList com os dados carregados do JSON
    }

    

    private void configurarJanela() {
        setTitle("SeriesTracker - Ola, " + usuario.getNome() + "!");
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // intercepamos o fechar
        setLocationRelativeTo(null); // centraliza na tela
        getContentPane().setBackground(new Color(18, 18, 18));

        //metodo que vai salvar automaticamente ao fechar a tela.

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                salvarDados();   
                System.exit(0); 
            }
        });
    }


    private void construirInterface() {
        setLayout(new BorderLayout(0, 0));
        add(criarHeader(),        BorderLayout.NORTH);
        add(criarPainelCentral(), BorderLayout.CENTER);
        add(criarRodape(),        BorderLayout.SOUTH);
    }

   
    private JPanel criarHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(20, 20, 20));
        header.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 2, 0, new Color(229, 9, 20)), // linha vermelha embaixo
            new EmptyBorder(10, 20, 10, 20)
        ));

        // Título do app à esquerda
        JLabel lblTitulo = new JLabel("▶ SeriesTracker");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(229, 9, 20));
        header.add(lblTitulo, BorderLayout.WEST);

        // Painel direito: nome do usuário + botão de trocar
        JPanel painelDireito = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        painelDireito.setBackground(new Color(20, 20, 20));

        // Label com o nome do usuário atual
        lblUsuarioHeader = new JLabel("Ola, " + usuario.getNome());
        lblUsuarioHeader.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblUsuarioHeader.setForeground(new Color(140, 140, 140));
        painelDireito.add(lblUsuarioHeader);

        // Separador visual entre nome e botão
        JLabel sep = new JLabel("|");
        sep.setForeground(new Color(60, 60, 60));
        painelDireito.add(sep);

        // Botão "Trocar Usuário" — pequeno e discreto, no canto direito do header
        JButton btnTrocar = new JButton("Trocar Usuario");
        btnTrocar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnTrocar.setBackground(new Color(40, 40, 40));
        btnTrocar.setForeground(new Color(180, 180, 180));
        btnTrocar.setOpaque(true);
        btnTrocar.setContentAreaFilled(true);
        btnTrocar.setBorderPainted(true);
        btnTrocar.setFocusPainted(false);
        btnTrocar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTrocar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(65, 65, 65), 1),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        btnTrocar.addActionListener(e -> trocarUsuario());
        painelDireito.add(btnTrocar);

        header.add(painelDireito, BorderLayout.EAST);

        return header;
    }

  
    private void trocarUsuario() {
        // 1. Salva os dados do usuário atual antes de trocar
        salvarDados();

        // 2. Abre a tela de login para digitar o novo nome
        // TelaLogin é modal: setVisible(true) bloqueia até o usuário confirmar
        TelaLogin telaLogin = new TelaLogin(this);
        telaLogin.setVisible(true);

        String novoNome = telaLogin.getNomeUsuario();

        // Se o usuário fechou sem digitar (X ou cancelou), não troca
        if (novoNome == null) return;

        // 3. Cria o novo perfil (começa com listas vazias)
        
        this.usuario = new Usuario(novoNome);

        // Atualiza o PainelDetalhes com o novo usuário (troca a referência)
        painelDetalhes.setUsuario(usuario);

        // 4. Atualiza a interface com o novo nome
        setTitle("SeriesTracker - Ola, " + novoNome + "!");
        lblUsuarioHeader.setText("Ola, " + novoNome);

        // Limpa as listas (o novo usuário começa do zero)
        atualizarListas();

        // Limpa os resultados de busca da aba anterior
        ((DefaultListModel<Serie>) listaBusca.getModel()).clear();

        mostrarStatus("Perfil trocado! Bem-vindo(a), " + novoNome + ".");
    }

    private JSplitPane criarPainelCentral() {
        
        painelDetalhes = new PainelDetalhes(usuario, this::atualizarListas);

        JSplitPane split = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            criarPainelEsquerdo(),
            painelDetalhes
        );
        split.setDividerLocation(750);
        split.setDividerSize(3);
        split.setBorder(null);
        split.setBackground(new Color(18, 18, 18));

        return split;
    }

    

    private JPanel criarPainelEsquerdo() {
        JPanel painel = new JPanel(new BorderLayout(0, 0));
        painel.setBackground(new Color(18, 18, 18));
        painel.add(criarBarraBusca(), BorderLayout.NORTH);
        painel.add(criarAbas(),       BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarBarraBusca() {
        JPanel painel = new JPanel(new BorderLayout(8, 0));
        painel.setBackground(new Color(28, 28, 28));
        painel.setBorder(new EmptyBorder(12, 16, 12, 16));

        // Campo de busca
        txtBusca = new JTextField();
        txtBusca.setBackground(new Color(40, 40, 40));
        txtBusca.setForeground(Color.WHITE);
        txtBusca.setCaretColor(Color.WHITE);
        txtBusca.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtBusca.setOpaque(true); // garante que o fundo seja pintado
        txtBusca.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(65, 65, 65), 1),
            BorderFactory.createEmptyBorder(9, 12, 9, 12)
        ));
        // Enter no campo dispara a busca (atalho de teclado)
        txtBusca.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) realizarBusca();
            }
        });

        // Botão buscar
        btnBuscar = new JButton("Buscar");
        btnBuscar.setBackground(new Color(229, 9, 20));
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnBuscar.setOpaque(true);            // ESSENCIAL: força pintura do fundo
        btnBuscar.setContentAreaFilled(true); // ESSENCIAL: preenche área interna
        btnBuscar.setBorderPainted(false);    // remove borda padrão do look&feel
        btnBuscar.setFocusPainted(false);
        btnBuscar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBuscar.setBorder(BorderFactory.createEmptyBorder(9, 22, 9, 22));
        btnBuscar.addActionListener(e -> realizarBusca());

        painel.add(txtBusca,  BorderLayout.CENTER);
        painel.add(btnBuscar, BorderLayout.EAST);

        return painel;
    }

 
    private JTabbedPane criarAbas() {
        /**
         * O Metal L&F usa UIManager para definir as cores das abas.
         * setBackground() no JTabbedPane não afeta as abas individuais —
         * precisamos configurar via UIManager antes de criar o componente.
         */
        UIManager.put("TabbedPane.background",       new Color(30, 30, 30));
        UIManager.put("TabbedPane.selected",         new Color(22, 22, 22));
        UIManager.put("TabbedPane.foreground",       new Color(200, 200, 200));
        UIManager.put("TabbedPane.contentAreaColor", new Color(22, 22, 22));
        UIManager.put("TabbedPane.darkShadow",       new Color(10, 10, 10));
        UIManager.put("TabbedPane.shadow",           new Color(40, 40, 40));
        UIManager.put("TabbedPane.light",            new Color(50, 50, 50));
        UIManager.put("TabbedPane.highlight",        new Color(60, 60, 60));
        UIManager.put("TabbedPane.focus",            new Color(229, 9, 20));
        UIManager.put("TabbedPane.selectHighlight",  new Color(229, 9, 20));

        abas = new JTabbedPane();
        abas.setBackground(new Color(30, 30, 30));
        abas.setForeground(new Color(200, 200, 200));
        abas.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        listaBusca = criarJList();
        abas.addTab("Busca", criarScrollComOrdenar(listaBusca, false));

        listaFavoritas = criarJList();
        abas.addTab("Favoritas", criarScrollComOrdenar(listaFavoritas, true));

        listaAssistidas = criarJList();
        abas.addTab("Assistidas", criarScrollComOrdenar(listaAssistidas, true));

        listaQuerAssistir = criarJList();
        abas.addTab("Quer Assistir", criarScrollComOrdenar(listaQuerAssistir, true));

        return abas;
    }

    /**
     * Empacota uma JList dentro de um JScrollPane.
     * Se comOrdenar=true, adiciona um ComboBox de ordenação no topo do painel.
     */
    private JPanel criarScrollComOrdenar(JList<Serie> lista, boolean comOrdenar) {
        JPanel painel = new JPanel(new BorderLayout(0, 0));
        painel.setBackground(new Color(22, 22, 22));

        if (comOrdenar) {
            // Painel superior com o seletor de ordenação
            JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
            topo.setBackground(new Color(28, 28, 28));

            JLabel lblOrdem = new JLabel("Ordenar por:");
            lblOrdem.setForeground(new Color(140, 140, 140));
            lblOrdem.setFont(new Font("Segoe UI", Font.PLAIN, 12));

            // ComboBox com os critérios de ordenação disponíveis
            JComboBox<String> combo = new JComboBox<>(new String[]{
                "Padrao", "Nome (A-Z)", "Nota (maior-menor)", "Status", "Data de estreia"
            });
            // Estiliza o ComboBox para fundo escuro
            combo.setBackground(new Color(45, 45, 45));
            combo.setForeground(Color.WHITE);
            combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            combo.setOpaque(true);

            // Ao trocar o critério, reordena a lista desta aba
            combo.addActionListener(e ->
                ordenarListaAtiva(lista, (String) combo.getSelectedItem())
            );

            topo.add(lblOrdem);
            topo.add(combo);
            painel.add(topo, BorderLayout.NORTH);
        }

        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBackground(new Color(22, 22, 22));
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        painel.add(scroll, BorderLayout.CENTER);
        return painel;
    }

    
    private JList<Serie> criarJList() {
        JList<Serie> jlist = new JList<>(new DefaultListModel<>());

        // Renderer customizado: define como cada célula da lista é desenhada
        jlist.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {

                // Chama o renderer padrão para herdar comportamento base
                JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);

                Serie s = (Serie) value;

                // HTML de duas linhas: nome em branco, status+nota em cinza
                String html = "<html>"
                    + "<b style='color:white'>" + s.getNome() + "</b>"
                    + "<br>"
                    + "<span style='color:#999999; font-size:10px'>"
                    + s.getStatusTraduzido() + " - "
                    + (s.getNota() > 0 ? "Nota: " + String.format("%.1f", s.getNota()) : "Sem nota")
                    + "</span>"
                    + "</html>";
                label.setText(html);

                // Cores de item selecionado vs normal (zebra striping)
                if (isSelected) {
                    label.setBackground(new Color(229, 9, 20, 100)); // vermelho semi-transparente
                    label.setForeground(Color.WHITE);
                } else {
                    // Zebra striping: linhas pares e ímpares com tons levemente diferentes
                    label.setBackground(index % 2 == 0
                        ? new Color(22, 22, 22)
                        : new Color(27, 27, 27));
                    label.setForeground(Color.WHITE);
                }

                label.setBorder(new EmptyBorder(10, 14, 10, 14));
                label.setOpaque(true);
                return label;
            }
        });

        jlist.setBackground(new Color(22, 22, 22));
        jlist.setForeground(Color.WHITE);
        jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jlist.setFixedCellHeight(58); // altura fixa para o HTML de duas linhas

        // ListSelectionListener: ao selecionar uma série, atualiza o PainelDetalhes
        jlist.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { // evita disparo duplo (pressed + released)
                Serie selecionada = jlist.getSelectedValue();
                if (selecionada != null) {
                    painelDetalhes.exibirSerie(selecionada);
                }
            }
        });

        return jlist;
    }

   
    private JPanel criarRodape() {
        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setBackground(new Color(20, 20, 20));
        rodape.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, new Color(45, 45, 45)),
            new EmptyBorder(7, 16, 7, 16)
        ));

        lblStatus = new JLabel("Pronto. Busque uma serie pelo nome acima.");
        lblStatus.setForeground(new Color(100, 100, 100));
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        rodape.add(lblStatus, BorderLayout.WEST);

        // Botão de salvar manual 
        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.setBackground(new Color(40, 40, 40));
        btnSalvar.setForeground(new Color(160, 160, 160));
        btnSalvar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnSalvar.setOpaque(true);
        btnSalvar.setContentAreaFilled(true);
        btnSalvar.setBorderPainted(false);
        btnSalvar.setFocusPainted(false);
        btnSalvar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSalvar.setBorder(new EmptyBorder(5, 14, 5, 14));
        btnSalvar.addActionListener(e -> salvarDados());
        rodape.add(btnSalvar, BorderLayout.EAST);

        return rodape;
    }

    // logica de busca

   
    private void realizarBusca() {
        String termo = txtBusca.getText().trim();
        if (termo.isEmpty()) {
            mostrarStatus("Digite um nome para buscar.");
            return;
        }

        // Feedback imediato ao usuário
        btnBuscar.setEnabled(false);
        btnBuscar.setText("Buscando...");
        mostrarStatus("Buscando por \"" + termo + "\"...");
        ((DefaultListModel<Serie>) listaBusca.getModel()).clear();
        abas.setSelectedIndex(0); // vai para a aba de busca

        SwingWorker<List<Serie>, Void> worker = new SwingWorker<List<Serie>, Void>() {
            @Override
            protected List<Serie> doInBackground() throws Exception {
                // Thread separada: faz a chamada HTTP aqui (pode demorar)
                return api.buscarSeries(termo);
            }

            @Override
            protected void done() {
                // EDT: atualiza a interface com o resultado
                try {
                    List<Serie> resultados = get(); // obtém o retorno de doInBackground()
                    DefaultListModel<Serie> model = (DefaultListModel<Serie>) listaBusca.getModel();

                    if (resultados.isEmpty()) {
                        mostrarStatus("Nenhuma serie encontrada para \"" + termo + "\".");
                    } else {
                        for (Serie s : resultados) model.addElement(s);
                        mostrarStatus(resultados.size() + " serie(s) encontrada(s) para \"" + termo + "\".");
                    }
                } catch (Exception ex) {
                    // Tratamento de exceção: exibe o erro sem fechar o programa
                    mostrarStatus("Erro na busca: " + ex.getMessage());
                    JOptionPane.showMessageDialog(
                        TelaPrincipal.this,
                        "Nao foi possivel buscar as series.\n"
                        + "Verifique sua conexao com a internet.\n\nDetalhes: " + ex.getMessage(),
                        "Erro de conexao",
                        JOptionPane.ERROR_MESSAGE
                    );
                } finally {
                    // finally garante que o botão é reabilitado SEMPRE (mesmo com erro)
                    btnBuscar.setEnabled(true);
                    btnBuscar.setText("Buscar");
                }
            }
        };

        worker.execute(); // inicia o worker em background
    }

    // atualiza lista

   
    private void atualizarListas() {
        popularJList(listaFavoritas,    usuario.getFavoritas());
        popularJList(listaAssistidas,   usuario.getAssistidas());
        popularJList(listaQuerAssistir, usuario.getQuerAssistir());

        // Atualiza os títulos das abas com a quantidade de séries em cada lista
        abas.setTitleAt(1, "Favoritas ("     + usuario.getFavoritas().size()    + ")");
        abas.setTitleAt(2, "Assistidas ("    + usuario.getAssistidas().size()   + ")");
        abas.setTitleAt(3, "Quer Assistir (" + usuario.getQuerAssistir().size() + ")");
    }

    /** Substitui o conteúdo de uma JList pelos itens da lista Java fornecida. */
    private void popularJList(JList<Serie> jlist, List<Serie> dados) {
        DefaultListModel<Serie> model = (DefaultListModel<Serie>) jlist.getModel();
        model.clear();
        for (Serie s : dados) model.addElement(s);
    }

    /**
     * Reordena os itens de uma JList conforme o critério selecionado no ComboBox.
     * Usa OrdenacaoService para não misturar lógica de ordenação na camada de View.
     */
    private void ordenarListaAtiva(JList<Serie> lista, String criterio) {
        DefaultListModel<Serie> model = (DefaultListModel<Serie>) lista.getModel();
        if (model.isEmpty()) return;

        // Converte o DefaultListModel para uma List Java comum
        java.util.List<Serie> itens = new java.util.ArrayList<>();
        for (int i = 0; i < model.size(); i++) itens.add(model.getElementAt(i));

        List<Serie> ordenada;
        switch (criterio) {
            case "Nome (A-Z)":         ordenada = ordenacao.ordenarPorNome(itens);        break;
            case "Nota (maior-menor)": ordenada = ordenacao.ordenarPorNota(itens);        break;
            case "Status":             ordenada = ordenacao.ordenarPorStatus(itens);      break;
            case "Data de estreia":    ordenada = ordenacao.ordenarPorDataEstreia(itens); break;
            default:                   return; // "Padrao" não reordena
        }

        // Substitui o conteúdo do model pela lista ordenada
        model.clear();
        for (Serie s : ordenada) model.addElement(s);
    }

    // ── Persistência ──────────────────────────────────────────────────────────────

    /**
     * Salva os dados do usuário (nome + três listas) no arquivo dados.json.
     * Trata IOException sem fechar o aplicativo — mostra o erro e continua.
     */
    private void salvarDados() {
        try {
            persistencia.salvar(usuario);
            mostrarStatus("Dados salvos com sucesso!");
        } catch (Exception ex) {
            mostrarStatus("Erro ao salvar: " + ex.getMessage());
            JOptionPane.showMessageDialog(
                this,
                "Nao foi possivel salvar os dados.\n" + ex.getMessage(),
                "Erro ao salvar",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /** Atualiza a mensagem da barra de status no rodapé. */
    private void mostrarStatus(String msg) {
        lblStatus.setText(msg);
    }
}
