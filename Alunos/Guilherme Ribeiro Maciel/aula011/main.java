package weather;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.awt.BorderLayout;
import java.awt.Color;
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
import java.util.Scanner;
import weather.Previsao;

public class main {

	static Scanner scan = new Scanner(System.in);
	
	public static void main(String[] args) throws Exception{
		Consulta();
	}
	
	public static void Tela() {
		JFrame tela = new JFrame("clima");
		tela.setSize(500, 500);
		tela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel consulta = new JPanel();
		JTextArea cidade = new JTextArea();
	}
	
	public static void Consulta() throws URISyntaxException, IOException, InterruptedException {
		try {
			LocalDateTime data = LocalDateTime.now();
			String cidade = scan.nextLine();
			ObjectMapper mapper = new ObjectMapper();
			Previsao previsao = new Previsao();
		
			HttpClient client = HttpClient.newHttpClient();
			URI url = new URI("https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/"
					+ cidade.replace(" ", "") + "/" + data.truncatedTo(ChronoUnit.SECONDS) 
					+ "?key=5YDGGFYSZ29DDEPL83HHEHD52&&unitGroup=metric&include=current&lang=pt");
		
			HttpRequest request = HttpRequest.newBuilder(url)
					.GET()
					.build();
		
			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		
			if (response.statusCode() == 200) {
				System.out.println(response.body());
				String json = response.body();
				previsao = mapper.readValue(json, Previsao.class);
				System.out.println(previsao.resumo());
			} else {
				System.out.println(response.statusCode() + " " + data);
			}
		} catch(IOException e) {
			System.out.println(e);
		}
	}

}
