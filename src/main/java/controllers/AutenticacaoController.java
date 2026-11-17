package controllers;

import models.Usuario;
import services.SessaoUsuario;
import services.UsuarioService;
import views.AutenticacaoView;

import java.sql.SQLException;
import java.util.Optional;

/** Coordena cadastro, entrada e saída. */
public class AutenticacaoController {

    private final AutenticacaoView view;
    private final UsuarioService usuarioService;
    private final SessaoUsuario sessao;

    public AutenticacaoController(AutenticacaoView view, UsuarioService usuarioService, SessaoUsuario sessao) {
        this.view = view;
        this.usuarioService = usuarioService;
        this.sessao = sessao;
    }

    public void cadastrar() {
        var d = view.solicitarDadosCadastro();
        try {
            usuarioService.cadastrar(d.nome(), d.email(), d.senha());
            view.exibirMensagem("Cadastro realizado! Agora você pode entrar.");
        } catch (IllegalArgumentException e) {
            view.exibirMensagem(e.getMessage());
        } catch (SQLException e) {
            view.exibirMensagem("Falha no banco de dados ao cadastrar.");
        }
    }

    public void entrar() {
        if (sessao.estaAutenticado()) {
            view.exibirMensagem("Você já está autenticado como " + sessao.getNomeUsuario() + ".");
            return;
        }
        var c = view.solicitarCredenciais();
        try {
            Optional<Usuario> usuario = usuarioService.autenticar(c.email(), c.senha());
            if (usuario.isPresent()) {
                sessao.iniciar(usuario.get());
                view.exibirMensagem("Bem-vindo(a), " + sessao.getNomeUsuario() + "!");
            } else {
                view.exibirMensagem("E-mail ou senha inválidos.");
            }
        } catch (SQLException e) {
            view.exibirMensagem("Falha no banco de dados ao autenticar.");
        }
    }

    public void sair() {
        if (!sessao.estaAutenticado()) {
            view.exibirMensagem("Você não está autenticado.");
            return;
        }
        sessao.encerrar();
        view.exibirMensagem("Sessão encerrada.");
    }
}
