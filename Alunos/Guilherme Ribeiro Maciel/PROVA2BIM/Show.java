package MySeries;

import java.time.LocalDate;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Show {
	//ArrayList<Show> show = new ArrayList<>();
	String name;
	String language;
	ArrayList<String> genres = new ArrayList<>();
	ArrayList<Rating> rating = new ArrayList<>();
	String status;
	LocalDate premiered;
	LocalDate ended;
	ArrayList<Network> network = new ArrayList<>();

	public Show() {
		
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

	public ArrayList<String> getGenres() {
		return genres;
	}

	public void setGenres(ArrayList<String> genres) {
		this.genres = genres;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDate getPremiered() {
		return premiered;
	}

	public void setPremiered(LocalDate premiered) {
		this.premiered = premiered;
	}

	public LocalDate getEnded() {
		return ended;
	}

	public void setEnded(LocalDate ended) {
		this.ended = ended;
	}

	public ArrayList<Rating> getRating() {
		return rating;
	}

	public void setRating(ArrayList<Rating> rating) {
		this.rating = rating;
	}

	public ArrayList<Network> getNetwork() {
		return network;
	}

	public void setNetwork(ArrayList<Network> network) {
		this.network = network;
	}
}
