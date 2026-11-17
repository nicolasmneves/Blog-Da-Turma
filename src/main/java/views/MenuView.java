package views;

/** Exibe o menu e devolve a escolha numérica (0 a 9). */
public class MenuView {

    public int exibirMenu() {
        System.out.println();
        System.out.println("===== BLOG DA TURMA =====");
        System.out.println("1 - Listar publicações");
        System.out.println("2 - Cadastrar-se");
        System.out.println("3 - Entrar");
        System.out.println("4 - Criar publicação");
        System.out.println("5 - Abrir publicação");
        System.out.println("6 - Editar publicação");
        System.out.println("7 - Excluir publicação");
        System.out.println("8 - Comentar publicação");
        System.out.println("9 - Sair da conta");
        System.out.println("0 - Encerrar");
        while (true) {
            int opcao = Entrada.lerInteiro("Escolha: ");
            if (opcao >= 0 && opcao <= 9) {
                return opcao;
            }
            System.out.println("Opção inválida. Escolha de 0 a 9.");
        }
    }
}
