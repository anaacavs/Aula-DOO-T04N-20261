package fag.main.persistence;

/**
 * Exceção lançada quando ocorre algum problema ao ler ou gravar o arquivo
 * de dados local (JSON) da aplicação.
 */
public class PersistenciaException extends Exception {

    public PersistenciaException(String message) {
        super(message);
    }

    public PersistenciaException(String message, Throwable cause) {
        super(message, cause);
    }
}
