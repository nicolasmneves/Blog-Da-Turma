package models;

import java.time.LocalDateTime;

/** Representa uma publicação. usuarioId identifica o autor. */
public record Publicacao(int id, String titulo, String texto,
                         LocalDateTime dataCriacao, int usuarioId) { }
