import java.util.ArrayList;

//Implementação de uma Fila de Prioridades (Min-Heap) usando ArrayList.

// O Min-Heap é conceitualmente uma árvore binária completa onde cada nó pai
// tem frequência MENOR ou IGUAL à de seus filhos. Isso garante que o elemento
// de menor frequência esteja sempre na raiz (índice 0).

// Implementação com vetor (ArrayList):
// Para um nó no índice i:
//  - Filho esquerdo: índice 2*i + 1
//  - Filho direito:  índice 2*i + 2
//  - Pai:            índice (i - 1) / 2
public class MinHeap {
    // Vetor interno que armazena os nós do heap
    private ArrayList<No> heap;

    public MinHeap() {
        this.heap = new ArrayList<>();
    }

    public int tamanho() {
        return heap.size();
    }

    public boolean estaVazio() {
        return heap.isEmpty();
    }

    // Insere um novo nó no heap.
    // Complexidade: O(log n).
    // Estratégia: Adiciona o nó ao final do vetor e depois sobe
    // ("heapify") até que a propriedade do heap seja restaurada.
    public void inserir(No no) {
        heap.add(no);
        subirUltimoElemento();
    }

    // Remove e retorna o nó de menor frequência (a raiz do heap).
    // Complexidade: O(log n).
    // Estratégia: Troca a raiz com o último elemento, remove o último
    // (que era a raiz) e desce ("heapify down" / "sift down") o novo elemento
    // da raiz até restaurar a propriedade do heap.
    public No removerMinimo() {
        if (estaVazio()) {
            throw new IllegalStateException("Heap está vazio.");
        }

        // Troca raiz com o último elemento
        trocar(0, heap.size() - 1);

        // Remove o último elemento (que era a raiz = mínimo)
        No minimo = heap.remove(heap.size() - 1);

        // Desce o novo elemento da raiz para restaurar o heap
        if (!estaVazio()) {
            descerElemento(0);
        }

        return minimo;
    }

    // Retorna (sem remover) o nó de menor frequência
    public No getMinimo() {
        if (estaVazio()) {
            throw new IllegalStateException("Heap está vazio.");
        }
        return heap.get(0);
    }

    // Sobe o último elemento inserido até sua posição correta no heap.
    // Compara o elemento com seu pai e troca se for menor.
    private void subirUltimoElemento() {
        int indiceAtual = heap.size() - 1;

        while (indiceAtual > 0) {
            int indicePai = (indiceAtual - 1) / 2;

            // Se o elemento atual for menor que o pai, troca
            if (heap.get(indiceAtual).compareTo(heap.get(indicePai)) < 0) {
                trocar(indiceAtual, indicePai);
                indiceAtual = indicePai;
            } else {
                break; // Propriedade do heap restaurada
            }
        }
    }


    // Desce o elemento no índice dado até sua posição correta no heap.
    // Compara o elemento com seus filhos e troca com o menor filho, se necessário.
    private void descerElemento(int indice) {
        int tamanho = heap.size();

        while (true) {
            int filhoEsq = 2 * indice + 1;
            int filhoDir = 2 * indice + 2;
            int indiceMenor = indice; // Assume que o pai é o menor

            // Verifica se o filho esquerdo existe e é menor
            if (filhoEsq < tamanho &&
                heap.get(filhoEsq).compareTo(heap.get(indiceMenor)) < 0) {
                indiceMenor = filhoEsq;
            }

            // Verifica se o filho direito existe e é menor que o atual menor
            if (filhoDir < tamanho &&
                heap.get(filhoDir).compareTo(heap.get(indiceMenor)) < 0) {
                indiceMenor = filhoDir;
            }

            // Se o menor não é o pai, troca e continua descendo
            if (indiceMenor != indice) {
                trocar(indice, indiceMenor);
                indice = indiceMenor;
            } else {
                break; // Propriedade do heap restaurada
            }
        }
    }

   // Troca dois elementos no vetor do heap através dos índices
    private void trocar(int i, int j) {
        No temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[ ");
        for (int i = 0; i < heap.size(); i++) {
            No no = heap.get(i);
            if (no.ehFolha()) {
                sb.append("No('").append(no.caractere).append("',").append(no.frequencia).append(")");
            } else {
                sb.append("No(interno,").append(no.frequencia).append(")");
            }
            if (i < heap.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(" ]");
        return sb.toString();
    }
}
