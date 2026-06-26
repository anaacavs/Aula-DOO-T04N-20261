package view;

import java.awt.BorderLayout;

import javax.swing.*;

import controller.UsuarioController;
import model.Serie;

// Tela responsável por exibir e gerenciar
// a lista de séries que o usuário já assistiu.
public class TelaAssistidas extends JFrame {

    // Controller responsável por acessar e manipular
    // os dados do usuário
    private UsuarioController controller;

    // Modelo da lista utilizado pela JList
    private DefaultListModel<Serie> modelo;

    // Componente visual que exibe as séries assistidas
    private JList<Serie> lista;

    // Construtor da tela
    public TelaAssistidas(
            UsuarioController controller) {

        // Recebe o controller compartilhado da aplicação
        this.controller =
                controller;

        // Define o título da janela
        setTitle("Séries Assistidas");

        // Define o tamanho da janela
        setSize(600,400);

        // Centraliza a janela na tela
        setLocationRelativeTo(null);

        // Define o layout da janela
        setLayout(new BorderLayout());

        // Cria o modelo da lista
        modelo =
                new DefaultListModel<>();

        // Cria a lista utilizando o modelo
        lista =
                new JList<>(modelo);

        // Carrega as séries assistidas salvas
        carregar();

        // Adiciona a lista à tela com barra de rolagem
        add(
                new JScrollPane(lista),
                BorderLayout.CENTER);

        // Cria o botão remover
        JButton btnRemover =
                new JButton("Remover");

        // Adiciona o botão na parte inferior da janela
        add(
                btnRemover,
                BorderLayout.SOUTH);

        // Define o evento do botão remover
        btnRemover.addActionListener(
                e -> remover());
    }

    // Carrega todas as séries assistidas
    // para a lista visual
    private void carregar() {

        // Remove todos os itens atuais da lista
        modelo.clear();

        // Percorre todas as séries assistidas
        for(Serie serie :
                controller.getDados()
                        .getAssistidas()) {

            // Adiciona cada série ao modelo da lista
            modelo.addElement(serie);
        }
    }

    // Remove uma série da lista de assistidas
    private void remover() {

        // Obtém a série selecionada pelo usuário
        Serie serie =
                lista.getSelectedValue();

        // Verifica se uma série foi selecionada
        if(serie != null) {

            // Remove a série da lista de assistidas
            controller.getDados()
                    .removerAssistida(serie);

            // Salva as alterações no arquivo JSON
            controller.salvar();

            // Atualiza a lista visual
            carregar();
        }
    }
}