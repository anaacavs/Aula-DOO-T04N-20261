package telecine;
import javax.swing.*;
import java.util.ArrayList;
public class Principal {
	// só para não dar branco (assinatura do método define quem vai ter a classe principal de execussão do código)
public static void main(String[] args) {

	//abrindo a tela swing
    SwingUtilities.invokeLater(() -> { // executa a tela swing
        TelaPrincipal tela = new TelaPrincipal(); //chama a classe swing que tem a tela em si mas e classe java no caso
        tela.setVisible(true); //deixa ela visível
    });


}
}