import java.util.ArrayList;
import java.util.Comparator; // interface funcional para definir critérios de ordenação
import java.util.List;


public class OrdenacaoService {

   //ordem alfabética 
    public List<Serie> ordenarPorNome(List<Serie> lista) {
        List<Serie> copia = new ArrayList<>(lista); // copia para não alterar o original
        copia.sort(Comparator.comparing(
            serie -> serie.getNome().toLowerCase() // toLowerCase garante ordem correta
        ));
        return copia;
    }

    //ordem decrescente de nota

    public List<Serie> ordenarPorNota(List<Serie> lista) {
        List<Serie> copia = new ArrayList<>(lista);
        copia.sort((a, b) -> Double.compare(b.getNota(), a.getNota())); // decrescente
        return copia;
    }

   //ordem por status de série
    public List<Serie> ordenarPorStatus(List<Serie> lista) {
        List<Serie> copia = new ArrayList<>(lista);
        copia.sort(Comparator.comparingInt(serie -> prioridadeStatus(serie.getStatus())));
        return copia;
    }

    /**
     * Define a prioridade numérica de cada status para ordenação.
     * Número menor = aparece primeiro.
     */
    private int prioridadeStatus(String status) {
        switch (status) {
            case "Running":            return 0; // Em exibição: mais relevante
            case "To Be Determined":   return 1; // Indefinido
            case "Ended":              return 2; // Encerrada
            case "Canceled":           return 3; // Cancelada: menos relevante
            default:                   return 4;
        }
    }

    /**
     * Ordena por data de estreia da mais antiga para a mais nova.
     * Datas no formato "YYYY-MM-DD" podem ser comparadas diretamente como String
     * pois o formato ISO 8601 é ordenável lexicograficamente.
     */
    public List<Serie> ordenarPorDataEstreia(List<Serie> lista) {
        List<Serie> copia = new ArrayList<>(lista);
        copia.sort((a, b) -> {
            String da = a.getDataEstreia() != null ? a.getDataEstreia() : ""; // null-safe
            String db = b.getDataEstreia() != null ? b.getDataEstreia() : "";
            return da.compareTo(db); // comparação lexicográfica 
        });
        return copia;
    }
}
