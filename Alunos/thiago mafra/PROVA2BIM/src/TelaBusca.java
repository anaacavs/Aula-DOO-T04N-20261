package view;

import controller.SerieController;
import controller.UsuarioController;
import model.Serie;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

// Tela responsável pela busca de séries na API TVMaze
// e pelo gerenciamento das listas do usuário.
public class TelaBusca extends JFrame {

    // Campo onde o usuário digita o nome da série
    private JTextField txtBusca;

    // Botões da interface
    private JButton btnBuscar;
    private JButton btnFavorito;
    private JButton btnAssistida;
    private JButton btnDesejo;

    // Lista visual que exibirá as séries encontradas
    private JList<Serie> listaSeries;

    // Modelo utilizado pela JList para armazenar os itens
    private DefaultListModel<Serie> modeloLista;

    // Controller responsável pela comunicação com a API
    private SerieController serieController;

    // Controller responsável pelos dados do usuário
    private UsuarioController usuarioController;

    // Construtor da tela
    public TelaBusca(
            UsuarioController usuarioController) {

        // Recebe o controller do usuário vindo da tela principal
        this.usuarioController =
                usuarioController;

        // Cria o controller responsável pela busca de séries
        serieController =
                new SerieController();

        // Configura as propriedades da janela
        configurarTela();

        // Cria os componentes visuais
        criarComponentes();

        // Configura os eventos dos botões e da lista
        configurarEventos();
    }

    // Configura tamanho, título e layout da janela
    private void configurarTela() {

        // Define o título da janela
        setTitle("Buscar Séries");

        // Define o tamanho da janela
        setSize(800, 600);

        // Centraliza a janela na tela
        setLocationRelativeTo(null);

        // Fecha apenas esta janela quando o usuário clicar no X
        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE);

        // Define o layout principal da tela
        setLayout(new BorderLayout());
    }

    // Cria todos os componentes gráficos da tela
    private void criarComponentes() {

        // Painel superior para busca
        JPanel painelBusca =
                new JPanel(
                        new BorderLayout());

        // Campo de texto para digitação
        txtBusca =
                new JTextField();

        // Botão de busca
        btnBuscar =
                new JButton("Buscar");

        // Adiciona o campo de texto no centro
        painelBusca.add(
                txtBusca,
                BorderLayout.CENTER);

        // Adiciona o botão à direita
        painelBusca.add(
                btnBuscar,
                BorderLayout.EAST);

        // Adiciona o painel de busca ao topo da tela
        add(
                painelBusca,
                BorderLayout.NORTH);

        // Cria o modelo da lista
        modeloLista =
                new DefaultListModel<>();

        // Cria a lista visual usando o modelo
        listaSeries =
                new JList<>(modeloLista);

        // Adiciona barra de rolagem à lista
        add(
                new JScrollPane(listaSeries),
                BorderLayout.CENTER);

        // Painel inferior contendo os botões
        JPanel painelBotoes =
                new JPanel(
                        new GridLayout(1,3));

        // Botão para adicionar aos favoritos
        btnFavorito =
                new JButton(
                        "Adicionar Favorito");

        // Botão para marcar como assistida
        btnAssistida =
                new JButton(
                        "Adicionar Assistida");

        // Botão para adicionar à lista de desejo
        btnDesejo =
                new JButton(
                        "Adicionar Desejo");

        // Adiciona os botões ao painel
        painelBotoes.add(
                btnFavorito);

        painelBotoes.add(
                btnAssistida);

        painelBotoes.add(
                btnDesejo);

        // Adiciona o painel à parte inferior da tela
        add(
                painelBotoes,
                BorderLayout.SOUTH);
    }

    // Configura os eventos dos componentes
    private void configurarEventos() {

        // Evento do botão Buscar
        btnBuscar.addActionListener(
                e -> pesquisarSerie());

        // Evento para adicionar aos favoritos
        btnFavorito.addActionListener(
                e -> adicionarFavorito());

        // Evento para adicionar às assistidas
        btnAssistida.addActionListener(
                e -> adicionarAssistida());

        // Evento para adicionar à lista de desejo
        btnDesejo.addActionListener(
                e -> adicionarDesejo());

        // Evento de clique na lista
        listaSeries.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            MouseEvent e) {

                        // Verifica se foi clique duplo
                        if(e.getClickCount() == 2) {

                            // Obtém a série selecionada
                            Serie serie =
                                    listaSeries
                                            .getSelectedValue();

                            // Se existir uma série selecionada
                            if(serie != null) {

                                // Abre a tela de detalhes da série
                                TelaDetalhesSerie tela =
                                        new TelaDetalhesSerie(
                                                serie);

                                tela.setVisible(true);
                            }
                        }
                    }
                });
    }

    // Realiza a busca da série na API
    private void pesquisarSerie() {

        try {

            // Obtém o texto digitado pelo usuário
            String nome =
                    txtBusca
                            .getText()
                            .trim();

            // Verifica se o campo está vazio
            if(nome.isBlank()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Digite o nome da série ou filme.");

                return;
            }

            // Pesquisa a série na API
            List<Serie> resultado =
                    serieController
                            .pesquisar(nome);

            // Limpa a lista atual
            modeloLista.clear();

            // Verifica se nenhum resultado foi encontrado
            if(resultado == null
                    || resultado.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Série ou filme não encontrado. Tente novamente.");

                return;
            }

            // Adiciona as séries encontradas na lista visual
            for(Serie serie : resultado) {

                modeloLista.addElement(
                        serie);
            }

        } catch (Exception ex) {

            // Exibe mensagem de erro
            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao realizar pesquisa.");

            // Mostra o erro no console para depuração
            ex.printStackTrace();
        }
    }

    // Adiciona uma série à lista de favoritos
    private void adicionarFavorito() {

        // Obtém a série selecionada
        Serie serie =
                listaSeries
                        .getSelectedValue();

        // Verifica se foi selecionada uma série
        if(serie == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecione uma série.");

            return;
        }

        // Adiciona aos favoritos
        usuarioController
                .getDados()
                .adicionarFavorito(serie);

        // Salva no JSON
        usuarioController.salvar();

        // Exibe confirmação
        JOptionPane.showMessageDialog(
                this,
                "Série adicionada aos favoritos.");
    }

    // Adiciona uma série à lista de assistidas
    private void adicionarAssistida() {

        Serie serie =
                listaSeries
                        .getSelectedValue();

        if(serie == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecione uma série.");

            return;
        }

        // Adiciona a série à lista de assistidas
        usuarioController
                .getDados()
                .adicionarAssistida(serie);

        // Salva os dados no JSON
        usuarioController.salvar();

        JOptionPane.showMessageDialog(
                this,
                "Série adicionada às assistidas.");
    }

    // Adiciona uma série à lista de desejo
    private void adicionarDesejo() {

        Serie serie =
                listaSeries
                        .getSelectedValue();

        if(serie == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecione uma série.");

            return;
        }

        // Adiciona à lista de desejo
        usuarioController
                .getDados()
                .adicionarDesejo(serie);

        // Salva os dados no arquivo JSON
        usuarioController.salvar();

        JOptionPane.showMessageDialog(
                this,
                "Série adicionada à lista de desejo.");
    }
}