package models;

/** Representa um usuário. Somente dados, sem operações de Console ou SQL. */
public record Usuario(int id, String nome, String email, String senhaHash) { }
