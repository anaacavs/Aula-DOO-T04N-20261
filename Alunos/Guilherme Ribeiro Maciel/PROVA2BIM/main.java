package MySeries;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Arrays;
import java.util.List;

public class main {

	static Scanner scan = new Scanner(System.in);
	static List<Series> series = new ArrayList<Series>();
	
	public static void main(String[] args) {
		Consulta();
	}

	private static void Consulta() {
		try {
			String serie = scan.nextLine();
			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
			mapper.registerModule(new JavaTimeModule());
			Series series = new Series();
		
			HttpClient client = HttpClient.newHttpClient();
		
			String params = URLEncoder.encode(serie);
			URI url = new URI("https://api.tvmaze.com/search/shows?q=" + params);
		
			HttpRequest request = HttpRequest.newBuilder(url)
					.GET()
					.build();
			
			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
			
			if(response.statusCode() == 200) {
				System.out.println(response.body());
				String json = response.body();
				Series[] series1 = mapper.readValue(json, Series[].class);
				System.out.println(series1[0].resumo());
			}
		} catch (URISyntaxException | IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
