package fag.main.persistence;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import fag.main.model.Usuario;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa a estrutura raiz do arquivo JSON persistido em disco
 * (dados.json). Guarda a lista de todos os usuários locais já criados,
 * permitindo que mais de uma pessoa use o sistema na mesma máquina, cada
 * uma com suas próprias listas de séries.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DadosPersistidos {

    @JsonProperty("usuarios")
    private List<Usuario> usuarios = new ArrayList<>();

    public DadosPersistidos() {}

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios != null ? usuarios : new ArrayList<>();
    }
}
