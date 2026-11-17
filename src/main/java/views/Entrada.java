package views;

import java.util.Scanner;

/** Apoio técnico das Views: um único Scanner compartilhado para o Console. */
final class Entrada {

    private static final Scanner SCANNER = new Scanner(System.in);

    private Entrada() { }

    static String lerLinha(String rotulo) {
        System.out.print(rotulo);
        return SCANNER.hasNextLine() ? SCANNER.nextLine() : "";
    }

    static int lerInteiro(String rotulo) {
        while (true) {
            if (!SCANNER.hasNextLine()) {
                return 0; // fim da entrada: encerra o programa
            }
            System.out.print(rotulo);
            String linha = SCANNER.nextLine().trim();
            try {
                return Integer.parseInt(linha);
            } catch (NumberFormatException e) {
                System.out.println("Digite um número válido.");
            }
        }
    }
}
