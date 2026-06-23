import controller.SeriesController;
import model.Conta;
import service.ContaService;
import view.LoginFrame;
import view.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * CLASSE PRINCIPAL: Ponto de e	ntrada da aplicação Java.
 * Contém o método 'main' — o primeiro método executado pela JVM.
 *
 * NOVO FLUXO COM LOGIN:
 *   1. Carrega todas as contas do arquivo contas.json
 *   2. Exibe o LoginFrame (tela de login/cadastro) — bloqueante (modal)
 *   3. Se o usuário autenticou: abre o MainFrame com os dados da conta
 *   4. Se o usuário fechou o login sem autenticar: encerra o programa
 */
public class Main {

    public static void main(String[] args) {

        // 1. Configurar aparência (Look and Feel do SO) 
        configurarAparencia();

        // 2. Carregar todas as contas do arquivo contas.json
        // Se o arquivo não existir, o ContaService cria automaticamente
        // com uma conta demo (login: demo, senha: 1234)
        List<Conta> contas = null;
        try {
            contas = ContaService.carregarContas(); // Lê C:\QuartoDoSaber\contas.json
        } catch (Exception e) {
            // ANTI-QUEBRA: se falhar ao carregar, avisa e encerra com dignidade
            JOptionPane.showMessageDialog(
                null,
                "Não foi possível carregar as contas.\n" +
                "Caminho: " + ContaService.CAMINHO_CONTAS + "\n" +
                "Erro: " + e.getMessage(),
                "Erro ao Iniciar",
                JOptionPane.ERROR_MESSAGE
            );
            System.exit(1); // Encerra com código de erro
        }

        // 'final' obrigatório para usar a variável dentro do lambda abaixo
        final List<Conta> contasFinal = contas;

        // 3. Iniciar na EDT: mostrar login e depois o app principal
        // invokeLater agenda o código para rodar na EDT (thread correta do Swing)
        SwingUtilities.invokeLater(() -> {

            // 3a. Exibe a tela de Login 
            // LoginFrame é um JDialog modal — bloqueia até o usuário fechar
            LoginFrame loginFrame = new LoginFrame(contasFinal);
            loginFrame.setVisible(true); // BLOQUEIA aqui até o diálogo fechar

            // 3b. Verifica se o usuário autenticou 
            // Após o diálogo fechar, getContaAutenticada() retorna:
            //   - null: fechou sem fazer login (clicou no X)
            //   - Conta: autenticou com sucesso
            Conta contaLogada = loginFrame.getContaAutenticada();

            if (contaLogada == null) {
                // Usuário fechou o login sem autenticar — encerra o programa
                System.exit(0);
                return;
            }

            // 3c. Login bem-sucedido: abre o MainFrame 
            // Cria o controller com o Usuario da conta autenticada
            // Passamos também a lista de contas e a conta logada para que
            // o MainFrame salve via ContaService ao fechar
            SeriesController controller = new SeriesController(
                contaLogada.getUsuario(), // Dados de séries do usuário logado
                contaLogada,              // Conta completa (login + senha + usuario)
                contasFinal               // Lista completa de contas (para salvar tudo)
            );

            MainFrame frame = new MainFrame(controller);
            frame.setVisible(true); // Exibe a janela principal
        });
    }

    /**
     * Configura o Look and Feel da interface Swing.
     * Aplica o estilo visual nativo do sistema operacional.
     */
    private static void configurarAparencia() {
        try {
            // getSystemLookAndFeelClassName() retorna a classe do tema do SO
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("ScrollBar.width", 8);
            UIManager.put("OptionPane.buttonFont",  new Font("Segoe UI", Font.PLAIN, 13));
            UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 13));
        } catch (Exception e) {
            // Não é crítico — usa o padrão Metal do Java
            System.err.println("Look and Feel do sistema indisponivel.");
        }
    }
}