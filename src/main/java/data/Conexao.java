package data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Lê a configuração (.env ou ambiente) e cria uma conexão nova a cada chamada. */
public class Conexao {

    private final Map<String, String> configuracao = new HashMap<>();
    private final String url;
    private final String usuario;
    private final String senha;

    public Conexao() {
        carregarArquivoEnv();
        String host = lerVariavel("DB_HOST");
        String porta = lerVariavel("DB_PORT");
        String banco = lerVariavel("DB_NAME");
        this.usuario = lerVariavel("DB_USER");
        this.senha = lerVariavel("DB_PASSWORD");
        this.url = "jdbc:mariadb://" + host + ":" + porta + "/" + banco;
    }

    /** Retorna uma nova conexão; quem a recebe deve fechá-la (try-with-resources). */
    public Connection criarConexao() throws SQLException {
        return DriverManager.getConnection(url, usuario, senha);
    }

    private void carregarArquivoEnv() {
        Path arquivo = Path.of(".env");
        if (!Files.exists(arquivo)) {
            return;
        }
        try {
            List<String> linhas = Files.readAllLines(arquivo);
            for (String linha : linhas) {
                String l = linha.trim();
                if (l.isEmpty() || l.startsWith("#") || !l.contains("=")) {
                    continue;
                }
                int i = l.indexOf('=');
                configuracao.put(l.substring(0, i).trim(), l.substring(i + 1).trim());
            }
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível ler o arquivo .env", e);
        }
    }

    private String lerVariavel(String nome) {
        String valor = System.getenv(nome);
        if (valor == null || valor.isBlank()) {
            valor = configuracao.get(nome);
        }
        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException("Variável de configuração ausente: " + nome);
        }
        return valor;
    }
}
