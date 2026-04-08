import java.io.*;

/*
Classe responsável por comprimir um arquivo usando o algoritmo de Huffman.

Formato do arquivo .huff gerado:
  CABEÇALHO
  - 256 ints (4 bytes cada) = frequências ASCI
  - 1 long (8 bytes) = total de bits dos dados

  DADOS
  - Bits comprimidos empacotados em bytes

O cabeçalho armazena as 256 frequências para que o descompressor possa
reconstruir exatamente a mesma árvore de Huffman. O total de bits é necessário
para saber quando parar de ler (o último byte pode ter bits de padding).
*/
public class Compressor {

    // Tamanho da tabela ASCII
    private static final int TAMANHO_ASCII = 256;

    // Comprime o arquivo de entrada e grava o resultado no arquivo de saída.
    public void comprimir(String caminhoEntrada, String caminhaSaida) throws IOException {
        long inicioTempo = System.nanoTime();

        // 1: Análise de frequência
        int[] frequencias = analisarFrequencias(caminhoEntrada);
        imprimirFrequencias(frequencias);

        // 2: Construir Min-Heap inicial para exibição
        MinHeap heapExibicao = construirHeapParaExibicao(frequencias);
        System.out.println("--------------------------------------------------");
        System.out.println("ETAPA 2: Min-Heap Inicial (Vetor)");
        System.out.println("--------------------------------------------------");
        System.out.println(heapExibicao);
        System.out.println();

        // 3: Construir a Árvore de Huffman ───────────────────────────
        ArvoreHuffman arvore = new ArvoreHuffman(frequencias);
        arvore.imprimirArvore();
        System.out.println();

        // 4: Exibir tabela de códigos ────────────────────────────────
        String[] codigos = arvore.getTabelaCodigos();
        imprimirTabelaCodigos(codigos);

        // 5: Codificar e escrever arquivo ────────────────────────────
        long totalBitsEscritos = escreverArquivoComprimido(
                caminhoEntrada, caminhaSaida, frequencias, codigos);

        long fimTempo = System.nanoTime();
        long tempoMs = (fimTempo - inicioTempo) / 1_000_000;

        // ── ETAPA 5: Resumo da compressão ────────────────────────────────────
        File arquivoOriginal = new File(caminhoEntrada);
        long bytesOriginais = arquivoOriginal.length();
        long bitsOriginais = bytesOriginais * 8;
        long bytesComprimidos = new File(caminhaSaida).length();

        double taxa = (1.0 - (double) totalBitsEscritos / bitsOriginais) * 100.0;

        System.out.println("--------------------------------------------------");
        System.out.println("ETAPA 5: Resumo da Compressao");
        System.out.println("--------------------------------------------------");
        System.out.printf("Tamanho original....: %d bits (%d bytes)%n", bitsOriginais, bytesOriginais);
        System.out.printf("Tamanho comprimido..: %d bits (%d bytes)%n", totalBitsEscritos,
                (totalBitsEscritos + 7) / 8);
        System.out.printf("Arquivo gerado......: %d bytes (com cabecalho)%n", bytesComprimidos);
        System.out.printf("Taxa de compressao..: %.2f%%%n", taxa);
        System.out.printf("Tempo de execucao...: %d ms%n", tempoMs);
        System.out.println("--------------------------------------------------");
    }

    // Lê o arquivo e conta a frequência de cada byte (caractere ASCII).
    // Lança IOException Se ocorrer erro de leitura.
    public int[] analisarFrequencias(String caminho) throws IOException {
        int[] frequencias = new int[TAMANHO_ASCII];

        try (FileInputStream fis = new FileInputStream(caminho)) {
            int byte_lido;
            while ((byte_lido = fis.read()) != -1) {
                frequencias[byte_lido]++;
            }
        }

        return frequencias;
    }

    // Constrói um Min-Heap apenas para exibição do estado inicial.
    // (A árvore real é construída dentro de ArvoreHuffman.)
    private MinHeap construirHeapParaExibicao(int[] frequencias) {
        MinHeap heap = new MinHeap();
        for (int i = 0; i < TAMANHO_ASCII; i++) {
            if (frequencias[i] > 0) {
                heap.inserir(new No((char) i, frequencias[i]));
            }
        }
        return heap;
    }

    // Grava o arquivo comprimido com cabeçalho e dados.
    // Retorna o total de bits de dados escritos (sem o cabeçalho).
    // Lança IOException Se ocorrer erro de I/O.
    private long escreverArquivoComprimido(String caminhoEntrada, String caminhoSaida, int[] frequencias, String[] codigos) throws IOException {
        // Calcular total de bits para gravar no cabeçalho
        long totalBits = 0;
        try (FileInputStream fis = new FileInputStream(caminhoEntrada)) {
            int b;
            while ((b = fis.read()) != -1) {
                if (codigos[b] != null) {
                    totalBits += codigos[b].length();
                }
            }
        }

        // Gravar cabeçalho + dados comprimidos
        try (DataOutputStream dos = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(caminhoSaida)))) {

            // Cabeçalho: 256 frequências (int = 4 bytes cada)
            for (int freq : frequencias) {
                dos.writeInt(freq);
            }

            // Cabeçalho: total de bits de dados
            dos.writeLong(totalBits);

            // Dados: bits comprimidos empacotados em bytes
            int buffer = 0;       // byte sendo montado
            int contadorBits = 0; // quantos bits já foram colocados no buffer

            try (FileInputStream fis = new FileInputStream(caminhoEntrada)) {
                int b;
                while ((b = fis.read()) != -1) {
                    String codigo = codigos[b];
                    if (codigo == null) continue;

                    for (char bit : codigo.toCharArray()) {
                        // Shift esquerdo e adiciona o bit
                        buffer = (buffer << 1) | (bit == '1' ? 1 : 0);
                        contadorBits++;

                        // Quando o buffer tiver 8 bits, escreve o byte
                        if (contadorBits == 8) {
                            dos.write(buffer);
                            buffer = 0;
                            contadorBits = 0;
                        }
                    }
                }
            }

            // Escreve o último byte parcial com padding de zeros à direita
            if (contadorBits > 0) {
                buffer = buffer << (8 - contadorBits);
                dos.write(buffer);
            }
        }

        return totalBits;
    }

    // ── Métodos de impressão no console ──────────────────────────────────────

    // Imprime a tabela de frequências no formato esperado.
    private void imprimirFrequencias(int[] frequencias) {
        System.out.println("--------------------------------------------------");
        System.out.println("ETAPA 1: Tabela de Frequencia de Caracteres");
        System.out.println("--------------------------------------------------");
        for (int i = 0; i < TAMANHO_ASCII; i++) {
            if (frequencias[i] > 0) {
                char c = (char) i;
                // Exibe o caractere de forma legível
                String exibicao;
                if (c == '\n') exibicao = "\\n";
                else if (c == '\r') exibicao = "\\r";
                else if (c == '\t') exibicao = "\\t";
                else if (c == ' ')  exibicao = "ESPACO";
                else exibicao = String.valueOf(c);

                System.out.printf("Caractere '%s' (ASCII: %d): %d%n",
                        exibicao, i, frequencias[i]);
            }
        }
        System.out.println();
    }

    // Imprime a tabela de códigos de Huffman no formato esperado.
    private void imprimirTabelaCodigos(String[] codigos) {
        System.out.println("--------------------------------------------------");
        System.out.println("ETAPA 4: Tabela de Codigos de Huffman");
        System.out.println("--------------------------------------------------");
        for (int i = 0; i < TAMANHO_ASCII; i++) {
            if (codigos[i] != null) {
                char c = (char) i;
                String exibicao;
                if (c == '\n') exibicao = "\\n";
                else if (c == '\r') exibicao = "\\r";
                else if (c == '\t') exibicao = "\\t";
                else if (c == ' ')  exibicao = "ESPACO";
                else exibicao = String.valueOf(c);

                System.out.printf("Caractere '%s': %s%n", exibicao, codigos[i]);
            }
        }
        System.out.println();
    }
}


// Referência usada: https://medium.com/javarevisited/fileinputstream-and-fileoutputstream-in-java-a-guide-to-reading-and-writing-files-f46cb8a648a3