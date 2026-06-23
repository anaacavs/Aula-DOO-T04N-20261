package fag.main.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Representa uma série de TV, mapeada a partir do JSON retornado pela API
 * pública da TVmaze (https://www.tvmaze.com/api).
 *
 * Esta classe é usada tanto para desserializar as respostas da API quanto
 * para ser persistida no arquivo de dados local do usuário (favoritos,
 * assistidas e lista de desejos), por isso todos os seus atributos são
 * simples e serializáveis diretamente pelo Jackson.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Show {

    private static final DateTimeFormatter FORMATO_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @JsonProperty("id")
    private int id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("language")
    private String language;

    @JsonProperty("genres")
    private List<String> genres = new ArrayList<>();

    @JsonProperty("status")
    private String status;

    @JsonProperty("premiered")
    private String premiered; // formato "yyyy-MM-dd", pode vir nulo

    @JsonProperty("ended")
    private String ended; // formato "yyyy-MM-dd", pode vir nulo

    @JsonProperty("rating")
    private ShowRating rating;

    @JsonProperty("network")
    private ShowNetwork network;

    @JsonProperty("webChannel")
    private ShowNetwork webChannel;

    @JsonProperty("image")
    private ShowImage image;

    @JsonProperty("summary")
    private String summary;

    public Show() {
        // Construtor padrão exigido pelo Jackson
    }

    // ----------------------------------------------------------------
    // Getters e Setters
    // ----------------------------------------------------------------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres != null ? genres : new ArrayList<>();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPremiered() {
        return premiered;
    }

    public void setPremiered(String premiered) {
        this.premiered = premiered;
    }

    public String getEnded() {
        return ended;
    }

    public void setEnded(String ended) {
        this.ended = ended;
    }

    public ShowRating getRating() {
        return rating;
    }

    public void setRating(ShowRating rating) {
        this.rating = rating;
    }

    public ShowNetwork getNetwork() {
        return network;
    }

    public void setNetwork(ShowNetwork network) {
        this.network = network;
    }

    public ShowNetwork getWebChannel() {
        return webChannel;
    }

    public void setWebChannel(ShowNetwork webChannel) {
        this.webChannel = webChannel;
    }

    public ShowImage getImage() {
        return image;
    }

    public void setImage(ShowImage image) {
        this.image = image;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    // ----------------------------------------------------------------
    // Métodos utilitários de apresentação (não mapeados pelo Jackson)
    // ----------------------------------------------------------------

    /**
     * Nome da emissora (TV tradicional) ou canal web que transmite/transmitiu
     * a série. Retorna um texto amigável caso nenhuma informação exista.
     */
    @JsonIgnore
    public String getNomeEmissora() {
        if (network != null && network.getName() != null) {
            return network.getName();
        }
        if (webChannel != null && webChannel.getName() != null) {
            return webChannel.getName() + " (streaming)";
        }
        return "Não informado";
    }

    /**
     * Gêneros concatenados em uma única String, separados por vírgula,
     * para facilitar a exibição em tabelas e listas.
     */
    @JsonIgnore
    public String getGenerosFormatados() {
        if (genres == null || genres.isEmpty()) {
            return "Não informado";
        }
        return String.join(", ", genres);
    }

    /**
     * Nota média formatada (ex.: "8.7"), ou texto padrão caso indisponível.
     */
    @JsonIgnore
    public String getNotaFormatada() {
        return rating != null ? rating.getNotaFormatada() : "Sem nota";
    }

    /**
     * Valor numérico da nota usado para fins de ordenação. Séries sem nota
     * recebem -1.0 para que fiquem ao final quando ordenadas de forma
     * decrescente.
     */
    @JsonIgnore
    public double getNotaNumerica() {
        if (rating != null && rating.getAverage() != null) {
            return rating.getAverage();
        }
        return -1.0;
    }

    /**
     * Data de estreia convertida para LocalDate, ou null se não houver
     * informação ou se o formato for inválido.
     */
    @JsonIgnore
    public LocalDate getDataEstreia() {
        return parseData(premiered);
    }

    /**
     * Data de término convertida para LocalDate, ou null se a série ainda
     * está em exibição ou não há informação.
     */
    @JsonIgnore
    public LocalDate getDataTermino() {
        return parseData(ended);
    }

    private LocalDate parseData(String data) {
        if (data == null || data.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(data);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Data de estreia já formatada no padrão brasileiro (dd/MM/yyyy).
     */
    @JsonIgnore
    public String getDataEstreiaFormatada() {
        LocalDate d = getDataEstreia();
        return d != null ? d.format(FORMATO_BR) : "Não informado";
    }

    /**
     * Data de término já formatada no padrão brasileiro (dd/MM/yyyy).
     * Caso a série ainda esteja em exibição, indica isso explicitamente.
     */
    @JsonIgnore
    public String getDataTerminoFormatada() {
        LocalDate d = getDataTermino();
        if (d != null) {
            return d.format(FORMATO_BR);
        }
        if (status != null && status.equalsIgnoreCase("Running")) {
            return "Em exibição";
        }
        return "Não informado";
    }

    /**
     * Status traduzido para português, para exibição amigável na interface.
     * Os valores possíveis retornados pela API são, em geral: "Running",
     * "Ended", "To Be Determined" e "In Development".
     */
    @JsonIgnore
    public String getStatusTraduzido() {
        if (status == null) {
            return "Desconhecido";
        }
        switch (status) {
            case "Running":
                return "Em exibição";
            case "Ended":
                return "Concluída";
            case "To Be Determined":
                return "Indefinido";
            case "In Development":
                return "Em desenvolvimento";
            default:
                return status;
        }
    }

    /**
     * Posição usada para ordenar por estado, na ordem solicitada pelo
     * enunciado da prova: em exibição, concluída, cancelada/outros.
     */
    @JsonIgnore
    public int getOrdemStatus() {
        if (status == null) {
            return 3;
        }
        switch (status) {
            case "Running":
                return 0;
            case "Ended":
                return 1;
            case "To Be Determined":
                return 2;
            default:
                return 3;
        }
    }

    /**
     * Resumo da série sem as tags HTML que a API costuma incluir (ex.: <p>).
     */
    @JsonIgnore
    public String getResumoSemHtml() {
        if (summary == null) {
            return "Sem resumo disponível.";
        }
        return summary.replaceAll("<[^>]*>", "").trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Show)) return false;
        Show show = (Show) o;
        return id == show.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name;
    }
}
