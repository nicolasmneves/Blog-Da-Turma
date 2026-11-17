package models;

import java.time.LocalDateTime;

/** Representa um comentário. usuarioId = autor; publicacaoId = publicação comentada. */
public record Comentario(int id, String texto, LocalDateTime dataCriacao,
                         int usuarioId, int publicacaoId) { }
