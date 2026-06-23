package fag.main.ui;

import fag.main.model.Show;

import java.awt.Color;

/**
 * Classe utilitária responsável por decidir qual cor de destaque usar para
 * representar o status de uma série (em exibição, concluída, etc.) nos
 * componentes visuais da aplicação.
 */
public final class CoresStatus {

    private CoresStatus() {
        // Classe utilitária, não deve ser instanciada
    }

    public static Color corPara(Show show) {
        if (show == null || show.getStatus() == null) {
            return Tema.TEXTO_SECUNDARIO;
        }
        switch (show.getStatus()) {
            case "Running":
                return Tema.ACENTO_VERDE;
            case "Ended":
                return Tema.ACENTO_AZUL;
            case "To Be Determined":
                return Tema.ACENTO_OURO;
            default:
                return Tema.ACENTO_VERMELHO;
        }
    }
}
