package model;

/**
 * CLASSE MODEL: Representa uma Conta de usuário completa.
 * Combina credenciais de acesso (login + senha) com os dados do perfil (Usuario).
 *
 * Por que criar uma classe Conta separada do Usuario?
 * → Separação de Responsabilidades: Usuario guarda dados de séries.
 *   Conta guarda dados de autenticação. São conceitos diferentes.
 * → Permite múltiplas contas no mesmo arquivo JSON (cada pessoa tem a sua).
 */
public class Conta {

    // ATRIBUTOS PRIVADOS (Encapsulamento)
    private String login;       // Nome de login único que identifica a conta
    private String senha;       // Senha da conta (armazenada em texto simples no JSON)
    private Usuario usuario;    // Objeto Usuario com nome/apelido e as 3 listas de séries

    //  CONSTRUTOR
    /**
     * Cria uma conta nova com login, senha e um Usuario associado.
     * O Usuario é criado com o mesmo texto do login como nome inicial.
     */
    public Conta(String login, String senha) {
        this.login = login;
        this.senha = senha;
        // Cria o objeto Usuario com o login como nome/apelido inicial
        // O usuário poderá trocar o apelido depois na sidebar
        this.usuario = new Usuario(login);
    }

    // CONSTRUTOR COMPLETO 
    // Usado ao carregar do arquivo JSON — recebe o Usuario já reconstruído
    public Conta(String login, String senha, Usuario usuario) {
        this.login = login;
        this.senha = senha;
        this.usuario = usuario;
    }

    // GETTERS E SETTERS 
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    // MÉTODO: Verificar senha 
    /**
     * Verifica se a senha fornecida corresponde à senha desta conta.
     * equals() compara o conteúdo das strings, não o endereço de memória.
     * @param senhaDigitada Senha que o usuário digitou na tela de login
     * @return true se a senha bater, false caso contrário
     */
    public boolean verificarSenha(String senhaDigitada) {
        if (senhaDigitada == null) return false; // Senha nula nunca é válida
        return this.senha.equals(senhaDigitada); // Compara caractere a caractere
    }

    // MÉTODO toString
    // Retorna o login como representação textual da Conta
    @Override
    public String toString() {
        return login;
    }
}