package fag.main.ui;

import fag.main.service.BibliotecaSeries;

import javax.swing.JFrame;
import java.awt.Dimension;

/**
 * Janela inicial exibida ao abrir o programa, responsável por hospedar o
 * {@link PainelLogin}. Após a identificação do usuário, esta janela é
 * fechada e a {@link JanelaPrincipal} é aberta em seu lugar.
 */
public class JanelaLogin extends JFrame {

    public JanelaLogin(BibliotecaSeries biblioteca) {
        super("Minhas Séries de TV — Identificação");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(700, 560));
        setLocationRelativeTo(null);
        getContentPane().setBackground(Tema.FUNDO_PRINCIPAL);

        PainelLogin painelLogin = new PainelLogin(biblioteca, ignorado -> {
            JanelaPrincipal janelaPrincipal = new JanelaPrincipal(biblioteca);
            janelaPrincipal.setVisible(true);
            dispose();
        });

        add(painelLogin);
    }
}
