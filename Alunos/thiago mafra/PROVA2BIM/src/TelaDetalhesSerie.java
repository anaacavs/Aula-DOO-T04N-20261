package view;

import model.Serie;

import javax.swing.*;
import java.awt.*;

// Tela responsável por exibir todas as informações
// detalhadas de uma série selecionada pelo usuário.
public class TelaDetalhesSerie extends JFrame {

    // Construtor que recebe uma série para exibir seus detalhes
    public TelaDetalhesSerie(Serie serie) {

        // Define o título da janela
        setTitle("Detalhes da Série");

        // Define o tamanho da janela
        setSize(600,500);

        // Centraliza a janela na tela
        setLocationRelativeTo(null);

        // Fecha apenas esta janela ao clicar no X
        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE);

        // Define o layout da janela
        setLayout(new BorderLayout());

        // Cria uma área de texto para exibir os dados
        JTextArea area =
                new JTextArea();

        // Impede que o usuário edite o conteúdo
        area.setEditable(false);

        // Permite quebra automática de linha
        area.setLineWrap(true);

        // Faz a quebra de linha respeitar as palavras
        area.setWrapStyleWord(true);

        // Objeto utilizado para montar o texto
        // de forma eficiente
        StringBuilder texto =
                new StringBuilder();

        // Adiciona o nome da série
        texto.append("Nome: ")
                .append(serie.getNome())
                .append("\n\n");

        // Adiciona o idioma da série
        texto.append("Idioma: ")
                .append(serie.getIdioma())
                .append("\n\n");

        // Adiciona a lista de gêneros
        texto.append("Gêneros: ")
                .append(serie.getGeneros())
                .append("\n\n");

        // Adiciona a nota geral da série
        texto.append("Nota Geral: ")
                .append(serie.getNota())
                .append("\n\n");

        // Adiciona o status da série
        // Exemplo: Running, Ended ou Canceled
        texto.append("Status: ")
                .append(serie.getStatus())
                .append("\n\n");

        // Adiciona a data de estreia
        texto.append("Data de Estreia: ")
                .append(serie.getEstreia())
                .append("\n\n");

        // Adiciona a data de término
        texto.append("Data de Término: ")
                .append(serie.getTermino())
                .append("\n\n");

        // Adiciona o nome da emissora
        texto.append("Emissora: ")
                .append(serie.getEmissora());

        // Define o texto montado na área de texto
        area.setText(
                texto.toString());

        // Adiciona a área de texto com barra de rolagem
        add(
                new JScrollPane(area),
                BorderLayout.CENTER);
    }
}