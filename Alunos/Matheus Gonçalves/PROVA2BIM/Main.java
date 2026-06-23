package fag.main;

import fag.main.persistence.PersistenciaException;
import fag.main.service.BibliotecaSeries;
import fag.main.ui.JanelaLogin;
import fag.main.ui.Tema;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Ponto de entrada da aplicação "Minhas Séries de TV".
 *
 * Responsabilidades desta classe:
 *  1. Configurar um tratador global de exceções não previstas, garantindo
 *     que o programa nunca feche inesperadamente, mesmo diante de um erro
 *     de programação não antecipado.
 *  2. Carregar os dados persistidos localmente (arquivo JSON).
 *  3. Abrir a janela de identificação do usuário (login local).
 *
 * Toda a interface é construída e manipulada exclusivamente na Event
 * Dispatch Thread do Swing, conforme a boa prática recomendada para
 * aplicações gráficas em Java.
 */
public class Main {

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((thread, excecao) -> {
            excecao.printStackTrace();
            try {
                SwingUtilities.invokeLater(() -> {
                    try {
                    	JOptionPane.showMessageDialog(null, "Ocorreu um erro inesperado no sistema:\n" + excecao.getMessage()
                    		+ "\n\nO programa tentará continuar em execução.",
                            "Erro inesperado", JOptionPane.ERROR_MESSAGE);
                    } catch (Throwable dialogoFalhou) {
                        System.err.println("Não foi possível exibir o diálogo de erro: " + dialogoFalhou.getMessage());
                    }
                });
            } catch (Throwable agendamentoFalhou) {
                System.err.println("Não foi possível agendar o diálogo de erro: " + agendamentoFalhou.getMessage());
            }
        });

        configurarAparenciaPadrao();

        SwingUtilities.invokeLater(Main::iniciarAplicacao);
    }


     /* Ajusta pequenas configurações globais do Swing para que componentes
     * padrão (como JOptionPane, JScrollBar, tooltips) também sigam, na
     * medida do possível, a identidade visual escura da aplicação.
     */
     
    private static void configurarAparenciaPadrao() {
        try {
            UIManager.put("OptionPane.background", Tema.FUNDO_CARTAO);
            UIManager.put("Panel.background", Tema.FUNDO_CARTAO);
            UIManager.put("OptionPane.messageForeground", Tema.TEXTO_PRIMARIO);
            UIManager.put("Button.background", Tema.FUNDO_CAMPO);
            UIManager.put("Button.foreground", Tema.TEXTO_PRIMARIO);
            UIManager.put("ToolTip.background", Tema.FUNDO_CARTAO);
            UIManager.put("ToolTip.foreground", Tema.TEXTO_PRIMARIO);
        } catch (Exception e) {
            System.err.println("Não foi possível aplicar o tema visual customizado: " + e.getMessage());
        }
    }

    /**
     * Carrega os dados locais e abre a primeira tela do sistema. Qualquer
     * falha ao ler o arquivo de dados é tratada aqui, perguntando ao
     * usuário se deseja continuar mesmo assim com uma base de dados vazia,
     * em vez de simplesmente encerrar o programa.
     */
    private static void iniciarAplicacao() {
        BibliotecaSeries biblioteca = new BibliotecaSeries();

        try {
            biblioteca.carregarDados();
        } catch (PersistenciaException e) {
            int opcao = JOptionPane.showConfirmDialog(null,
                    "Não foi possível carregar os dados salvos anteriormente:\n" + e.getMessage()
                            + "\n\nDeseja continuar mesmo assim com uma base de dados vazia?"
                            + "\n(Atenção: ao salvar novamente, o arquivo anterior poderá ser sobrescrito)",
                    "Erro ao carregar dados",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (opcao != JOptionPane.YES_OPTION) {
                System.exit(0);
                return;
            }
        }

        JanelaLogin janelaLogin = new JanelaLogin(biblioteca);
        janelaLogin.setVisible(true);
    }
}
