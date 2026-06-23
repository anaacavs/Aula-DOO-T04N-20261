package fag.main.ui;

import fag.main.persistence.PersistenciaException;
import fag.main.service.BibliotecaSeries;
import fag.main.ui.components.BotaoArredondado;
import fag.main.ui.components.PainelArredondado;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;
import java.util.function.Consumer;

/**
 * Tela inicial de identificação do usuário. Como o enunciado pede apenas um
 * controle local (nome/apelido, sem senha), esta tela permite tanto
 * selecionar um usuário já existente (cujas listas já foram salvas
 * anteriormente) quanto digitar um nome novo para começar do zero.
 */
public class PainelLogin extends JPanel {

    public PainelLogin(BibliotecaSeries biblioteca, Consumer<Void> aoEntrar) {
        setLayout(new GridBagLayout());
        setBackground(Tema.FUNDO_PRINCIPAL);

        PainelArredondado cartao = new PainelArredondado(Tema.FUNDO_CARTAO);
        cartao.setLayout(new BoxLayout(cartao, BoxLayout.Y_AXIS));
        cartao.setBorder(new EmptyBorder(36, 40, 36, 40));
        cartao.setPreferredSize(new Dimension(460, 380));

        JLabel logo = new JLabel("📺  Minhas Séries de TV");
        logo.setFont(Tema.FONTE_TITULO);
        logo.setForeground(Tema.TEXTO_PRIMARIO);
        logo.setAlignmentX(CENTER_ALIGNMENT);
        cartao.add(logo);

        cartao.add(Box.createVerticalStrut(6));

        JLabel subtitulo = new JLabel("Acompanhe suas séries favoritas, com dados da TVmaze");
        subtitulo.setFont(Tema.FONTE_PEQUENA);
        subtitulo.setForeground(Tema.TEXTO_SECUNDARIO);
        subtitulo.setAlignmentX(CENTER_ALIGNMENT);
        cartao.add(subtitulo);

        cartao.add(Box.createVerticalStrut(28));

        JLabel rotuloNome = new JLabel("Digite seu nome ou apelido:");
        rotuloNome.setFont(Tema.FONTE_CORPO_NEGRITO);
        rotuloNome.setForeground(Tema.TEXTO_PRIMARIO);
        rotuloNome.setAlignmentX(CENTER_ALIGNMENT);
        cartao.add(rotuloNome);

        cartao.add(Box.createVerticalStrut(8));

        JTextField campoNome = new JTextField();
        estilizarCampo(campoNome);
        campoNome.setAlignmentX(CENTER_ALIGNMENT);
        cartao.add(campoNome);

        cartao.add(Box.createVerticalStrut(18));

        List<String> usuariosExistentes = biblioteca.listarNomesUsuarios();
        if (!usuariosExistentes.isEmpty()) {
            JLabel rotuloExistentes = new JLabel("...ou selecione um usuário já cadastrado:");
            rotuloExistentes.setFont(Tema.FONTE_CORPO);
            rotuloExistentes.setForeground(Tema.TEXTO_SECUNDARIO);
            rotuloExistentes.setAlignmentX(CENTER_ALIGNMENT);
            cartao.add(rotuloExistentes);
            cartao.add(Box.createVerticalStrut(8));

            JComboBox<String> combo = new JComboBox<>(usuariosExistentes.toArray(new String[0]));
            combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            combo.setPreferredSize(new Dimension(200, 36));
            combo.setAlignmentX(CENTER_ALIGNMENT);
            combo.setBackground(Tema.FUNDO_CAMPO);
            combo.setForeground(Tema.TEXTO_PRIMARIO);
            combo.addActionListener(e -> {
                String selecionado = (String) combo.getSelectedItem();
                if (selecionado != null) {
                    campoNome.setText(selecionado);
                }
            });
            cartao.add(combo);
            cartao.add(Box.createVerticalStrut(18));
        }

        BotaoArredondado entrar = new BotaoArredondado("Entrar", BotaoArredondado.Variante.PRIMARIO);
        entrar.setAlignmentX(CENTER_ALIGNMENT);
        entrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        Runnable tentarEntrar = () -> {
            String nome = campoNome.getText();
            if (nome == null || nome.isBlank()) {
                JOptionPane.showMessageDialog(this,
                        "Por favor, digite um nome ou apelido para continuar.",
                        "Nome obrigatório", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                biblioteca.selecionarOuCriarUsuario(nome);
                aoEntrar.accept(null);
            } catch (PersistenciaException ex) {
                JOptionPane.showMessageDialog(this,
                        "Não foi possível salvar os dados do usuário:\n" + ex.getMessage(),
                        "Erro ao salvar", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Nome inválido", JOptionPane.WARNING_MESSAGE);
            }
        };

        entrar.addActionListener(e -> tentarEntrar.run());
        // Permite confirmar o login pressionando Enter diretamente no campo
        // de texto, sem precisar clicar no botão "Entrar" com o mouse.
        campoNome.addActionListener(e -> tentarEntrar.run());

        cartao.add(entrar);

        GridBagConstraints gbc = new GridBagConstraints();
        add(cartao, gbc);
    }

    private void estilizarCampo(JTextField campo) {
        campo.setFont(Tema.FONTE_CORPO);
        campo.setForeground(Tema.TEXTO_PRIMARIO);
        campo.setBackground(Tema.FUNDO_CAMPO);
        campo.setCaretColor(Tema.TEXTO_PRIMARIO);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.BORDA_SUTIL, 1, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
    }
}
