package view;

import controller.UsuarioController;
import model.Serie;
import service.OrdenacaoService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// Tela responsável por exibir e gerenciar
// a lista de séries favoritas do usuário.
public class TelaFavoritos extends JFrame {

    // Controller responsável pelos dados do usuário
    private UsuarioController controller;

    // Modelo da lista que armazena as séries exibidas
    private DefaultListModel<Serie> modelo;

    // Componente visual que exibe as séries
    private JList<Serie> lista;

    // Botões da interface
    private JButton btnRemover;
    private JButton btnNome;
    private JButton btnNota;
    private JButton btnStatus;
    private JButton btnEstreia;

    // Construtor da tela
    public TelaFavoritos(
            UsuarioController controller) {

        // Recebe o controller compartilhado da aplicação
        this.controller = controller;

        // Configura propriedades da janela
        configurarTela();

        // Cria os componentes visuais
        criarComponentes();

        // Carrega as séries favoritas na lista
        carregarLista();
    }

    // Configura as propriedades básicas da janela
    private void configurarTela() {

        // Define o título da janela
        setTitle("Favoritos");

        // Define o tamanho da janela
        setSize(800,500);

        // Centraliza a janela na tela
        setLocationRelativeTo(null);

        // Fecha apenas esta janela
        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE);

        // Define o layout da janela
        setLayout(new BorderLayout());
    }

    // Cria todos os componentes gráficos da interface
    private void criarComponentes() {

        // Painel superior contendo os botões de ordenação
        JPanel painelTopo =
                new JPanel();

        // Botão para ordenar por nome
        btnNome =
                new JButton("Nome");

        // Botão para ordenar por nota
        btnNota =
                new JButton("Nota");

        // Botão para ordenar por status
        btnStatus =
                new JButton("Status");

        // Botão para ordenar por data de estreia
        btnEstreia =
                new JButton("Estreia");

        // Adiciona os botões ao painel
        painelTopo.add(btnNome);
        painelTopo.add(btnNota);
        painelTopo.add(btnStatus);
        painelTopo.add(btnEstreia);

        // Adiciona o painel ao topo da janela
        add(
                painelTopo,
                BorderLayout.NORTH);

        // Cria o modelo da lista
        modelo =
                new DefaultListModel<>();

        // Cria a lista usando o modelo
        lista =
                new JList<>(modelo);

        // Adiciona a lista com barra de rolagem
        add(
                new JScrollPane(lista),
                BorderLayout.CENTER);

        // Cria o botão remover
        btnRemover =
                new JButton("Remover");

        // Adiciona o botão na parte inferior
        add(
                btnRemover,
                BorderLayout.SOUTH);

        // Configura os eventos da interface
        configurarEventos();
    }

    // Configura os eventos dos componentes
    private void configurarEventos() {

        // Evento do botão remover
        btnRemover.addActionListener(
                e -> remover());

        // Evento para ordenar por nome
        btnNome.addActionListener(
                e -> {

                    // Ordena alfabeticamente
                    OrdenacaoService
                            .ordenarPorNome(
                                    controller
                                            .getDados()
                                            .getFavoritos());

                    // Atualiza a lista
                    carregarLista();
                });

        // Evento para ordenar por nota
        btnNota.addActionListener(
                e -> {

                    // Ordena da maior nota para a menor
                    OrdenacaoService
                            .ordenarPorNota(
                                    controller
                                            .getDados()
                                            .getFavoritos());

                    // Atualiza a lista
                    carregarLista();
                });

        // Evento para ordenar por status
        btnStatus.addActionListener(
                e -> {

                    // Ordena pelo estado da série
                    OrdenacaoService
                            .ordenarPorStatus(
                                    controller
                                            .getDados()
                                            .getFavoritos());

                    // Atualiza a lista
                    carregarLista();
                });

        // Evento para ordenar por estreia
        btnEstreia.addActionListener(
                e -> {

                    // Ordena pela data de estreia
                    OrdenacaoService
                            .ordenarPorEstreia(
                                    controller
                                            .getDados()
                                            .getFavoritos());

                    // Atualiza a lista
                    carregarLista();
                });

        // Evento de clique na lista
        lista.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            MouseEvent e) {

                        // Verifica se o usuário deu duplo clique
                        if(e.getClickCount() == 2) {

                            // Obtém a série selecionada
                            Serie serie =
                                    lista.getSelectedValue();

                            // Verifica se existe uma série selecionada
                            if(serie != null) {

                                // Abre a tela de detalhes da série
                                new TelaDetalhesSerie(
                                        serie)
                                        .setVisible(true);
                            }
                        }
                    }
                });
    }

    // Carrega as séries favoritas na lista visual
    private void carregarLista() {

        // Limpa os itens atuais da lista
        modelo.clear();

        // Percorre todas as séries favoritas
        for(Serie serie :
                controller
                        .getDados()
                        .getFavoritos()) {

            // Adiciona cada série ao modelo
            modelo.addElement(serie);
        }
    }

    // Remove uma série da lista de favoritos
    private void remover() {

        // Obtém a série selecionada
        Serie serie =
                lista.getSelectedValue();

        // Verifica se alguma série foi selecionada
        if(serie == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecione uma série.");

            return;
        }

        // Remove a série da lista de favoritos
        controller
                .getDados()
                .removerFavorito(serie);

        // Salva os dados atualizados no JSON
        controller.salvar();

        // Atualiza a lista visual
        carregarLista();
    }
}