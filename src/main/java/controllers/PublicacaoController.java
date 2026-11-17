package controllers;

import models.Comentario;
import models.Publicacao;
import services.ComentarioService;
import services.PublicacaoService;
import services.SessaoUsuario;
import views.PublicacaoView;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Coordena listagem, criação, detalhes, edição, exclusão e comentários. */
public class PublicacaoController {

    private static final String ERRO_BANCO = "Falha no banco de dados. Tente novamente.";

    private final PublicacaoView view;
    private final PublicacaoService service;
    private final ComentarioService comentarioService;
    private final SessaoUsuario sessao;

    public PublicacaoController(PublicacaoView view, PublicacaoService service,
                                ComentarioService comentarioService, SessaoUsuario sessao) {
        this.view = view;
        this.service = service;
        this.comentarioService = comentarioService;
        this.sessao = sessao;
    }

    public void listar() {
        try {
            view.exibirPublicacoes(service.listarRecentes());
        } catch (SQLException e) {
            view.exibirMensagem(ERRO_BANCO);
        }
    }

    public void criar() {
        if (!verificarSessao()) {
            return;
        }
        var d = view.solicitarDadosPublicacao();
        try {
            service.criar(d.titulo(), d.texto());
            view.exibirMensagem("Publicação criada com sucesso!");
        } catch (IllegalArgumentException | IllegalStateException e) {
            view.exibirMensagem(e.getMessage());
        } catch (SQLException e) {
            view.exibirMensagem(ERRO_BANCO);
        }
    }

    public void visualizar() {
        int id = view.solicitarId();
        try {
            Optional<Publicacao> pub = service.obterPorId(id);
            if (pub.isEmpty()) {
                view.exibirMensagem("Publicação não encontrada.");
                return;
            }
            String autor = service.obterNomeAutor(id);
            List<Map.Entry<Comentario, String>> comentarios = comentarioService.listarPorPublicacao(id);
            view.exibirDetalhes(pub.get(), autor, comentarios);
        } catch (IllegalArgumentException e) {
            view.exibirMensagem(e.getMessage());
        } catch (SQLException e) {
            view.exibirMensagem(ERRO_BANCO);
        }
    }

    public void editar() {
        if (!verificarSessao()) {
            return;
        }
        int id = view.solicitarId();
        var d = view.solicitarDadosPublicacao();
        try {
            service.editar(id, d.titulo(), d.texto());
            view.exibirMensagem("Publicação atualizada.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            view.exibirMensagem(e.getMessage());
        } catch (SQLException e) {
            view.exibirMensagem(ERRO_BANCO);
        }
    }

    public void excluir() {
        if (!verificarSessao()) {
            return;
        }
        int id = view.solicitarId();
        if (!view.confirmarExclusao()) {
            view.exibirMensagem("Exclusão cancelada.");
            return;
        }
        try {
            service.excluir(id);
            view.exibirMensagem("Publicação excluída (comentários removidos por cascata).");
        } catch (IllegalArgumentException | IllegalStateException e) {
            view.exibirMensagem(e.getMessage());
        } catch (SQLException e) {
            view.exibirMensagem(ERRO_BANCO);
        }
    }

    public void comentar() {
        if (!verificarSessao()) {
            return;
        }
        int id = view.solicitarId();
        String texto = view.solicitarTextoComentario();
        try {
            comentarioService.adicionar(id, texto);
            view.exibirMensagem("Comentário adicionado.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            view.exibirMensagem(e.getMessage());
        } catch (SQLException e) {
            view.exibirMensagem(ERRO_BANCO);
        }
    }

    private boolean verificarSessao() {
        if (!sessao.estaAutenticado()) {
            view.exibirMensagem("Faça login (opção 3) para executar esta ação.");
            return false;
        }
        return true;
    }
}
