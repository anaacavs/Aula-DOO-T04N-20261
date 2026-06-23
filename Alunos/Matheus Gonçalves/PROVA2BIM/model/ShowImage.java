package fag.main.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Mapeia o objeto "image" retornado pela API da TVmaze, contendo as URLs
 * da imagem em tamanho médio e em tamanho original.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShowImage {

    @JsonProperty("medium")
    private String medium;

    @JsonProperty("original")
    private String original;

    public ShowImage() {}

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }
}
