package services;

import data.Conexao;
import models.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Optional;

/** Regras e SQL de cadastro e autenticação de usuários. */
public class UsuarioService {

    private final Conexao conexao;
    private final SenhaService senhaService;

    public UsuarioService(Conexao conexao) {
        this.conexao = conexao;
        this.senhaService = new SenhaService();
    }

    public void cadastrar(String nome, String email, String senha) throws SQLException {
        if (nome == null || nome.isBlank() || nome.length() > 100) {
            throw new IllegalArgumentException("Nome obrigatório (até 100 caracteres).");
        }
        if (email == null || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$") || email.length() > 150) {
            throw new IllegalArgumentException("E-mail inválido.");
        }
        if (senha == null || senha.length() < 6) {
            throw new IllegalArgumentException("A senha deve ter ao menos 6 caracteres.");
        }
        String sql = "INSERT INTO usuarios (nome, email, senha_hash) VALUES (?, ?, ?)";
        try (Connection c = conexao.criarConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nome.trim());
            ps.setString(2, email.trim().toLowerCase());
            ps.setString(3, senhaService.gerarHash(senha));
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalArgumentException("E-mail já cadastrado.");
        }
    }

    /** Retorna o usuário se as credenciais forem válidas; vazio se inválidas. */
    public Optional<Usuario> autenticar(String email, String senha) throws SQLException {
        if (email == null || senha == null || email.isBlank() || senha.isEmpty()) {
            return Optional.empty();
        }
        String sql = "SELECT id, nome, email, senha_hash FROM usuarios WHERE email = ?";
        try (Connection c = conexao.criarConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                Usuario u = new Usuario(rs.getInt("id"), rs.getString("nome"),
                        rs.getString("email"), rs.getString("senha_hash"));
                return senhaService.verificar(senha, u.senhaHash()) ? Optional.of(u) : Optional.empty();
            }
        }
    }
}
