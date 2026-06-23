package fag.main.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Mapeia o objeto "rating" retornado pela API da TVmaze.
 * Exemplo no JSON: { "average": 8.7 }
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShowRating {

    @JsonProperty("average")
    private Double average;

    public ShowRating() {}

    public Double getAverage() {
        return average;
    }

    public void setAverage(Double average) {
        this.average = average;
    }

    /**
     * Retorna a nota formatada para exibição, ou "Sem nota" caso não exista.
     */
    public String getNotaFormatada() {
        if (average == null) {
            return "Sem nota";
        }
        return String.format("%.1f", average);
    }
}
