package fag.main.api;

/**
 * Exceção lançada quando ocorre algum problema ao comunicar-se com a API
 * da TVmaze (falha de rede, tempo esgotado, resposta inesperada, etc.).
 *
 * Trata-se de uma exceção verificada (checked exception) propositalmente:
 * isso obriga as camadas superiores (serviço/UI) a tratarem o erro de forma
 * explícita, evitando que o programa feche inesperadamente caso o usuário
 * esteja sem conexão com a internet, por exemplo.
 */
public class ApiException extends Exception {

    public ApiException(String message) {
        super(message);
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
