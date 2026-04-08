import java.io.*;

// Classe responsável por descomprimir um arquivo .huff gerado pelo Compressor.
// Processo de descompressão:
//  1. Lê o cabeçalho do arquivo comprimido (256 frequências + total de bits).
//  2. Reconstrói a mesma Árvore de Huffman usando as frequências do cabeçalho.
//  3. Percorre os bits do arquivo guiado pela árvore:
//     - bit '0' → vai para o filho esquerdo
//     - bit '1' → vai para o filho direito
//     - ao atingir folha → escreve o caractere e volta para a raiz
//  4. Para quando totalBits bits foram consumidos.
public class Descompressor {

    // Tamanho da tabela ASCII.
    private static final int TAMANHO_ASCII = 256;

    // Descomprime o arquivo .huff e recria o arquivo original.
    // Lança IOException se ocorrer erro de leitura/escrita.
    public void descomprimir(String caminhoEntrada, String caminhoSaida) throws IOException {
        long inicioTempo = System.nanoTime();

        // Verifica se o arquivo existe e tem a extensão correta
        File arquivo = new File(caminhoEntrada);
        if (!arquivo.exists()) {
            throw new FileNotFoundException("Arquivo nao encontrado: " + caminhoEntrada);
        }

        try (DataInputStream dis = new DataInputStream(
                new BufferedInputStream(new FileInputStream(caminhoEntrada)))) {

            // PASSO 1: Ler cabeçalho
            int[] frequencias = new int[TAMANHO_ASCII];
            for (int i = 0; i < TAMANHO_ASCII; i++) {
                frequencias[i] = dis.readInt();
            }
            long totalBits = dis.readLong();

            // PASSO 2: Reconstruir a Árvore de Huffman
            ArvoreHuffman arvore = new ArvoreHuffman(frequencias);
            No raiz = arvore.getRaiz();

            if (raiz == null) {
                throw new IOException("Arquivo corrompido: nao foi possivel reconstruir a arvore.");
            }

            // PASSO 3: Decodificar os bits usando percurso guiado
            try (BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(caminhoSaida))) {

                No noAtual = raiz;
                long bitsLidos = 0;
                int byteLido;

                // Lê byte por byte do arquivo comprimido
                while (bitsLidos < totalBits && (byteLido = dis.read()) != -1) {

                    // Processa cada bit do byte (do mais significativo para o menos)
                    for (int i = 7; i >= 0 && bitsLidos < totalBits; i--) {
                        int bit = (byteLido >> i) & 1;

                        // Percurso guiado pelos dados
                        if (bit == 0) {
                            noAtual = noAtual.esquerda;
                        } else {
                            noAtual = noAtual.direita;
                        }

                        // Se chegou a uma folha, decodificou um caractere
                        if (noAtual != null && noAtual.ehFolha()) {
                            bos.write(noAtual.caractere);
                            noAtual = raiz; // Volta para a raiz para o próximo caractere
                        }

                        bitsLidos++;
                    }
                }
            }
        }

        long fimTempo = System.nanoTime();
        long tempoMs = (fimTempo - inicioTempo) / 1_000_000;

        // Exibe resumo
        long bytesRestaurados = new File(caminhoSaida).length();
        System.out.println("DESCOMPRESSAO CONCLUIDA");
        System.out.printf("Arquivo restaurado..: %s%n", caminhoSaida);
        System.out.printf("Tamanho restaurado..: %d bytes%n", bytesRestaurados);
        System.out.printf("Tempo de execucao...: %d ms%n", tempoMs);
    }
}
