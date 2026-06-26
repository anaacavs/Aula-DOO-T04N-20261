package controller;

import java.util.List;

import model.Serie;
import service.TvMazeService;

// Controller responsável pela comunicação entre
// a interface gráfica (View) e o serviço da API TVMaze.
public class SerieController {

    // Serviço responsável por acessar a API do TVMaze
    private final TvMazeService service;

    // Construtor da classe
    public SerieController() {

        // Cria uma instância do serviço da API
        service = new TvMazeService();
    }

    // Método responsável por pesquisar séries
    // a partir do nome informado pelo usuário
    public List<Serie> pesquisar(
            String nomeSerie) {

        // Chama o serviço da API e retorna
        // a lista de séries encontradas
        return service.buscarSeries(nomeSerie);
    }
}