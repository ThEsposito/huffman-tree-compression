// Classe que representa um nó da Árvore de Huffman.
// É usada tanto como nó folha (com caractere) quanto como nó interno (sem caractere).
// Implementa Comparable para permitir ordenação no Min-Heap por frequência.
public class No implements Comparable<No> {

    // Caractere armazenado neste nó (apenas nós folha possuem caractere válido).
    char caractere;

    // Frequência de ocorrência do caractere (ou soma das frequências dos filhos).
    int frequencia;

    // Referência ao filho esquerdo (representa bit '0').
    No esquerda;

    // Referência ao filho direito (representa bit '1').
    No direita;

    // Construtor para nó folha (contém um caractere).
    public No(char caractere, int frequencia) {
        this.caractere = caractere;
        this.frequencia = frequencia;
        this.esquerda = null;
        this.direita = null;
    }


    // Construtor para nó interno (não contém caractere, apenas frequência combinada).
    // Usado durante a construção da árvore ao combinar dois nós do heap.
    public No(int frequencia, No esquerda, No direita) {
        this.caractere = '\0'; // nó interno não tem caractere
        this.frequencia = frequencia;
        this.esquerda = esquerda;
        this.direita = direita;
    }

    public boolean ehFolha() {
        return esquerda == null && direita == null;
    }


    // Compara este nó com outro baseado na frequência.
    // Utilizado pelo Min-Heap para ordenação (menor frequência = maior prioridade).
    @Override
    public int compareTo(No outroNo) {
        return this.frequencia - outroNo.frequencia;
    }

    // Para debug e impressão do heap.
    @Override
    public String toString() {
        if (ehFolha()) {
            return "No('" + caractere + "'," + frequencia + ")";
        } else {
            return "No(interno," + frequencia + ")";
        }
    }
}
