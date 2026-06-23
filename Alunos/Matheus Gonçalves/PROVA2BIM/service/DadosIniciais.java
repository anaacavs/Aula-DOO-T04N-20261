package fag.main.service;

import fag.main.model.Show;
import fag.main.model.ShowNetwork;
import fag.main.model.ShowRating;
import fag.main.model.Usuario;

import java.util.Arrays;
import java.util.List;

/**
 * Responsável por popular o sistema com um usuário de exemplo e algumas
 * séries pré-cadastradas em suas listas.
 *
 * Esses dados são inseridos apenas na primeira execução do programa, ou
 * seja, somente quando ainda não existe nenhum usuário salvo localmente.
 * Os identificadores (id) usados correspondem aos IDs reais dessas séries
 * na base da TVmaze, então é possível buscar novamente qualquer uma delas
 * pelo nome (com conexão à internet) para atualizar as informações.
 */
public final class DadosIniciais {

    private DadosIniciais() {}

    /**
     * Cria um usuário de exemplo chamado "Convidado" com algumas séries já
     * organizadas em cada uma das três listas, para que o avaliador possa
     * usar o sistema imediatamente, sem depender de conexão com a internet
     * apenas para visualizar a tela de listas funcionando.
     */
    public static Usuario criarUsuarioExemplo() {
        Usuario convidado = new Usuario("Convidado");

        Show breakingBad = criarShow(169, "Breaking Bad", "English",
                Arrays.asList("Drama", "Crime", "Thriller"),
                "Ended", "2008-01-20", "2013-09-29", 9.3,
                "AMC", "Um professor de química com câncer terminal entra no mundo da fabricação de metanfetamina.");

        Show gameOfThrones = criarShow(82, "Game of Thrones", "English",
                Arrays.asList("Drama", "Adventure", "Fantasy"),
                "Ended", "2011-04-17", "2019-05-19", 8.8,
                "HBO", "Nobres famílias disputam o controle do Trono de Ferro em Westeros.");

        Show underTheDome = criarShow(1, "Under the Dome", "English",
                Arrays.asList("Drama", "Science-Fiction", "Thriller"),
                "Ended", "2013-06-24", "2015-09-10", 6.5,
                "CBS", "Uma cidade fica isolada do mundo após ser coberta por uma misteriosa cúpula invisível.");

        Show house = criarShow(73, "House", "English",
                Arrays.asList("Drama", "Mystery"),
                "Ended", "2004-11-16", "2012-05-21", 8.7,
                "FOX", "O brilhante e cínico Dr. House resolve casos médicos quase impossíveis de diagnosticar.");

        Show vikings = criarShow(143, "Vikings", "English",
                Arrays.asList("Action", "Adventure", "Drama"),
                "Ended", "2013-03-03", "2020-12-30", 8.5,
                "History", "A saga do lendário guerreiro nórdico Ragnar Lothbrok e seus descendentes.");

        Show moneyHeist = criarShow(139, "Money Heist", "Spanish",
                Arrays.asList("Crime", "Drama", "Thriller"),
                "Ended", "2017-05-02", "2021-12-03", 8.3,
                "Antena 3", "Um grupo de assaltantes liderado pelo Professor planeja o maior roubo da história.");

        Show theFlash = criarShow(310, "The Flash", "English",
                Arrays.asList("Action", "Adventure", "Science-Fiction"),
                "Running", "2014-10-07", null, 7.6,
                "The CW", "Após um acidente, o policial Barry Allen ganha super velocidade e se torna o herói Flash.");

        // ---- Favoritos: as séries preferidas do usuário de exemplo ----
        convidado.getFavoritos().put(breakingBad.getId(), breakingBad);
        convidado.getFavoritos().put(gameOfThrones.getId(), gameOfThrones);

        // ---- Já assistidas ----
        convidado.getAssistidas().put(underTheDome.getId(), underTheDome);
        convidado.getAssistidas().put(house.getId(), house);
        convidado.getAssistidas().put(breakingBad.getId(), breakingBad);

        // ---- Quero assistir ----
        convidado.getDesejaAssistir().put(vikings.getId(), vikings);
        convidado.getDesejaAssistir().put(moneyHeist.getId(), moneyHeist);
        convidado.getDesejaAssistir().put(theFlash.getId(), theFlash);

        return convidado;
    }

    private static Show criarShow(int id, String nome, String idioma, List<String> generos,
                                   String status, String estreia, String termino, double nota,
                                   String emissora, String resumo) {
        Show show = new Show();
        show.setId(id);
        show.setName(nome);
        show.setLanguage(idioma);
        show.setGenres(generos);
        show.setStatus(status);
        show.setPremiered(estreia);
        show.setEnded(termino);
        show.setSummary("<p>" + resumo + "</p>");

        ShowRating rating = new ShowRating();
        rating.setAverage(nota);
        show.setRating(rating);

        ShowNetwork network = new ShowNetwork();
        network.setName(emissora);
        show.setNetwork(network);

        return show;
    }
}
