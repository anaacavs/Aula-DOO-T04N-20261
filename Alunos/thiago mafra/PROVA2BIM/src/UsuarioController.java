package controller;

import model.DadosUsuario;
import model.Serie;
import service.JsonService;

// Controller responsável por controlar os dados do usuário,
// realizando operações de carregamento, salvamento e consultas.
public class UsuarioController {

    // Serviço responsável por ler e salvar os dados em JSON
    private final JsonService jsonService;

    // Objeto que armazena todas as informações do usuário
    // como favoritos, assistidas e desejo assistir
    private DadosUsuario dados;

    // Construtor da classe
    public UsuarioController() {

        // Cria o serviço JSON
        jsonService = new JsonService();

        // Carrega os dados salvos no arquivo JSON
        dados = jsonService.carregar();
    }

    // Retorna os dados do usuário
    public DadosUsuario getDados() {
        return dados;
    }

    // Salva os dados atuais no arquivo JSON
    public void salvar() {

        jsonService.salvar(dados);
    }

    // Recarrega os dados do arquivo JSON
    public void carregar() {

        dados = jsonService.carregar();
    }

    // Verifica se uma série já está
    // na lista de favoritos
    public boolean existeNosFavoritos(
            Serie serie) {

        // Retorna verdadeiro se a série existir
        return dados
                .getFavoritos()
                .contains(serie);
    }

    // Verifica se uma série já está
    // na lista de séries assistidas
    public boolean existeNasAssistidas(
            Serie serie) {

        // Retorna verdadeiro se a série existir
        return dados
                .getAssistidas()
                .contains(serie);
    }

    // Verifica se uma série já está
    // na lista de desejo assistir
    public boolean existeNaListaDesejo(
            Serie serie) {

        // Retorna verdadeiro se a série existir
        return dados
                .getDesejoAssistir()
                .contains(serie);
    }
}