/*
 Classe principal do programa de compressão/descompressão de Huffman.

 Uso via linha de comando:
 COMPRIMIR:
   java -jar huffman.jar -c arquivo_original arquivo_comprimido

 DESCOMPRIMIR:
   java -jar huffman.jar -d arquivo_comprimido arquivo_restaurado

 Exemplos:
   java -jar huffman.jar -c texto.txt texto.huff
   java -jar huffman.jar -d texto.huff texto_restaurado.txt

*/
public class Huffman {

     /* Argumentos da linha de comando:
            args[0] = "-c" (comprimir) ou "-d" (descomprimir)
            args[1] = arquivo de entrada
            args[2] = arquivo de saída
     */
    public static void main(String[] args) {
        // Validação dos argumentos
        if (args.length != 3) {
            exibirAjuda();
            System.exit(1);
        }

        String operacao = args[0];
        String arquivoEntrada = args[1];
        String arquivoSaida = args[2];

        try {
            switch (operacao) {
                case "-c":
                    // Operação de compressão
                    System.out.println("=== COMPRESSAO DE HUFFMAN ===");
                    System.out.println("Entrada: " + arquivoEntrada);
                    System.out.println("Saida  : " + arquivoSaida);
                    System.out.println();

                    Compressor compressor = new Compressor();
                    compressor.comprimir(arquivoEntrada, arquivoSaida);
                    break;

                case "-d":
                    // Operação de descompressão
                    System.out.println("=== DESCOMPRESSAO DE HUFFMAN ===");
                    System.out.println("Entrada: " + arquivoEntrada);
                    System.out.println("Saida  : " + arquivoSaida);
                    System.out.println();

                    Descompressor descompressor = new Descompressor();
                    descompressor.descomprimir(arquivoEntrada, arquivoSaida);
                    break;

                default:
                    System.err.println("Operacao invalida: " + operacao);
                    exibirAjuda();
                    System.exit(1);
            }

        } catch (java.io.FileNotFoundException e) {
            System.err.println("ERRO: Arquivo nao encontrado - " + e.getMessage());
            System.exit(1);
        } catch (java.io.IOException e) {
            System.err.println("ERRO de I/O: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("ERRO inesperado: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    // Exibe as instruções de uso do programa.
    private static void exibirAjuda() {
        System.out.println("Uso:");
        System.out.println("  Comprimir  : java -jar huffman.jar -c <arquivo_original> <arquivo_comprimido>");
        System.out.println("  Descomprimir: java -jar huffman.jar -d <arquivo_comprimido> <arquivo_restaurado>");
        System.out.println();
        System.out.println("Exemplos:");
        System.out.println("  java -jar huffman.jar -c documento.txt documento.huff");
        System.out.println("  java -jar huffman.jar -d documento.huff documento_restaurado.txt");
    }
}
