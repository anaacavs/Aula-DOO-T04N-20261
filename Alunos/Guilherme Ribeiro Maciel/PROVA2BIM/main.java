package MySeries;

import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
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

	static Arquivo jsonArquivo = new Arquivo();
	static File arquivo = new File("Series.json");
	//caminho do arquivo json é guardado la nas variaveis de ambiente do computador para nao ter que ficar trocando a string caso precise usar outro pc
	static String caminho = System.getenv("arquivoJson");
	static Path caminhoArquivo = Paths.get(caminho + arquivo);
	static Scanner scan = new Scanner(System.in);
	
	public static void main(String[] args) {
		ConsultarArquivo();
		if(jsonArquivo.getUsuario()==null) {
			TelaIniciar();
		}
		TelaPrincipal();
	}

	public static void TelaPrincipal() {
		JFrame principal = new JFrame("MySeries");
		principal.setSize(1500, 750);
		principal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel menu = new JPanel();
		JButton pesquisar = new JButton("Pesquisar Series");
		JButton favoritos = new JButton("Series Favoritadas");
		JButton assistidos = new JButton("Series Assistidas");
		JButton watchlist = new JButton("Series que Pretendo Assistir");
		menu.add(pesquisar);
		menu.add(favoritos);
		menu.add(watchlist);
		menu.add(assistidos);
		
		JPanel pesquisa = new JPanel();
		JTextField campo = new JTextField();
		campo.setPreferredSize(new Dimension(100, 25));
		JButton search = new JButton("Pesquisar");
		JLabel info = new JLabel("Digite o Nome de Serie aqui!(somente Ingles)");
		pesquisa.add(info);
		pesquisa.add(campo);
		pesquisa.add(search);
		
		JPanel resultado = new JPanel();
		String[] colunas = {"ID", "Nome", "Idioma", "Generos", "Nota Geral", "Status Atual", "Data de Estreia", "Data de Termino", "Emissora"};
		DefaultTableModel modelo = new DefaultTableModel(colunas, 0);
		JTable tabela = new JTable(modelo);
		JScrollPane resultados = new JScrollPane(tabela);
		resultado.add(resultados);
		
		principal.add(menu, BorderLayout.NORTH);
		principal.add(pesquisa, BorderLayout.CENTER);
		principal.setVisible(true);
	}
	
	private static void ConsultaSeries() {
		try {
			System.out.println("Entre com o nome da serie");
			String serie = scan.nextLine();
			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
			mapper.registerModule(new JavaTimeModule());
			mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		
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
				System.out.println(series1.length);
				for (int i = 0; i < series1.length; i++) {
					System.out.println(series1[i].resumo());
				}
			}
		} catch (URISyntaxException | IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void SalvarSerie() {
		try {
			String serie = scan.nextLine();
			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			mapper.registerModule(new JavaTimeModule());
			mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
			
			HttpClient client = HttpClient.newHttpClient();
			
			String params = URLEncoder.encode(serie);
			URI url = new URI("https://api.tvmaze.com/shows/" + params);
			
			HttpRequest request = HttpRequest.newBuilder(url)
					.GET()
					.build();
			
			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
			
			if(response.statusCode() == 200) {
				//System.out.println(response.body());
				String json = response.body();
				Show series1 = mapper.readValue(json, Show.class);
				System.out.println(series1.sla());
				jsonArquivo.setSeries1(series1);
				String jsonArchive = mapper.writeValueAsString(jsonArquivo);
				Files.writeString(caminhoArquivo, jsonArchive);
			}
		} catch (URISyntaxException | IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void TelaIniciar() {
		JFrame iniciando = new JFrame("Iniciando MySeries...");
		iniciando.setSize(new Dimension(500, 250));
		
		JPanel dados = new JPanel();
		JTextField nome = new JTextField();
		JLabel info = new JLabel("Insira seu nome aqui:");
		JButton iniciar = new JButton("Iniciar");
		dados.add(info);
		dados.add(nome);
		dados.add(iniciar);
		
		iniciar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e1) {
				try {
					PegarNome(nome.getText());
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				iniciando.setVisible(false);
			}
		});
		
		iniciando.add(dados);
		iniciando.setVisible(true);
	}
	
	public static void ConsultarArquivo() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		try {
			FileReader reader = new FileReader(caminho + arquivo);
			jsonArquivo = mapper.readValue(reader, Arquivo.class);
			//System.out.println(jsonArquivo.resumo());
		} catch (FileNotFoundException e) {
			TelaIniciar();
		} catch (StreamReadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DatabindException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private static void PegarNome(String nome) throws JsonProcessingException {
		jsonArquivo.setUsuario(nome);
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.registerModule(new JavaTimeModule());
		String json = mapper.writeValueAsString(jsonArquivo);
		try {
			Files.writeString(caminhoArquivo, json);
		}catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
