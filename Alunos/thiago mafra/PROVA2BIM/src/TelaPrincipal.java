package view;

import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import controller.UsuarioController;

// Tela principal do sistema.
// É a primeira janela exibida ao usuário e serve como menu principal.
public class TelaPrincipal extends JFrame {

    // Botão para abrir a tela de busca de séries
    private JButton btnBuscar;

    // Botão para abrir a lista de favoritos
    private JButton btnFavoritos;

    // Botão para abrir a lista de séries assistidas
    private JButton btnAssistidas;

    // Botão para abrir a lista de desejo de assistir
    private JButton btnDesejo;

    // Label que exibe o nome e apelido do usuário
    private JLabel lblUsuario;

    // Controller responsável por manipular os dados do usuário
    private UsuarioController usuarioController;

    // Construtor da tela principal
    public TelaPrincipal() {

        // Cria o controller do usuário
        usuarioController =
                new UsuarioController();

        // Verifica se já existe um usuário cadastrado
        verificarUsuario();

        // Define o título da janela
        setTitle(
                "Controle de Séries TV");

        // Define o tamanho da janela
        setSize(450,400);

        // Centraliza a janela na tela
        setLocationRelativeTo(null);

        // Fecha a aplicação ao clicar no X
        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE);

        // Define o layout em grade:
        // 6 linhas e 1 coluna com espaçamento
        setLayout(
                new GridLayout(6,1,10,10));

        // Cria o label que exibirá o usuário centralizado
        lblUsuario =
                new JLabel(
                        "",
                        SwingConstants.CENTER);

        // Atualiza o texto do usuário na tela
        atualizarNomeUsuario();

        // Cria o botão para buscar séries
        btnBuscar =
                new JButton(
                        "Buscar Série");

        // Cria o botão de favoritos
        btnFavoritos =
                new JButton(
                        "Favoritos");

        // Cria o botão de séries assistidas
        btnAssistidas =
                new JButton(
                        "Já Assistidas");

        // Cria o botão da lista de desejo
        btnDesejo =
                new JButton(
                        "Desejo Assistir");

        // Adiciona os componentes à janela
        add(lblUsuario);
        add(btnBuscar);
        add(btnFavoritos);
        add(btnAssistidas);
        add(btnDesejo);

        // Configura os eventos dos botões
        configurarEventos();

        // Adiciona um evento para salvar os dados
        // automaticamente ao fechar o programa
        addWindowListener(
                new WindowAdapter() {

                    @Override
                    public void windowClosing(
                            WindowEvent e) {

                        // Salva os dados do usuário no JSON
                        usuarioController.salvar();
                    }
                });
    }

    // Verifica se já existe um usuário cadastrado
    private void verificarUsuario() {

        // Caso o usuário seja nulo,
        // abre a tela de cadastro
        if(usuarioController
                .getDados()
                .getUsuario() == null) {

            TelaCadastroUsuario tela =
                    new TelaCadastroUsuario(
                            usuarioController);

            // Exibe a tela de cadastro
            tela.setVisible(true);
        }
    }

    // Atualiza o texto exibindo nome e apelido do usuário
    private void atualizarNomeUsuario() {

        lblUsuario.setText(

                "Usuário: "
                        + usuarioController
                        .getDados()
                        .getUsuario()
                        .getNome()

                        + " ("

                        + usuarioController
                        .getDados()
                        .getUsuario()
                        .getApelido()

                        + ")"
        );
    }

    // Configura os eventos dos botões da tela
    private void configurarEventos() {

        // Evento do botão Buscar Série
        btnBuscar.addActionListener(e -> {

            // Cria a tela de busca
            TelaBusca tela =
                    new TelaBusca(
                            usuarioController);

            // Exibe a tela
            tela.setVisible(true);
        });

        // Evento do botão Favoritos
        btnFavoritos.addActionListener(e -> {

            // Cria a tela de favoritos
            TelaFavoritos tela =
                    new TelaFavoritos(
                            usuarioController);

            // Exibe a tela
            tela.setVisible(true);
        });

        // Evento do botão Já Assistidas
        btnAssistidas.addActionListener(e -> {

            // Cria a tela das séries assistidas
            TelaAssistidas tela =
                    new TelaAssistidas(
                            usuarioController);

            // Exibe a tela
            tela.setVisible(true);
        });

        // Evento do botão Desejo Assistir
        btnDesejo.addActionListener(e -> {

            // Cria a tela da lista de desejo
            TelaDesejoAssistir tela =
                    new TelaDesejoAssistir(
                            usuarioController);

            // Exibe a tela
            tela.setVisible(true);
        });
    }
}