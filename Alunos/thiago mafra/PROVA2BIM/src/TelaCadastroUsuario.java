package view;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import controller.UsuarioController;
import model.Usuario;

// Tela responsável pelo cadastro do usuário.
// Nela o usuário informa seu nome e apelido.
public class TelaCadastroUsuario extends JDialog {

    // Construtor da tela de cadastro
    public TelaCadastroUsuario(
            UsuarioController controller) {

        // Define o título da janela
        setTitle("Cadastro do Usuário");

        // Define o tamanho da janela
        setSize(350,200);

        // Define a janela como modal.
        // Enquanto ela estiver aberta, o usuário
        // não poderá interagir com outras telas.
        setModal(true);

        // Centraliza a janela na tela
        setLocationRelativeTo(null);

        // Define o layout em grade:
        // 3 linhas, 2 colunas e espaçamento
        setLayout(new GridLayout(3,2,5,5));

        // Cria o rótulo do campo nome
        JLabel lblNome =
                new JLabel("Nome:");

        // Campo de texto para o nome
        JTextField txtNome =
                new JTextField();

        // Cria o rótulo do campo apelido
        JLabel lblApelido =
                new JLabel("Apelido:");

        // Campo de texto para o apelido
        JTextField txtApelido =
                new JTextField();

        // Botão para salvar os dados
        JButton btnSalvar =
                new JButton("Salvar");

        // Adiciona os componentes na janela
        add(lblNome);
        add(txtNome);

        add(lblApelido);
        add(txtApelido);

        // Espaço vazio para alinhar o botão
        add(new JLabel());

        // Adiciona o botão salvar
        add(btnSalvar);

        // Evento executado ao clicar no botão Salvar
        btnSalvar.addActionListener(e -> {

            // Obtém o nome digitado removendo espaços extras
            String nome =
                    txtNome.getText().trim();

            // Obtém o apelido digitado removendo espaços extras
            String apelido =
                    txtApelido.getText().trim();

            // Verifica se o nome foi informado
            if(nome.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Informe o nome.");

                return;
            }

            // Verifica se o apelido foi informado
            if(apelido.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Informe o apelido.");

                return;
            }

            // Cria um objeto Usuario com os dados informados
            Usuario usuario =
                    new Usuario(
                            nome,
                            apelido);

            // Salva o usuário dentro dos dados da aplicação
            controller
                    .getDados()
                    .setUsuario(usuario);

            // Persiste os dados no arquivo JSON
            controller.salvar();

            // Fecha a janela de cadastro
            dispose();
        });
    }
}