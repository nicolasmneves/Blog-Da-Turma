package services;

import models.Usuario;

/** Guarda, em memória, a identificação de quem está autenticado. */
public class SessaoUsuario {

    private Integer usuarioId;
    private String nomeUsuario;

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public boolean estaAutenticado() {
        return usuarioId != null;
    }

    public void iniciar(Usuario usuario) {
        this.usuarioId = usuario.id();
        this.nomeUsuario = usuario.nome();
    }

    public void encerrar() {
        this.usuarioId = null;
        this.nomeUsuario = null;
    }
}
