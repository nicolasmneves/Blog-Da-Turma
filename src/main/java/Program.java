import controllers.AutenticacaoController;
import controllers.PublicacaoController;
import data.Conexao;
import services.ComentarioService;
import services.PublicacaoService;
import services.SessaoUsuario;
import services.UsuarioService;
import views.AutenticacaoView;
import views.MenuView;
import views.PublicacaoView;

/** Ponto de entrada: cria os objetos, conecta dependências e roda o menu. */
public class Program {

    public static void main(String[] args) {
        Conexao conexao = new Conexao();            // carrega .env
        SessaoUsuario sessao = new SessaoUsuario(); // uma única sessão compartilhada

        UsuarioService usuarioService = new UsuarioService(conexao);
        PublicacaoService publicacaoService = new PublicacaoService(conexao, sessao);
        ComentarioService comentarioService = new ComentarioService(conexao, sessao);

        MenuView menuView = new MenuView();
        AutenticacaoController autenticacao =
                new AutenticacaoController(new AutenticacaoView(), usuarioService, sessao);
        PublicacaoController publicacao =
                new PublicacaoController(new PublicacaoView(), publicacaoService, comentarioService, sessao);

        int opcao;
        do {
            opcao = menuView.exibirMenu();
            switch (opcao) {
                case 1 -> publicacao.listar();
                case 2 -> autenticacao.cadastrar();
                case 3 -> autenticacao.entrar();
                case 4 -> publicacao.criar();
                case 5 -> publicacao.visualizar();
                case 6 -> publicacao.editar();
                case 7 -> publicacao.excluir();
                case 8 -> publicacao.comentar();
                case 9 -> autenticacao.sair();
                default -> { }
            }
        } while (opcao != 0);
        System.out.println("Até logo!");
    }
}
