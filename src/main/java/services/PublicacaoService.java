package services;

import data.Conexao;
import models.Publicacao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Regras e SQL de publicações. */
public class PublicacaoService {

    private final Conexao conexao;
    private final SessaoUsuario sessao;

    public PublicacaoService(Conexao conexao, SessaoUsuario sessao) {
        this.conexao = conexao;
        this.sessao = sessao;
    }

    public List<Publicacao> listarRecentes() throws SQLException {
        String sql = "SELECT id, titulo, texto, data_criacao, usuario_id "
                   + "FROM publicacoes ORDER BY data_criacao DESC, id DESC";
        List<Publicacao> lista = new ArrayList<>();
        try (Connection c = conexao.criarConexao();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public void criar(String titulo, String texto) throws SQLException {
        int usuarioId = obterUsuarioAutenticado();
        validarDados(titulo, texto);
        String sql = "INSERT INTO publicacoes (titulo, texto, usuario_id) VALUES (?, ?, ?)";
        try (Connection c = conexao.criarConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, titulo.trim());
            ps.setString(2, texto.trim());
            ps.setInt(3, usuarioId);
            ps.executeUpdate();
        }
    }

    public Optional<Publicacao> obterPorId(int id) throws SQLException {
        String sql = "SELECT id, titulo, texto, data_criacao, usuario_id FROM publicacoes WHERE id = ?";
        try (Connection c = conexao.criarConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    public String obterNomeAutor(int publicacaoId) throws SQLException {
        String sql = "SELECT u.nome FROM publicacoes p JOIN usuarios u ON u.id = p.usuario_id WHERE p.id = ?";
        try (Connection c = conexao.criarConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, publicacaoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalArgumentException("Publicação não encontrada.");
                }
                return rs.getString("nome");
            }
        }
    }

    /** Verifica autoria e altera na mesma transação. */
    public void editar(int id, String titulo, String texto) throws SQLException {
        int usuarioId = obterUsuarioAutenticado();
        validarDados(titulo, texto);
        try (Connection c = conexao.criarConexao()) {
            c.setAutoCommit(false);
            try {
                verificarAutoria(id, usuarioId, c);
                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE publicacoes SET titulo = ?, texto = ? WHERE id = ?")) {
                    ps.setString(1, titulo.trim());
                    ps.setString(2, texto.trim());
                    ps.setInt(3, id);
                    ps.executeUpdate();
                }
                c.commit();
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }

    /** Verifica autoria e exclui na mesma transação; comentários saem por cascata no banco. */
    public void excluir(int id) throws SQLException {
        int usuarioId = obterUsuarioAutenticado();
        try (Connection c = conexao.criarConexao()) {
            c.setAutoCommit(false);
            try {
                verificarAutoria(id, usuarioId, c);
                try (PreparedStatement ps = c.prepareStatement("DELETE FROM publicacoes WHERE id = ?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
                c.commit();
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }

    private void validarDados(String titulo, String texto) {
        if (titulo == null || titulo.isBlank() || titulo.length() > 150) {
            throw new IllegalArgumentException("Título obrigatório (até 150 caracteres).");
        }
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("O texto é obrigatório.");
        }
    }

    private int obterUsuarioAutenticado() {
        if (!sessao.estaAutenticado()) {
            throw new IllegalStateException("É necessário estar autenticado.");
        }
        return sessao.getUsuarioId();
    }

    /** Lê a publicação com bloqueio na transação e confere se o usuário é o autor. */
    private void verificarAutoria(int publicacaoId, int usuarioId, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT usuario_id FROM publicacoes WHERE id = ? FOR UPDATE")) {
            ps.setInt(1, publicacaoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalArgumentException("Publicação não encontrada.");
                }
                if (rs.getInt("usuario_id") != usuarioId) {
                    throw new IllegalArgumentException("Somente o autor pode alterar esta publicação.");
                }
            }
        }
    }

    private Publicacao mapear(ResultSet rs) throws SQLException {
        return new Publicacao(rs.getInt("id"), rs.getString("titulo"), rs.getString("texto"),
                rs.getTimestamp("data_criacao").toLocalDateTime(), rs.getInt("usuario_id"));
    }
}
