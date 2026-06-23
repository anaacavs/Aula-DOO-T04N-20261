package fag.main.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Mapeia cada item retornado pelo endpoint de busca da TVmaze
 * (/search/shows?q=...), que devolve um array de objetos no formato:
 * { "score": 8.32, "show": { ... } }
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResult {

    @JsonProperty("score")
    private Double score;

    @JsonProperty("show")
    private Show show;

    public SearchResult() {}

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Show getShow() {
        return show;
    }

    public void setShow(Show show) {
        this.show = show;
    }
}
