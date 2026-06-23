package fag.main.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Representa o usuário local da aplicação (apenas identificação por nome ou
 * apelido, sem senha) e as três listas de
 * séries que ele mantém: favoritos, já assistidas e desejo de assistir.
 *
 * Cada lista é armazenada como um mapa de (id da série -&gt; Show), o que evita
 * duplicidade de séries dentro da mesma lista e permite recuperação rápida
 * pelo identificador da TVmaze.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Usuario {

    @JsonProperty("nome")
    private String nome;

    @JsonProperty("favoritos")
    private Map<Integer, Show> favoritos = new LinkedHashMap<>();

    @JsonProperty("assistidas")
    private Map<Integer, Show> assistidas = new LinkedHashMap<>();

    @JsonProperty("desejaAssistir")
    private Map<Integer, Show> desejaAssistir = new LinkedHashMap<>();

    public Usuario() {
        // Construtor padrão exigido pelo Jackson
    }

    public Usuario(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Map<Integer, Show> getFavoritos() {
        return favoritos;
    }

    public void setFavoritos(Map<Integer, Show> favoritos) {
        this.favoritos = favoritos != null ? favoritos : new LinkedHashMap<>();
    }

    public Map<Integer, Show> getAssistidas() {
        return assistidas;
    }

    public void setAssistidas(Map<Integer, Show> assistidas) {
        this.assistidas = assistidas != null ? assistidas : new LinkedHashMap<>();
    }

    public Map<Integer, Show> getDesejaAssistir() {
        return desejaAssistir;
    }

    public void setDesejaAssistir(Map<Integer, Show> desejaAssistir) {
        this.desejaAssistir = desejaAssistir != null ? desejaAssistir : new LinkedHashMap<>();
    }

    /**
     * Retorna o mapa correspondente ao tipo de lista informado, centralizando
     * a lógica de "qual lista usar" em um único lugar.
     */
    public Map<Integer, Show> getLista(ListaTipo tipo) {
        switch (tipo) {
            case FAVORITOS:
                return favoritos;
            case ASSISTIDAS:
                return assistidas;
            case DESEJA_ASSISTIR:
                return desejaAssistir;
            default:
                throw new IllegalArgumentException("Tipo de lista desconhecido: " + tipo);
        }
    }
}
