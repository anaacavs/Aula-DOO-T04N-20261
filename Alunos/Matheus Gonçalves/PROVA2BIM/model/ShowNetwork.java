package fag.main.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Mapeia tanto o objeto "network" quanto o objeto "webChannel" do JSON da
 * TVmaze, já que ambos possuem a mesma estrutura básica (id, name, country).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShowNetwork {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

    public ShowNetwork() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
