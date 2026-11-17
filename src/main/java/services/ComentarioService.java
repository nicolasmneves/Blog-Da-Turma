package services;

import data.Conexao;
import models.Comentario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Regras e SQL de comentários. */
public class ComentarioService {

    private final Conexao conexao;
    private final SessaoUsuario sessao;

    public ComentarioService(Conexao conexao, SessaoUsuario sessao) {
        this.conexao = conexao;
        this.sessao = sessao;
    }

    /** Pares (Comentario, nome do autor), do mais antigo ao mais recente. */
    public List<Map.Entry<Comentario, String>> listarPorPublicacao(int publicacaoId) throws SQLException {
        String sql = "SELECT c.id, c.texto, c.data_criacao, c.usuario_id, c.publicacao_id, u.nome "
                   + "FROM comentarios c JOIN usuarios u ON u.id = c.usuario_id "
                   + "WHERE c.publicacao_id = ? ORDER BY c.data_criacao, c.id";
        List<Map.Entry<Comentario, String>> lista = new ArrayList<>();
        try (Connection c = conexao.criarConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, publicacaoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Comentario com = new Comentario(rs.getInt("id"), rs.getString("texto"),
                            rs.getTimestamp("data_criacao").toLocalDateTime(),
                            rs.getInt("usuario_id"), rs.getInt("publicacao_id"));
                    lista.add(Map.entry(com, rs.getString("nome")));
                }
            }
        }
        return lista;
    }

    public void adicionar(int publicacaoId, String texto) throws SQLException {
        if (!sessao.estaAutenticado()) {
            throw new IllegalStateException("É necessário estar autenticado.");
        }
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("O comentário não pode ser vazio.");
        }
        try (Connection c = conexao.criarConexao()) {
            try (PreparedStatement ps = c.prepareStatement("SELECT 1 FROM publicacoes WHERE id = ?")) {
                ps.setInt(1, publicacaoId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new IllegalArgumentException("Publicação não encontrada.");
                    }
                }
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO comentarios (texto, usuario_id, publicacao_id) VALUES (?, ?, ?)")) {
                ps.setString(1, texto.trim());
                ps.setInt(2, sessao.getUsuarioId());
                ps.setInt(3, publicacaoId);
                ps.executeUpdate();
            }
        }
    }
}
