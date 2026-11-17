-- Blog da Turma - esquema MariaDB
CREATE DATABASE IF NOT EXISTS blog_turma CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE blog_turma;

CREATE TABLE IF NOT EXISTS usuarios (
  id         INT AUTO_INCREMENT PRIMARY KEY,
  nome       VARCHAR(100) NOT NULL,
  email      VARCHAR(150) NOT NULL UNIQUE,
  senha_hash VARCHAR(100) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS publicacoes (
  id           INT AUTO_INCREMENT PRIMARY KEY,
  titulo       VARCHAR(150) NOT NULL,
  texto        TEXT NOT NULL,
  data_criacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  usuario_id   INT NOT NULL,
  CONSTRAINT fk_publicacoes_usuario FOREIGN KEY (usuario_id)
    REFERENCES usuarios(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS comentarios (
  id            INT AUTO_INCREMENT PRIMARY KEY,
  texto         TEXT NOT NULL,
  data_criacao  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  usuario_id    INT NOT NULL,
  publicacao_id INT NOT NULL,
  CONSTRAINT fk_comentarios_usuario FOREIGN KEY (usuario_id)
    REFERENCES usuarios(id) ON DELETE RESTRICT,
  CONSTRAINT fk_comentarios_publicacao FOREIGN KEY (publicacao_id)
    REFERENCES publicacoes(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_publicacoes_data ON publicacoes(data_criacao);
CREATE INDEX idx_comentarios_publicacao ON comentarios(publicacao_id);
