import java.util.ArrayList;
import java.util.List;


public class Usuario {

    // ── Atributos privados ────────────────────────────────────────────────────────
    private String      nome;         // Nome ou apelido do usuário (ex: "Maria")
    private List<Serie> favoritas;    // Séries marcadas como favoritas
    private List<Serie> assistidas;   // Séries que o usuário já assistiu
    private List<Serie> querAssistir; // Séries que o usuário quer assistir futuramente

    // ── Construtor ────────────────────────────────────────────────────────────────
   
    public Usuario(String nome) {
        this.nome         = nome;
        this.favoritas    = new ArrayList<>();
        this.assistidas   = new ArrayList<>();
        this.querAssistir = new ArrayList<>();
    }

    // ── Getters e Setters ─────────────────────────────────────────────────────────
    public String      getNome()         { return nome; }
    public void        setNome(String n) { this.nome = n; }
    public List<Serie> getFavoritas()    { return favoritas; }
    public List<Serie> getAssistidas()   { return assistidas; }
    public List<Serie> getQuerAssistir() { return querAssistir; }

    // ── Métodos para gerenciar Favoritas ──────────────────────────────────────────

    /**
     * Adiciona uma série às favoritas, evitando duplicatas.
     * contains() usa o equals() de Serie, que compara pelo ID.
     * @return true se adicionou, false se já estava na lista
     */
    public boolean adicionarFavorita(Serie s) {
        if (!favoritas.contains(s)) {
            favoritas.add(s);
            return true;
        }
        return false;
    }

    /** Remove a série das favoritas pelo objeto (equals por ID). */
    public boolean removerFavorita(Serie s) {
        return favoritas.remove(s);
    }

    // ── Métodos para gerenciar Assistidas ─────────────────────────────────────────

    public boolean adicionarAssistida(Serie s) {
        if (!assistidas.contains(s)) {
            assistidas.add(s);
            return true;
        }
        return false;
    }

    public boolean removerAssistida(Serie s) {
        return assistidas.remove(s);
    }

    // ── Métodos para gerenciar Quer Assistir ──────────────────────────────────────

    public boolean adicionarQuerAssistir(Serie s) {
        if (!querAssistir.contains(s)) {
            querAssistir.add(s);
            return true;
        }
        return false;
    }

    public boolean removerQuerAssistir(Serie s) {
        return querAssistir.remove(s);
    }
}
