/*
 Classe responsável por construir a Árvore de Huffman e gerar a tabela de códigos.

 1. Recebe o vetor de frequências dos caracteres.
 2. Insere todos os caracteres com frequência > 0 no Min-Heap.
 3. Constrói a árvore combinando repetidamente os dois nós de menor frequência.
 4. Percorre a árvore recursivamente para gerar os códigos binários.
*/
public class ArvoreHuffman {
    private No raiz;

    // Tabela de códigos: tabela[c] = código binário do caractere c (String de '0' e '1').
    private String[] tabelaCodigos;

    // Tamanho da tabela (256 para cobrir todos os caracteres ASCII).
    private static final int TAMANHO_ASCII = 256;

    public ArvoreHuffman(int[] frequencias) {
        tabelaCodigos = new String[TAMANHO_ASCII];
        construir(frequencias);
    }

    // Constrói a árvore de Huffman a partir do vetor de frequências.
    // Usa o Min-Heap para sempre combinar os dois nós de menor frequência.
    private void construir(int[] frequencias) {
        MinHeap heap = new MinHeap();

        // Passo 1: Inserir todos os caracteres com frequência > 0 no heap
        for (int i = 0; i < TAMANHO_ASCII; i++) {
            if (frequencias[i] > 0) {
                heap.inserir(new No((char) i, frequencias[i]));
            }
        }

        // Edge case: apenas um caractere único no texto
        if (heap.tamanho() == 1) {
            No unico = heap.removerMinimo();
            raiz = new No(unico.frequencia, unico, null);
            tabelaCodigos[unico.caractere] = "0";
            return;
        }

        // Passo 2: Combinar nós até restar apenas a raiz
        while (heap.tamanho() > 1) {
            No menor1 = heap.removerMinimo(); // Menor frequência
            No menor2 = heap.removerMinimo(); // Segunda menor frequência

            // Cria nó pai com a soma das frequências
            No pai = new No(menor1.frequencia + menor2.frequencia, menor1, menor2);
            heap.inserir(pai);
        }

        raiz = heap.removerMinimo(); // O último nó restante é a raiz da árvore

        // Passo 3: Gerar tabela de códigos percorrendo a árvore
        gerarCodigos(raiz, "");
    }

    // Percorre a árvore recursivamente e preenche a tabela de códigos.
    // Esquerda = '0', Direita = '1'.
    private void gerarCodigos(No no, String codigo) {
        if (no == null) return;

        if (no.ehFolha()) {
            // Nó folha: armazena o código gerado para este caractere
            tabelaCodigos[no.caractere] = codigo.isEmpty() ? "0" : codigo;
        } else {
            // Nó interno: desce para esquerda (0) e direita (1)
            gerarCodigos(no.esquerda, codigo + "0");
            gerarCodigos(no.direita, codigo + "1");
        }
    }

    public No getRaiz() {
        return raiz;
    }

    // Retorna a tabela de códigos gerada.
    public String[] getTabelaCodigos() {
        return tabelaCodigos;
    }

    // Retorna o código binário de um caractere específico.
    public String getCodigo(char c) {
        return tabelaCodigos[(int) c];
    }

    // Imprime a árvore de Huffman em pré-ordem
    public void imprimirArvore() {
        System.out.println("ETAPA 3: Arvore de Huffman");
        imprimirArvoreRecursivo(raiz, 0);
    }

    private void imprimirArvoreRecursivo(No no, int nivel) {
        if (no == null) return;

        String prefixo = "- ";
        if (no.ehFolha()) {
            System.out.println(prefixo + "('" + no.caractere + "', " + no.frequencia + ")");
        } else {
            if (nivel == 0) {
                System.out.println(prefixo + "(RAIZ, " + no.frequencia + ")");
            } else {
                System.out.println(prefixo + "(N" + nivel + ", " + no.frequencia + ")");
            }
        }

        imprimirArvoreRecursivo(no.esquerda, nivel + 1);
        imprimirArvoreRecursivo(no.direita, nivel + 1);
    }
}
