package principal;

import javax.swing.SwingUtilities;

import view.TelaPrincipal;

public class Main {

    public static void main(String[] args) {

        // Executa a interface gráfica na Event Dispatch Thread (EDT),
        // que é a thread apropriada para aplicações Swing.
        SwingUtilities.invokeLater(() -> {

            // Cria uma instância da tela principal do sistema.
            TelaPrincipal tela =
                    new TelaPrincipal();

            // Torna a janela visível para o usuário.
            tela.setVisible(true);

        });
    }
}