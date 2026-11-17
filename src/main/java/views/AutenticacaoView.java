package views;

import java.io.Console;

/** Interação de Console para cadastro e entrada. */
public class AutenticacaoView {

    public record DadosCadastro(String nome, String email, String senha) { }

    public record Credenciais(String email, String senha) { }

    public DadosCadastro solicitarDadosCadastro() {
        String nome = Entrada.lerLinha("Nome: ");
        String email = Entrada.lerLinha("E-mail: ");
        String senha = lerSenha();
        return new DadosCadastro(nome, email, senha);
    }

    public Credenciais solicitarCredenciais() {
        String email = Entrada.lerLinha("E-mail: ");
        String senha = lerSenha();
        return new Credenciais(email, senha);
    }

    public void exibirMensagem(String mensagem) {
        System.out.println(mensagem);
    }

    private String lerSenha() {
        Console console = System.console();
        if (console != null) {
            char[] s = console.readPassword("Senha: ");
            return s == null ? "" : new String(s);
        }
        return Entrada.lerLinha("Senha: ");
    }
}
