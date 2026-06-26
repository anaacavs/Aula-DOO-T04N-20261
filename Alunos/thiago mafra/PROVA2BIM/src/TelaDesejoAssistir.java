package view;

import java.awt.BorderLayout;

import javax.swing.*;

import controller.UsuarioController;
import model.Serie;

// Tela responsável por exibir e gerenciar
// a lista de séries que o usuário deseja assistir.
public class TelaDesejoAssistir
        extends JFrame {

    // Controller responsável por acessar e modificar
    // os dados do usuário
    private UsuarioController controller;

    // Modelo que armazena as séries exibidas na lista
    private DefaultListModel<Serie> modelo;

    // Componente visual que exibe as séries
    private JList<Serie> lista;

    // Construtor da tela
    public TelaDesejoAssistir(
            UsuarioController controller) {

        // Recebe o controller compartilhado da aplicação
        this.controller =
                controller;

        // Define o título da janela
        setTitle(
                "Desejo Assistir");

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

        // Carrega as séries já armazenadas
        carregar();

        // Adiciona a lista à janela com barra de rolagem
        add(
                new JScrollPane(lista),
                BorderLayout.CENTER);

        // Cria o botão para remover séries da lista
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

    // Carrega todas as séries da lista
    // "Desejo Assistir" no componente visual
    private void carregar() {

        // Limpa os itens atuais da lista
        modelo.clear();

        // Percorre todas as séries da lista de desejo
        for(Serie serie :
                controller.getDados()
                        .getDesejoAssistir()) {

            // Adiciona cada série ao modelo visual
            modelo.addElement(serie);
        }
    }

    // Remove uma série da lista de desejo
    private void remover() {

        // Obtém a série selecionada pelo usuário
        Serie serie =
                lista.getSelectedValue();

        // Verifica se uma série foi selecionada
        if(serie != null) {

            // Remove a série da lista de desejo
            controller.getDados()
                    .removerDesejo(serie);

            // Salva as alterações no arquivo JSON
            controller.salvar();

            // Atualiza a lista exibida na tela
            carregar();
        }
    }
}