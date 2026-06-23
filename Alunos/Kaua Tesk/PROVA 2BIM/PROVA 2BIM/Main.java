import javax.swing.*;


public class Main {

    public static void main(String[] args) {

        
        SwingUtilities.invokeLater(() -> {

            //tema visual - antes tava invisivel no windows 
            try {
               
                UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");

              
                // Cores gerais de painéis e fundo
                UIManager.put("Panel.background",             new java.awt.Color(18, 18, 18));
                UIManager.put("Panel.foreground",             java.awt.Color.WHITE);

                // Campos de texto
                UIManager.put("TextField.background",         new java.awt.Color(40, 40, 40));
                UIManager.put("TextField.foreground",         java.awt.Color.WHITE);
                UIManager.put("TextField.caretForeground",    java.awt.Color.WHITE);
                UIManager.put("TextField.selectionBackground",new java.awt.Color(229, 9, 20));
                UIManager.put("TextField.selectionForeground",java.awt.Color.WHITE);
                UIManager.put("TextField.inactiveForeground", new java.awt.Color(130, 130, 130));

                // Listas (JList)
                UIManager.put("List.background",              new java.awt.Color(22, 22, 22));
                UIManager.put("List.foreground",              java.awt.Color.WHITE);
                UIManager.put("List.selectionBackground",     new java.awt.Color(229, 9, 20));
                UIManager.put("List.selectionForeground",     java.awt.Color.WHITE);

                // ScrollPane e ScrollBar
                UIManager.put("ScrollPane.background",        new java.awt.Color(22, 22, 22));
                UIManager.put("ScrollBar.background",         new java.awt.Color(30, 30, 30));
                UIManager.put("ScrollBar.thumb",              new java.awt.Color(60, 60, 60));
                UIManager.put("ScrollBar.thumbHighlight",     new java.awt.Color(80, 80, 80));
                UIManager.put("ScrollBar.thumbShadow",        new java.awt.Color(40, 40, 40));
                UIManager.put("ScrollBar.track",              new java.awt.Color(25, 25, 25));
                UIManager.put("ScrollBar.trackHighlight",     new java.awt.Color(30, 30, 30));

                // ComboBox
                UIManager.put("ComboBox.background",          new java.awt.Color(45, 45, 45));
                UIManager.put("ComboBox.foreground",          java.awt.Color.WHITE);
                UIManager.put("ComboBox.selectionBackground", new java.awt.Color(229, 9, 20));
                UIManager.put("ComboBox.selectionForeground", java.awt.Color.WHITE);
                UIManager.put("ComboBox.disabledBackground",  new java.awt.Color(35, 35, 35));
                UIManager.put("ComboBox.buttonBackground",    new java.awt.Color(50, 50, 50));

                // Diálogos (JOptionPane)
                UIManager.put("OptionPane.background",        new java.awt.Color(30, 30, 30));
                UIManager.put("OptionPane.messageForeground", java.awt.Color.WHITE);
                UIManager.put("OptionPane.buttonAreaBackground", new java.awt.Color(30, 30, 30));

                // SplitPane (divisor entre painel esquerdo e PainelDetalhes)
                UIManager.put("SplitPane.background",         new java.awt.Color(18, 18, 18));
                UIManager.put("SplitPaneDivider.background",  new java.awt.Color(50, 50, 50));

            } catch (Exception e) {
                // Se falhar (raro), o Java usa o Metal com cores padrão — não é crítico
                System.err.println("Nao foi possivel aplicar o tema: " + e.getMessage());
            }

           // tenta carregar dados salvos
            PersistenciaService persistencia = new PersistenciaService();
            Usuario usuario = null;

            try {
                usuario = persistencia.carregar(); // retorna null no primeiro acesso
            } catch (Exception e) {
                // avisa e começa do zero
                JOptionPane.showMessageDialog(
                    null,
                    "Nao foi possivel carregar os dados salvos.\n"
                    + "O sistema iniciara com perfil em branco.\n\nDetalhes: " + e.getMessage(),
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
                );
                usuario = null; // garante que vai cair no fluxo de primeiro acesso
            }

            // ── 3. Primeiro acesso ou retorno 
            if (usuario == null) {
               
                TelaLogin telaLogin = new TelaLogin(null);
                telaLogin.setVisible(true); // bloqueante (modal)

                String nome = telaLogin.getNomeUsuario();

                if (nome == null) {
                    // Usuário fechou a tela sem digitar o nome → encerra o programa
                    System.exit(0);
                }

                usuario = new Usuario(nome); // cria perfil novo

            } else {
                // Retorno: dados carregados com sucesso → mensagem de boas-vindas
                JOptionPane.showMessageDialog(
                    null,
                    "Bem-vindo de volta, " + usuario.getNome() + "! \n"
                    + "Suas listas foram carregadas com sucesso.",
                    "SeriesTracker",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }

            // ── 4. Abre a tela principal ──────────────────────────────────────────
            TelaPrincipal telaPrincipal = new TelaPrincipal(usuario);
            telaPrincipal.setVisible(true);
        });
    }
}
