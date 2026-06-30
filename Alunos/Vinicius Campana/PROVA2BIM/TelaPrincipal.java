package view;

import model.Serie;
import model.Usuario;
import service.JsonService;
import service.OrdenacaoService;
import service.TvMazeService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TelaPrincipal extends JFrame {

    private final JsonService jsonService = new JsonService();
    private final TvMazeService tvMazeService = new TvMazeService();
    private final OrdenacaoService ordenacaoService = new OrdenacaoService();

    private Usuario usuario;

    private JTextField txtApelido;
    private JTextField txtBusca;

    private JTable tabela;
    private DefaultTableModel modeloTabela;

    private JComboBox<String> cbOrdenacao;

    private List<Serie> listaBusca = new ArrayList<>();
    private List<Serie> listaExibida = new ArrayList<>();

    private enum TipoLista {
        BUSCA,
        FAVORITOS,
        ASSISTIDAS,
        DESEJA_ASSISTIR
    }

    private TipoLista listaAtual = TipoLista.BUSCA;

    public TelaPrincipal() {

        usuario = jsonService.carregarUsuario();

        configurarJanela();

        criarPainelSuperior();
        criarTabela();
        criarPainelInferior();

        setVisible(true);
    }

    private void configurarJanela() {

        setTitle("Minha Série TV");
        setSize(1150, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        getContentPane().setBackground(new Color(245, 245, 245));
    }

    private void criarPainelSuperior() {

        JPanel painelSuperior = new JPanel(new BorderLayout());
        painelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel lblTitulo = new JLabel("📺 Minha Série TV");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 24));

        painelSuperior.add(lblTitulo, BorderLayout.NORTH);

        JPanel painelCampos = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        painelCampos.add(new JLabel("Apelido:"));

        txtApelido = new JTextField(15);

        if (usuario.getApelido() != null) {
            txtApelido.setText(usuario.getApelido());
        }

        painelCampos.add(txtApelido);

        JButton btnSalvarUsuario = new JButton("Salvar");

        btnSalvarUsuario.addActionListener(e -> {

            usuario.setApelido(txtApelido.getText().trim());

            jsonService.salvarUsuario(usuario);

            JOptionPane.showMessageDialog(
                    this,
                    "Usuário salvo com sucesso."
            );
        });

        painelCampos.add(btnSalvarUsuario);

        painelCampos.add(Box.createHorizontalStrut(20));

        painelCampos.add(new JLabel("Buscar Série:"));

        txtBusca = new JTextField(25);

        painelCampos.add(txtBusca);

        JButton btnBuscar = new JButton("Buscar");

        btnBuscar.addActionListener(e -> buscarSeries());

        txtBusca.addActionListener(e -> buscarSeries());

        painelCampos.add(btnBuscar);

        painelSuperior.add(painelCampos, BorderLayout.CENTER);

        add(painelSuperior, BorderLayout.NORTH);
    }
    
    private void criarTabela() {

        modeloTabela = new DefaultTableModel(
                new Object[]{
                        "Nome",
                        "Idioma",
                        "Gêneros",
                        "Nota",
                        "Estado",
                        "Estreia",
                        "Término",
                        "Emissora"
                }, 0) {

            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };

        tabela = new JTable(modeloTabela);

        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setRowHeight(28);
        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.setFillsViewportHeight(true);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(
                BorderFactory.createTitledBorder("Resultados")
        );

        add(scroll, BorderLayout.CENTER);
    }

    private void criarPainelInferior() {

        JPanel painelPrincipal = new JPanel(new BorderLayout());

        painelPrincipal.setBorder(
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        );

        JPanel painelListas = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnFavoritos = new JButton("Adicionar aos Favoritos");
        JButton btnAssistidas = new JButton("Adicionar às Assistidas");
        JButton btnDesejos = new JButton("Adicionar aos Desejos");

        painelListas.add(btnFavoritos);
        painelListas.add(btnAssistidas);
        painelListas.add(btnDesejos);

        JPanel painelVisualizacao = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton btnVerFavoritos = new JButton("Ver Favoritos");
        JButton btnVerAssistidas = new JButton("Ver Assistidas");
        JButton btnVerDesejos = new JButton("Ver Desejos");
        JButton btnRemover = new JButton("Remover da Lista");

        painelVisualizacao.add(btnVerFavoritos);
        painelVisualizacao.add(btnVerAssistidas);
        painelVisualizacao.add(btnVerDesejos);
        painelVisualizacao.add(btnRemover);

        JPanel painelOrdenacao = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        painelOrdenacao.add(new JLabel("Ordenar por:"));

        cbOrdenacao = new JComboBox<>(new String[]{
                "Nome",
                "Nota",
                "Estado",
                "Data de Estreia"
        });

        JButton btnOrdenar = new JButton("Ordenar");

        painelOrdenacao.add(cbOrdenacao);
        painelOrdenacao.add(btnOrdenar);

        painelPrincipal.add(painelListas, BorderLayout.NORTH);
        painelPrincipal.add(painelVisualizacao, BorderLayout.CENTER);
        painelPrincipal.add(painelOrdenacao, BorderLayout.SOUTH);

        add(painelPrincipal, BorderLayout.SOUTH);

        btnFavoritos.addActionListener(e ->
                adicionarSerie(usuario.getFavoritos()));

        btnAssistidas.addActionListener(e ->
                adicionarSerie(usuario.getAssistidas()));

        btnDesejos.addActionListener(e ->
                adicionarSerie(usuario.getDesejaAssistir()));

        btnVerFavoritos.addActionListener(e -> {

            listaAtual = TipoLista.FAVORITOS;
            mostrarLista(usuario.getFavoritos());

        });

        btnVerAssistidas.addActionListener(e -> {

            listaAtual = TipoLista.ASSISTIDAS;
            mostrarLista(usuario.getAssistidas());

        });

        btnVerDesejos.addActionListener(e -> {

            listaAtual = TipoLista.DESEJA_ASSISTIR;
            mostrarLista(usuario.getDesejaAssistir());

        });

        btnRemover.addActionListener(e -> removerSerie());

        btnOrdenar.addActionListener(e -> ordenarLista());
    }
    
    private void buscarSeries() {

        String nome = txtBusca.getText().trim();

        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Digite o nome de uma série."
            );
            return;
        }

        try {

            listaBusca = tvMazeService.buscarSeries(nome);

            listaAtual = TipoLista.BUSCA;
            listaExibida = listaBusca;

            if (listaBusca.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Nenhuma série encontrada."
                );

            }

            atualizarTabela(listaExibida);

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao buscar séries."
            );
        }
    }

    private void adicionarSerie(List<Serie> listaDestino) {

        int linha = tabela.getSelectedRow();

        if (linha == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecione uma série."
            );

            return;
        }

        Serie serie = listaExibida.get(linha);

        if (listaDestino.contains(serie)) {

            JOptionPane.showMessageDialog(
                    this,
                    "A série já está nesta lista."
            );

            return;
        }

        listaDestino.add(serie);

        jsonService.salvarUsuario(usuario);

        JOptionPane.showMessageDialog(
                this,
                "Série adicionada com sucesso!"
        );
    }

    private void mostrarLista(List<Serie> lista) {

        listaExibida = lista;

        atualizarTabela(listaExibida);
    }

    private void removerSerie() {

        int linha = tabela.getSelectedRow();

        if (linha == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecione uma série."
            );

            return;
        }

        Serie serie = listaExibida.get(linha);

        switch (listaAtual) {

            case FAVORITOS:
                usuario.getFavoritos().remove(serie);
                break;

            case ASSISTIDAS:
                usuario.getAssistidas().remove(serie);
                break;

            case DESEJA_ASSISTIR:
                usuario.getDesejaAssistir().remove(serie);
                break;

            default:

                JOptionPane.showMessageDialog(
                        this,
                        "Selecione uma lista para remover."
                );
                return;
        }

        jsonService.salvarUsuario(usuario);

        atualizarTabela(listaExibida);

        JOptionPane.showMessageDialog(
                this,
                "Série removida com sucesso."
        );
    }

    private void ordenarLista() {

        String criterio = (String) cbOrdenacao.getSelectedItem();

        if ("Nome".equals(criterio)) {

            ordenacaoService.ordenarPorNome(listaExibida);

        } else if ("Nota".equals(criterio)) {

            ordenacaoService.ordenarPorNota(listaExibida);

        } else if ("Estado".equals(criterio)) {

            ordenacaoService.ordenarPorEstado(listaExibida);

        } else {

            ordenacaoService.ordenarPorDataEstreia(listaExibida);
        }

        atualizarTabela(listaExibida);
    }

    private void atualizarTabela(List<Serie> lista) {

        modeloTabela.setRowCount(0);

        for (Serie serie : lista) {

            modeloTabela.addRow(new Object[]{

                    serie.getNome(),
                    serie.getIdioma(),
                    String.join(", ", serie.getGeneros()),
                    serie.getNota(),
                    serie.getEstado(),
                    serie.getDataEstreia(),
                    serie.getDataTermino(),
                    serie.getEmissora()

            });
        }
    }

}