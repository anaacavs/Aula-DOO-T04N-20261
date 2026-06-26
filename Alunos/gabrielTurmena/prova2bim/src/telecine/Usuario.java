package telecine;
import java.util.ArrayList;
import java.util.List;
public class Usuario {

	private String apelido;
	private List<Serie> favoritas;
	private List<Serie> assistidas;
	private List<Serie> queroAssistir;
	//tela de favoritos assistidos e quero assistir para retornar somente isso
	public Usuario() {
		this.favoritas = new ArrayList<>();
		this.assistidas = new ArrayList<>();
		this.queroAssistir = new ArrayList<>();
	}
	
	//método privado de dentro do código que serve para suporte na filtragem de ID de item para favoritos assistidos e quero assistir
	private boolean contemSerie(List<Serie> lista, Serie serie) {
	    for (Serie item : lista) {
	        if (item.getId().equals(serie.getId())) {
	            return true;
	        }
	    }

	    return false;
	}
	//nome e favoritos tipo um (meu perfil)
	public Usuario(String apelido) {
		 	this.apelido = apelido;
	        this.favoritas = new ArrayList<>();
	        this.assistidas = new ArrayList<>();
	        this.queroAssistir = new ArrayList<>();
	}
	
	 public String getApelido() {
	        return apelido;
	    }

	    public void setApelido(String apelido) {
	        this.apelido = apelido;
	    }

	    public List<Serie> getFavoritas() {
	        return favoritas;
	    }

	    public void setFavoritas(List<Serie> favoritas) {
	        this.favoritas = favoritas;
	    }

	    public List<Serie> getAssistidas() {
	        return assistidas;
	    }

	    public void setAssistidas(List<Serie> assistidas) {
	        this.assistidas = assistidas;
	    }

	    public List<Serie> getQueroAssistir() {
	        return queroAssistir;
	    }

	    public void setQueroAssistir(List<Serie> queroAssistir) {
	        this.queroAssistir = queroAssistir;
	    }
	
	    public void adicionarFavorita(Serie serie) {
	        if (!contemSerie(this.favoritas, serie)) {
	            this.favoritas.add(serie);
	        }
	    }

	    public void removerFavorita(Serie serie) {
	        this.favoritas.remove(serie);
	    }
	    public void adicionarAssistida(Serie serie) {
	        if (!contemSerie(this.assistidas, serie)) {
	            this.assistidas.add(serie);
	        }
	    }
	    //método para adicionar uma série a lista de favoritos  
	    public void adicionarQueroAssistir(Serie serie) {
	        if (!contemSerie(this.queroAssistir, serie)) {
	            this.queroAssistir.add(serie);
	        }
	    }
	    

	    
}
