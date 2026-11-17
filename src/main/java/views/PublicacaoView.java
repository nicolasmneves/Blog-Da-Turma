package views;

import models.Comentario;
import models.Publicacao;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/** Interação de Console para publicações e comentários. */
public class PublicacaoView {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /** Tupla nomeada (equivalente Java de uma tupla com campos nomeados). */
    public record DadosPublicacao(String titulo, String texto) { }

    public void exibirPublicacoes(List<Publicacao> publicacoes) {
        if (publicacoes.isEmpty()) {
            exibirMensagem("Ainda não há publicações.");
            return;
        }
        System.out.println("\n--- Publicações (mais recentes primeiro) ---");
        for (Publicacao p : publicacoes) {
            System.out.printf("[%d] %s  (%s)%n", p.id(), p.titulo(), p.dataCriacao().format(FORMATO));
        }
    }

    public void exibirMensagem(String mensagem) {
        System.out.println(mensagem);
    }

    public DadosPublicacao solicitarDadosPublicacao() {
        String titulo = Entrada.lerLinha("Título: ");
        String texto = Entrada.lerLinha("Texto: ");
        return new DadosPublicacao(titulo, texto);
    }

    public int solicitarId() {
        return Entrada.lerInteiro("ID da publicação: ");
    }

    public void exibirDetalhes(Publicacao publicacao, String nomeAutor,
                               List<Map.Entry<Comentario, String>> comentarios) {
        System.out.println("\n=== " + publicacao.titulo() + " ===");
        System.out.println("Autor: " + nomeAutor + " | " + publicacao.dataCriacao().format(FORMATO));
        System.out.println();
        System.out.println(publicacao.texto());
        System.out.println("\n--- Comentários (" + comentarios.size() + ") ---");
        if (comentarios.isEmpty()) {
            System.out.println("Nenhum comentário ainda.");
        }
        for (Map.Entry<Comentario, String> par : comentarios) {
            System.out.printf("%s (%s): %s%n", par.getValue(),
                    par.getKey().dataCriacao().format(FORMATO), par.getKey().texto());
        }
    }

    public String solicitarTextoComentario() {
        return Entrada.lerLinha("Comentário: ");
    }

    public boolean confirmarExclusao() {
        String r = Entrada.lerLinha("Confirmar exclusão? (s/n): ").trim().toLowerCase();
        return r.equals("s") || r.equals("sim");
    }
}
